package com.projteam.competico.service.group;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.projteam.competico.dao.group.GroupDAO;
import com.projteam.competico.dao.group.GroupGameResultDAO;
import com.projteam.competico.dao.group.GroupJoinRequestDAO;
import com.projteam.competico.dao.group.GroupMessageDAO;
import com.projteam.competico.domain.Account;
import com.projteam.competico.domain.group.Group;
import com.projteam.competico.domain.group.GroupJoinRequest;
import com.projteam.competico.domain.group.GroupMessage;
import com.projteam.competico.dto.group.GroupDTO;
import com.projteam.competico.dto.group.GroupInfoDTO;
import com.projteam.competico.dto.group.GroupJoinRequestDTO;
import com.projteam.competico.dto.group.GroupJoinRequestFullDTO;
import com.projteam.competico.dto.group.GroupMessageDTO;
import com.projteam.competico.service.AccountService;
import com.projteam.competico.service.game.GameService;

@Service
public class GroupService
{
	private GroupDAO groupDao;
	private GroupGameResultDAO ggrDao;
	private GroupJoinRequestDAO gjrDao;
	private GroupMessageDAO gmDao;
	
	private AccountService accServ;
	
	private final char[] groupCodeChars;
	private static final int GROUP_CODE_LENGTH = 9;
	private static final int MAX_GROUP_CODE_REROLL_COUNT = 1000;
	private static final String GROUP_NAME_REGEX =
			"^[a-zA-Z0-9ąĄłŁśŚćĆńŃóÓżŻźŹęĘ ./<>?;:\"'`!@#$%^&*\\(\\)\\[\\]\\{\\}_+=|\\\\-]{2,32}$";
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final int GROUP_LIST_PAGE_SIZE = 30;
	
	@Autowired
	public GroupService(AccountService accServ,
			GroupDAO groupDao,
			GroupGameResultDAO ggrDao,
			GroupJoinRequestDAO gjrDao,
			GroupMessageDAO gmDao)
	{
		this.accServ = accServ;
		this.groupDao = groupDao;
		this.ggrDao = ggrDao;
		this.gjrDao = gjrDao;
		this.gmDao = gmDao;
		
		groupCodeChars = IntStream.range(0, 256)
				.filter(GroupService::isValidGroupCodeChar)
				.mapToObj(c -> Character.toString((char) c))
	            .collect(Collectors.joining())
	            .toCharArray();
	}

	@Transactional
	public String createGroup(String groupName)
	{
		return createGroup(groupName, getAccount());
	}
	@Transactional
	public String createGroup(String groupName, Account creator)
	{
		if (!creator.hasRole(Account.LECTURER_ROLE))
			throw new IllegalArgumentException("NOT_LECTURER");
		
		UUID creatorId = creator.getId();
		String name = validateGroupName(groupName);
		
		if (groupDao.existsByNameAndLecturers_id(name, creatorId))
			throw new IllegalArgumentException("DUPLICATE_GROUP_NAME");
		
		String code = getAvailableGroupCode();
		
		Group group = new Group(UUID.randomUUID(), name, code, List.of(creator));
		
		groupDao.saveAndFlush(group);
		return code;
	}
	@Transactional
	public Page<GroupInfoDTO> getGroupList(int page)
	{
		return getGroupList(page, getAccount());
	}
	@Transactional
	public Page<GroupInfoDTO> getGroupList(int page, Account acc)
	{
		UUID accId = acc.getId();
		return groupDao.findAllByPlayers_idOrLecturers_id(accId, accId,
					PageRequest.of(Math.max(page, 0),
							GROUP_LIST_PAGE_SIZE,
							Sort.by(Order.desc("name"),
									Order.desc("creationDate"))))
				.map(g -> new GroupInfoDTO(
						g.getName(),
						g.getGroupCode(),
						DATE_FORMAT.format(g.getCreationDate()),
						g.getLecturers().size() + g.getPlayers().size()));
	}
	@Transactional
	public List<String> getGroupNameList()
	{
		return getGroupNameList(getAccount());
	}
	@Transactional
	public List<String> getGroupNameList(Account acc)
	{
		if (!acc.hasRole(Account.LECTURER_ROLE))
			throw new IllegalArgumentException("NOT_LECTURER");
		
		UUID accId = acc.getId();
		
		return groupDao.findAllByLecturers_id(accId)
				.stream()
				.map(g -> g.getName())
				.collect(Collectors.toList());
	}
	@Transactional
	public void changeGroupName(String groupCode, String newGroupName)
	{
		changeGroupName(groupCode, newGroupName, getAccount());
	}
	@Transactional
	public void changeGroupName(String groupCode, String newGroupName, Account acc)
	{
		Group group = groupDao.findByGroupCode(groupCode)
				.orElseThrow(() -> new IllegalArgumentException("GROUP_NOT_FOUND"));
		
		UUID accId = acc.getId();
		
		if (group.getLecturers()
				.stream()
				.map(a -> a.getId())
				.noneMatch(id -> id.equals(accId)))
			throw new IllegalArgumentException("ACCESS_DENIED");
		
		String name = validateGroupName(newGroupName);
		
		if (groupDao.existsByNameAndLecturers_id(name, accId))
			throw new IllegalArgumentException("DUPLICATE_GROUP_NAME");
		
		group.setName(name);
		groupDao.save(group);
	}
	@Transactional
	public GroupDTO getGroupInfo(String groupCode)
	{
		return getGroupInfo(groupCode, getAccount());
	}
	@Transactional
	public GroupDTO getGroupInfo(String groupCode, Account acc)
	{
		Group group = groupDao.findByGroupCode(groupCode)
				.orElseThrow(() -> new IllegalArgumentException("GROUP_NOT_FOUND"));
		
		if (!groupContainsAccount(group, acc))
			throw new IllegalArgumentException("NOT_IN_GROUP");
		
		return GroupDTO.builder()
				.name(group.getName())
				.groupCode(group.getGroupCode())
				.creationDate(DATE_FORMAT.format(group.getCreationDate()))
				.lecturers(group.getLecturers()
						.stream()
						.map(a -> a.getUsername())
						.collect(Collectors.toList()))
				.players(group.getPlayers()
						.stream()
						.map(a -> a.getUsername())
						.collect(Collectors.toList()))
				.gameCodes(new ArrayList<>(group.getGameCodes()))
				.build();
	}
	@Transactional
	public void deleteGroup(String groupCode)
	{
		deleteGroup(groupCode, getAccount());
	}
	@Transactional
	public void deleteGroup(String groupCode, Account creator)
	{
		Group group = groupDao.findByGroupCode(groupCode)
				.orElseThrow(() -> new IllegalArgumentException("GROUP_NOT_FOUND"));
		
		if (group.getLecturers()
				.stream()
				.map(a -> a.getId())
				.noneMatch(id -> id.equals(creator.getId())))
			throw new IllegalArgumentException("ACCESS_DENIED");
		
		deleteGroup(group);
	}
	private void deleteGroup(Group group)
	{
		UUID id = group.getId();
		
		ggrDao.deleteAllByGroup_id(id);
		gjrDao.deleteAllByGroup_id(id);
		gmDao.deleteAllByGroup_id(id);
		groupDao.deleteById(id);
	}
	@Transactional
	public void leaveGroup(String groupCode)
	{
		leaveGroup(groupCode, getAccount());
	}
	@Transactional
	public void leaveGroup(String groupCode, Account acc)
	{
		Group group = groupDao.findByGroupCode(groupCode)
				.orElseThrow(() -> new IllegalArgumentException("GROUP_NOT_FOUND"));
		
		if (!groupContainsAccount(group, acc))
			throw new IllegalArgumentException("NOT_IN_GROUP");
		
		UUID accId = acc.getId();
		
		if (group.getLecturers()
				.stream()
				.map(a -> a.getId())
				.anyMatch(aId -> aId.equals(accId)))
			group.setLecturers(new ArrayList<>(
					group.getLecturers()
						.stream()
						.filter(a -> !a.getId().equals(accId))
						.collect(Collectors.toList())));
		if (group.getPlayers()
				.stream()
				.map(a -> a.getId())
				.anyMatch(aId -> aId.equals(accId)))
			group.setPlayers(new ArrayList<>(
					group.getPlayers()
						.stream()
						.filter(a -> !a.getId().equals(accId))
						.collect(Collectors.toList())));
		
		if (group.getLecturers().isEmpty())
			deleteGroup(group);
		else
			groupDao.save(group);
	}
	@Transactional
	public boolean removeUserFromGroup(
			String groupCode, String username)
	{
		return removeUserFromGroup(groupCode, username, getAccount());
	}
	@Transactional
	public boolean removeUserFromGroup(
			String groupCode, String username, Account acc)
	{
		Group group = groupDao.findByGroupCode(groupCode)
				.orElseThrow(() -> new IllegalArgumentException("GROUP_NOT_FOUND"));
		
		UUID accId = acc.getId();
		
		if (group.getLecturers()
				.stream()
				.map(a -> a.getId())
				.noneMatch(id -> id.equals(accId)))
			throw new IllegalArgumentException("ACCESS_DENIED");
		
		Account accToRemove = accServ.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));
		UUID accToRemoveId = accToRemove.getId();
		
		if (accToRemoveId.equals(accId))
			throw new IllegalArgumentException("CANNOT_DELETE_SELF");
		
		boolean userRemoved = false;
		if (group.getLecturers()
				.stream()
				.map(a -> a.getId())
				.anyMatch(aId -> aId.equals(accToRemoveId)))
		{
			group.setLecturers(new ArrayList<>(
					group.getLecturers()
						.stream()
						.filter(a -> !a.getId().equals(accToRemoveId))
						.collect(Collectors.toList())));
			userRemoved = true;
		}
		if (group.getPlayers()
				.stream()
				.map(a -> a.getId())
				.anyMatch(aId -> aId.equals(accToRemoveId)))
		{
			group.setPlayers(new ArrayList<>(
					group.getPlayers()
						.stream()
						.filter(a -> !a.getId().equals(accToRemoveId))
						.collect(Collectors.toList())));
			userRemoved = true;
		}
		
		groupDao.save(group);
		
		return userRemoved;
	}
	
	@Transactional
	public void requestToJoinGroup(String groupCode)
	{
		requestToJoinGroup(groupCode, getAccount());
	}
	@Transactional
	public void requestToJoinGroup(String groupCode, Account acc)
	{
		Group group = groupDao.findByGroupCode(groupCode)
				.orElseThrow(() -> new IllegalArgumentException("GROUP_NOT_FOUND"));
		
		UUID groupId = group.getId();
		UUID accId = acc.getId();
		
		if (groupContainsAccount(group, acc))
			throw new IllegalArgumentException("ALREADY_IN_GROUP");
		
		if (gjrDao.existsByAccount_idAndGroup_id(accId, groupId))
			throw new IllegalArgumentException("ALREADY_REQUESTED");
		
		GroupJoinRequest request = new GroupJoinRequest(
				UUID.randomUUID(), acc, group, new Date());
		
		gjrDao.save(request);
	}
	@Transactional
	public void deleteRequestToJoinGroup(String groupCode)
	{
		deleteRequestToJoinGroup(groupCode, getAccount());
	}
	@Transactional
	public void deleteRequestToJoinGroup(String groupCode, Account acc)
	{
		Group group = groupDao.findByGroupCode(groupCode)
				.orElseThrow(() -> new IllegalArgumentException("GROUP_NOT_FOUND"));
		
		UUID groupId = group.getId();
		UUID accId = acc.getId();
		
		gjrDao.findByAccount_idAndGroup_id(accId, groupId)
				.ifPresentOrElse(req -> gjrDao.deleteById(req.getId()),
				() -> {
					throw new IllegalArgumentException("REQUEST_NOT_FOUND");
				});
		
	}
	@Transactional
	public List<GroupJoinRequestDTO> getGroupJoinRequests(String groupCode)
	{
		return getGroupJoinRequests(groupCode, getAccount());
	}
	@Transactional
	public List<GroupJoinRequestDTO> getGroupJoinRequests(String groupCode, Account acc)
	{
		Group group = groupDao.findByGroupCode(groupCode)
				.orElseThrow(() -> new IllegalArgumentException("GROUP_NOT_FOUND"));
		
		if (!groupContainsAccount(group, acc))
			throw new IllegalArgumentException("NOT_IN_GROUP");
		
		return gjrDao.findAllByGroup_id(group.getId())
				.stream()
				.map(req -> new GroupJoinRequestDTO(
						req.getId(),
						req.getAccount().getUsername(),
						new ArrayList<>(req.getAccount().getRoles()),
						DATE_FORMAT.format(req.getCreationDate())))
				.collect(Collectors.toList());
	}
	
	@Transactional
	public Page<GroupJoinRequestFullDTO> getOwnGroupJoinRequests(int page)
	{
		return getOwnGroupJoinRequests(page, getAccount());
	}
	@Transactional
	public Page<GroupJoinRequestFullDTO> getOwnGroupJoinRequests(int page, Account acc)
	{
		UUID accId = acc.getId();
		
		return gjrDao.findAllByAccount_id(accId, PageRequest.of(
					Math.max(page, 0), GROUP_LIST_PAGE_SIZE,
					Sort.by(Order.by("groupCode"), Order.by("creationDate"))))
				.map(req -> new GroupJoinRequestFullDTO(
						req.getId(),
						req.getAccount().getUsername(),
						new ArrayList<>(req.getAccount().getRoles()),
						req.getGroup().getName(),
						req.getGroup().getGroupCode(),
						DATE_FORMAT.format(req.getCreationDate())));
	}
	@Transactional
	public Page<GroupJoinRequestFullDTO> getGroupJoinRequests(int page)
	{
		return getGroupJoinRequests(page, getAccount());
	}
	@Transactional
	public Page<GroupJoinRequestFullDTO> getGroupJoinRequests(int page, Account lecturer)
	{
		if (!lecturer.hasRole(Account.LECTURER_ROLE))
			throw new IllegalArgumentException("NOT_LECTURER");
		
		UUID accId = lecturer.getId();

		return gjrDao.findAllByGroup_Lecturers_id(accId, PageRequest.of(
					Math.max(page, 0), GROUP_LIST_PAGE_SIZE,
					Sort.by(Order.by("groupCode"), Order.by("creationDate"))))
				.map(req -> new GroupJoinRequestFullDTO(
						req.getId(),
						req.getAccount().getUsername(),
						new ArrayList<>(req.getAccount().getRoles()),
						req.getGroup().getName(),
						req.getGroup().getGroupCode(),
						DATE_FORMAT.format(req.getCreationDate())));
	}
	
	@Transactional
	public List<GroupJoinRequestFullDTO> getOwnGroupJoinRequests()
	{
		return getOwnGroupJoinRequests(getAccount());
	}
	@Transactional
	public List<GroupJoinRequestFullDTO> getOwnGroupJoinRequests(Account acc)
	{
		UUID accId = acc.getId();
		
		return gjrDao.findAllByAccount_id(accId)
				.stream()
				.map(req -> new GroupJoinRequestFullDTO(
						req.getId(),
						req.getAccount().getUsername(),
						new ArrayList<>(req.getAccount().getRoles()),
						req.getGroup().getName(),
						req.getGroup().getGroupCode(),
						DATE_FORMAT.format(req.getCreationDate())))
				.collect(Collectors.toList());
	}
	@Transactional
	public List<GroupJoinRequestFullDTO> getGroupJoinRequests()
	{
		return getGroupJoinRequests(getAccount());
	}
	@Transactional
	public List<GroupJoinRequestFullDTO> getGroupJoinRequests(Account lecturer)
	{
		if (!lecturer.hasRole(Account.LECTURER_ROLE))
			throw new IllegalArgumentException("NOT_LECTURER");
		
		UUID accId = lecturer.getId();

		return gjrDao.findAllByGroup_Lecturers_id(accId)
				.stream()
				.map(req -> new GroupJoinRequestFullDTO(
						req.getId(),
						req.getAccount().getUsername(),
						new ArrayList<>(req.getAccount().getRoles()),
						req.getGroup().getName(),
						req.getGroup().getGroupCode(),
						DATE_FORMAT.format(req.getCreationDate())))
				.collect(Collectors.toList());
	}
	
	@Transactional
	public void processGroupJoinRequest(UUID requestId, boolean accept)
	{
		processGroupJoinRequest(requestId, accept, getAccount());
	}
	@Transactional
	public void processGroupJoinRequest(UUID requestId, boolean accept, Account acc)
	{
		GroupJoinRequest request = gjrDao.findById(requestId)
				.orElseThrow(() -> new IllegalArgumentException("REQUEST_NOT_FOUND"));
		
		Group group = request.getGroup();
		
		UUID accId = acc.getId();
		
		if (group.getLecturers()
				.stream()
				.map(a -> a.getId())
				.noneMatch(id -> id.equals(accId)))
			throw new IllegalArgumentException("ACCESS_DENIED");
		
		if (accept)
		{
			Account accToAdd = request.getAccount();
			if (accToAdd.hasRole(Account.PLAYER_ROLE))
				group.getPlayers().add(accToAdd);
			else if (accToAdd.hasRole(Account.LECTURER_ROLE))
				group.getLecturers().add(accToAdd);
			else
				throw new IllegalArgumentException("INVALID_USER_ROLES");
			gjrDao.deleteById(request.getId());
			groupDao.save(group);
		}
		else
			gjrDao.deleteById(request.getId());
	}
	@Transactional
	public void postGroupMessage(String groupCode,
			String title, String content)
	{
		postGroupMessage(groupCode, title, content, getAccount());
	}
	@Transactional
	public void postGroupMessage(String groupCode,
			String title, String content, Account acc)
	{
		Group group = groupDao.findByGroupCode(groupCode)
				.orElseThrow(() -> new IllegalArgumentException("GROUP_NOT_FOUND"));
		
		UUID accId = acc.getId();
		
		if (group.getLecturers()
				.stream()
				.map(a -> a.getId())
				.noneMatch(id -> id.equals(accId)))
			throw new IllegalArgumentException("ACCESS_DENIED");
		
		gmDao.save(new GroupMessage(UUID.randomUUID(),
				acc, group, title, content,
				new Date(), null, new ArrayList<>(List.of(accId))));
	}
	@Transactional
	public List<GroupMessageDTO> getGroupMessages(String groupCode)
	{
		return getGroupMessages(groupCode, getAccount());
	}
	@Transactional
	public List<GroupMessageDTO> getGroupMessages(String groupCode, Account acc)
	{
		Group group = groupDao.findByGroupCode(groupCode)
				.orElseThrow(() -> new IllegalArgumentException("GROUP_NOT_FOUND"));
		
		if (!groupContainsAccount(group, acc))
			throw new IllegalArgumentException("NOT_IN_GROUP");
		
		UUID accId = acc.getId();
		UUID groupId = group.getId();
		
		return gmDao.findAllByGroup_id(groupId)
				.stream()
				.map(msg -> GroupMessageDTO.builder()
						.id(msg.getId())
						.username(msg.getAccount().getUsername())
						.title(msg.getTitle())
						.content(msg.getContent())
						.creationDate(DATE_FORMAT.format(msg.getCreationDate()))
						.editDate(Optional.ofNullable(msg.getEditDate())
								.map(date -> DATE_FORMAT.format(date))
								.orElse(null))
						.read(msg.getReadBy()
								.stream()
								.anyMatch(id -> id.equals(accId)))
						.build())
				.collect(Collectors.toList());
	}
	@Transactional
	public void setReadStatusOnGroupMessage(UUID msgId, boolean msgRead)
	{
		setReadStatusOnGroupMessage(msgId, msgRead, getAccount());
	}
	@Transactional
	public void setReadStatusOnGroupMessage(
			UUID msgId, boolean msgRead, Account acc)
	{
		GroupMessage msg = gmDao.findById(msgId)
				.orElseThrow(() -> new IllegalArgumentException("MESSAGE_NOT_FOUND"));
		
		Group group = msg.getGroup();
		
		if (!groupContainsAccount(group, acc))
			throw new IllegalArgumentException("NOT_IN_GROUP");
		
		UUID accId = acc.getId();
		
		List<UUID> readBy = msg.getReadBy();
		if (msgRead)
		{
			if (!readBy.contains(accId))
				readBy.add(accId);
		}
		else if (!msgRead && readBy.contains(accId))
			readBy.remove(accId);
		
		msg.setReadBy(readBy);
		
		gmDao.save(msg);
	}
	@Transactional
	public void editGroupMessage(UUID msgId, String title, String content)
	{
		editGroupMessage(msgId, title, content, getAccount());
	}
	@Transactional
	public void editGroupMessage(UUID msgId,
			String title, String content, Account acc)
	{
		GroupMessage msg = gmDao.findById(msgId)
				.orElseThrow(() -> new IllegalArgumentException("MESSAGE_NOT_FOUND"));
		
		UUID accId = acc.getId();
		
		if (!msg.getAccount().getId().equals(accId))
			throw new IllegalArgumentException("ACCESS_DENIED");
		
		msg.setTitle(title);
		msg.setContent(content);
		msg.setEditDate(new Date());
		
		gmDao.save(msg);
	}
	
	@Transactional
	public Page<Map<String, String>> getGameHistory(String groupCode, int page)
	{
		return getGameHistory(groupCode, page, getAccount());
	}
	@Transactional
	public Page<Map<String, String>> getGameHistory(String groupCode, int page, Account acc)
	{
		Group group = groupDao.findByGroupCode(groupCode)
				.orElseThrow(() -> new IllegalArgumentException("GROUP_NOT_FOUND"));
		
		if (!groupContainsAccount(group, acc))
			throw new IllegalArgumentException("NOT_IN_GROUP");
		
		return ggrDao.findAllByGroup_id(group.getId(),
				PageRequest.of(page, GameService.HISTORY_PAGE_SIZE,
						Sort.by(Order.desc("gameResult.date"))))
				.map(ggr -> ggr.getGameResult())
				.map(gr -> Map.of(
						"id", gr.getGameID().toString(),
						"date", DATE_FORMAT.format(gr.getDate())));
	}
	
	private String getAvailableGroupCode()
	{
		for (int i = 0; i < MAX_GROUP_CODE_REROLL_COUNT; i++)
		{
			String code = generateGroupCode();
			if (!groupDao.existsByGroupCode(code))
				return code;
		}
		
		throw new IllegalArgumentException("CANNOT_GENERATE_GROUP_CODE");
	}
	private String generateGroupCode()
	{
		return new Random().ints(GROUP_CODE_LENGTH, 0, groupCodeChars.length)
			.mapToObj(i -> Character.toString(groupCodeChars[i]))
	        .collect(Collectors.joining());
	}
	private boolean groupContainsAccount(Group group, Account acc)
	{
		UUID accId = acc.getId();
		return group.getLecturers()
				.stream()
				.map(a -> a.getId())
				.anyMatch(id -> id.equals(accId))
			|| group.getPlayers()
				.stream()
				.map(a -> a.getId())
				.anyMatch(id -> id.equals(accId));
	}
	
	private String validateGroupName(String groupName)
	{
		if (groupName == null)
			throw new IllegalArgumentException("INVALID_GROUP_NAME");
		
		if (groupName.matches(GROUP_NAME_REGEX))
			return groupName;
		throw new IllegalArgumentException("INVALID_GROUP_NAME");
	}
	private static boolean isValidGroupCodeChar(int c)
	{
		return ((c >= '0') && (c <= '9'))
				|| ((c >= 'a') && (c <= 'z'))
				|| ((c >= 'A') && (c <= 'Z'));
	}
	
	private Account getAccount()
	{
		return accServ.getAuthenticatedAccount()
				.orElseThrow(() -> new IllegalArgumentException("Not authenticated."));
	}
}
