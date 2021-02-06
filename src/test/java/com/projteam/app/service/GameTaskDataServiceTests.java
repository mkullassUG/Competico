package com.projteam.app.service;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import com.projteam.app.domain.game.tasks.ChoiceWordFill;
import com.projteam.app.domain.game.tasks.ChoiceWordFillElement;
import com.projteam.app.domain.game.tasks.ChronologicalOrder;
import com.projteam.app.domain.game.tasks.ListChoiceWordFill;
import com.projteam.app.domain.game.tasks.ListSentenceForming;
import com.projteam.app.domain.game.tasks.ListWordFill;
import com.projteam.app.domain.game.tasks.MultipleChoice;
import com.projteam.app.domain.game.tasks.MultipleChoiceElement;
import com.projteam.app.domain.game.tasks.SentenceFormingElement;
import com.projteam.app.domain.game.tasks.SingleChoice;
import com.projteam.app.domain.game.tasks.WordConnect;
import com.projteam.app.domain.game.tasks.WordFill;
import com.projteam.app.domain.game.tasks.WordFillElement;
import com.projteam.app.domain.game.tasks.ChoiceWordFillElement.WordChoice;
import com.projteam.app.domain.game.tasks.WordFillElement.EmptySpace;
import com.projteam.app.dto.game.tasks.create.TaskDTO;
import com.projteam.app.mapper.game.tasks.TaskMapper;
import com.projteam.app.mapper.game.tasks.create.ChoiceWordFillMapper;
import com.projteam.app.mapper.game.tasks.create.ChronologicalOrderMapper;
import com.projteam.app.mapper.game.tasks.create.ListChoiceWordFillMapper;
import com.projteam.app.mapper.game.tasks.create.ListSentenceFormingMapper;
import com.projteam.app.mapper.game.tasks.create.ListWordFillMapper;
import com.projteam.app.mapper.game.tasks.create.MultipleChoiceMapper;
import com.projteam.app.mapper.game.tasks.create.SingleChoiceMapper;
import com.projteam.app.mapper.game.tasks.create.WordConnectMapper;
import com.projteam.app.mapper.game.tasks.create.WordFillMapper;
import com.projteam.app.service.game.GameTaskDataService;
import com.projteam.app.service.game.tasks.ChoiceWordFillService;
import com.projteam.app.service.game.tasks.ChronologicalOrderService;
import com.projteam.app.service.game.tasks.ListChoiceWordFillService;
import com.projteam.app.service.game.tasks.ListSentenceFormingService;
import com.projteam.app.service.game.tasks.ListWordFillService;
import com.projteam.app.service.game.tasks.MultipleChoiceService;
import com.projteam.app.service.game.tasks.SingleChoiceService;
import com.projteam.app.service.game.tasks.WordConnectService;
import com.projteam.app.service.game.tasks.WordFillService;

class GameTaskDataServiceTests
{
	private @Mock ChoiceWordFillService cwfServ;
	private @Mock ChronologicalOrderService coServ;
	private @Mock ListChoiceWordFillService lcwfServ;
	private @Mock ListSentenceFormingService lsfServ;
	private @Mock ListWordFillService lwfServ;
	private @Mock MultipleChoiceService mcServ;
	private @Mock SingleChoiceService scServ;
	private @Mock WordConnectService wcServ;
	private @Mock WordFillService wfServ;
	
	private @Mock AccountService aServ;
	
	private @Spy ChoiceWordFillMapper cwfMapper;
	private @Spy ChronologicalOrderMapper coMapper;
	private @Spy ListChoiceWordFillMapper lcwfMapper;
	private @Spy ListSentenceFormingMapper lsfMapper;
	private @Spy ListWordFillMapper lwfMapper;
	private @Spy MultipleChoiceMapper mcMapper;
	private @Spy SingleChoiceMapper scMapper;
	private @Spy WordConnectMapper wcMapper;
	private @Spy WordFillMapper wfMapper;
	
	private TaskMapper taskMapper;
	
	private GameTaskDataService gtdServ;
	
	@BeforeEach
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		
		taskMapper = new TaskMapper(List.of(
				cwfMapper, coMapper, lcwfMapper,
				lsfMapper, lwfMapper, mcMapper,
				scMapper, wcMapper, wfMapper));
		
		gtdServ = new GameTaskDataService(List.of(
					cwfServ, coServ, lcwfServ,
					lsfServ, lwfServ, mcServ,
					scServ, wcServ, wfServ),
				aServ, taskMapper);
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
		Mockito.when(cwfServ.genericFindAll())
			.thenReturn(List.of(mockChoiceWordFill()));
		Mockito.when(coServ.genericFindAll())
			.thenReturn(List.of(mockChronologicalOrder()));
		Mockito.when(lcwfServ.genericFindAll())
			.thenReturn(List.of(mockListChoiceWordFill()));
		Mockito.when(lsfServ.genericFindAll())
			.thenReturn(List.of(mockListSentenceForming()));
		Mockito.when(lwfServ.genericFindAll())
			.thenReturn(List.of(mockListWordFill()));
		Mockito.when(mcServ.genericFindAll())
			.thenReturn(List.of(mockMultipleChoice()));
		Mockito.when(scServ.genericFindAll())
			.thenReturn(List.of(mockSingleChoice()));
		Mockito.when(wcServ.genericFindAll())
			.thenReturn(List.of(mockWordConnect()));
		Mockito.when(wfServ.genericFindAll())
			.thenReturn(List.of(mockWordFill()));
		
		int mockedTaskCount = 9;
		
		List<TaskDTO> res = gtdServ.getAllTasks();
		assertNotNull(res);
		assertThat(res.size(), greaterThanOrEqualTo(mockedTaskCount));
		assertThat(res, not(hasItem(nullValue())));
	}
	@Test
	public void shouldThrowWhenNoServiceCanAcceptTask()
	{
		WordFill task = mockWordFill();
		
		Mockito.when(cwfServ.canAccept(task)).thenReturn(false);
		Mockito.when(coServ.canAccept(task)).thenReturn(false);
		Mockito.when(lcwfServ.canAccept(task)).thenReturn(false);
		Mockito.when(lsfServ.canAccept(task)).thenReturn(false);
		Mockito.when(lwfServ.canAccept(task)).thenReturn(false);
		Mockito.when(mcServ.canAccept(task)).thenReturn(false);
		Mockito.when(scServ.canAccept(task)).thenReturn(false);
		Mockito.when(wcServ.canAccept(task)).thenReturn(false);
		Mockito.when(wfServ.canAccept(task)).thenReturn(false);
		
		assertThrows(IllegalStateException.class, () ->
				gtdServ.saveTask(task));
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
		public static SingleChoice mockSingleChoice()
		{
			String scContent = "Lorem ipsum dolor sit amet";
			String scAnswer = "consectetur";
			List<String> scIncorrectAnswers = List.of(
					"adipiscing", "elit", "sed");
			
			return new SingleChoice(UUID.randomUUID(), 
					"Test instruction", List.of(),
					scContent, scAnswer, scIncorrectAnswers, 100);
		}
		public static MultipleChoice mockMultipleChoice()
		{
			String mcContent = "Lorem ipsum dolor sit amet";
			List<String> mcCorrectAnswers = List.of(
					"eiusmod", "tempor", "incididunt ut");
			List<String> mcIncorrectAnswers = List.of(
					"adipiscing", "elit", "sed", "labore", "et dolore");
			
			return new MultipleChoice(UUID.randomUUID(),
					"Test instruction", List.of(),
					new MultipleChoiceElement(UUID.randomUUID(),
							mcContent, mcCorrectAnswers, mcIncorrectAnswers), 100);
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
