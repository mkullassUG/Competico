package com.projteam.app.service.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projteam.app.domain.Account;
import com.projteam.app.domain.game.tasks.ChoiceWordFill;
import com.projteam.app.domain.game.tasks.ChronologicalOrder;
import com.projteam.app.domain.game.tasks.ListChoiceWordFill;
import com.projteam.app.domain.game.tasks.ListSentenceForming;
import com.projteam.app.domain.game.tasks.ListWordFill;
import com.projteam.app.domain.game.tasks.MultipleChoice;
import com.projteam.app.domain.game.tasks.SingleChoice;
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.domain.game.tasks.WordConnect;
import com.projteam.app.domain.game.tasks.WordFill;
import com.projteam.app.dto.game.tasks.create.ChoiceWordFillDTO;
import com.projteam.app.dto.game.tasks.create.ChronologicalOrderDTO;
import com.projteam.app.dto.game.tasks.create.ListChoiceWordFillDTO;
import com.projteam.app.dto.game.tasks.create.ListSentenceFormingDTO;
import com.projteam.app.dto.game.tasks.create.ListWordFillDTO;
import com.projteam.app.dto.game.tasks.create.MultipleChoiceDTO;
import com.projteam.app.dto.game.tasks.create.SingleChoiceDTO;
import com.projteam.app.dto.game.tasks.create.TaskDTO;
import com.projteam.app.dto.game.tasks.create.WordConnectDTO;
import com.projteam.app.dto.game.tasks.create.WordFillDTO;
import com.projteam.app.service.AccountService;
import com.projteam.app.service.game.tasks.TaskService;
import com.projteam.app.service.game.tasks.mappers.GenericTaskMapper;

@Service
public class GameTaskDataService
{
	private List<TaskService> taskServices;
	private AccountService accountService;
	private GenericTaskMapper taskMapper;
	
	private Map<UUID, List<Task>> globalImportedTasks;
	
	private Map<String, Class<? extends TaskDTO>> taskDtoNameToClass;
	private Map<String, Class<? extends Task>> taskNameToClass;
	private Map<String, String> taskDtoClassNameToName;
	private Map<String, String> taskClassNameToName;
	
	private final ObjectMapper mapperByField;
	
	@Autowired
	public GameTaskDataService(List<TaskService> taskServiceList,
			AccountService accServ,
			GenericTaskMapper taskMapper)
	{
		taskServices = new ArrayList<>(taskServiceList);
		accountService = accServ;
		this.taskMapper = taskMapper;
		
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
				saveTask(readTask(taskInfo));
		}
	}
	@Transactional
	public void saveTask(Task task)
	{
		for (TaskService ts: taskServices)
		{
			if (ts.canAccept(task))
			{
				if (!ts.genericExistsById(task))
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
		
		ArrayList<TaskService> taskServs = new ArrayList<>(taskServices);
		Collections.shuffle(taskServs, r);
		
		for (TaskService taskServ: taskServs)
		{
			Task ret = taskServ.genericFindRandom(r);
			if (ret != null)
				return ret;
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
	
	public void importGlobalTask(JsonNode task) throws IOException, ClassNotFoundException
	{
		importGlobalTask(task, getAccount());
	}
	public void importGlobalTask(JsonNode task, Account acc) throws IOException, ClassNotFoundException
	{
		UUID id = acc.getId();
		globalImportedTasks.computeIfAbsent(id, k -> new ArrayList<>());
		globalImportedTasks.get(id).add(taskDtoJsonToTask(task));
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
					.orElseGet(() -> new ArrayList<>()));
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
		addTaskNameMapping("SingleChoice", SingleChoiceDTO.class, SingleChoice.class);
		addTaskNameMapping("MultipleChoice", MultipleChoiceDTO.class, MultipleChoice.class);
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
}
