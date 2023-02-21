package com.projteam.competico.service.group;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import com.projteam.competico.dao.group.GroupDAO;
import com.projteam.competico.dao.group.GroupGameResultDAO;
import com.projteam.competico.dao.group.GroupJoinRequestDAO;
import com.projteam.competico.dao.group.GroupMessageDAO;
import com.projteam.competico.domain.Account;
import com.projteam.competico.domain.game.GameResult;
import com.projteam.competico.domain.game.PlayerResult;
import com.projteam.competico.domain.group.Group;
import com.projteam.competico.domain.group.GroupGameResult;
import com.projteam.competico.domain.group.GroupJoinRequest;
import com.projteam.competico.domain.group.GroupMessage;
import com.projteam.competico.service.AccountService;

class GroupServiceTests
{
	private @Mock GroupDAO groupDao;
	private @Mock GroupGameResultDAO ggrDao;
	private @Mock GroupJoinRequestDAO gjrDao;
	private @Mock GroupMessageDAO gmDao;
	private @Mock AccountService accServ;
	
	private @InjectMocks GroupService groupServ;
	
	@BeforeEach
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void canCreateGroup()
	{
		Account lecturer = mockLecturer();
		String groupCode = "groupCode";
		
		groupServ.createGroup(groupCode, lecturer);
		
		ArgumentCaptor<Group> taskCap = ArgumentCaptor.forClass(Group.class);
		
		verify(groupDao, atLeast(0)).save(taskCap.capture());
		verify(groupDao, atLeast(0)).saveAndFlush(taskCap.capture());
		
		assertEquals(taskCap.getAllValues().size(), 1);
	}
	@Test
	public void canCreateGroupWhileAuthenticated()
	{
		when(accServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(mockLecturer()));
		String groupCode = "groupCode";
		
		groupServ.createGroup(groupCode);
		
		ArgumentCaptor<Group> taskCap = ArgumentCaptor.forClass(Group.class);
		
		verify(groupDao, atLeast(0)).save(taskCap.capture());
		verify(groupDao, atLeast(0)).saveAndFlush(taskCap.capture());
		
		assertEquals(taskCap.getAllValues().size(), 1);
	}
	
	@Test
	public void canGetGroupList()
	{
		Account lecturer = mockLecturer();
		
		when(groupDao.findAllDistinctByPlayers_idOrLecturers_id(any(), any(), any()))
			.thenReturn(new PageImpl<>(List.of(
					mockGroup("group1", lecturer), mockGroup("group2", lecturer))));
		
		assertNotNull(groupServ.getGroupList(1, lecturer));
	}
	@Test
	public void canGetGroupListWhileAuthenticated()
	{
		Account lecturer = mockLecturer();
		
		when(accServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(lecturer));
		
		when(groupDao.findAllDistinctByPlayers_idOrLecturers_id(any(), any(), any()))
			.thenReturn(new PageImpl<>(List.of(
					mockGroup("group1", lecturer), mockGroup("group2", lecturer))));
		
		assertNotNull(groupServ.getGroupList(1));
	}

	@Test
	public void canGetGroupNameList()
	{
		Account lecturer = mockLecturer();
		
		assertNotNull(groupServ.getGroupNameList(lecturer));
	}
	@Test
	public void canGetGroupListNameWhileAuthenticated()
	{
		when(accServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(mockLecturer()));
		
		assertNotNull(groupServ.getGroupNameList());
	}
	
	@Test
	public void canChangeGroupName()
	{
		Account lecturer = mockLecturer();
		String groupCode = "groupCode";
		String newGroupName = "newGroupName";
		
		when(groupDao.findByGroupCode(groupCode))
			.thenReturn(Optional.of(mockGroup("groupName", groupCode, lecturer)));
		
		groupServ.changeGroupName(groupCode, newGroupName, lecturer);
		
		ArgumentCaptor<Group> taskCap = ArgumentCaptor.forClass(Group.class);
		
		verify(groupDao, atLeast(0)).save(taskCap.capture());
		verify(groupDao, atLeast(0)).saveAndFlush(taskCap.capture());
		
		assertEquals(taskCap.getAllValues().size(), 1);
	}

	@Test
	public void canChangeGroupNameWhileAuthenticated()
	{
		Account lecturer = mockLecturer();
		when(accServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(lecturer));
		String groupCode = "groupCode";
		String newGroupName = "newGroupName";
		
		when(groupDao.findByGroupCode(groupCode))
			.thenReturn(Optional.of(mockGroup("groupName", groupCode, lecturer)));
		
		groupServ.changeGroupName(groupCode, newGroupName);
		
		ArgumentCaptor<Group> taskCap = ArgumentCaptor.forClass(Group.class);
		
		verify(groupDao, atLeast(0)).save(taskCap.capture());
		verify(groupDao, atLeast(0)).saveAndFlush(taskCap.capture());
		
		assertEquals(taskCap.getAllValues().size(), 1);
	}
	
	@Test
	public void canGetGroupInfo()
	{
		Account lecturer = mockLecturer();
		String groupCode = "groupCode";
		
		when(groupDao.findByGroupCode(groupCode))
			.thenReturn(Optional.of(mockGroup("groupName", groupCode, lecturer)));
		
		assertNotNull(groupServ.getGroupInfo(groupCode, lecturer));
	}
	@Test
	public void canGetGroupInfoWhileAuthenticated()
	{
		Account lecturer = mockLecturer();
		when(accServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(lecturer));
		String groupCode = "groupCode";
		
		when(groupDao.findByGroupCode(groupCode))
			.thenReturn(Optional.of(mockGroup("groupName", groupCode, lecturer)));
		
		assertNotNull(groupServ.getGroupInfo(groupCode));
	}
	
	@Test
	public void canDeleteGroup()
	{
		Account lecturer = mockLecturer();
		String groupCode = "groupCode";
		
		when(groupDao.findByGroupCode(groupCode))
			.thenReturn(Optional.of(mockGroup("groupName", groupCode, lecturer)));
		
		assertDoesNotThrow(() -> groupServ.deleteGroup(groupCode, lecturer));
	}
	@Test
	public void canDeleteGroupWhileAuthenticated()
	{
		Account lecturer = mockLecturer();
		when(accServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(lecturer));
		String groupCode = "groupCode";
		
		when(groupDao.findByGroupCode(groupCode))
			.thenReturn(Optional.of(mockGroup("groupName", groupCode, lecturer)));
		
		assertDoesNotThrow(() -> groupServ.deleteGroup(groupCode));
	}
	
	@Test
	public void canLeaveGroup()
	{
		Account lecturer = mockLecturer();
		String groupCode = "groupCode";
		
		when(groupDao.findByGroupCode(groupCode))
			.thenReturn(Optional.of(mockGroup("groupName", groupCode, lecturer)));
		
		assertDoesNotThrow(() -> groupServ.leaveGroup(groupCode, lecturer));
	}
	@Test
	public void canLeaveGroupWhileAuthenticated()
	{
		Account lecturer = mockLecturer();
		when(accServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(lecturer));
		String groupCode = "groupCode";
		
		when(groupDao.findByGroupCode(groupCode))
			.thenReturn(Optional.of(mockGroup("groupName", groupCode, lecturer)));
		
		assertDoesNotThrow(() -> groupServ.leaveGroup(groupCode));
	}
	
	@Test
	public void canRemoveUserGromGroup()
	{
		Account lecturer = mockLecturer();
		Account player = mockPlayer();
		String groupCode = "groupCode";
		String username = player.getUsername();

		when(accServ.findByUsername(username))
			.thenReturn(Optional.of(player));
		when(groupDao.findByGroupCode(groupCode))
			.thenReturn(Optional.of(mockGroup("groupName", groupCode, lecturer, player)));
		
		assertTrue(groupServ.removeUserFromGroup(groupCode, username, lecturer));
	}
	@Test
	public void canRemoveUserGromGroupWhileAuthenticated()
	{
		Account lecturer = mockLecturer();
		when(accServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(lecturer));
		Account player = mockPlayer();
		String groupCode = "groupCode";
		String username = player.getUsername();
		
		when(accServ.findByUsername(username))
			.thenReturn(Optional.of(player));
		when(groupDao.findByGroupCode(groupCode))
			.thenReturn(Optional.of(mockGroup("groupName", groupCode, lecturer, player)));
		
		assertTrue(groupServ.removeUserFromGroup(groupCode, username));
	}
	
	@Test
	public void canGetGroupJoinRequests()
	{
		Account lecturer = mockLecturer();
		String groupCode = "groupCode";
		
		when(groupDao.findByGroupCode(groupCode))
			.thenReturn(Optional.of(mockGroup("groupName", groupCode, lecturer)));
		
		assertNotNull(groupServ.getGroupJoinRequests(groupCode, lecturer));
	}
	@Test
	public void canGetGroupJoinRequestsWhileAuthenticated()
	{
		Account lecturer = mockLecturer();
		when(accServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(lecturer));
		String groupCode = "groupCode";
		
		when(groupDao.findByGroupCode(groupCode))
			.thenReturn(Optional.of(mockGroup("groupName", groupCode, lecturer)));
		
		assertNotNull(groupServ.getGroupJoinRequests(groupCode));
	}
	
	@Test
	public void canGetAllGroupJoinRequests()
	{
		Account lecturer = mockLecturer();
		
		when(gjrDao.findAllByGroup_Lecturers_id(lecturer.getId()))
			.thenReturn(List.of(mockGroupJoinRequest(
					mockGroup("groupName", lecturer),
					mockPlayer())));
		
		assertNotNull(groupServ.getGroupJoinRequests(lecturer));
	}
	@Test
	public void canGetAllGroupJoinRequestsWhileAuthenticated()
	{
		Account lecturer = mockLecturer();
		when(accServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(lecturer));
		
		when(gjrDao.findAllByGroup_Lecturers_id(lecturer.getId()))
			.thenReturn(List.of(mockGroupJoinRequest(
					mockGroup("groupName", lecturer),
					mockPlayer())));
		
		assertNotNull(groupServ.getGroupJoinRequests());
	}
	@Test
	public void canGetAllGroupJoinRequestsPage()
	{
		Account lecturer = mockLecturer();
		
		when(gjrDao.findAllByGroup_Lecturers_id(any(), any()))
			.thenReturn(new PageImpl<>(List.of(mockGroupJoinRequest(
					mockGroup("groupName", lecturer),
					mockPlayer()))));
		
		assertNotNull(groupServ.getGroupJoinRequests(1, lecturer));
	}
	@Test
	public void canGetAllGroupJoinRequestsPageWhileAuthenticated()
	{
		Account lecturer = mockLecturer();
		when(accServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(lecturer));
		
		when(gjrDao.findAllByGroup_Lecturers_id(any(), any()))
			.thenReturn(new PageImpl<>(List.of(mockGroupJoinRequest(
					mockGroup("groupName", lecturer),
					mockPlayer()))));
		
		assertNotNull(groupServ.getGroupJoinRequests(1));
	}
	
	@Test
	public void canGetOwnGroupJoinRequests()
	{
		Account lecturer = mockLecturer();
		Account player = mockPlayer();
		
		when(gjrDao.findAllByAccount_id(player.getId()))
		.thenReturn(List.of(mockGroupJoinRequest(
				mockGroup("groupName", lecturer),
				player)));
		
		assertNotNull(groupServ.getOwnGroupJoinRequests(player));
	}
	@Test
	public void canGetOwnGroupJoinRequestsWhileAuthenticated()
	{
		Account lecturer = mockLecturer();
		Account player = mockPlayer();
		when(accServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(player));
		
		when(gjrDao.findAllByAccount_id(player.getId()))
			.thenReturn(List.of(mockGroupJoinRequest(
					mockGroup("groupName", lecturer),
					player)));
		
		assertNotNull(groupServ.getOwnGroupJoinRequests());
	}
	@Test
	public void canGetOwnGroupJoinRequestsPage()
	{
		Account lecturer = mockLecturer();
		Account player = mockPlayer();
		
		when(gjrDao.findAllByAccount_id(any(), any()))
			.thenReturn(new PageImpl<>(List.of(mockGroupJoinRequest(
					mockGroup("groupName", lecturer),
					player))));
		
		assertNotNull(groupServ.getOwnGroupJoinRequests(1, player));
	}
	@Test
	public void canGetOwnGroupJoinRequestsPageWhileAuthenticated()
	{
		Account lecturer = mockLecturer();
		Account player = mockPlayer();
		when(accServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(player));
		
		when(gjrDao.findAllByAccount_id(any(), any()))
			.thenReturn(new PageImpl<>(List.of(mockGroupJoinRequest(
					mockGroup("groupName", lecturer),
					player))));
		
		assertNotNull(groupServ.getOwnGroupJoinRequests(1));
	}
	
	@Test
	public void canPostGroupMessage()
	{
		Account lecturer = mockLecturer();
		String groupCode = "groupCode";
		String title = "title";
		String content = "content";
		
		when(groupDao.findByGroupCode(groupCode))
			.thenReturn(Optional.of(mockGroup("groupName", groupCode, lecturer)));
		
		assertDoesNotThrow(() -> groupServ.postGroupMessage(
				groupCode, title, content, lecturer));
	}
	@Test
	public void canPostGroupMessageWhileAuthenticated()
	{
		Account lecturer = mockLecturer();
		when(accServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(lecturer));
		String groupCode = "groupCode";
		String title = "title";
		String content = "content";
		
		when(groupDao.findByGroupCode(groupCode))
			.thenReturn(Optional.of(mockGroup("groupName", groupCode, lecturer)));
		
		assertDoesNotThrow(() -> groupServ.postGroupMessage(
				groupCode, title, content));
	}
	
	@Test
	public void canGetGroupMessages()
	{
		Account lecturer = mockLecturer();
		String groupCode = "groupCode";
		
		when(groupDao.findByGroupCode(groupCode))
			.thenReturn(Optional.of(mockGroup("groupName", groupCode, lecturer)));
		
		assertNotNull(groupServ.getGroupMessages(groupCode, lecturer));
	}
	@Test
	public void canGetGroupMessagesWhileAuthenticated()
	{
		Account lecturer = mockLecturer();
		when(accServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(lecturer));
		String groupCode = "groupCode";
		
		when(groupDao.findByGroupCode(groupCode))
			.thenReturn(Optional.of(mockGroup("groupName", groupCode, lecturer)));
		
		assertNotNull(groupServ.getGroupMessages(groupCode));
	}
	
	@Test
	public void canEditGroupMessage()
	{
		Account lecturer = mockLecturer();
		UUID msgId = UUID.randomUUID();
		String title = "title";
		String content = "content";
		
		when(gmDao.findById(msgId))
			.thenReturn(Optional.of(mockMessage(lecturer,
					mockGroup("groupName", lecturer))));
		
		assertDoesNotThrow(() -> groupServ.editGroupMessage(
				msgId, title, content, lecturer));
	}

	@Test
	public void canEditGroupMessageWhileAuthenticated()
	{
		Account lecturer = mockLecturer();
		when(accServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(lecturer));
		UUID msgId = UUID.randomUUID();
		String title = "title";
		String content = "content";
		
		when(gmDao.findById(msgId))
			.thenReturn(Optional.of(mockMessage(lecturer,
					mockGroup("groupName", lecturer))));
		
		assertDoesNotThrow(() -> groupServ.editGroupMessage(
				msgId, title, content));
	}
	
	@Test
	public void canGetGameHistory()
	{
		Account lecturer = mockLecturer();
		Account player = mockPlayer();
		String groupCode = "groupCode";
		Group group = mockGroup("groupName", groupCode, lecturer);
		
		when(groupDao.findByGroupCode(groupCode))
			.thenReturn(Optional.of(group));
		when(ggrDao.findAllByGroup_id(any(), any()))
			.thenReturn(new PageImpl<>(List.of(
					mockGroupGameResult(group, player))));
		
		assertNotNull(groupServ.getGameHistory(groupCode, 1, lecturer));
	}

	@Test
	public void canGetGameHistoryWhileAuthenticated()
	{
		Account lecturer = mockLecturer();
		Account player = mockPlayer();
		when(accServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(lecturer));
		String groupCode = "groupCode";
		Group group = mockGroup("groupName", groupCode, lecturer);
		
		when(groupDao.findByGroupCode(groupCode))
			.thenReturn(Optional.of(group));
		when(ggrDao.findAllByGroup_id(any(), any()))
			.thenReturn(new PageImpl<>(List.of(
					mockGroupGameResult(group, player))));
		
		assertNotNull(groupServ.getGameHistory(groupCode, 1));
	}
	
	//---Helpers---
	
	private static Account mockLecturer()
	{
		return new Account.Builder()
				.withID(UUID.randomUUID())
				.withEmail("testLecturer@test.pl")
				.withUsername("TestLecturer")
				.withPassword("QWERTY")
				.withRoles(List.of(Account.LECTURER_ROLE))
				.build();
	}
	private static Account mockPlayer()
	{
		return new Account.Builder()
				.withID(UUID.randomUUID())
				.withEmail("testPlayer@test.pl")
				.withUsername("TestPlayer")
				.withPassword("QWERTY")
				.withRoles(List.of(Account.PLAYER_ROLE))
				.build();
	}
	
	private static Group mockGroup(String name, Account lecturer)
	{
		return new Group(UUID.randomUUID(),
				name, new Date(), name + "code",
				List.of(), List.of(lecturer), List.of());
	}
	private static Group mockGroup(String name, String groupCode, Account lecturer)
	{
		return new Group(UUID.randomUUID(),
				name, new Date(), groupCode,
				List.of(), List.of(lecturer), List.of());
	}
	private static Group mockGroup(String name, String groupCode,
			Account lecturer, Account player)
	{
		return new Group(UUID.randomUUID(),
				name, new Date(), groupCode,
				List.of(), List.of(lecturer), List.of(player));
	}
	private static GroupJoinRequest mockGroupJoinRequest(Group group, Account sender)
	{
		return new GroupJoinRequest(UUID.randomUUID(),
				sender, group, new Date());
	}
	private static GroupMessage mockMessage(Account sender, Group group)
	{
		return new GroupMessage(UUID.randomUUID(),
				sender, group, "title", "content",
				new Date(), null, new ArrayList<>(
						List.of(sender.getId())));
	}
	private GroupGameResult mockGroupGameResult(Group group, Account player)
	{
		return new GroupGameResult(UUID.randomUUID(), group,
				new GameResult(UUID.randomUUID(), 
						new HashMap<>(Map.of(UUID.randomUUID(),
								mockPlayerResult(player))),
						new Date()));
	}
	private PlayerResult mockPlayerResult(Account player)
	{
		return new PlayerResult(
				UUID.randomUUID(),
				player.getId(),
				new HashMap<>(Map.of(0, 1.0)),
				new HashMap<>(Map.of(0, 100.0)),
				new HashMap<>(Map.of(0, 25000l)),
				false);
	}
}
