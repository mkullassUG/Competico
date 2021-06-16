package com.projteam.competico.service.game;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projteam.competico.dao.game.GlobalTaskDAO;
import com.projteam.competico.dao.game.TaskInfoDAO;
import com.projteam.competico.dao.game.TaskSetDAO;
import com.projteam.competico.domain.Account;
import com.projteam.competico.domain.game.TaskInfo;
import com.projteam.competico.domain.game.TaskSet;
import com.projteam.competico.domain.game.tasks.ChoiceWordFill;
import com.projteam.competico.domain.game.tasks.ChronologicalOrder;
import com.projteam.competico.domain.game.tasks.ListChoiceWordFill;
import com.projteam.competico.domain.game.tasks.ListSentenceForming;
import com.projteam.competico.domain.game.tasks.ListWordFill;
import com.projteam.competico.domain.game.tasks.OptionSelect;
import com.projteam.competico.domain.game.tasks.Task;
import com.projteam.competico.domain.game.tasks.WordConnect;
import com.projteam.competico.domain.game.tasks.WordFill;
import com.projteam.competico.dto.game.tasks.create.ChoiceWordFillDTO;
import com.projteam.competico.dto.game.tasks.create.ChronologicalOrderDTO;
import com.projteam.competico.dto.game.tasks.create.ListChoiceWordFillDTO;
import com.projteam.competico.dto.game.tasks.create.ListSentenceFormingDTO;
import com.projteam.competico.dto.game.tasks.create.ListWordFillDTO;
import com.projteam.competico.dto.game.tasks.create.OptionSelectDTO;
import com.projteam.competico.dto.game.tasks.create.TaskDTO;
import com.projteam.competico.dto.game.tasks.create.WordConnectDTO;
import com.projteam.competico.dto.game.tasks.create.WordFillDTO;
import com.projteam.competico.service.AccountService;
import com.projteam.competico.service.game.tasks.TaskService;
import com.projteam.competico.service.game.tasks.mappers.GenericTaskMapper;
import com.projteam.competico.utils.Initializable;

@Service
public class TaskSetDataService
{
	private List<TaskService> taskServices;
	private AccountService accountService;
	private GenericTaskMapper taskMapper;
	
	private Map<String, Class<? extends TaskDTO>> taskDtoNameToClass;
	private Map<String, Class<? extends Task>> taskNameToClass;
	private Map<String, String> taskDtoClassNameToName;
	private Map<String, String> taskClassNameToName;
	
	private GlobalTaskDAO gtDao;
	private TaskSetDAO tsDao;
	private TaskInfoDAO tiDao;
	
	private final ObjectMapper mapperByField;
	
	private static final String DEFAULT_TASKSET_NAME = "default";
	private static final String TASKSET_NAME_REGEX =
			"^[a-zA-Z0-9ąĄłŁśŚćĆńŃóÓżŻźŹęĘ ./<>?;:\"'`!@#$%^&*\\(\\)\\[\\]\\{\\}_+=|\\\\-]{2,32}$";
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	public TaskSetDataService(List<TaskService> taskServiceList,
			AccountService accServ,
			GenericTaskMapper taskMapper,
			GlobalTaskDAO gtDao,
			TaskSetDAO tsDao,
			TaskInfoDAO tiDao)
	{
		taskServices = new ArrayList<>(taskServiceList);
		accountService = accServ;
		this.taskMapper = taskMapper;
		this.gtDao = gtDao;
		this.tsDao = tsDao;
		this.tiDao = tiDao; 
		
		initTaskNameMaps();
		
		mapperByField = new ObjectMapper();
		mapperByField.setVisibility(mapperByField.getSerializationConfig()
				.getDefaultVisibilityChecker()
                .withFieldVisibility(Visibility.ANY)
                .withGetterVisibility(Visibility.NONE)
                .withSetterVisibility(Visibility.NONE)
                .withCreatorVisibility(Visibility.NONE));
	}
	
	@Transactional
	public void saveTask(Task task)
	{
		saveTask(task, false);
	}
	@Transactional
	public void saveTaskAndFlush(Task task)
	{
		saveTask(task, true);
	}
	private void saveTask(Task task, boolean flush)
	{
		for (TaskService ts: taskServices)
		{
			if (ts.canAccept(task))
			{
				if (!ts.genericExistsById(task.getId()))
				{
					if (flush)
						ts.genericSaveAndFlush(task);
					else
						ts.genericSave(task);
				}
				return;
			}
		}
		
		throw new IllegalStateException("Cannot save task type "
				+ Optional.ofNullable(task)
					.map(t -> t.getClass().getName())
					.orElse(null)
				+ ", no applicable service.");
	}
	
	@Transactional
	public List<UUID> importTask(JsonNode tasksetData) throws IOException, ClassNotFoundException
	{
		return importTask(tasksetData, getAccount());
	}
	@Transactional
	public List<UUID> importTask(JsonNode tasksetData, Account acc) throws IOException, ClassNotFoundException
	{
		return importTask(Optional.ofNullable(tasksetData
					.get("tasksetContent"))
					.orElseThrow(() -> new IllegalArgumentException("Invalid format")), 
				Optional.ofNullable(tasksetData.get("tasksetName"))
					.map(node -> 
					{
						List<String> ret = new ArrayList<>();
						if (node.isArray())
						{
							for (JsonNode n: node)
								ret.add(Optional.ofNullable(n)
										.map(no -> no.textValue())
										.orElse(null));
						}
						else
							ret.add(Optional.ofNullable(node)
									.map(no -> no.textValue())
									.orElse(null));
						return ret;
					})
					.orElse(null),
				acc);
	}
	@Transactional
	public List<UUID> importTask(JsonNode taskData, List<String> tasksetNames, Account acc) throws IOException, ClassNotFoundException
	{
		if (!acc.hasRole(Account.LECTURER_ROLE))
			throw new IllegalArgumentException("NOT_LECTURER");
		
		List<UUID> ret = new ArrayList<>();
		
		for (String name: tasksetNames.stream()
				.map(tsn -> validateTasksetName(tsn))
				.distinct()
				.collect(Collectors.toList()))
		{
			UUID accId = acc.getId();
			Task task = taskDtoJsonToTask(taskData);
			
			UUID taskId = task.getId();
			
			saveTaskAndFlush(task);
			
			TaskInfo ti = tiDao.save(new TaskInfo(taskId));
			TaskSet ts = tsDao.findByNameAndLecturerID(name, accId)
					.orElseGet(() -> new TaskSet(UUID.randomUUID(), name, accId, new HashSet<>()));
			
			ts.getTaskInfos().add(ti);
			ts = tsDao.save(ts);
			
			ret.add(taskId);
		}
		
		tiDao.flush();
		tsDao.flush();
		
		return ret;
	}
	@Transactional
	public void importTasks(MultipartFile file) throws IOException, ClassNotFoundException
	{
		importTasks(file, getAccount());
	}
	@Transactional
	public void importTasks(MultipartFile file, Account acc) throws IOException, ClassNotFoundException
	{
		JsonNode data = mapperByField.readTree(file.getInputStream());
		List<JsonNode> inner;
		if (data.isArray())
		{
			inner = StreamSupport
					  .stream(data.spliterator(), false)
					  .collect(Collectors.toList());
		}
		else
		{
			inner = List.of(data);
		}
		
		for (JsonNode node: inner)
		{
			JsonNode nameNode = node.get("tasksetName");
			String name = validateTasksetName((nameNode == null)?null:nameNode.asText());
			JsonNode content = node.get("tasksetContent");
			if (content.isArray())
			{
				for (JsonNode task: content)
					importTask(task, List.of(name), acc);
			}
			else
			{
				importTask(content, List.of(name), acc);
			}
		}
	}
	
	@Transactional
	public void importTasksToTaskset(MultipartFile file, String tasksetName)
			throws IOException, ClassNotFoundException
	{
		importTasksToTaskset(file, tasksetName, getAccount());
	}
	@Transactional
	public void importTasksToTaskset(MultipartFile file, String tasksetName, Account acc)
			throws IOException, ClassNotFoundException
	{
		String name = validateTasksetName(tasksetName);
		
		JsonNode data = mapperByField.readTree(file.getInputStream());
		List<JsonNode> inner;
		if (data.isArray())
		{
			inner = StreamSupport
					  .stream(data.spliterator(), false)
					  .collect(Collectors.toList());
		}
		else
		{
			inner = List.of(data);
		}
		
		for (JsonNode node: inner)
		{
			JsonNode content = node.get("tasksetContent");
			if (content.isArray())
			{
				for (JsonNode task: content)
					importTask(task, List.of(name), acc);
			}
			else
			{
				importTask(content, List.of(name), acc);
			}
		}
	}
	
	@Transactional
	public int getTaskCount()
	{
		return getTaskCount(getAccount());
	}
	@Transactional
	public int getTaskCount(Account account)
	{
		if (!account.hasRole(Account.LECTURER_ROLE))
			throw new IllegalArgumentException("NOT_LECTURER");
		ensureDefaultTasksetExists(account);
		return tsDao.findAllByLecturerID(account.getId())
				.stream()
				.mapToInt(ts -> ts.getTaskInfos().size())
				.sum();
	}
	@Transactional
	public int getTaskCount(String tasksetName)
	{
		return getTaskCount(tasksetName, getAccount());
	}
	@Transactional
	public int getTaskCount(String tasksetName, Account account)
	{
		if (!account.hasRole(Account.LECTURER_ROLE))
			throw new IllegalArgumentException("NOT_LECTURER");
		ensureDefaultTasksetExists(account);
		String name = validateTasksetName(tasksetName);
		
		return tsDao.findByNameAndLecturerID(name,
				account.getId())
				.map(ts -> ts.getTaskInfos().size())
				.orElse(0);
	}
	
	@Transactional
	public List<TaskDTO> getTasks(String tasksetName)
	{
		return getTasks(tasksetName, getAccount());
	}
	@Transactional
	public List<TaskDTO> getTasks(String tasksetName, Account account)
	{
		if (!account.hasRole(Account.LECTURER_ROLE))
			throw new IllegalArgumentException("NOT_LECTURER");
		String name = validateTasksetName(tasksetName);
		
		return taskListToDTO(init(tsDao.findByNameAndLecturerID(
					name, account.getId()))
				.map(ts -> ts.getTaskInfos()
						.stream()
						.map(ti -> ti.getTaskID())
						.collect(Collectors.toList()))
				.orElse(List.of())
				.stream()
				.flatMap(id -> 
				{
					Task t = Initializable.init(findTaskById(id));
					return (t == null)?Stream.empty():Stream.of(t);
				})
				.collect(Collectors.toList()));
	}
	@Transactional
	public Map<String, List<TaskDTO>> getAllTasks()
	{
		return getAllTasks(getAccount());
	}
	@Transactional
	public Map<String, List<TaskDTO>> getAllTasks(Account account)
	{
		if (!account.hasRole(Account.LECTURER_ROLE))
			throw new IllegalArgumentException("NOT_LECTURER");
		return tsDao.findAllByLecturerID(account.getId())
				.stream()
				.map(ts -> Map.entry(ts.getName(), ts.getTaskInfos()
						.stream()
						.map(ti -> ti.getTaskID())
						.flatMap(id -> 
						{
							Task t = Initializable.init(findTaskById(id));
							return (t == null)?Stream.empty():Stream.of(t);
						})
						.collect(Collectors.toList())))
				.collect(Collectors.toMap(en -> en.getKey(), en -> taskListToDTO(en.getValue())));
	}
	@Transactional
	public Map<String, List<Map<String, String>>> getTaskInfo()
	{
		return getTaskInfo(getAccount());
	}
	@Transactional
	public Map<String, List<Map<String, String>>> getTaskInfo(Account account)
	{
		if (!account.hasRole(Account.LECTURER_ROLE))
			throw new IllegalArgumentException("NOT_LECTURER");
		ensureDefaultTasksetExists(account);
		return tsDao.findAllByLecturerID(account.getId())
				.stream()
				.collect(Collectors.toMap(TaskSet::getName, ts ->
				{
					return ts.getTaskInfos()
						.stream()
						.map(ti -> Map.of(
							"taskID", ti.getTaskID().toString(),
							"taskName", getTaskDtoName(taskMapper.toDTO(findTaskById(ti.getTaskID()))),
							"creationDate", DATE_FORMAT.format(ti.getCreationDate())
						))
						.collect(Collectors.toList());
				}));
	}
	@Transactional
	public List<Map<String, String>> getTaskInfo(String tasksetName)
	{
		return getTaskInfo(tasksetName, getAccount());
	}
	@Transactional
	public List<Map<String, String>> getTaskInfo(String tasksetName, Account account)
	{
		if (!account.hasRole(Account.LECTURER_ROLE))
			throw new IllegalArgumentException("NOT_LECTURER");
		String name = validateTasksetName(tasksetName);
		
		return tsDao.findByNameAndLecturerID(name, account.getId())
				.stream()
				.flatMap(ts -> ts.getTaskInfos()
						.stream()
						.map(ti -> Map.of(
							"taskID", ti.getTaskID().toString(),
							"taskName", getTaskDtoName(taskMapper.toDTO(findTaskById(ti.getTaskID()))),
							"creationDate", DATE_FORMAT.format(ti.getCreationDate()))))
				.collect(Collectors.toList());
	}
	@Transactional
	public List<String> getTasksetInfo()
	{
		return getTasksetInfo(getAccount());
	}
	@Transactional
	public List<String> getTasksetInfo(Account account)
	{
		ensureDefaultTasksetExists(account);
		return tsDao.findAllByLecturerID(account.getId())
				.stream()
				.map(ts -> ts.getName())
				.collect(Collectors.toList());
	}
	@Transactional
	public Optional<TaskDTO> getTask(UUID id)
	{
		return Optional.ofNullable(findTaskById(id))
				.map(t -> taskMapper.toDTO(t));
	}
	@Transactional
	public boolean removeTask(UUID id)
	{
		return removeTask(id, getAccount());
	}
	@Transactional
	public boolean removeTask(UUID id, Account account)
	{
		if (!account.hasRole(Account.LECTURER_ROLE))
			throw new IllegalArgumentException("NOT_LECTURER");
		UUID lectId = account.getId();
		
		TaskService tService = null;
		for (TaskService tServ: taskServices)
		{
			if (tServ.genericExistsById(id))
			{
				tService = tServ;
				break;
			}
		}
		if (tService == null)
			return false;
		
		tsDao.findAllByLecturerID(lectId)
			.forEach(ts ->
			{
				List<TaskInfo> tiToRemove = ts.getTaskInfos()
						.stream()
						.filter(ti -> ti.getTaskID().equals(id))
						.collect(Collectors.toList());
				
				if (tiToRemove.size() > 0)
				{
					ts.getTaskInfos().removeAll(tiToRemove);
					tsDao.save(ts);
				}
			});
		tsDao.flush();
		
		if (gtDao.existsById(id))
			gtDao.deleteById(id);
		gtDao.flush();
		
		tService.genericDeleteById(id);
		return true;
	}
	@Transactional
	public boolean removeTaskset(String tasksetName)
	{
		return removeTaskset(tasksetName, getAccount());
	}
	@Transactional
	public boolean removeTaskset(String tasksetName, Account acc)
	{
		if (!acc.hasRole(Account.LECTURER_ROLE))
			throw new IllegalArgumentException("NOT_LECTURER");
		ensureDefaultTasksetExists(acc);
		String name = validateTasksetName(tasksetName);
		if (name.equals(DEFAULT_TASKSET_NAME))
		{
			try
			{
				removeAllTasksFromTaskset(name);
				return true;
			}
			catch (Exception e)
			{
				return false;
			}
		}
		
		TaskSet ts = tsDao.findByNameAndLecturerID(name, acc.getId())
				.orElse(null);
		if (ts == null)
			return false;
		
		var taskInfoIds = new ArrayList<>(ts.getTaskInfos()
				.stream()
				.map(ti -> ti.getTaskID())
				.collect(Collectors.toList()));
		tsDao.deleteById(ts.getId());
		tsDao.flush();
		taskInfoIds.forEach(tiId ->
			{
				if (gtDao.existsById(tiId))
					gtDao.deleteById(tiId);
				for (TaskService tServ: taskServices)
				{
					if (tServ.genericExistsById(tiId))
					{
						tServ.genericDeleteById(tiId);
						break;
					}
				}
				tiDao.deleteById(tiId);
			});
		
		gtDao.flush();
		tiDao.flush();
		
		return true;
	}
	@Transactional
	public void createTaskset(String tasksetName)
	{
		createTaskset(tasksetName, getAccount());
	}
	@Transactional
	public void createTaskset(String tasksetName, Account acc)
	{
		if (!acc.hasRole(Account.LECTURER_ROLE))
			throw new IllegalArgumentException("NOT_LECTURER");
		String name = validateTasksetName(tasksetName);
		UUID lecturerId = acc.getId();
		
		if (tsDao.existsByNameAndLecturerID(name, lecturerId))
			throw new IllegalArgumentException("TASKSET_ALREADY_EXISTS");
		
		tsDao.saveAndFlush(new TaskSet(UUID.randomUUID(), name, lecturerId, new HashSet<>()));
	}
	@Transactional
	public void removeAllTasks()
	{
		removeAllTasks(getAccount());
	}
	@Transactional
	public void removeAllTasks(Account account)
	{
		if (!account.hasRole(Account.LECTURER_ROLE))
			throw new IllegalArgumentException("NOT_LECTURER");
		tsDao.findAllByLecturerID(account.getId())
			.forEach(ts ->
		{
			var tis = ts.getTaskInfos();
			tsDao.delete(ts);
			tis.forEach(ti ->
			{
				UUID id = ti.getTaskID();
				if (gtDao.existsById(id))
					gtDao.deleteById(id);
				for (TaskService tServ: taskServices)
				{
					if (tServ.genericExistsById(id))
					{
						tServ.genericDeleteById(id);
						break;
					}
				}
				tiDao.deleteById(id);
			});
		});
		
		gtDao.flush();
		tiDao.flush();
		tsDao.flush();
	}
	@Transactional
	public void removeAllTasksFromTaskset(String tasksetName)
	{
		removeAllTasksFromTaskset(tasksetName, getAccount());
	}
	@Transactional
	public void removeAllTasksFromTaskset(String tasksetName, Account acc)
	{
		if (!acc.hasRole(Account.LECTURER_ROLE))
			throw new IllegalArgumentException("NOT_LECTURER");
		String name = validateTasksetName(tasksetName);
		
		TaskSet ts = tsDao.findByNameAndLecturerID(name, acc.getId())
				.orElseThrow(() -> new IllegalArgumentException("TASKSET_NOT_FOUND"));
		
		var tis = ts.getTaskInfos();
		ts.setTaskInfos(new HashSet<>());
		tsDao.save(ts);
		tis.forEach(ti ->
		{
			UUID id = ti.getTaskID();
			if (gtDao.existsById(id))
				gtDao.deleteById(id);
			for (TaskService tServ: taskServices)
			{
				if (tServ.genericExistsById(id))
				{
					tServ.genericDeleteById(id);
					break;
				}
			}
			tiDao.deleteById(id);
		});
		
		tsDao.flush();
		gtDao.flush();
		tiDao.flush();
	}
	@Transactional
	public boolean editTask(UUID taskId, JsonNode taskData) throws IOException, ClassNotFoundException
	{
		return editTask(taskId, taskData, getAccount());
	}
	@Transactional
	public boolean editTask(UUID taskId, JsonNode taskData, Account acc) throws IOException, ClassNotFoundException
	{
		if (!acc.hasRole(Account.LECTURER_ROLE))
			throw new IllegalArgumentException("NOT_LECTURER");
		
		return replaceTaskById(taskId, taskDtoJsonToTask(taskData), true);
	}
	@Transactional
	public boolean changeTasksetName(String tasksetName, String newTasksetName)
	{
		return changeTasksetName(tasksetName, newTasksetName, getAccount());
	}
	@Transactional
	public boolean changeTasksetName(String tasksetName, String newTasksetName, Account acc)
	{
		if (!acc.hasRole(Account.LECTURER_ROLE))
			throw new IllegalArgumentException("NOT_LECTURER");
		String name = validateTasksetName(tasksetName);
		String newName = validateTasksetName(newTasksetName);
		
		TaskSet ts = init(tsDao.findByNameAndLecturerID(name, acc.getId()))
				.orElse(null);
		if (ts == null)
			return false;
		
		ts.setName(newName);
		tsDao.saveAndFlush(ts);
		
		return true;
	}
	@Transactional
	public void moveTask(UUID taskId, String newTasksetName)
	{
		moveTask(taskId, newTasksetName, getAccount());
	}
	@Transactional
	public boolean moveTask(UUID taskId, String newTasksetName, Account acc)
	{
		if (!acc.hasRole(Account.LECTURER_ROLE))
			throw new IllegalArgumentException("NOT_LECTURER");
		String newName = validateTasksetName(newTasksetName);
		UUID lecturerId = acc.getId();
		
		TaskInfo ti = tiDao.findById(taskId)
				.orElse(null);
		if (ti == null)
			return false;
		
		tsDao.findAllByLecturerID(lecturerId)
			.forEach(ts ->
			{
				if (ts.getTaskInfos()
						.removeIf(tin -> tin.getTaskID().equals(taskId)))
					tsDao.save(ts);
			});
		tsDao.flush();
		
		TaskSet ts = tsDao.findByNameAndLecturerID(newName, lecturerId)
				.orElseGet(() -> new TaskSet(UUID.randomUUID(),
						newName, lecturerId, new HashSet<>()));
		
		if (ts.getTaskInfos()
				.stream()
				.noneMatch(tin -> tin.getTaskID().equals(taskId)))
			ts.getTaskInfos().add(ti);
		tsDao.saveAndFlush(ts);
		
		return true;
	}
	
	private void ensureDefaultTasksetExists(Account acc)
	{
		if (!tsDao.existsByNameAndLecturerID(DEFAULT_TASKSET_NAME, acc.getId()))
			createTaskset(DEFAULT_TASKSET_NAME, acc);
	}
	
	private Task findTaskById(UUID id)
	{
		for (TaskService tServ: taskServices)
		{
			Task t = tServ.genericFindById(id);
			if (t != null)
				return t;
		}
		return null;
	}
	private boolean replaceTaskById(UUID id, Task task, boolean flush)
	{
		for (TaskService ts: taskServices)
		{
			if (ts.canAccept(task))
			{
				if (!ts.genericExistsById(task.getId()))
				{
					if (flush)
						ts.genericReplaceAndFlush(id, task);
					else
						ts.genericReplace(id, task);
					return true;
				}
			}
		}
		return false;
	}
	private List<TaskDTO> taskListToDTO(List<Task> tasks)
	{
		return tasks
			.stream()
			.map(t -> taskMapper.toDTO(t))
			.collect(Collectors.toList());
	}
	public String getTaskDtoName(TaskDTO dto)
	{
		return taskDtoClassNameToName.get(dto.getClass().getName());
	}
	private Task taskDtoJsonToTask(JsonNode task) throws IOException, ClassNotFoundException
	{
		String taskName = task.get("taskName").textValue();
		JsonNode taskContent = task.get("taskContent");
		Class<? extends TaskDTO> taskClass = taskDtoNameToClass.get(taskName);
		if (taskClass == null)
			throw new ClassNotFoundException("Could not find an applicable task definition");
		
		return taskMapper.toEntity(mapperByField.treeToValue(taskContent, taskClass));
	}

	private void initTaskNameMaps()
	{
		taskDtoNameToClass = new HashMap<>();
		taskDtoClassNameToName = new HashMap<>();
		
		taskNameToClass = new HashMap<>();
		taskClassNameToName = new HashMap<>();
		
		addTaskNameMapping("WordFill", WordFillDTO.class, WordFill.class);
		addTaskNameMapping("ListWordFill", ListWordFillDTO.class, ListWordFill.class);
		addTaskNameMapping("ChoiceWordFill", ChoiceWordFillDTO.class, ChoiceWordFill.class);
		addTaskNameMapping("ListChoiceWordFill", ListChoiceWordFillDTO.class, ListChoiceWordFill.class);
		addTaskNameMapping("ListSentenceForming", ListSentenceFormingDTO.class, ListSentenceForming.class);
		addTaskNameMapping("OptionSelect", OptionSelectDTO.class, OptionSelect.class);
		addTaskNameMapping("WordConnect", WordConnectDTO.class, WordConnect.class);
		addTaskNameMapping("ChronologicalOrder", ChronologicalOrderDTO.class, ChronologicalOrder.class);
	}
	private void addTaskNameMapping(String taskName,
			Class<? extends TaskDTO> taskDtoClass,
			Class<? extends Task> taskClass)
	{
		taskDtoNameToClass.put(taskName, taskDtoClass);
		taskDtoClassNameToName.put(taskDtoClass.getName(), taskName);
		taskNameToClass.put(taskName, taskClass);
		taskClassNameToName.put(taskClass.getName(), taskName);
	}
	
	private String validateTasksetName(String tasksetName)
	{
		if ((tasksetName == null) || (tasksetName.equals(DEFAULT_TASKSET_NAME)))
			return DEFAULT_TASKSET_NAME;
		if (tasksetName.matches(TASKSET_NAME_REGEX))
			return tasksetName;
		throw new IllegalArgumentException("BAD_TASKSET_NAME");
	}
	private Account getAccount()
	{
		return accountService.getAuthenticatedAccount()
				.orElseThrow(() -> new IllegalArgumentException("Not authenticated."));
	}
	private static <T extends Initializable> Optional<T> init(Optional<T> in)
	{
		in.ifPresent(i -> i.initialize());
		return in;
	}
}
