package com.projteam.app.domain.game.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import com.projteam.app.domain.game.tasks.ChoiceWordFillElement.WordChoice;
import com.projteam.app.domain.game.tasks.WordFillElement.EmptySpace;
import com.projteam.app.domain.game.tasks.answers.ChoiceWordFillAnswer;
import com.projteam.app.domain.game.tasks.answers.ChronologicalOrderAnswer;
import com.projteam.app.domain.game.tasks.answers.ListChoiceWordFillAnswer;
import com.projteam.app.domain.game.tasks.answers.ListSentenceFormingAnswer;
import com.projteam.app.domain.game.tasks.answers.ListWordFillAnswer;
import com.projteam.app.domain.game.tasks.answers.MultipleChoiceAnswer;
import com.projteam.app.domain.game.tasks.answers.SingleChoiceAnswer;
import com.projteam.app.domain.game.tasks.answers.WordConnectAnswer;
import com.projteam.app.domain.game.tasks.answers.WordFillAnswer;

public class AnswerAcceptTests
{
	@Test
	public void wordFillAcceptsAnswer()
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
				new WordFillElement(UUID.randomUUID(), text, emptySpaces, false, possibleAnswers), 100);
		WordFillAnswer wfa = new WordFillAnswer(answers);
		
		assertEquals(wf.acceptAnswer(wfa), 1);
	}
	@Test
	public void choiceWordFillAcceptsAnswer()
	{
		List<String> text = List.of("Lorem ", " ipsum ", " dolor ", " sit ", " amet");
		List<String> answers = List.of("abc", "def", "ghi", "jkl");
		List<WordChoice> wordChoices = answers
				.stream()
				.map(ans -> new WordChoice(UUID.randomUUID(), ans, List.of("qwr")))
				.collect(Collectors.toList());
		
		ChoiceWordFill cwf = new ChoiceWordFill(UUID.randomUUID(), 
				"Test instruction", List.of(),
				new ChoiceWordFillElement(UUID.randomUUID(), text, wordChoices, false), 100);
		ChoiceWordFillAnswer cwfa = new ChoiceWordFillAnswer(answers);
		
		assertEquals(cwf.acceptAnswer(cwfa), 1);
	}
	@Test
	public void listWordFillAcceptsAnswer()
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
				wordFillElemList, 100);
		ListWordFillAnswer lwfa = new ListWordFillAnswer(answers);
		
		assertEquals(lwf.acceptAnswer(lwfa), 1);
	}
	@Test
	public void listChoiceWordFillAcceptsAnswer()
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
				wordFillElemList, 100);
		ListChoiceWordFillAnswer lcwfa = new ListChoiceWordFillAnswer(answers);
		
		assertEquals(lcwf.acceptAnswer(lcwfa), 1);
	}
	@Test
	public void chronologicalOrderAcceptsAnswer()
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
				text, 100);
		ChronologicalOrderAnswer coa = new ChronologicalOrderAnswer(text);
		
		assertEquals(co.acceptAnswer(coa), 1);
	}
	@Test
	public void listSentenceFormingAcceptsAnswer()
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
				wordFillElemList, 100);
		ListSentenceFormingAnswer lsfa = new ListSentenceFormingAnswer(text);
		
		assertEquals(lsf.acceptAnswer(lsfa), 1);
	}
	@Test
	public void singleChoiceAcceptsAnswer()
	{
		String content = "Lorem ipsum dolor sit amet";
		String answer = "consectetur";
		List<String> incorrectAnswers = List.of(
				"adipiscing", "elit", "sed");
		
		SingleChoice sc = new SingleChoice(UUID.randomUUID(),
				"Test instruction", List.of(),
				content, answer, incorrectAnswers, 100);
		SingleChoiceAnswer sca = new SingleChoiceAnswer(answer);
		
		assertEquals(sc.acceptAnswer(sca), 1);
	}
	@Test
	public void multipleChoiceAcceptsAnswer()
	{
		String content = "Lorem ipsum dolor sit amet";
		List<String> correctAnswers = List.of(
				"eiusmod", "tempor", "incididunt ut");
		List<String> incorrectAnswers = List.of(
				"adipiscing", "elit", "sed", "labore", "et dolore");
		
		MultipleChoice mc = new MultipleChoice(UUID.randomUUID(),
				"Test instruction", List.of(),
				new MultipleChoiceElement(UUID.randomUUID(), content, correctAnswers, incorrectAnswers), 100);
		MultipleChoiceAnswer mca = new MultipleChoiceAnswer(correctAnswers);
		
		assertEquals(mc.acceptAnswer(mca), 1);
	}
	@Test
	public void wordConnectAcceptsAnswer()
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
				leftWords, rightWords, correctMapping, 100);
		WordConnectAnswer wca = new WordConnectAnswer(correctMapping
				.entrySet()
				.stream()
				.collect(Collectors.toMap(
						e -> leftWords.get(e.getKey()),
						e -> rightWords.get(e.getValue()))));
		
		assertEquals(wc.acceptAnswer(wca), 1);
	}
}
