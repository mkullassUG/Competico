package com.projteam.competico.service.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projteam.competico.dao.game.GlobalTaskDAO;
import com.projteam.competico.dao.game.TaskInfoDAO;
import com.projteam.competico.domain.Account;
import com.projteam.competico.domain.game.GlobalTask;
import com.projteam.competico.domain.game.TaskInfo;
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

@Service
public class GameTaskDataService
{
	private List<TaskService> taskServices;
	private AccountService accountService;
	private GenericTaskMapper taskMapper;
	
	private Map<UUID, List<Entry<UUID, Task>>> globalImportedTasks;
	
	private Map<String, Class<? extends TaskDTO>> taskDtoNameToClass;
	private Map<String, Class<? extends Task>> taskNameToClass;
	private Map<String, String> taskDtoClassNameToName;
	private Map<String, String> taskClassNameToName;
	
	private GlobalTaskDAO gtDao;
	private TaskInfoDAO tiDao;
	
	private final ObjectMapper mapperByField;
	
	@Autowired
	public GameTaskDataService(List<TaskService> taskServiceList,
			AccountService accServ,
			GenericTaskMapper taskMapper,
			GlobalTaskDAO gtDao,
			TaskInfoDAO tiDao)
	{
		taskServices = new ArrayList<>(taskServiceList);
		accountService = accServ;
		this.taskMapper = taskMapper;
		this.gtDao = gtDao;
		this.tiDao = tiDao; 
		
		globalImportedTasks = new HashMap<>();
		
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
	@EventListener(ContextRefreshedEvent.class)
	public void initTaskDataFromJSON() throws IOException, ClassNotFoundException
	{
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(
				this.getClass().getClassLoader());
		
		for (Resource res: resolver.getResources("classpath:tasks/*.json"))
		{
			JsonNode tree = mapperByField.readTree(res.getInputStream());
			for (JsonNode taskInfo: tree)
			{
				Task task = readTask(taskInfo);
				saveTask(task);
				UUID taskId = task.getId();
				gtDao.save(new GlobalTask(taskId));
				tiDao.save(new TaskInfo(taskId));
			}
		}
		gtDao.flush();
	}
	@Transactional
	public void saveTask(Task task)
	{
		for (TaskService ts: taskServices)
		{
			if (ts.canAccept(task))
			{
				if (!ts.genericExistsById(task.getId()))
					ts.genericSave(task);
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
	public Task generateRandomTask(double targetDifficulty)
	{
		Random r = new Random();
		
		long count = gtDao.count();
		int index = r.nextInt((int) count);
		UUID taskId = gtDao.findAll(PageRequest.of(index, 1))
				.get()
				.findAny()
				.map(gt -> gt.getTaskID())
				.orElse(null);
		
		if (taskId != null)
		{
			for (TaskService taskServ: taskServices)
			{
				Task ret = taskServ.genericFindById(taskId);
				if (ret != null)
					return ret;
			}
		}
		
		return defaultTask(targetDifficulty);
	}
	
	public Task defaultTask(double targetDifficulty)
	{
		List<String> leftWords1 = List.of("data mining", "pattern identification", "quantitative modelling", "class label", "class membership", "explanatory variable", "variable", "fault-tolerant", "spurious pattern", "outlier");
		List<String> rightWords1 = List.of("eksploracja danych", "identyfikacja wzorca", "modelowanie ilościowe", "etykieta klasy", "przynależność do klasy", "zmienna objaśniająca", "zmienna", "odporny na błędy", "fałszywy wzorzec", "wartość skrajna");
		Map<Integer, Integer> correctMapping1 = Map.ofEntries(
				Map.entry(0, 0),
				Map.entry(1, 1),
				Map.entry(2, 2),
				Map.entry(3, 3),
				Map.entry(4, 4),
				Map.entry(5, 5),
				Map.entry(6, 6),
				Map.entry(7, 7),
				Map.entry(8, 8),
				Map.entry(9, 9));

		return new WordConnect(UUID.randomUUID(),
				"Match the words with their translations:", List.of(),
				leftWords1, rightWords1, correctMapping1, targetDifficulty);
	}

	@Transactional
	public List<TaskDTO> getAllTasks()
	{
		List<Task> ret = new ArrayList<>();
		
		for (TaskService taskServ: taskServices)
			ret.addAll(taskServ.genericFindAll());
		
		return new ArrayList<>(ret.stream()
				.map(task -> taskMapper.toDTO(task))
				.collect(Collectors.toList()));
	}
	
	public void importGlobalTask(JsonNode taskData) throws IOException, ClassNotFoundException
	{
		importGlobalTask(taskData, getAccount());
	}
	public void importGlobalTask(JsonNode taskData, Account acc) throws IOException, ClassNotFoundException
	{
		UUID id = acc.getId();
		globalImportedTasks.computeIfAbsent(id, k -> new ArrayList<>());
		globalImportedTasks.get(id).add(Map.entry(
				UUID.randomUUID(),
				taskDtoJsonToTask(taskData)));
	}
	public void importGlobalTasks(MultipartFile file) throws IOException, ClassNotFoundException
	{
		importGlobalTasks(file, getAccount());
	}
	public void importGlobalTasks(MultipartFile file, Account acc) throws IOException, ClassNotFoundException
	{
		JsonNode data = mapperByField.readTree(file.getInputStream());
		if (data.isArray())
		{
			for (JsonNode task: data)
				importGlobalTask(task, acc);
		}
		else
		{
			importGlobalTask(data, acc);
		}
	}
	public int getImportedGlobalTaskCount()
	{
		return getImportedGlobalTaskCount(getAccount());
	}
	public int getImportedGlobalTaskCount(Account account)
	{
		return Optional.ofNullable(globalImportedTasks.get(account.getId()))
				.map(l -> l.size())
				.orElse(0);
	}
	public List<TaskDTO> getImportedGlobalTasks()
	{
		return getImportedGlobalTasks(getAccount());
	}
	public List<TaskDTO> getImportedGlobalTasks(Account account)
	{
		return taskListToDTO(
				Optional.ofNullable(globalImportedTasks.get(account.getId()))
					.orElseGet(() -> new ArrayList<>())
				.stream()
				.map(e -> e.getValue())
				.collect(Collectors.toList()));
	}
	public List<Map<String, String>> getImportedGlobalTaskInfo()
	{
		return getImportedGlobalTaskInfo(getAccount());
	}
	public List<Map<String, String>> getImportedGlobalTaskInfo(Account account)
	{
		return Optional.ofNullable(globalImportedTasks.get(account.getId()))
					.orElseGet(() -> new ArrayList<>())
				.stream()
				.map(e -> Map.of(
						"taskID", e.getKey().toString(),
						"taskName", getTaskDtoName(taskMapper.toDTO(e.getValue()))))
				.collect(Collectors.toList());
	}
	public Optional<TaskDTO> getImportedGlobalTask(UUID id)
	{
		return getImportedGlobalTask(id, getAccount());
	}
	public Optional<TaskDTO> getImportedGlobalTask(UUID id, Account account)
	{
		return Optional.ofNullable(
				globalImportedTasks.get(account.getId()))
			.map(tasks ->
			{
				for (var e: tasks)
				{
					if (e.getKey().equals(id))
						return taskMapper.toDTO(e.getValue());
				}
				return null;
			});
	}
	public boolean removeImportedGlobalTask(UUID id)
	{
		return removeImportedGlobalTask(id, getAccount());
	}
	public boolean removeImportedGlobalTask(UUID id, Account account)
	{
		List<Entry<UUID, Task>> list = globalImportedTasks.get(account.getId());
		if (list == null)
			return false;
		Iterator<Entry<UUID, Task>> it = list.iterator();
		while (it.hasNext())
		{
			Entry<UUID, Task> e = it.next();
			if (e.getKey().equals(id))
			{
				it.remove();
				return true;
			}
		}
		return false;
	}
	public void removeAllImportedGlobalTasks()
	{
		removeAllImportedGlobalTasks(getAccount());
	}
	public void removeAllImportedGlobalTasks(Account account)
	{
		globalImportedTasks.remove(account.getId());
	}
	public boolean editImportedGlobalTask(UUID taskId, JsonNode taskData) throws IOException, ClassNotFoundException
	{
		return editImportedGlobalTask(taskId, taskData, getAccount());
	}
	public boolean editImportedGlobalTask(UUID taskId, JsonNode taskData, Account acc) throws IOException, ClassNotFoundException
	{
		UUID accId = acc.getId();
		globalImportedTasks.computeIfAbsent(accId, k -> new ArrayList<>());
		var list = globalImportedTasks.get(accId);
		int l = list.size();
		
		for (int i = 0; i < l; i++)
		{
			var e = list.get(i);
			if (e.getKey().equals(taskId))
			{
				list.set(i, Map.entry(taskId, taskDtoJsonToTask(taskData)));
				return true;
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
	private Task readTask(JsonNode task) throws IOException, ClassNotFoundException
	{
		String taskName = task.get("taskName").textValue();
		JsonNode taskContent = task.get("taskContent");
		Class<? extends Task> taskClass = taskNameToClass.get(taskName);
		if (taskClass == null)
			throw new ClassNotFoundException("Could not find an applicable task definition");
		
		return mapperByField.treeToValue(taskContent, taskClass);
	}
	private JsonNode taskEntityToJson(Task task)
	{
		String taskName = taskClassNameToName.get(task.getClass().getName());
		return mapperByField.valueToTree(Map.of(
				"taskName", taskName,
				"taskContent", task));
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

	private Account getAccount()
	{
		return accountService.getAuthenticatedAccount()
				.orElseThrow(() -> new IllegalArgumentException("Not authenticated."));
	}

	public JsonNode convertDtoToEntity(MultipartFile file) throws ClassNotFoundException, IOException
	{
		JsonNode data = mapperByField.readTree(file.getInputStream());
		List<JsonNode> taskList = new ArrayList<>();
		List<JsonNode> retList = new ArrayList<>();
		if (data.isArray())
		{
			for (JsonNode task: data)
				taskList.add(task);
		}
		else
			taskList.add(data);
		
		taskList = taskList.stream()
				.flatMap(node ->
				{
					JsonNode tasksetContent = node.get("tasksetContent");
					if (tasksetContent != null)
					{
						if (tasksetContent.isArray())
						{
							List<JsonNode> tList = new ArrayList<>();
							for (JsonNode t: tasksetContent)
								tList.add(t);
							return tList.stream();
						}
						return Stream.of(tasksetContent);
					}
					return Stream.of(node);
				})
				.collect(Collectors.toList());
		for (JsonNode task: taskList)
			retList.add(taskEntityToJson(taskDtoJsonToTask(task)));
		
		return mapperByField.valueToTree(retList);
	}
}
