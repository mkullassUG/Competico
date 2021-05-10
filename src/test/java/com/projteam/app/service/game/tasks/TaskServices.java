package com.projteam.app.service.game.tasks;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import com.projteam.app.dao.game.tasks.ChoiceWordFillDAO;
import com.projteam.app.dao.game.tasks.ChoiceWordFillElementDAO;
import com.projteam.app.dao.game.tasks.ChoiceWordFillElementWordChoiceDAO;
import com.projteam.app.dao.game.tasks.ChronologicalOrderDAO;
import com.projteam.app.dao.game.tasks.ListChoiceWordFillDAO;
import com.projteam.app.dao.game.tasks.ListSentenceFormingDAO;
import com.projteam.app.dao.game.tasks.ListWordFillDAO;
import com.projteam.app.dao.game.tasks.OptionSelectDAO;
import com.projteam.app.dao.game.tasks.OptionSelectElementDAO;
import com.projteam.app.dao.game.tasks.SentenceFormingElementDAO;
import com.projteam.app.dao.game.tasks.WordConnectDAO;
import com.projteam.app.dao.game.tasks.WordFillDAO;
import com.projteam.app.dao.game.tasks.WordFillElementDAO;
import com.projteam.app.domain.game.tasks.ChoiceWordFill;
import com.projteam.app.domain.game.tasks.ChoiceWordFillElement;
import com.projteam.app.domain.game.tasks.ChoiceWordFillElement.WordChoice;
import com.projteam.app.domain.game.tasks.ChronologicalOrder;
import com.projteam.app.domain.game.tasks.ListChoiceWordFill;
import com.projteam.app.domain.game.tasks.ListSentenceForming;
import com.projteam.app.domain.game.tasks.ListWordFill;
import com.projteam.app.domain.game.tasks.OptionSelect;
import com.projteam.app.domain.game.tasks.OptionSelectElement;
import com.projteam.app.domain.game.tasks.SentenceFormingElement;
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.domain.game.tasks.WordConnect;
import com.projteam.app.domain.game.tasks.WordFill;
import com.projteam.app.domain.game.tasks.WordFillElement;
import com.projteam.app.domain.game.tasks.WordFillElement.EmptySpace;

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
	@MethodSource("mockTasks")
	public void wordFillExistsByIdAcceptsOnlyCorrectTasks(Task task)
	{
		when(wfDao.existsById(any())).thenReturn(true);
		
		if (task instanceof WordFill)
			assertTrue(wfService.genericExistsById(task));
		else
			assertThrows(IllegalArgumentException.class,
					() -> wfService.genericExistsById(task));
	}
	@ParameterizedTest
	@MethodSource("mockTasks")
	public void choiceWordFillExistsByIdAcceptsOnlyCorrectTasks(Task task)
	{
		when(cwfDao.existsById(any())).thenReturn(true);
		
		if (task instanceof ChoiceWordFill)
			assertTrue(cwfService.genericExistsById(task));
		else
			assertThrows(IllegalArgumentException.class,
					() -> cwfService.genericExistsById(task));
	}
	@ParameterizedTest
	@MethodSource("mockTasks")
	public void listWordFillExistsByIdAcceptsOnlyCorrectTasks(Task task)
	{
		when(lwfDao.existsById(any())).thenReturn(true);
		
		if (task instanceof ListWordFill)
			assertTrue(lwfService.genericExistsById(task));
		else
			assertThrows(IllegalArgumentException.class,
					() -> lwfService.genericExistsById(task));
	}
	@ParameterizedTest
	@MethodSource("mockTasks")
	public void listChoiceWordFillExistsByIdAcceptsOnlyCorrectTasks(Task task)
	{
		when(lcwfDao.existsById(any())).thenReturn(true);
		
		if (task instanceof ListChoiceWordFill)
			assertTrue(lcwfService.genericExistsById(task));
		else
			assertThrows(IllegalArgumentException.class,
					() -> lcwfService.genericExistsById(task));
	}
	@ParameterizedTest
	@MethodSource("mockTasks")
	public void chronologicalOrderExistsByIdAcceptsOnlyCorrectTasks(Task task)
	{
		when(coDao.existsById(any())).thenReturn(true);
		
		if (task instanceof ChronologicalOrder)
			assertTrue(coService.genericExistsById(task));
		else
			assertThrows(IllegalArgumentException.class,
					() -> coService.genericExistsById(task));
	}
	@ParameterizedTest
	@MethodSource("mockTasks")
	public void listSentenceFormingExistsByIdAcceptsOnlyCorrectTasks(Task task)
	{
		when(lsfDao.existsById(any())).thenReturn(true);
		
		if (task instanceof ListSentenceForming)
			assertTrue(lsfService.genericExistsById(task));
		else
			assertThrows(IllegalArgumentException.class,
					() -> lsfService.genericExistsById(task));
	}
	@ParameterizedTest
	@MethodSource("mockTasks")
	public void optionSelectExistsByIdAcceptsOnlyCorrectTasks(Task task)
	{
		when(osDao.existsById(any())).thenReturn(true);
		
		if (task instanceof OptionSelect)
			assertTrue(osService.genericExistsById(task));
		else
			assertThrows(IllegalArgumentException.class,
					() -> osService.genericExistsById(task));
	}
	@ParameterizedTest
	@MethodSource("mockTasks")
	public void wordConnectExistsByIdAcceptsOnlyCorrectTasks(Task task)
	{
		when(wcDao.existsById(any())).thenReturn(true);
		
		if (task instanceof WordConnect)
			assertTrue(wcService.genericExistsById(task));
		else
			assertThrows(IllegalArgumentException.class,
					() -> wcService.genericExistsById(task));
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
	public void wordFillCanGetRandomTask()
	{
		WordFill mockTask = mockWordFill();
		Random r = new Random();

		when(wfDao.count()).thenReturn(1l);
		when(wfDao.findAll((PageRequest) any())).thenReturn(new PageImpl<>(List.of(mockTask)));
		
		assertEquals(wfService.genericFindRandom(r), mockTask);
	}
	@Test
	public void choiceWordFillCanGetRandomTask()
	{
		ChoiceWordFill mockTask = mockChoiceWordFill();
		Random r = new Random();

		when(cwfDao.count()).thenReturn(1l);
		when(cwfDao.findAll((PageRequest) any())).thenReturn(new PageImpl<>(List.of(mockTask)));
		
		assertEquals(cwfService.genericFindRandom(r), mockTask);
	}
	@Test
	public void listWordFillCanGetRandomTask()
	{
		ListWordFill mockTask = mockListWordFill();
		Random r = new Random();

		when(lwfDao.count()).thenReturn(1l);
		when(lwfDao.findAll((PageRequest) any())).thenReturn(new PageImpl<>(List.of(mockTask)));
		
		assertEquals(lwfService.genericFindRandom(r), mockTask);
	}
	@Test
	public void listChoiceWordFillCanGetRandomTask()
	{
		ListChoiceWordFill mockTask = mockListChoiceWordFill();
		Random r = new Random();

		when(lcwfDao.count()).thenReturn(1l);
		when(lcwfDao.findAll((PageRequest) any())).thenReturn(new PageImpl<>(List.of(mockTask)));
		
		assertEquals(lcwfService.genericFindRandom(r), mockTask);
	}
	@Test
	public void chronologicalOrderCanGetRandomTask()
	{
		ChronologicalOrder mockTask = mockChronologicalOrder();
		Random r = new Random();

		when(coDao.count()).thenReturn(1l);
		when(coDao.findAll((PageRequest) any())).thenReturn(new PageImpl<>(List.of(mockTask)));
		
		assertEquals(coService.genericFindRandom(r), mockTask);
	}
	@Test
	public void listSentenceFormingCanGetRandomTask()
	{
		ListSentenceForming mockTask = mockListSentenceForming();
		Random r = new Random();

		when(lsfDao.count()).thenReturn(1l);
		when(lsfDao.findAll((PageRequest) any())).thenReturn(new PageImpl<>(List.of(mockTask)));
		
		assertEquals(lsfService.genericFindRandom(r), mockTask);
	}
	@Test
	public void optionSelectCanGetRandomTask()
	{
		OptionSelect mockTask = mockOptionSelect();
		Random r = new Random();

		when(osDao.count()).thenReturn(1l);
		when(osDao.findAll((PageRequest) any())).thenReturn(new PageImpl<>(List.of(mockTask)));
		
		assertEquals(osService.genericFindRandom(r), mockTask);
	}
	@Test
	public void wordConnectCanGetRandomTask()
	{
		WordConnect mockTask = mockWordConnect();
		Random r = new Random();

		when(wcDao.count()).thenReturn(1l);
		when(wcDao.findAll((PageRequest) any())).thenReturn(new PageImpl<>(List.of(mockTask)));
		
		assertEquals(wcService.genericFindRandom(r), mockTask);
	}
	
	@Test
	public void wordFillCannotGetRandomTaskIfEmpty()
	{
		Random r = new Random();

		when(wfDao.count()).thenReturn(0l);
		
		assertNull(wfService.genericFindRandom(r));
	}
	@Test
	public void choiceWordFillCannotGetRandomTaskIfEmpty()
	{
		Random r = new Random();

		when(cwfDao.count()).thenReturn(0l);
		
		assertNull(cwfService.genericFindRandom(r));
	}
	@Test
	public void listWordFillCannotGetRandomTaskIfEmpty()
	{
		Random r = new Random();

		when(lwfDao.count()).thenReturn(0l);
		
		assertNull(lwfService.genericFindRandom(r));
	}
	@Test
	public void listChoiceWordFillCannotGetRandomTaskIfEmpty()
	{
		Random r = new Random();

		when(lcwfDao.count()).thenReturn(0l);
		
		assertNull(lcwfService.genericFindRandom(r));
	}
	@Test
	public void chronologicalOrderCannotGetRandomTaskIfEmpty()
	{
		Random r = new Random();

		when(coDao.count()).thenReturn(0l);
		
		assertNull(coService.genericFindRandom(r));
	}
	@Test
	public void listSentenceFormingCannotGetRandomTaskIfEmpty()
	{
		Random r = new Random();

		when(lsfDao.count()).thenReturn(0l);
		
		assertNull(lsfService.genericFindRandom(r));
	}
	@Test
	public void optionSelectCannotGetRandomTaskIfEmpty()
	{
		Random r = new Random();

		when(osDao.count()).thenReturn(0l);
		
		assertNull(osService.genericFindRandom(r));
	}
	@Test
	public void wordConnectCannotGetRandomTaskIfEmpty()
	{
		Random r = new Random();

		when(wcDao.count()).thenReturn(0l);
		
		assertNull(wcService.genericFindRandom(r));
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
