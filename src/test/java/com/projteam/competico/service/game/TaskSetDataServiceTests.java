package com.projteam.competico.service.game;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
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
import com.projteam.competico.dao.game.TaskSetDAO;
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
import com.projteam.competico.domain.game.tasks.Task;
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

class TaskSetDataServiceTests
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
	private @Mock TaskSetDAO tsDao;
	private @Mock TaskInfoDAO tiDao;
	
	private GenericTaskMapper taskMapper;
	
	private TaskSetDataService tsdServ;
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	@BeforeEach
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		
		taskMapper = new GenericTaskMapper(List.of(
				cwfMapper, coMapper, lcwfMapper,
				lsfMapper, lwfMapper, osMapper,
				wcMapper, wfMapper));
		
		tsdServ = new TaskSetDataService(List.of(
					cwfServ, coServ, lcwfServ,
					lsfServ, lwfServ, osServ,
					wcServ, wfServ),
				aServ, taskMapper,
				gtDao, tsDao, tiDao);
		
		when(cwfServ.canAccept(any()))
			.thenCallRealMethod();
		when(coServ.canAccept(any()))
			.thenCallRealMethod();
		when(lcwfServ.canAccept(any()))
			.thenCallRealMethod();
		when(lsfServ.canAccept(any()))
			.thenCallRealMethod();
		when(lwfServ.canAccept(any()))
			.thenCallRealMethod();
		when(osServ.canAccept(any()))
			.thenCallRealMethod();
		when(wcServ.canAccept(any()))
			.thenCallRealMethod();
		when(wfServ.canAccept(any()))
			.thenCallRealMethod();
	}
	
	@ParameterizedTest
	@MethodSource("mockTasksetDTOs")
	public void shouldImportTask(String tasksetName, JsonNode tasksetDto)
			throws ClassNotFoundException, IOException
	{
		Account lecturer = mockLecturer();
		
		tsdServ.importTask(tasksetDto, lecturer);
		
		verifyTasksSaved(1);
	}
	@ParameterizedTest
	@MethodSource("mockTasksetDTOs")
	public void shouldImportTaskWhileAuthenticated(
			String tasksetName, JsonNode tasksetDto)
			throws ClassNotFoundException, IOException
	{
		when(aServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(mockLecturer()));
		
		tsdServ.importTask(tasksetDto);
		
		verifyTasksSaved(1);
	}
	
	@ParameterizedTest
	@MethodSource("mockTaskDTOfile")
	public void shouldImportTasksFromJsonFile(
			String taksetName,
			int taskCount,
			MultipartFile taskDtoFile)
			throws ClassNotFoundException, IOException
	{
		Account lecturer = mockLecturer();
		
		tsdServ.importTasks(taskDtoFile, lecturer);
		
		verifyTasksSaved(taskCount);
	}
	@ParameterizedTest
	@MethodSource("mockTaskDTOfile")
	public void shouldImportTasksFromJsonFileWhileAuthenticated(
			String taksetName,
			int taskCount,
			MultipartFile taskDtoFile)
			throws ClassNotFoundException, IOException
	{
		when(aServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(mockLecturer()));
		
		tsdServ.importTasks(taskDtoFile);
		
		verifyTasksSaved(taskCount);
	}
	
	@ParameterizedTest
	@MethodSource("mockTaskDTOfile")
	public void shouldImportTasksToTasksetFromJsonFile(
			String taksetName,
			int taskCount,
			MultipartFile taskDtoFile)
			throws ClassNotFoundException, IOException
	{
		Account lecturer = mockLecturer();
		String newTasksetName = taksetName + "n";
		
		tsdServ.importTasksToTaskset(taskDtoFile, newTasksetName, lecturer);
		
		verifyTasksSaved(taskCount);
	}
	@ParameterizedTest
	@MethodSource("mockTaskDTOfile")
	public void shouldImportTasksToTasksetFromJsonFileWhileAuthenticated(
			String taksetName,
			int taskCount,
			MultipartFile taskDtoFile)
			throws ClassNotFoundException, IOException
	{
		when(aServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(mockLecturer()));
		
		String newTasksetName = taksetName + "n";
		
		tsdServ.importTasksToTaskset(taskDtoFile, newTasksetName);
		
		verifyTasksSaved(taskCount);
	}
	
	@Test
	public void shouldGetTaskCount()
	{
		assertDoesNotThrow(() -> tsdServ.getTaskCount(mockLecturer()));
	}
	@Test
	public void shouldGetDefaultTasksetsTaskCount()
	{
		assertDoesNotThrow(() -> tsdServ.getTaskCount(null, mockLecturer()));
	}
	@Test
	public void shouldGetTaskCountWhileAutenticated()
	{
		when(aServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(mockLecturer()));
		
		assertDoesNotThrow(() -> tsdServ.getTaskCount());
	}
	@Test
	public void shouldGetTasksetsTaskCountWhileAutenticated()
	{
		when(aServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(mockLecturer()));
		
		assertDoesNotThrow(() -> tsdServ.getTaskCount((String) null));
	}
	
	@Test
	public void shouldGetTasksFronDefaultTaskset()
	{
		assertNotNull(tsdServ.getTasks(null, mockLecturer()));
	}
	@Test
	public void shouldGetTasksFronDefaultTasksetWhileAuthenticated()
	{
		when(aServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(mockLecturer()));
		
		assertNotNull(tsdServ.getTasks(null));
	}
	@Test
	public void shouldGetTaskInfo()
	{
		assertNotNull(tsdServ.getTaskInfo(mockLecturer()));
	}
	@Test
	public void shouldGetTaskInfoWhileAuthenticated()
	{
		when(aServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(mockLecturer()));
		
		assertNotNull(tsdServ.getTaskInfo());
	}
	
	@ParameterizedTest
	@MethodSource("mockTasksetDTOs")
	public void shouldRemoveTask(String tasksetName, JsonNode tasksetDto)
	{
		Account lecturer = mockLecturer();
		UUID id = UUID.randomUUID();
		
		when(osServ.genericExistsById(id)).thenReturn(true);
		
		boolean success = tsdServ.removeTask(id, lecturer);
		
		assertTrue(success);
		verifyTasksRemoved(List.of(id));
	}
	@ParameterizedTest
	@MethodSource("mockTasksetDTOs")
	public void shouldRemoveTaskWithAuthenticatedLecturer(
			String tasksetName, JsonNode tasksetDto)
	{
		when(aServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(mockLecturer()));
		UUID id = UUID.randomUUID();
		
		when(wfServ.genericExistsById(id)).thenReturn(true);
		
		boolean success = tsdServ.removeTask(id);
		
		assertTrue(success);
		verifyTasksRemoved(List.of(id));
	}
	@ParameterizedTest
	@MethodSource("mockTasksetDTOs")
	public void shouldRemoveAllTasks(String tasksetName, JsonNode tasksetDto)
	{
		Account lecturer = mockLecturer();
		
		assertDoesNotThrow(() -> tsdServ.removeAllTasks(lecturer));
	}
	@ParameterizedTest
	@MethodSource("mockTasksetDTOs")
	public void shouldRemoveAllTasksWithAuthenticatedLecturer(String tasksetName, JsonNode tasksetDto)
	{
		when(aServ.getAuthenticatedAccount())
			.thenReturn(Optional.of(mockLecturer()));
		
		assertDoesNotThrow(() -> tsdServ.removeAllTasks());
	}
	
	@ParameterizedTest
	@MethodSource("mockTasksetDTOs")
	public void shouldGetTask(String tasksetName, JsonNode tasksetDto)
	{
		UUID id = UUID.randomUUID();
		
		when(cwfServ.genericFindById(id)).thenReturn(mockChoiceWordFill());
		
		Optional<TaskDTO> ret = tsdServ.getTask(id);
		
		assertTrue(ret.isPresent());
	}
	@ParameterizedTest
	@MethodSource("mockTasksetDTOs")
	public void shouldNotGetTasksWithIncorrectID(String tasksetName, JsonNode tasksetDto)
	{
		UUID id = UUID.randomUUID();
		
		Optional<TaskDTO> ret = tsdServ.getTask(id);
		
		assertTrue(ret.isEmpty());
	}
	
	//---Verifiers---
	
	private void verifyTasksSaved(int taskCount)
	{
		ArgumentCaptor<Task> taskCap = ArgumentCaptor.forClass(Task.class);
		
		verify(cwfServ, atLeast(0)).genericSave(taskCap.capture());
		verify(cwfServ, atLeast(0)).genericSaveAndFlush(taskCap.capture());
		verify(coServ, atLeast(0)).genericSave(taskCap.capture());
		verify(coServ, atLeast(0)).genericSaveAndFlush(taskCap.capture());
		verify(lcwfServ, atLeast(0)).genericSave(taskCap.capture());
		verify(lcwfServ, atLeast(0)).genericSaveAndFlush(taskCap.capture());
		verify(lsfServ, atLeast(0)).genericSave(taskCap.capture());
		verify(lsfServ, atLeast(0)).genericSaveAndFlush(taskCap.capture());
		verify(lwfServ, atLeast(0)).genericSave(taskCap.capture());
		verify(lwfServ, atLeast(0)).genericSaveAndFlush(taskCap.capture());
		verify(osServ, atLeast(0)).genericSave(taskCap.capture());
		verify(osServ, atLeast(0)).genericSaveAndFlush(taskCap.capture());
		verify(wcServ, atLeast(0)).genericSave(taskCap.capture());
		verify(wcServ, atLeast(0)).genericSaveAndFlush(taskCap.capture());
		verify(wfServ, atLeast(0)).genericSave(taskCap.capture());
		verify(wfServ, atLeast(0)).genericSaveAndFlush(taskCap.capture());
	
		assertEquals(taskCap.getAllValues().size(), taskCount);
	}
	private void verifyTasksRemoved(List<UUID> taskIds)
	{
		ArgumentCaptor<UUID> taskIdCap = ArgumentCaptor.forClass(UUID.class);
		
		verify(cwfServ, atLeast(0)).genericDeleteById(taskIdCap.capture());
		verify(coServ, atLeast(0)).genericDeleteById(taskIdCap.capture());
		verify(lcwfServ, atLeast(0)).genericDeleteById(taskIdCap.capture());
		verify(lsfServ, atLeast(0)).genericDeleteById(taskIdCap.capture());
		verify(lwfServ, atLeast(0)).genericDeleteById(taskIdCap.capture());
		verify(osServ, atLeast(0)).genericDeleteById(taskIdCap.capture());
		verify(wcServ, atLeast(0)).genericDeleteById(taskIdCap.capture());
		verify(wfServ, atLeast(0)).genericDeleteById(taskIdCap.capture());
	
		List<UUID> captured = taskIdCap.getAllValues();
		
		assertEquals(captured.size(), taskIds.size());
		assertTrue(captured.containsAll(taskIds));
		assertTrue(taskIds.containsAll(captured));
	}
	
	//---Sources---
	
	public static List<Arguments> mockTasksetDTOs()
	{
		String tasksetName = "default";
		
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
				.map(cont -> Map.of(
						"tasksetName", tasksetName,
						"tasksetContent", cont))
				.map(map -> mapper.valueToTree(map))
				.map(jn -> Arguments.of(tasksetName, jn))
				.collect(Collectors.toList());
	}
	public static List<Arguments> mockTaskDTOfile()
	{
		String tasksetName = "default";
		
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

		return List.of(Arguments.of(tasksetName, ret.size(),
				new MockMultipartFile("file", "data.json",
						MediaType.TEXT_PLAIN_VALUE,
						mapper.valueToTree(Map.of(
					"tasksetName", tasksetName,
					"tasksetContent", ret
				)).toString().getBytes())));
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
