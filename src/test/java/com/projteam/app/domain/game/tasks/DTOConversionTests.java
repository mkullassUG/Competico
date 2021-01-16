package com.projteam.app.domain.game.tasks;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projteam.app.domain.game.tasks.ChoiceWordFillElement.WordChoice;
import com.projteam.app.domain.game.tasks.WordFillElement.EmptySpace;
import com.projteam.app.dto.game.tasks.TaskInfoDTO;

public class DTOConversionTests
{
	@Test
	void wordFillConvertsToDTO()
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
		TaskInfoDTO ti = wf.toDTO(0);
		assertNotNull(ti);
		assertDoesNotThrow(() -> new ObjectMapper().valueToTree(ti));
	}
	@Test
	void choiceWordFillConvertsToDTO()
	{
		List<String> text = List.of("Lorem ", " ipsum ", " dolor ", " sit ", " amet");
		List<String> answers = List.of("abc", "def", "ghi", "jkl");
		List<WordChoice> wordChoices = answers
				.stream()
				.map(ans -> new WordChoice(UUID.randomUUID(), ans, List.of("qwr")))
				.collect(Collectors.toList());
		
		ChoiceWordFill cwf = new ChoiceWordFill(UUID.randomUUID(),
				new ChoiceWordFillElement(UUID.randomUUID(), text, wordChoices, false), 1);
		TaskInfoDTO ti = cwf.toDTO(0);
		assertNotNull(ti);
		assertDoesNotThrow(() -> new ObjectMapper().valueToTree(ti));
	}
	@Test
	void listWordFillConvertsToDTO()
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
		TaskInfoDTO ti = lwf.toDTO(0);
		assertNotNull(ti);
		assertDoesNotThrow(() -> new ObjectMapper().valueToTree(ti));
	}
	@Test
	void listChoiceWordFillConvertsToDTO()
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
		TaskInfoDTO ti = lcwf.toDTO(0);
		assertNotNull(ti);
		assertDoesNotThrow(() -> new ObjectMapper().valueToTree(ti));
	}
	@Test
	void chronologicalOrderConvertsToDTO()
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
		TaskInfoDTO ti = co.toDTO(0);
		assertNotNull(ti);
		assertDoesNotThrow(() -> new ObjectMapper().valueToTree(ti));
	}
	@Test
	void listSentenceFormingConvertsToDTO()
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
		TaskInfoDTO ti = lsf.toDTO(0);
		assertNotNull(ti);
		assertDoesNotThrow(() -> new ObjectMapper().valueToTree(ti));
	}
	@Test
	void singleChoiceConvertsToDTO()
	{
		String content = "Lorem ipsum dolor sit amet";
		String answer = "consectetur";
		List<String> incorrectAnswers = List.of(
				"adipiscing", "elit", "sed");
		
		SingleChoice sc = new SingleChoice(UUID.randomUUID(), content, answer, incorrectAnswers, 1);
		TaskInfoDTO ti = sc.toDTO(0);
		assertNotNull(ti);
		assertDoesNotThrow(() -> new ObjectMapper().valueToTree(ti));
	}
	@Test
	void multipleChoiceConvertsToDTO()
	{
		String content = "Lorem ipsum dolor sit amet";
		List<String> correctAnswers = List.of(
				"eiusmod", "tempor", "incididunt ut");
		List<String> incorrectAnswers = List.of(
				"adipiscing", "elit", "sed", "labore", "et dolore");
		
		MultipleChoice mc = new MultipleChoice(UUID.randomUUID(),
				new MultipleChoiceElement(UUID.randomUUID(), content, correctAnswers, incorrectAnswers), 1);
		TaskInfoDTO ti = mc.toDTO(0);
		assertNotNull(ti);
		assertDoesNotThrow(() -> new ObjectMapper().valueToTree(ti));
	}
	@Test
	void wordConnectConvertsToDTO()
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
		TaskInfoDTO ti = wc.toDTO(0);
		assertNotNull(ti);
		assertDoesNotThrow(() -> new ObjectMapper().valueToTree(ti));
	}
}
