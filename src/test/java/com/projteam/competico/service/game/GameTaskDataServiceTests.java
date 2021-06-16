package com.projteam.competico.service.game;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projteam.competico.dao.game.GlobalTaskDAO;
import com.projteam.competico.dao.game.TaskInfoDAO;
import com.projteam.competico.domain.Account;
import com.projteam.competico.domain.game.tasks.ChoiceWordFill;
import com.projteam.competico.domain.game.tasks.ChoiceWordFillElement;
import com.projteam.competico.domain.game.tasks.ChronologicalOrder;
import com.projteam.competico.domain.game.tasks.ListChoiceWordFill;
import com.projteam.competico.domain.game.tasks.ListSentenceForming;
import com.projteam.competico.domain.game.tasks.ListWordFill;
import com.projteam.competico.domain.game.tasks.OptionSelect;
import com.projteam.competico.domain.game.tasks.OptionSelectElement;
import com.projteam.competico.domain.game.tasks.SentenceFormingElement;
import com.projteam.competico.domain.game.tasks.WordConnect;
import com.projteam.competico.domain.game.tasks.WordFill;
import com.projteam.competico.domain.game.tasks.WordFillElement;
import com.projteam.competico.domain.game.tasks.ChoiceWordFillElement.WordChoice;
import com.projteam.competico.domain.game.tasks.WordFillElement.EmptySpace;
import com.projteam.competico.dto.game.tasks.create.ChoiceWordFillDTO;
import com.projteam.competico.dto.game.tasks.create.ChoiceWordFillElementDTO;
import com.projteam.competico.dto.game.tasks.create.ChronologicalOrderDTO;
import com.projteam.competico.dto.game.tasks.create.ListChoiceWordFillDTO;
import com.projteam.competico.dto.game.tasks.create.ListSentenceFormingDTO;
import com.projteam.competico.dto.game.tasks.create.ListWordFillDTO;
import com.projteam.competico.dto.game.tasks.create.OptionSelectDTO;
import com.projteam.competico.dto.game.tasks.create.OptionSelectElementDTO;
import com.projteam.competico.dto.game.tasks.create.SentenceFormingElementDTO;
import com.projteam.competico.dto.game.tasks.create.TaskDTO;
import com.projteam.competico.dto.game.tasks.create.WordConnectDTO;
import com.projteam.competico.dto.game.tasks.create.WordFillDTO;
import com.projteam.competico.dto.game.tasks.create.WordFillElementDTO;
import com.projteam.competico.dto.game.tasks.create.ChoiceWordFillElementDTO.WordChoiceDTO;
import com.projteam.competico.dto.game.tasks.create.WordFillElementDTO.EmptySpaceDTO;
import com.projteam.competico.service.AccountService;
import com.projteam.competico.service.game.tasks.ChoiceWordFillService;
import com.projteam.competico.service.game.tasks.ChronologicalOrderService;
import com.projteam.competico.service.game.tasks.ListChoiceWordFillService;
import com.projteam.competico.service.game.tasks.ListSentenceFormingService;
import com.projteam.competico.service.game.tasks.ListWordFillService;
import com.projteam.competico.service.game.tasks.OptionSelectService;
import com.projteam.competico.service.game.tasks.WordConnectService;
import com.projteam.competico.service.game.tasks.WordFillService;
import com.projteam.competico.service.game.tasks.mappers.GenericTaskMapper;
import com.projteam.competico.service.game.tasks.mappers.create.ChoiceWordFillMapper;
import com.projteam.competico.service.game.tasks.mappers.create.ChronologicalOrderMapper;
import com.projteam.competico.service.game.tasks.mappers.create.ListChoiceWordFillMapper;
import com.projteam.competico.service.game.tasks.mappers.create.ListSentenceFormingMapper;
import com.projteam.competico.service.game.tasks.mappers.create.ListWordFillMapper;
import com.projteam.competico.service.game.tasks.mappers.create.OptionSelectMapper;
import com.projteam.competico.service.game.tasks.mappers.create.WordConnectMapper;
import com.projteam.competico.service.game.tasks.mappers.create.WordFillMapper;

class GameTaskDataServiceTests
{
	private @Mock ChoiceWordFillService cwfServ;
	private @Mock ChronologicalOrderService coServ;
	private @Mock ListChoiceWordFillService lcwfServ;
	private @Mock ListSentenceFormingService lsfServ;
	private @Mock ListWordFillService lwfServ;
	private @Mock OptionSelectService osServ;
	private @Mock WordConnectService wcServ;
	private @Mock WordFillService wfServ;
	
	private @Mock AccountService aServ;
	
	private @Spy ChoiceWordFillMapper cwfMapper;
	private @Spy ChronologicalOrderMapper coMapper;
	private @Spy ListChoiceWordFillMapper lcwfMapper;
	private @Spy ListSentenceFormingMapper lsfMapper;
	private @Spy ListWordFillMapper lwfMapper;
	private @Spy OptionSelectMapper osMapper;
	private @Spy WordConnectMapper wcMapper;
	private @Spy WordFillMapper wfMapper;
	
	private @Mock GlobalTaskDAO gtDao;
	private @Mock TaskInfoDAO tiDao;
	
	private GenericTaskMapper taskMapper;
	
	private GameTaskDataService gtdServ;
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	@BeforeEach
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		
		taskMapper = new GenericTaskMapper(List.of(
				cwfMapper, coMapper, lcwfMapper,
				lsfMapper, lwfMapper, osMapper,
				wcMapper, wfMapper));
		
		gtdServ = new GameTaskDataService(List.of(
					cwfServ, coServ, lcwfServ,
					lsfServ, lwfServ, osServ,
					wcServ, wfServ),
				aServ, taskMapper,
				gtDao, tiDao);
	}
	
	@ParameterizedTest
	@ValueSource(ints = {1, 2, 10, 50, 100, 125, 250, 9999})
	public void canCreateDefaultTask(int targetDifficulty)
	{
		assertNotNull(gtdServ.defaultTask(targetDifficulty));
	}
	@Test
	public void canGetAllTasksAsJson()
	{
		when(cwfServ.genericFindAll())
			.thenReturn(List.of(mockChoiceWordFill()));
		when(coServ.genericFindAll())
			.thenReturn(List.of(mockChronologicalOrder()));
		when(lcwfServ.genericFindAll())
			.thenReturn(List.of(mockListChoiceWordFill()));
		when(lsfServ.genericFindAll())
			.thenReturn(List.of(mockListSentenceForming()));
		when(lwfServ.genericFindAll())
			.thenReturn(List.of(mockListWordFill()));
		when(osServ.genericFindAll())
			.thenReturn(List.of(mockOptionSelect()));
		when(wcServ.genericFindAll())
			.thenReturn(List.of(mockWordConnect()));
		when(wfServ.genericFindAll())
			.thenReturn(List.of(mockWordFill()));
		
		int mockedTaskCount = 8;
		
		List<TaskDTO> res = gtdServ.getAllTasks();
		assertNotNull(res);
		assertThat(res.size(), greaterThanOrEqualTo(mockedTaskCount));
		assertThat(res, not(hasItem(nullValue())));
	}
	@Test
	public void shouldThrowWhenNoServiceCanAcceptTask()
	{
		WordFill task = mockWordFill();
		
		when(cwfServ.canAccept(task)).thenReturn(false);
		when(coServ.canAccept(task)).thenReturn(false);
		when(lcwfServ.canAccept(task)).thenReturn(false);
		when(lsfServ.canAccept(task)).thenReturn(false);
		when(lwfServ.canAccept(task)).thenReturn(false);
		when(osServ.canAccept(task)).thenReturn(false);
		when(wcServ.canAccept(task)).thenReturn(false);
		when(wfServ.canAccept(task)).thenReturn(false);
		
		assertThrows(IllegalStateException.class, () ->
				gtdServ.saveTask(task));
	}
	
	@ParameterizedTest
	@MethodSource("mockTaskDTOsWithNames")
	public void shouldImportGlobalTask(JsonNode taskDtoWithName)
			throws ClassNotFoundException, IOException
	{
		Account admin = mockTaskDataAdmin();
		
		int taskCount = gtdServ.getImportedGlobalTaskCount(admin);
		
		gtdServ.importGlobalTask(taskDtoWithName, admin);
		
		assertEquals(gtdServ.getImportedGlobalTaskCount(admin), taskCount + 1);
	}
	@ParameterizedTest
	@MethodSource("mockTaskDTOsWithNames")
	public void shouldImportGlobalTaskWithAuthenticatedAdmin(JsonNode taskDtoWithName)
			throws ClassNotFoundException, IOException
	{
		when(aServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(mockTaskDataAdmin()));
		
		int taskCount = gtdServ.getImportedGlobalTaskCount();
		
		gtdServ.importGlobalTask(taskDtoWithName);
		
		assertEquals(gtdServ.getImportedGlobalTaskCount(), taskCount + 1);
	}
	
	@ParameterizedTest
	@MethodSource("mockTaskDTOfile")
	public void shouldImportGlobalTasksFromJsonFile(MultipartFile taskDtoFile)
			throws ClassNotFoundException, IOException
	{
		Account admin = mockTaskDataAdmin();
		
		int taskCount = gtdServ.getImportedGlobalTaskCount(admin);
		
		gtdServ.importGlobalTasks(taskDtoFile, admin);
		
		assertTrue(gtdServ.getImportedGlobalTaskCount(admin) > taskCount);
	}
	@ParameterizedTest
	@MethodSource("mockTaskDTOfile")
	public void shouldImportGlobalTasksFromJsonFileWithAuthenticatedAdmin(MultipartFile taskDtoFile)
			throws ClassNotFoundException, IOException
	{
		when(aServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(mockTaskDataAdmin()));
		
		int taskCount = gtdServ.getImportedGlobalTaskCount();
		
		gtdServ.importGlobalTasks(taskDtoFile);
		
		assertTrue(gtdServ.getImportedGlobalTaskCount() > taskCount);
	}
	
	@Test
	public void shouldGetImportedGlobalTasks()
	{
		assertNotNull(gtdServ.getImportedGlobalTasks(mockTaskDataAdmin()));
	}
	@ParameterizedTest
	@MethodSource("mockTaskDTOsWithNames")
	public void shouldGetImportedGlobalTasksAfterAddingTask(JsonNode taskDTO)
			throws ClassNotFoundException, IOException
	{
		Account admin = mockTaskDataAdmin();
		
		gtdServ.importGlobalTask(taskDTO, admin);
		int taskCount = gtdServ.getImportedGlobalTaskCount(admin);
		
		var res = gtdServ.getImportedGlobalTasks(admin);
		
		assertNotNull(res);
		assertEquals(res.size(), taskCount);
	}
	@Test
	public void shouldGetImportedGlobalTasksWithAuthenticatedAdmin()
	{
		when(aServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(mockTaskDataAdmin()));
		
		assertNotNull(gtdServ.getImportedGlobalTasks());
	}
	@Test
	public void shouldGetImportedGlobalTaskInfo()
	{
		assertNotNull(gtdServ.getImportedGlobalTaskInfo(mockTaskDataAdmin()));
	}
	@Test
	public void shouldGetImportedGlobalTaskInfoWithAuthenticatedAdmin()
	{
		when(aServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(mockTaskDataAdmin()));
		
		assertNotNull(gtdServ.getImportedGlobalTaskInfo());
	}
	@ParameterizedTest
	@MethodSource("mockTaskDTOsWithNames")
	public void shouldGetTaskInfoWithImportedTask(JsonNode taskDtoWithName)
			throws ClassNotFoundException, IOException
	{
		Account admin = mockTaskDataAdmin();
		int taskCount = gtdServ.getImportedGlobalTaskCount(admin);
		
		gtdServ.importGlobalTask(taskDtoWithName, admin);
		List<Map<String, String>> res = gtdServ.getImportedGlobalTaskInfo(admin);
		
		assertNotNull(res);
		assertEquals(res.size(), taskCount + 1);
	}
	
	@ParameterizedTest
	@MethodSource("mockTaskDTOsWithNames")
	public void shouldRemoveImportedGlobalTask(JsonNode taskDtoWithName)
			throws ClassNotFoundException, IOException
	{
		Account admin = mockTaskDataAdmin();
		int taskCount = gtdServ.getImportedGlobalTaskCount(admin);
		
		gtdServ.importGlobalTask(taskDtoWithName, admin);
		UUID id = UUID.fromString(gtdServ.getImportedGlobalTaskInfo(admin)
				.get(0)
				.get("taskID"));
		boolean success = gtdServ.removeImportedGlobalTask(id, admin);
		
		assertTrue(success);
		assertEquals(gtdServ.getImportedGlobalTaskCount(admin), taskCount);
	}
	@ParameterizedTest
	@MethodSource("mockTaskDTOsWithNames")
	public void shouldRemoveImportedGlobalTaskWithAuthenticatedAdmin(JsonNode taskDtoWithName)
			throws ClassNotFoundException, IOException
	{
		when(aServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(mockTaskDataAdmin()));
		int taskCount = gtdServ.getImportedGlobalTaskCount();
		
		gtdServ.importGlobalTask(taskDtoWithName);
		UUID id = UUID.fromString(gtdServ.getImportedGlobalTaskInfo()
				.get(0)
				.get("taskID"));
		boolean success = gtdServ.removeImportedGlobalTask(id);
		
		assertTrue(success);
		assertEquals(gtdServ.getImportedGlobalTaskCount(), taskCount);
	}
	@ParameterizedTest
	@MethodSource("mockTaskDTOsWithNames")
	public void shouldRemoveAllImportedGlobalTasks(JsonNode taskDtoWithName)
			throws ClassNotFoundException, IOException
	{
		Account admin = mockTaskDataAdmin();
		
		gtdServ.importGlobalTask(taskDtoWithName, admin);
		gtdServ.removeAllImportedGlobalTasks(admin);
		
		assertEquals(gtdServ.getImportedGlobalTaskCount(admin), 0);
	}
	@ParameterizedTest
	@MethodSource("mockTaskDTOsWithNames")
	public void shouldRemoveAllImportedGlobalTasksWithAuthenticatedAdmin(JsonNode taskDtoWithName)
			throws ClassNotFoundException, IOException
	{
		when(aServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(mockTaskDataAdmin()));
		
		gtdServ.importGlobalTask(taskDtoWithName);
		gtdServ.removeAllImportedGlobalTasks();
		
		assertEquals(gtdServ.getImportedGlobalTaskCount(), 0);
	}
	@ParameterizedTest
	@MethodSource("mockTaskDTOsWithNames")
	public void shouldNotRemoveTasksWithIncorrectID(JsonNode taskDtoWithName)
			throws ClassNotFoundException, IOException
	{
		Account admin = mockTaskDataAdmin();
		UUID wrongID = UUID.randomUUID();
		
		gtdServ.importGlobalTask(taskDtoWithName, admin);
		int taskCount = gtdServ.getImportedGlobalTaskCount(admin);
		
		boolean success = gtdServ.removeImportedGlobalTask(wrongID, admin);
		
		assertFalse(success);
		assertEquals(gtdServ.getImportedGlobalTaskCount(admin), taskCount);
	}
	@Test
	public void shouldNotRemoveTasksWhenEmpty()
	{
		Account admin = mockTaskDataAdmin();
		UUID wrongID = UUID.randomUUID();
		
		boolean success = gtdServ.removeImportedGlobalTask(wrongID, admin);
		
		assertFalse(success);
		assertEquals(gtdServ.getImportedGlobalTaskCount(admin), 0);
	}
	
	@ParameterizedTest
	@MethodSource("mockTaskDTOsWithNames")
	public void shouldGetImportedGlobalTask(JsonNode taskDtoWithName)
			throws ClassNotFoundException, IOException
	{
		Account admin = mockTaskDataAdmin();
		
		gtdServ.importGlobalTask(taskDtoWithName, admin);
		UUID id = UUID.fromString(gtdServ.getImportedGlobalTaskInfo(admin)
				.get(0)
				.get("taskID"));
		Optional<TaskDTO> ret = gtdServ.getImportedGlobalTask(id, admin);
		
		assertTrue(ret.isPresent());
	}
	@ParameterizedTest
	@MethodSource("mockTaskDTOsWithNames")
	public void shouldGetImportedGlobalTaskWithAuthenticatedAdmin(JsonNode taskDtoWithName)
			throws ClassNotFoundException, IOException
	{
		when(aServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(mockTaskDataAdmin()));
		
		gtdServ.importGlobalTask(taskDtoWithName);
		UUID id = UUID.fromString(gtdServ.getImportedGlobalTaskInfo()
				.get(0)
				.get("taskID"));
		Optional<TaskDTO> ret = gtdServ.getImportedGlobalTask(id);
		
		assertTrue(ret.isPresent());
	}
	@ParameterizedTest
	@MethodSource("mockTaskDTOsWithNames")
	public void shouldNotGetTasksWithIncorrectID(JsonNode taskDtoWithName)
			throws ClassNotFoundException, IOException
	{
		Account admin = mockTaskDataAdmin();
		UUID wrongID = UUID.randomUUID();
		
		gtdServ.importGlobalTask(taskDtoWithName, admin);
		
		Optional<TaskDTO> ret = gtdServ.getImportedGlobalTask(wrongID, admin);
		
		assertTrue(ret.isEmpty());
	}
	@Test
	public void shouldNotGetTasksWhenEmpty()
	{
		Account admin = mockTaskDataAdmin();
		UUID wrongID = UUID.randomUUID();
		
		Optional<TaskDTO> ret = gtdServ.getImportedGlobalTask(wrongID, admin);
		
		assertTrue(ret.isEmpty());
	}
	
	@ParameterizedTest
	@MethodSource("mockTwoTaskDTOsWithNames")
	public void shouldEditGlobalTask(JsonNode originalTaskDTO, JsonNode newTaskDTO)
			throws ClassNotFoundException, IOException
	{
		Account admin = mockTaskDataAdmin();
		
		gtdServ.importGlobalTask(originalTaskDTO, admin);
		UUID id = UUID.fromString(gtdServ.getImportedGlobalTaskInfo(admin)
				.get(0)
				.get("taskID"));
		int taskCount = gtdServ.getImportedGlobalTaskCount(admin);
		
		boolean success = gtdServ.editImportedGlobalTask(id, newTaskDTO, admin);
		
		assertTrue(success);
		assertEquals(gtdServ.getImportedGlobalTaskCount(admin), taskCount);
	}
	@ParameterizedTest
	@MethodSource("mockTwoTaskDTOsWithNames")
	public void shouldEditGlobalTaskWithAuthenticatedAccount(
			JsonNode originalTaskDTO, JsonNode newTaskDTO)
			throws ClassNotFoundException, IOException
	{
		when(aServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(mockTaskDataAdmin()));
		
		gtdServ.importGlobalTask(originalTaskDTO);
		UUID id = UUID.fromString(gtdServ.getImportedGlobalTaskInfo()
				.get(0)
				.get("taskID"));
		int taskCount = gtdServ.getImportedGlobalTaskCount();
		
		boolean success = gtdServ.editImportedGlobalTask(id, newTaskDTO);
		
		assertTrue(success);
		assertEquals(gtdServ.getImportedGlobalTaskCount(), taskCount);
	}
	@ParameterizedTest
	@MethodSource("mockTwoTaskDTOsWithNames")
	public void shouldNotEditGlobalTaskWithIncorrectID(JsonNode originalTaskDTO, JsonNode newTaskDTO)
			throws ClassNotFoundException, IOException
	{
		Account admin = mockTaskDataAdmin();
		
		gtdServ.importGlobalTask(originalTaskDTO, admin);
		int taskCount = gtdServ.getImportedGlobalTaskCount(admin);
		UUID wrongID = UUID.randomUUID();
		
		boolean success = gtdServ.editImportedGlobalTask(wrongID, newTaskDTO, admin);
		
		assertFalse(success);
		assertEquals(gtdServ.getImportedGlobalTaskCount(admin), taskCount);
	}
	@ParameterizedTest
	@MethodSource("mockTaskDTOsWithNames")
	public void shouldNotEditGlobalTaskWhenEmpty(JsonNode taskDTO)
			throws ClassNotFoundException, IOException
	{
		Account admin = mockTaskDataAdmin();
		
		int taskCount = gtdServ.getImportedGlobalTaskCount(admin);
		UUID wrongID = UUID.randomUUID();
		
		boolean success = gtdServ.editImportedGlobalTask(wrongID, taskDTO, admin);
		
		assertFalse(success);
		assertEquals(gtdServ.getImportedGlobalTaskCount(admin), taskCount);
	}
	
	@ParameterizedTest
	@MethodSource("mockTaskDTOs")
	public void shouldImportGlobalTaskWithAuthenticatedAdmin(TaskDTO dto)
	{
		assertNotNull(gtdServ.getTaskDtoName(dto));
	}
	
	//---Sources---
	
	public static List<Arguments> mockTaskDTOs()
	{
		return List.of(
				Arguments.of(mockWordFillDTO()),
				Arguments.of(mockChoiceWordFillDTO()),
				Arguments.of(mockListWordFillDTO()),
				Arguments.of(mockListChoiceWordFillDTO()),
				Arguments.of(mockChronologicalOrderDTO()),
				Arguments.of(mockListSentenceFormingDTO()),
				Arguments.of(mockOptionSelectDTO()),
				Arguments.of(mockWordConnectDTO()));
	}
	public static List<Arguments> mockTaskDTOsWithNames()
	{
		var ret = List.of(
						Map.of(
								"taskName", "WordFill",
								"taskContent", mockWordFillDTO()),
						Map.of(
								"taskName", "ChoiceWordFill",
								"taskContent", mockChoiceWordFillDTO()),
						Map.of(
								"taskName", "ListWordFill",
								"taskContent", mockListWordFillDTO()),
						Map.of(
								"taskName", "ListChoiceWordFill",
								"taskContent", mockListChoiceWordFillDTO()),
						Map.of(
								"taskName", "ChronologicalOrder",
								"taskContent", mockChronologicalOrderDTO()),
						Map.of(
								"taskName", "ListSentenceForming",
								"taskContent", mockListSentenceFormingDTO()),
						Map.of(
								"taskName", "OptionSelect",
								"taskContent", mockOptionSelectDTO()),
						Map.of(
								"taskName", "WordConnect",
								"taskContent", mockWordConnectDTO()));
		
		return ret.stream()
				.map(m -> mapper.valueToTree(m))
				.map(jn -> Arguments.of(jn))
				.collect(Collectors.toList());
	}
	public static List<Arguments> mockTwoTaskDTOsWithNames()
	{
		var src = List.of(
				Map.of(
						"taskName", "WordFill",
						"taskContent", mockWordFillDTO()),
				Map.of(
						"taskName", "ChoiceWordFill",
						"taskContent", mockChoiceWordFillDTO()),
				Map.of(
						"taskName", "ListWordFill",
						"taskContent", mockListWordFillDTO()),
				Map.of(
						"taskName", "ListChoiceWordFill",
						"taskContent", mockListChoiceWordFillDTO()),
				Map.of(
						"taskName", "ChronologicalOrder",
						"taskContent", mockChronologicalOrderDTO()),
				Map.of(
						"taskName", "ListSentenceForming",
						"taskContent", mockListSentenceFormingDTO()),
				Map.of(
						"taskName", "OptionSelect",
						"taskContent", mockOptionSelectDTO()),
				Map.of(
						"taskName", "WordConnect",
						"taskContent", mockWordConnectDTO()));

		var listMapped = src.stream()
				.map(m -> mapper.valueToTree(m))
				.collect(Collectors.toList());
		int l = listMapped.size();
		Random r = new Random(l);
		
		List<Arguments> ret = new ArrayList<Arguments>();
		for (int i = 0; i < l; i++)
		{
			ret.add(Arguments.of(
					listMapped.get(i),
					listMapped.get(r.nextInt(l))));
		}
		
		return ret;
	}
	public static List<Arguments> mockTaskDTOfile()
	{
		var ret = List.of(
				Map.of(
						"taskName", "WordFill",
						"taskContent", mockWordFillDTO()),
				Map.of(
						"taskName", "ChoiceWordFill",
						"taskContent", mockChoiceWordFillDTO()),
				Map.of(
						"taskName", "ListWordFill",
						"taskContent", mockListWordFillDTO()),
				Map.of(
						"taskName", "ListChoiceWordFill",
						"taskContent", mockListChoiceWordFillDTO()),
				Map.of(
						"taskName", "ChronologicalOrder",
						"taskContent", mockChronologicalOrderDTO()),
				Map.of(
						"taskName", "ListSentenceForming",
						"taskContent", mockListSentenceFormingDTO()),
				Map.of(
						"taskName", "OptionSelect",
						"taskContent", mockOptionSelectDTO()),
				Map.of(
						"taskName", "WordConnect",
						"taskContent", mockWordConnectDTO()));

		return List.of(Arguments.of(new MockMultipartFile(
				"file", "data.json",
				MediaType.TEXT_PLAIN_VALUE,
				mapper.valueToTree(ret).toString().getBytes())));
	}
	
	//---Helpers---
	
	private static Account mockTaskDataAdmin()
	{
		return new Account.Builder()
				.withID(UUID.randomUUID())
				.withEmail("testAdmin@test.pl")
				.withUsername("TestAdmin")
				.withPassword("QWERTY")
				.withRoles(List.of(Account.TASK_DATA_ADMIN))
				.build();
	}
	
	public static WordFill mockWordFill()
	{
		List<String> wfText = List.of("Lorem ", " ipsum ", " dolor ", " sit ", " amet");
		List<String> wfAnswers = List.of("abc", "def", "ghi", "jkl");
		List<EmptySpace> wfEmptySpaces = wfAnswers
				.stream()
				.map(ans -> new EmptySpace(ans))
				.collect(Collectors.toList());
		List<String> wfPossibleAnswers = List.of("abc", "def", "ghi", "jkl", "mno", "pqr");
		
		return new WordFill(UUID.randomUUID(),
				"Test instruction", List.of(),
				new WordFillElement(UUID.randomUUID(),
						wfText, wfEmptySpaces, false, wfPossibleAnswers), 100);
	}
	public static ChoiceWordFill mockChoiceWordFill()
	{
		List<String> cwfText = List.of("Lorem ", " ipsum ", " dolor ", " sit ", " amet");
		List<String> cwfAnswers = List.of("abc", "def", "ghi", "jkl");
		List<WordChoice> wordChoices = cwfAnswers
				.stream()
				.map(ans -> new WordChoice(UUID.randomUUID(), ans, List.of("qwr")))
				.collect(Collectors.toList());
		
		return new ChoiceWordFill(UUID.randomUUID(),
				"Test instruction", List.of(),
				new ChoiceWordFillElement(UUID.randomUUID(),
						cwfText, wordChoices, false), 100);
	}
	public static ListWordFill mockListWordFill()
	{
		List<List<String>> lwfText = List.of(
				List.of("Lorem ", " ipsum ", " dolor"),
				List.of("sit ", " amet"),
				List.of("consectetur adipiscing  ", " elit"),
				List.of("sed ", " do"));
		List<List<String>> lwfAnswers = List.of(
				List.of("abc", "def"),
				List.of("ghi"),
				List.of("jkl"),
				List.of("mno"));
		List<List<String>> lwfPossibleAnswers = List.of(
				List.of("abc", "def", "ghi", "jkl"),
				List.of("ghi", "def", "ghi"),
				List.of("jkl", "ghi", "jkl"),
				List.of("mno", "ghi", "def"));
		
		List<WordFillElement> lwfWordFillElemList = new ArrayList<>();
		Iterator<List<String>> lwfTextIter = lwfText.iterator();
		Iterator<List<String>> lwfPossAnsIter = lwfPossibleAnswers.iterator();
		for (List<String> answerList: lwfAnswers)
		{
			List<String> textList = lwfTextIter.next();
			List<String> possibleAnswersList = lwfPossAnsIter.next();
			lwfWordFillElemList.add(new WordFillElement(UUID.randomUUID(),
					textList,
					answerList.stream()
						.map(ans -> new EmptySpace(ans))
						.collect(Collectors.toList()),
					true,
					possibleAnswersList));
		}
		
		return new ListWordFill(UUID.randomUUID(),
				"Test instruction", List.of(),
				lwfWordFillElemList, 100);
	}
	public static ListChoiceWordFill mockListChoiceWordFill()
	{
		List<List<String>> lcwfText = List.of(
				List.of("Lorem ", " ipsum ", " dolor"),
				List.of("sit ", " amet"),
				List.of("consectetur adipiscing  ", " elit"),
				List.of("sed ", " do"));
		List<List<String>> lcwfAnswers = List.of(
				List.of("abc", "def"),
				List.of("ghi"),
				List.of("jkl"),
				List.of("mno"));
		
		List<ChoiceWordFillElement> lcwfWordFillElemList = new ArrayList<>();
		Iterator<List<String>> lcwfTextIter = lcwfText.iterator();
		for (List<String> answerList: lcwfAnswers)
		{
			List<String> textList = lcwfTextIter.next();
			lcwfWordFillElemList.add(new ChoiceWordFillElement(UUID.randomUUID(),
					textList,
					answerList.stream()
						.map(ans -> new WordChoice(UUID.randomUUID(), ans, List.of("qwe", "poi")))
						.collect(Collectors.toList()),
					true));
		}
		
		return new ListChoiceWordFill(UUID.randomUUID(),
				"Test instruction", List.of(),
				lcwfWordFillElemList, 100);
	}
	public static ChronologicalOrder mockChronologicalOrder()
	{
		List<String> coText = List.of("Lorem ipsum dolor sit amet",
				"consectetur adipiscing elit",
				"sed do eiusmod tempor incididunt",
				"ut labore et dolore magna aliqua",
				"Ut enim ad minim veniam",
				"quis nostrud exercitation",
				"ullamco laboris nisi ut",
				"aliquip ex ea commodo consequat");
		
		return new ChronologicalOrder(UUID.randomUUID(),
				"Test instruction", List.of(),
				coText, 100);
	}
	public static ListSentenceForming mockListSentenceForming()
	{
		List<List<String>> text = List.of(
				List.of("Lorem ", " ipsum ", " dolor"),
				List.of("sit ", " amet"),
				List.of("consectetur adipiscing  ", " elit"),
				List.of("sed ", " do"));
		
		List<SentenceFormingElement> lsfWordFillElemList = text.stream()
				.map(textList -> new SentenceFormingElement(UUID.randomUUID(), textList))
				.collect(Collectors.toList());
		
		return new ListSentenceForming(UUID.randomUUID(),
				"Test instruction", List.of(),
				lsfWordFillElemList, 100);
	}
	public static OptionSelect mockOptionSelect()
	{
		String osContent = "Lorem ipsum dolor sit amet";
		List<String> osCorrectAnswers = List.of(
				"eiusmod", "tempor", "incididunt ut");
		List<String> osIncorrectAnswers = List.of(
				"adipiscing", "elit", "sed", "labore", "et dolore");
		
		return new OptionSelect(UUID.randomUUID(),
				"Test instruction", List.of(),
				new OptionSelectElement(UUID.randomUUID(),
						osContent, osCorrectAnswers, osIncorrectAnswers), 100);
	}
	public static WordConnect mockWordConnect()
	{
		List<String> wcLeftWords = List.of("Lorem", "ipsum", "dolor", "sit", "amet");
		List<String> wcRightWords = List.of("consectetur", "adipiscing", "elit", "sed do", "eiusmod");
		Map<Integer, Integer> wcCorrectMapping = Map.of(
				0, 3,
				1, 0,
				2, 4,
				3, 2,
				4, 1);
		
		return new WordConnect(UUID.randomUUID(),
				"Test instruction", List.of(),
				wcLeftWords, wcRightWords, wcCorrectMapping, 100);
	}
	
	public static WordFillDTO mockWordFillDTO()
	{
		List<String> wfText = List.of("Lorem ", " ipsum ", " dolor ", " sit ", " amet");
		List<String> wfAnswers = List.of("abc", "def", "ghi", "jkl");
		List<EmptySpaceDTO> wfEmptySpaces = wfAnswers
				.stream()
				.map(ans -> new EmptySpaceDTO(ans))
				.collect(Collectors.toList());
		List<String> wfPossibleAnswers = List.of("abc", "def", "ghi", "jkl", "mno", "pqr");
		
		return new WordFillDTO("Test instruction", List.of(), 100,
				new WordFillElementDTO(wfText, wfEmptySpaces, false, wfPossibleAnswers));
	}
	public static ChoiceWordFillDTO mockChoiceWordFillDTO()
	{
		List<String> cwfText = List.of("Lorem ", " ipsum ", " dolor ", " sit ", " amet");
		List<String> cwfAnswers = List.of("abc", "def", "ghi", "jkl");
		List<WordChoiceDTO> wordChoices = cwfAnswers
				.stream()
				.map(ans -> new WordChoiceDTO(ans, List.of("qwr")))
				.collect(Collectors.toList());
		
		return new ChoiceWordFillDTO(
				"Test instruction", List.of(), 100,
				new ChoiceWordFillElementDTO(
						cwfText, wordChoices, false));
	}
	public static ListWordFillDTO mockListWordFillDTO()
	{
		List<List<String>> lwfText = List.of(
				List.of("Lorem ", " ipsum ", " dolor"),
				List.of("sit ", " amet"),
				List.of("consectetur adipiscing  ", " elit"),
				List.of("sed ", " do"));
		List<List<String>> lwfAnswers = List.of(
				List.of("abc", "def"),
				List.of("ghi"),
				List.of("jkl"),
				List.of("mno"));
		List<List<String>> lwfPossibleAnswers = List.of(
				List.of("abc", "def", "ghi", "jkl"),
				List.of("ghi", "def", "ghi"),
				List.of("jkl", "ghi", "jkl"),
				List.of("mno", "ghi", "def"));
		
		List<WordFillElementDTO> lwfWordFillElemList = new ArrayList<>();
		Iterator<List<String>> lwfTextIter = lwfText.iterator();
		Iterator<List<String>> lwfPossAnsIter = lwfPossibleAnswers.iterator();
		for (List<String> answerList: lwfAnswers)
		{
			List<String> textList = lwfTextIter.next();
			List<String> possibleAnswersList = lwfPossAnsIter.next();
			lwfWordFillElemList.add(new WordFillElementDTO(
					textList,
					answerList.stream()
						.map(ans -> new EmptySpaceDTO(ans))
						.collect(Collectors.toList()),
					true,
					possibleAnswersList));
		}
		
		return new ListWordFillDTO(
				"Test instruction", List.of(), 100,
				lwfWordFillElemList);
	}
	public static ListChoiceWordFillDTO mockListChoiceWordFillDTO()
	{
		List<List<String>> lcwfText = List.of(
				List.of("Lorem ", " ipsum ", " dolor"),
				List.of("sit ", " amet"),
				List.of("consectetur adipiscing  ", " elit"),
				List.of("sed ", " do"));
		List<List<String>> lcwfAnswers = List.of(
				List.of("abc", "def"),
				List.of("ghi"),
				List.of("jkl"),
				List.of("mno"));
		
		List<ChoiceWordFillElementDTO> lcwfWordFillElemList = new ArrayList<>();
		Iterator<List<String>> lcwfTextIter = lcwfText.iterator();
		for (List<String> answerList: lcwfAnswers)
		{
			List<String> textList = lcwfTextIter.next();
			lcwfWordFillElemList.add(new ChoiceWordFillElementDTO(
					textList,
					answerList.stream()
						.map(ans -> new WordChoiceDTO(ans, List.of("qwe", "poi")))
						.collect(Collectors.toList()),
					true));
		}
		
		return new ListChoiceWordFillDTO(
				"Test instruction", List.of(), 100,
				lcwfWordFillElemList);
	}
	public static ChronologicalOrderDTO mockChronologicalOrderDTO()
	{
		List<String> coText = List.of("Lorem ipsum dolor sit amet",
				"consectetur adipiscing elit",
				"sed do eiusmod tempor incididunt",
				"ut labore et dolore magna aliqua",
				"Ut enim ad minim veniam",
				"quis nostrud exercitation",
				"ullamco laboris nisi ut",
				"aliquip ex ea commodo consequat");
		
		return new ChronologicalOrderDTO(
				"Test instruction", List.of(), 100,
				coText);
	}
	public static ListSentenceFormingDTO mockListSentenceFormingDTO()
	{
		List<List<String>> text = List.of(
				List.of("Lorem ", " ipsum ", " dolor"),
				List.of("sit ", " amet"),
				List.of("consectetur adipiscing  ", " elit"),
				List.of("sed ", " do"));
		
		List<SentenceFormingElementDTO> lsfWordFillElemList = text.stream()
				.map(textList -> new SentenceFormingElementDTO(textList))
				.collect(Collectors.toList());
		
		return new ListSentenceFormingDTO(
				"Test instruction", List.of(), 100,
				lsfWordFillElemList);
	}
	public static OptionSelectDTO mockOptionSelectDTO()
	{
		String osContent = "Lorem ipsum dolor sit amet";
		List<String> osCorrectAnswers = List.of(
				"eiusmod", "tempor", "incididunt ut");
		List<String> osIncorrectAnswers = List.of(
				"adipiscing", "elit", "sed", "labore", "et dolore");
		
		return new OptionSelectDTO(
				"Test instruction", List.of(), 100,
				new OptionSelectElementDTO(
						osContent, osCorrectAnswers, osIncorrectAnswers));
	}
	public static WordConnectDTO mockWordConnectDTO()
	{
		List<String> wcLeftWords = List.of("Lorem", "ipsum", "dolor", "sit", "amet");
		List<String> wcRightWords = List.of("consectetur", "adipiscing", "elit", "sed do", "eiusmod");
		Map<Integer, Integer> wcCorrectMapping = Map.of(
				0, 3,
				1, 0,
				2, 4,
				3, 2,
				4, 1);
		
		return new WordConnectDTO(
				"Test instruction", List.of(), 100,
				wcLeftWords, wcRightWords, wcCorrectMapping);
	}
}
