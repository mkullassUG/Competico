package com.projteam.app.domain.game.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

public class AnswerWrongDataTests
{
	@Test
	void wordFillRejectsWrongAnswer()
	{
		List<String> text = List.of("Lorem ", " ipsum ", " dolor ", " sit ", " amet");
		List<String> answers = List.of("abc", "def", "ghi", "jkl");
		List<EmptySpace> emptySpaces = answers
				.stream()
				.map(ans -> new EmptySpace(ans))
				.collect(Collectors.toList());
		List<String> possibleAnswers = List.of("abc", "def", "ghi", "jkl", "mno", "pqr");
		
		WordFill wf = new WordFill(UUID.randomUUID(),
				new WordFillElement(UUID.randomUUID(), text, emptySpaces, false, possibleAnswers), 1);
		WordFillAnswer wfa = new WordFillAnswer(answers.stream()
				.map(a -> a + "wrong")
				.collect(Collectors.toList()));
		
		assertEquals(wf.acceptAnswer(wfa), 0);
	}
	@Test
	void choiceWordFillRejectsWrongAnswer()
	{
		List<String> text = List.of("Lorem ", " ipsum ", " dolor ", " sit ", " amet");
		List<String> answers = List.of("abc", "def", "ghi", "jkl");
		List<WordChoice> wordChoices = answers
				.stream()
				.map(ans -> new WordChoice(UUID.randomUUID(), ans, List.of("qwr")))
				.collect(Collectors.toList());
		
		ChoiceWordFill cwf = new ChoiceWordFill(UUID.randomUUID(),
				new ChoiceWordFillElement(UUID.randomUUID(), text, wordChoices, false), 1);
		ChoiceWordFillAnswer cwfa = new ChoiceWordFillAnswer(answers.stream()
				.map(a -> a + "wrong")
				.collect(Collectors.toList()));
		
		assertEquals(cwf.acceptAnswer(cwfa), 0);
	}
	@Test
	void listWordFillRejectsWrongAnswer()
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
		
		ListWordFill lwf = new ListWordFill(UUID.randomUUID(), wordFillElemList, 1);
		ListWordFillAnswer lwfa = new ListWordFillAnswer(answers.stream()
				.map(aList -> aList.stream()
						.map(a -> a + "wrong")
						.collect(Collectors.toList()))
				.collect(Collectors.toList()));
		
		assertEquals(lwf.acceptAnswer(lwfa), 0);
	}
	@Test
	void listChoiceWordFillRejectsWrongAnswer()
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
		
		ListChoiceWordFill lcwf = new ListChoiceWordFill(UUID.randomUUID(), wordFillElemList, 1);
		ListChoiceWordFillAnswer lcwfa = new ListChoiceWordFillAnswer(answers.stream()
				.map(aList -> aList.stream()
						.map(a -> a + "wrong")
						.collect(Collectors.toList()))
				.collect(Collectors.toList()));
		
		assertEquals(lcwf.acceptAnswer(lcwfa), 0);
	}
	@Test
	void chronologicalOrderRejectsWrongAnswer()
	{
		List<String> text = List.of("Lorem ipsum dolor sit amet",
				"consectetur adipiscing elit",
				"sed do eiusmod tempor incididunt",
				"ut labore et dolore magna aliqua",
				"Ut enim ad minim veniam",
				"quis nostrud exercitation",
				"ullamco laboris nisi ut",
				"aliquip ex ea commodo consequat");
		
		ChronologicalOrder co = new ChronologicalOrder(UUID.randomUUID(), text, 1);
		ChronologicalOrderAnswer coa = new ChronologicalOrderAnswer(text.stream()
				.map(a -> a + "wrong")
				.collect(Collectors.toList()));
		
		assertEquals(co.acceptAnswer(coa), 0);
	}
	@Test
	void listSentenceFormingRejectsWrongAnswer()
	{
		List<List<String>> text = List.of(
				List.of("Lorem ", " ipsum ", " dolor"),
				List.of("sit ", " amet"),
				List.of("consectetur adipiscing  ", " elit"),
				List.of("sed ", " do"));
		
		List<SentenceFormingElement> wordFillElemList = text.stream()
				.map(textList -> new SentenceFormingElement(UUID.randomUUID(), textList))
				.collect(Collectors.toList());
		
		ListSentenceForming lsf = new ListSentenceForming(UUID.randomUUID(), wordFillElemList, 1);
		ListSentenceFormingAnswer lsfa = new ListSentenceFormingAnswer(text.stream()
				.map(aList -> aList.stream()
						.map(a -> a + "wrong")
						.collect(Collectors.toList()))
				.collect(Collectors.toList()));
		
		assertEquals(lsf.acceptAnswer(lsfa), 0);
	}
	@Test
	void singleChoiceRejectsWrongAnswer()
	{
		String content = "Lorem ipsum dolor sit amet";
		String answer = "consectetur";
		List<String> incorrectAnswers = List.of(
				"adipiscing", "elit", "sed");
		
		SingleChoice sc = new SingleChoice(UUID.randomUUID(), content, answer, incorrectAnswers, 1);
		SingleChoiceAnswer sca = new SingleChoiceAnswer(answer + "wrong");
		
		assertEquals(sc.acceptAnswer(sca), 0);
	}
	@Test
	void multipleChoiceRejectsWrongAnswer()
	{
		String content = "Lorem ipsum dolor sit amet";
		List<String> correctAnswers = List.of(
				"eiusmod", "tempor", "incididunt ut");
		List<String> incorrectAnswers = List.of(
				"adipiscing", "elit", "sed", "labore", "et dolore");
		
		MultipleChoice mc = new MultipleChoice(UUID.randomUUID(),
				new MultipleChoiceElement(UUID.randomUUID(), content, correctAnswers, incorrectAnswers), 1);
		MultipleChoiceAnswer mca = new MultipleChoiceAnswer(correctAnswers.stream()
				.map(a -> a + "wrong")
				.collect(Collectors.toList()));
		
		assertEquals(mc.acceptAnswer(mca), 0);
	}
	@Test
	void wordConnectRejectsWrongAnswer()
	{
		List<String> leftWords = List.of("Lorem", "ipsum", "dolor", "sit", "amet");
		List<String> rightWords = List.of("consectetur", "adipiscing", "elit", "sed do", "eiusmod");
		Map<Integer, Integer> correctMapping = Map.of(
				0, 3,
				1, 0,
				2, 4,
				3, 2,
				4, 1);
		
		WordConnect wc = new WordConnect(UUID.randomUUID(), leftWords, rightWords, correctMapping, 1);
		WordConnectAnswer wca = new WordConnectAnswer(correctMapping.entrySet()
				.stream()
				.collect(Collectors.toMap(Entry::getKey, e ->
					(e.getValue() >= correctMapping.size())?
						0:(e.getValue() + 1))));
		
		assertEquals(wc.acceptAnswer(wca), 0);
	}
}