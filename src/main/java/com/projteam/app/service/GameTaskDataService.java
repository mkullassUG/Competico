package com.projteam.app.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.domain.game.tasks.WordConnect;
import com.projteam.app.service.game.tasks.TaskService;

@Service
public class GameTaskDataService
{
	private List<TaskService> taskServices;
	
	private final ObjectMapper mapperByField;
	
	@Autowired
	public GameTaskDataService(List<TaskService> taskServiceList)
	{
		taskServices = new ArrayList<>(taskServiceList);
		
		mapperByField = new ObjectMapper();
		mapperByField.setVisibility(mapperByField.getSerializationConfig()
				.getDefaultVisibilityChecker()
                .withFieldVisibility(Visibility.ANY)
                .withGetterVisibility(Visibility.NONE)
                .withSetterVisibility(Visibility.NONE)
                .withCreatorVisibility(Visibility.NONE));
	}
	
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
				String taskClassName = taskInfo.get("taskName").textValue();
				JsonNode taskContent = taskInfo.get("taskContent");
				Class<?> taskClass = Class.forName(taskClassName);
				
				saveTask((Task) mapperByField.treeToValue(taskContent, taskClass));
			}
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
					.map(t -> t.getClass().getTypeName())
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

		return new WordConnect(UUID.randomUUID(), "Match the words with their translations:",
				leftWords1, rightWords1, correctMapping1, targetDifficulty);
	}

	@Transactional
	public JsonNode getAllTasksAsJson()
	{
		List<Task> ret = new ArrayList<>();
		
		for (TaskService taskServ: taskServices)
			ret.addAll(taskServ.genericFindAll());
		
		return mapperByField.valueToTree(ret
				.stream()
				.map(t -> Map.of(
						"taskName", t.getClass(),
						"taskContent", t
						))
				.collect(Collectors.toList()));
	}
}
