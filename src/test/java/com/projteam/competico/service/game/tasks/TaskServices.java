package com.projteam.competico.service.game.tasks;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
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
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.projteam.competico.dao.game.tasks.ChoiceWordFillDAO;
import com.projteam.competico.dao.game.tasks.ChoiceWordFillElementDAO;
import com.projteam.competico.dao.game.tasks.ChoiceWordFillElementWordChoiceDAO;
import com.projteam.competico.dao.game.tasks.ChronologicalOrderDAO;
import com.projteam.competico.dao.game.tasks.ListChoiceWordFillDAO;
import com.projteam.competico.dao.game.tasks.ListSentenceFormingDAO;
import com.projteam.competico.dao.game.tasks.ListWordFillDAO;
import com.projteam.competico.dao.game.tasks.OptionSelectDAO;
import com.projteam.competico.dao.game.tasks.OptionSelectElementDAO;
import com.projteam.competico.dao.game.tasks.SentenceFormingElementDAO;
import com.projteam.competico.dao.game.tasks.WordConnectDAO;
import com.projteam.competico.dao.game.tasks.WordFillDAO;
import com.projteam.competico.dao.game.tasks.WordFillElementDAO;
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

public class TaskServices
{
	private @Mock ChoiceWordFillDAO cwfDao;
	private @Mock ChoiceWordFillElementDAO cwfeDao;
	private @Mock ChoiceWordFillElementWordChoiceDAO cwfewcDao;
	private @Mock ChronologicalOrderDAO coDao;
	private @Mock ListChoiceWordFillDAO lcwfDao;
	private @Mock ListSentenceFormingDAO lsfDao;
	private @Mock ListWordFillDAO lwfDao;
	private @Mock OptionSelectDAO osDao;
	private @Mock OptionSelectElementDAO oseDao;
	private @Mock SentenceFormingElementDAO sfeDao;
	private @Mock WordConnectDAO wcDao;
	private @Mock WordFillDAO wfDao;
	private @Mock WordFillElementDAO wfeDao;
	
	private @InjectMocks ChoiceWordFillService cwfService;
	private @InjectMocks ChronologicalOrderService coService;
	private @InjectMocks ListChoiceWordFillService lcwfService;
	private @InjectMocks ListSentenceFormingService lsfService;
	private @InjectMocks ListWordFillService lwfService;
	private @InjectMocks OptionSelectService osService;
	private @InjectMocks WordConnectService wcService;
	private @InjectMocks WordFillService wfService;
	
	@BeforeEach
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}
	
	@ParameterizedTest
	@MethodSource("mockTasks")
	public void wordFillSaveAcceptsOnlyCorrectTasks(Task task)
	{
		if (task instanceof WordFill)
			assertDoesNotThrow(() -> wfService.genericSave(task));
		else
			assertThrows(IllegalArgumentException.class,
					() -> wfService.genericSave(task));
	}
	@ParameterizedTest
	@MethodSource("mockTasks")
	public void choiceWordFillSaveAcceptsOnlyCorrectTasks(Task task)
	{
		if (task instanceof ChoiceWordFill)
			assertDoesNotThrow(() -> cwfService.genericSave(task));
		else
			assertThrows(IllegalArgumentException.class,
					() -> cwfService.genericSave(task));
	}
	@ParameterizedTest
	@MethodSource("mockTasks")
	public void listWordFillSaveAcceptsOnlyCorrectTasks(Task task)
	{
		if (task instanceof ListWordFill)
			assertDoesNotThrow(() -> lwfService.genericSave(task));
		else
			assertThrows(IllegalArgumentException.class,
					() -> lwfService.genericSave(task));
	}
	@ParameterizedTest
	@MethodSource("mockTasks")
	public void listChoiceWordFillSaveAcceptsOnlyCorrectTasks(Task task)
	{
		if (task instanceof ListChoiceWordFill)
			assertDoesNotThrow(() -> lcwfService.genericSave(task));
		else
			assertThrows(IllegalArgumentException.class,
					() -> lcwfService.genericSave(task));
	}
	@ParameterizedTest
	@MethodSource("mockTasks")
	public void chronologicalOrderSaveAcceptsOnlyCorrectTasks(Task task)
	{
		if (task instanceof ChronologicalOrder)
			assertDoesNotThrow(() -> coService.genericSave(task));
		else
			assertThrows(IllegalArgumentException.class,
					() -> coService.genericSave(task));
	}
	@ParameterizedTest
	@MethodSource("mockTasks")
	public void listSentenceFormingSaveAcceptsOnlyCorrectTasks(Task task)
	{
		if (task instanceof ListSentenceForming)
			assertDoesNotThrow(() -> lsfService.genericSave(task));
		else
			assertThrows(IllegalArgumentException.class,
					() -> lsfService.genericSave(task));
	}
	@ParameterizedTest
	@MethodSource("mockTasks")
	public void optionSelectSaveAcceptsOnlyCorrectTasks(Task task)
	{
		if (task instanceof OptionSelect)
			assertDoesNotThrow(() -> osService.genericSave(task));
		else
			assertThrows(IllegalArgumentException.class,
					() -> osService.genericSave(task));
	}
	@ParameterizedTest
	@MethodSource("mockTasks")
	public void wordConnectSaveAcceptsOnlyCorrectTasks(Task task)
	{
		if (task instanceof WordConnect)
			assertDoesNotThrow(() -> wcService.genericSave(task));
		else
			assertThrows(IllegalArgumentException.class,
					() -> wcService.genericSave(task));
	}
	
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 25, 999})
	public void wordFillCanCount(int count)
	{
		when(wfDao.count()).thenReturn((long) count);
		
		assertEquals(wfService.count(), count);
	}
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 25, 999})
	public void choiceWordFillCanCount(int count)
	{
		when(cwfDao.count()).thenReturn((long) count);
		
		assertEquals(cwfService.count(), count);
	}
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 25, 999})
	public void listWordFillCanCount(int count)
	{
		when(lwfDao.count()).thenReturn((long) count);
		
		assertEquals(lwfService.count(), count);
	}
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 25, 999})
	public void listChoiceWordFillCanCount(int count)
	{
		when(lcwfDao.count()).thenReturn((long) count);
		
		assertEquals(lcwfService.count(), count);
	}
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 25, 999})
	public void chronologicalOrderCanCount(int count)
	{
		when(coDao.count()).thenReturn((long) count);
		
		assertEquals(coService.count(), count);
	}
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 25, 999})
	public void listSentenceFormingCanCount(int count)
	{
		when(lsfDao.count()).thenReturn((long) count);
		
		assertEquals(lsfService.count(), count);
	}
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 25, 999})
	public void optionSelectCanCount(int count)
	{
		when(osDao.count()).thenReturn((long) count);
		
		assertEquals(osService.count(), count);
	}
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 25, 999})
	public void wordConnectCanCount(int count)
	{
		when(wcDao.count()).thenReturn((long) count);
		
		assertEquals(wcService.count(), count);
	}
	
	@Test
	public void wordFillCanGetTaskById()
	{
		WordFill mockTask = mockWordFill();
		UUID id = mockTask.getId();

		when(wfDao.count()).thenReturn(1l);
		when(wfDao.findById(id)).thenReturn(Optional.of(mockTask));
		//when(wfDao.findAll((PageRequest) any())).thenReturn(new PageImpl<>(List.of(mockTask)));
		
		assertEquals(wfService.genericFindById(id), mockTask);
	}
	@Test
	public void choiceWordFillCanGetTaskById()
	{
		ChoiceWordFill mockTask = mockChoiceWordFill();
		UUID id = mockTask.getId();

		when(cwfDao.count()).thenReturn(1l);
		when(cwfDao.findById(id)).thenReturn(Optional.of(mockTask));
		//when(cwfDao.findAll((PageRequest) any())).thenReturn(new PageImpl<>(List.of(mockTask)));
		
		assertEquals(cwfService.genericFindById(id), mockTask);
	}
	@Test
	public void listWordFillCanGetTaskById()
	{
		ListWordFill mockTask = mockListWordFill();
		UUID id = mockTask.getId();

		when(lwfDao.count()).thenReturn(1l);
		when(lwfDao.findById(id)).thenReturn(Optional.of(mockTask));
		//when(lwfDao.findAll((PageRequest) any())).thenReturn(new PageImpl<>(List.of(mockTask)));
		
		assertEquals(lwfService.genericFindById(id), mockTask);
	}
	@Test
	public void listChoiceWordFillCanGetTaskById()
	{
		ListChoiceWordFill mockTask = mockListChoiceWordFill();
		UUID id = mockTask.getId();

		when(lcwfDao.count()).thenReturn(1l);
		when(lcwfDao.findById(id)).thenReturn(Optional.of(mockTask));
		//when(lcwfDao.findAll((PageRequest) any())).thenReturn(new PageImpl<>(List.of(mockTask)));
		
		assertEquals(lcwfService.genericFindById(id), mockTask);
	}
	@Test
	public void chronologicalOrderCanGetTaskById()
	{
		ChronologicalOrder mockTask = mockChronologicalOrder();
		UUID id = mockTask.getId();

		when(coDao.count()).thenReturn(1l);
		when(coDao.findById(id)).thenReturn(Optional.of(mockTask));
		//when(coDao.findAll((PageRequest) any())).thenReturn(new PageImpl<>(List.of(mockTask)));
		
		assertEquals(coService.genericFindById(id), mockTask);
	}
	@Test
	public void listSentenceFormingCanGetTaskById()
	{
		ListSentenceForming mockTask = mockListSentenceForming();
		UUID id = mockTask.getId();

		when(lsfDao.count()).thenReturn(1l);
		when(lsfDao.findById(id)).thenReturn(Optional.of(mockTask));
		//when(lsfDao.findAll((PageRequest) any())).thenReturn(new PageImpl<>(List.of(mockTask)));
		
		assertEquals(lsfService.genericFindById(id), mockTask);
	}
	@Test
	public void optionSelectCanGetTaskById()
	{
		OptionSelect mockTask = mockOptionSelect();
		UUID id = mockTask.getId();

		when(osDao.count()).thenReturn(1l);
		when(osDao.findById(id)).thenReturn(Optional.of(mockTask));
		//when(osDao.findAll((PageRequest) any())).thenReturn(new PageImpl<>(List.of(mockTask)));
		
		assertEquals(osService.genericFindById(id), mockTask);
	}
	@Test
	public void wordConnectCanGetTaskById()
	{
		WordConnect mockTask = mockWordConnect();
		UUID id = mockTask.getId();

		when(wcDao.count()).thenReturn(1l);
		when(wcDao.findById(id)).thenReturn(Optional.of(mockTask));
		//when(wcDao.findAll((PageRequest) any())).thenReturn(new PageImpl<>(List.of(mockTask)));
		
		assertEquals(wcService.genericFindById(id), mockTask);
	}
	
	@Test
	public void wordFillCannotGetTaskByIdIfEmpty()
	{
		UUID id = UUID.randomUUID();

		when(wfDao.count()).thenReturn(0l);
		
		assertNull(wfService.genericFindById(id));
	}
	@Test
	public void choiceWordFillCannotGetTaskByIdIfEmpty()
	{
		UUID id = UUID.randomUUID();

		when(cwfDao.count()).thenReturn(0l);
		
		assertNull(cwfService.genericFindById(id));
	}
	@Test
	public void listWordFillCannotGetTaskByIdIfEmpty()
	{
		UUID id = UUID.randomUUID();

		when(lwfDao.count()).thenReturn(0l);
		
		assertNull(lwfService.genericFindById(id));
	}
	@Test
	public void listChoiceWordFillCannotGetTaskByIdIfEmpty()
	{
		UUID id = UUID.randomUUID();

		when(lcwfDao.count()).thenReturn(0l);
		
		assertNull(lcwfService.genericFindById(id));
	}
	@Test
	public void chronologicalOrderCannotGetTaskByIdIfEmpty()
	{
		UUID id = UUID.randomUUID();

		when(coDao.count()).thenReturn(0l);
		
		assertNull(coService.genericFindById(id));
	}
	@Test
	public void listSentenceFormingCannotGetTaskByIdIfEmpty()
	{
		UUID id = UUID.randomUUID();

		when(lsfDao.count()).thenReturn(0l);
		
		assertNull(lsfService.genericFindById(id));
	}
	@Test
	public void optionSelectCannotGetTaskByIdIfEmpty()
	{
		UUID id = UUID.randomUUID();

		when(osDao.count()).thenReturn(0l);
		
		assertNull(osService.genericFindById(id));
	}
	@Test
	public void wordConnectCannotGetTaskByIdIfEmpty()
	{
		UUID id = UUID.randomUUID();

		when(wcDao.count()).thenReturn(0l);
		
		assertNull(wcService.genericFindById(id));
	}
	
	@Test
	public void wordFillCanGetAllTasks()
	{
		WordFill mockTask = mockWordFill();

		when(wfDao.findAll()).thenReturn(List.of(mockTask));
		
		List<Task> res = wfService.genericFindAll();
		assertEquals(res.size(), 1);
		assertEquals(res.get(0), mockTask);
	}
	@Test
	public void choiceWordFillCanGetAllTasks()
	{
		ChoiceWordFill mockTask = mockChoiceWordFill();

		when(cwfDao.findAll()).thenReturn(List.of(mockTask));
		
		List<Task> res = cwfService.genericFindAll();
		assertEquals(res.size(), 1);
		assertEquals(res.get(0), mockTask);
	}
	@Test
	public void listWordFillCanGetAllTasks()
	{
		ListWordFill mockTask = mockListWordFill();

		when(lwfDao.findAll()).thenReturn(List.of(mockTask));
		
		List<Task> res = lwfService.genericFindAll();
		assertEquals(res.size(), 1);
		assertEquals(res.get(0), mockTask);
	}
	@Test
	public void listChoiceWordFillCanGetAllTasks()
	{
		ListChoiceWordFill mockTask = mockListChoiceWordFill();

		when(lcwfDao.findAll()).thenReturn(List.of(mockTask));
		
		List<Task> res = lcwfService.genericFindAll();
		assertEquals(res.size(), 1);
		assertEquals(res.get(0), mockTask);
	}
	@Test
	public void chronologicalOrderCanGetAllTasks()
	{
		ChronologicalOrder mockTask = mockChronologicalOrder();

		when(coDao.findAll()).thenReturn(List.of(mockTask));
		
		List<Task> res = coService.genericFindAll();
		assertEquals(res.size(), 1);
		assertEquals(res.get(0), mockTask);
	}
	@Test
	public void listSentenceFormingCanGetAllTasks()
	{
		ListSentenceForming mockTask = mockListSentenceForming();

		when(lsfDao.findAll()).thenReturn(List.of(mockTask));
		
		List<Task> res = lsfService.genericFindAll();
		assertEquals(res.size(), 1);
		assertEquals(res.get(0), mockTask);
	}
	@Test
	public void optionSelectCanGetAllTasks()
	{
		OptionSelect mockTask = mockOptionSelect();

		when(osDao.findAll()).thenReturn(List.of(mockTask));
		
		List<Task> res = osService.genericFindAll();
		assertEquals(res.size(), 1);
		assertEquals(res.get(0), mockTask);
	}
	@Test
	public void wordConnectCanGetAllTasks()
	{
		WordConnect mockTask = mockWordConnect();

		when(wcDao.findAll()).thenReturn(List.of(mockTask));
		
		List<Task> res = wcService.genericFindAll();
		assertEquals(res.size(), 1);
		assertEquals(res.get(0), mockTask);
	}
	
	//---Sources---
	
	public static List<Arguments> mockTasks()
	{
		return List.of(
				Arguments.of(mockWordFill()),
				Arguments.of(mockChoiceWordFill()),
				Arguments.of(mockListWordFill()),
				Arguments.of(mockListChoiceWordFill()),
				Arguments.of(mockChronologicalOrder()),
				Arguments.of(mockListSentenceForming()),
				Arguments.of(mockOptionSelect()),
				Arguments.of(mockWordConnect()));
	}
	
	//---Helpers---
	
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
}
