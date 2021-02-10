package com.projteam.app.integration.game.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import com.projteam.app.dao.game.tasks.ChoiceWordFillDAO;
import com.projteam.app.dao.game.tasks.ChoiceWordFillElementDAO;
import com.projteam.app.dao.game.tasks.ChronologicalOrderDAO;
import com.projteam.app.dao.game.tasks.ListChoiceWordFillDAO;
import com.projteam.app.dao.game.tasks.ListSentenceFormingDAO;
import com.projteam.app.dao.game.tasks.ListWordFillDAO;
import com.projteam.app.dao.game.tasks.MultipleChoiceDAO;
import com.projteam.app.dao.game.tasks.MultipleChoiceElementDAO;
import com.projteam.app.dao.game.tasks.SentenceFormingElementDAO;
import com.projteam.app.dao.game.tasks.SingleChoiceDAO;
import com.projteam.app.dao.game.tasks.WordConnectDAO;
import com.projteam.app.dao.game.tasks.WordFillDAO;
import com.projteam.app.dao.game.tasks.WordFillElementDAO;
import com.projteam.app.dao.game.tasks.ChoiceWordFillElementWordChoiceDAO;
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

@SpringBootTest
@ContextConfiguration(name = "Integration-tests")
@AutoConfigureMockMvc
public class TaskDAOTests
{
	private @Autowired WordFillDAO wfDAO;
	private @Autowired WordFillElementDAO wfeDAO;
	private @Autowired ListWordFillDAO lwfDAO;
	private @Autowired ChoiceWordFillDAO cwfDAO;
	private @Autowired ChoiceWordFillElementDAO cwfeDAO;
	private @Autowired ChoiceWordFillElementWordChoiceDAO cfwewcDAO;
	private @Autowired ListChoiceWordFillDAO lcwfDAO;
	private @Autowired SingleChoiceDAO scDAO;
	private @Autowired MultipleChoiceDAO mcDAO;
	private @Autowired MultipleChoiceElementDAO mceDAO;
	private @Autowired WordConnectDAO wcDAO;
	private @Autowired ChronologicalOrderDAO coDAO;
	private @Autowired ListSentenceFormingDAO lsfDAO;
	private @Autowired SentenceFormingElementDAO sfeDAO;
	
	@Test
	@Transactional
	public void shouldSaveAndRetrieveWordFill()
	{
		List<String> text = List.of("Lorem ", " ipsum ", " dolor ", " sit ", " amet");
		List<String> answers = List.of("abc", "def", "ghi", "jkl");
		List<EmptySpace> emptySpaces = answers
				.stream()
				.map(ans -> new EmptySpace(ans))
				.collect(Collectors.toList());
		List<String> possibleAnswers = List.of("abc", "def", "ghi", "jkl", "mno", "pqr");
		
		WordFill wf = new WordFill(UUID.randomUUID(),
				"Test instruction", List.of(),
				new WordFillElement(UUID.randomUUID(),
						text, emptySpaces, false, possibleAnswers), 1);
		
		wfeDAO.saveAndFlush(wf.getContent());
		wfeDAO.flush();
		WordFill wf2 = wfDAO.saveAndFlush(wf);
		wfDAO.flush();
		WordFill wf3 = wfDAO.findById(wf.getId()).orElse(null);
		
		assertEquals(wf, wf2);
		assertEquals(wf2, wf3);
		assertEquals(wf, wf3);
	}
	@Test
	@Transactional
	public void shouldSaveAndRetrieveChoiceWordFill()
	{
		List<String> text = List.of("Lorem ", " ipsum ", " dolor ", " sit ", " amet");
		List<String> answers = List.of("abc", "def", "ghi", "jkl");
		List<WordChoice> wordChoices = answers
				.stream()
				.map(ans -> new WordChoice(UUID.randomUUID(), ans, List.of("qwr")))
				.collect(Collectors.toList());
		
		ChoiceWordFill cwf = new ChoiceWordFill(UUID.randomUUID(),
				"Test instruction", List.of(),
				new ChoiceWordFillElement(UUID.randomUUID(),
						text, wordChoices, false), 1);
		
		cfwewcDAO.saveAll(cwf.getContent().getWordChoices());
		cfwewcDAO.flush();
		cwfeDAO.saveAndFlush(cwf.getContent());
		cwfeDAO.flush();
		ChoiceWordFill cwf2 = cwfDAO.saveAndFlush(cwf);
		cwfDAO.flush();
		ChoiceWordFill cwf3 = cwfDAO.findById(cwf.getId()).orElse(null);
		
		assertEquals(cwf, cwf2);
		assertEquals(cwf2, cwf3);
		assertEquals(cwf, cwf3);
	}
	@Test
	@Transactional
	public void shouldSaveAndRetrieveListWordFill()
	{
		List<List<String>> text = List.of(
				List.of("Lorem ", " ipsum ", " dolor"),
				List.of("sit ", " amet"),
				List.of("consectetur adipiscing  ", " elit"),
				List.of("sed ", " do"));
		List<List<String>> answers = List.of(
				List.of("abc", "def"),
				List.of("ghi"),
				List.of("jkl"),
				List.of("mno"));
		List<List<String>> possibleAnswers = List.of(
				List.of("abc", "def", "ghi", "jkl"),
				List.of("ghi", "def", "ghi"),
				List.of("jkl", "ghi", "jkl"),
				List.of("mno", "ghi", "def"));
		
		List<WordFillElement> wordFillElemList = new ArrayList<>();
		Iterator<List<String>> textIter = text.iterator();
		Iterator<List<String>> possAnsIter = possibleAnswers.iterator();
		for (List<String> answerList: answers)
		{
			List<String> textList = textIter.next();
			List<String> possibleAnswersList = possAnsIter.next();
			wordFillElemList.add(new WordFillElement(UUID.randomUUID(),
					textList,
					answerList.stream()
						.map(ans -> new EmptySpace(ans))
						.collect(Collectors.toList()),
					true,
					possibleAnswersList));
		}
		
		ListWordFill lwf = new ListWordFill(UUID.randomUUID(),
				"Test instruction", List.of(),
				wordFillElemList, 1);
		
		wfeDAO.saveAll(lwf.getRows());
		wfeDAO.flush();
		ListWordFill lwf2 = lwfDAO.saveAndFlush(lwf);
		lwfDAO.flush();
		ListWordFill lwf3 = lwfDAO.findById(lwf.getId()).orElse(null);
		
		assertEquals(lwf, lwf2);
		assertEquals(lwf2, lwf3);
		assertEquals(lwf, lwf3);
	}
	@Test
	@Transactional
	public void shouldSaveAndRetrieveListChoiceWordFill()
	{
		List<List<String>> text = List.of(
				List.of("Lorem ", " ipsum ", " dolor"),
				List.of("sit ", " amet"),
				List.of("consectetur adipiscing  ", " elit"),
				List.of("sed ", " do"));
		List<List<String>> answers = List.of(
				List.of("abc", "def"),
				List.of("ghi"),
				List.of("jkl"),
				List.of("mno"));
		
		List<ChoiceWordFillElement> wordFillElemList = new ArrayList<>();
		Iterator<List<String>> textIter = text.iterator();
		for (List<String> answerList: answers)
		{
			List<String> textList = textIter.next();
			wordFillElemList.add(new ChoiceWordFillElement(UUID.randomUUID(),
					textList,
					answerList.stream()
						.map(ans -> new WordChoice(UUID.randomUUID(), ans, List.of("qwe", "poi")))
						.collect(Collectors.toList()),
					true));
		}
		
		ListChoiceWordFill lcwf = new ListChoiceWordFill(UUID.randomUUID(),
				"Test instruction", List.of(),
				wordFillElemList, 1);
		
		cfwewcDAO.saveAll(lcwf.getRows()
				.stream()
				.flatMap(row -> row.getWordChoices().stream())
				.collect(Collectors.toList()));
		cfwewcDAO.flush();
		cwfeDAO.saveAll(lcwf.getRows());
		cwfeDAO.flush();
		ListChoiceWordFill lcwf2 = lcwfDAO.saveAndFlush(lcwf);
		lcwfDAO.flush();
		ListChoiceWordFill lcwf3 = lcwfDAO.findById(lcwf.getId()).orElse(null);
		
		assertEquals(lcwf, lcwf2);
		assertEquals(lcwf2, lcwf3);
		assertEquals(lcwf, lcwf3);
	}
	@Test
	@Transactional
	public void shouldSaveAndRetrieveListChronologicalOrder()
	{
		List<String> text = List.of("Lorem ipsum dolor sit amet",
				"consectetur adipiscing elit",
				"sed do eiusmod tempor incididunt",
				"ut labore et dolore magna aliqua",
				"Ut enim ad minim veniam",
				"quis nostrud exercitation",
				"ullamco laboris nisi ut",
				"aliquip ex ea commodo consequat");
		
		ChronologicalOrder co = new ChronologicalOrder(UUID.randomUUID(),
				"Test instruction", List.of(),
				text, 1);
		
		ChronologicalOrder co2 = coDAO.saveAndFlush(co);
		coDAO.flush();
		ChronologicalOrder co3 = coDAO.findById(co.getId()).orElse(null);
		
		assertEquals(co, co2);
		assertEquals(co2, co3);
		assertEquals(co, co3);
	}
	@Test
	@Transactional
	public void shouldSaveAndRetrieveListSentenceForming()
	{
		List<List<String>> text = List.of(
				List.of("Lorem ", " ipsum ", " dolor"),
				List.of("sit ", " amet"),
				List.of("consectetur adipiscing  ", " elit"),
				List.of("sed ", " do"));
		
		List<SentenceFormingElement> wordFillElemList = text.stream()
				.map(textList -> new SentenceFormingElement(UUID.randomUUID(), textList))
				.collect(Collectors.toList());
		
		ListSentenceForming lsf = new ListSentenceForming(UUID.randomUUID(),
				"Test instruction", List.of(),
				wordFillElemList, 1);
		
		sfeDAO.saveAll(lsf.getRows());
		sfeDAO.flush();
		ListSentenceForming lsf2 = lsfDAO.saveAndFlush(lsf);
		lsfDAO.flush();
		ListSentenceForming lsf3 = lsfDAO.findById(lsf.getId()).orElse(null);
		
		assertEquals(lsf, lsf2);
		assertEquals(lsf2, lsf3);
		assertEquals(lsf, lsf3);
	}
	@Test
	@Transactional
	public void shouldSaveAndRetrieveSingleChoice()
	{
		String content = "Lorem ipsum dolor sit amet";
		String answer = "consectetur";
		List<String> incorrectAnswers = List.of(
				"adipiscing", "elit", "sed");
		
		SingleChoice sc = new SingleChoice(UUID.randomUUID(),
				"Test instruction", List.of(),
				content, answer, incorrectAnswers, 1);
		
		SingleChoice sc2 = scDAO.saveAndFlush(sc);
		scDAO.flush();
		SingleChoice sc3 = scDAO.findById(sc.getId()).orElse(null);
		
		assertEquals(sc, sc2);
		assertEquals(sc2, sc3);
		assertEquals(sc, sc3);
	}
	@Test
	@Transactional
	public void shouldSaveAndRetrieveMultipleChoice()
	{
		String content = "Lorem ipsum dolor sit amet";
		List<String> correctAnswers = List.of(
				"eiusmod", "tempor", "incididunt ut");
		List<String> incorrectAnswers = List.of(
				"adipiscing", "elit", "sed", "labore", "et dolore");
		
		MultipleChoice mc = new MultipleChoice(UUID.randomUUID(),
				"Test instruction", List.of(),
				new MultipleChoiceElement(UUID.randomUUID(),
						content, correctAnswers, incorrectAnswers), 1);
		
		mceDAO.save(mc.getContent());
		mceDAO.flush();
		MultipleChoice mc2 = mcDAO.saveAndFlush(mc);
		mcDAO.flush();
		MultipleChoice mc3 = mcDAO.findById(mc.getId()).orElse(null);
		
		assertEquals(mc, mc2);
		assertEquals(mc2, mc3);
		assertEquals(mc, mc3);
	}
	@Test
	@Transactional
	public void shouldSaveAndRetrieveWordConnect()
	{
		List<String> leftWords = List.of("Lorem", "ipsum", "dolor", "sit", "amet");
		List<String> rightWords = List.of("consectetur", "adipiscing", "elit", "sed do", "eiusmod");
		Map<Integer, Integer> correctMapping = Map.of(
				0, 3,
				1, 0,
				2, 4,
				3, 2,
				4, 1);
		
		WordConnect wc = new WordConnect(UUID.randomUUID(),
				"Test instruction", List.of(),
				leftWords, rightWords, correctMapping, 1);
		
		WordConnect wc2 = wcDAO.saveAndFlush(wc);
		wcDAO.flush();
		WordConnect wc3 = wcDAO.findById(wc.getId()).orElse(null);
		
		assertEquals(wc, wc2);
		assertEquals(wc2, wc3);
		assertEquals(wc, wc3);
	}
}
