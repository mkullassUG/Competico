package com.projteam.app.domain.game.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import com.projteam.app.domain.game.tasks.answers.ChoiceWordFillAnswer;
import com.projteam.app.domain.game.tasks.answers.ChronologicalOrderAnswer;
import com.projteam.app.domain.game.tasks.answers.ListChoiceWordFillAnswer;
import com.projteam.app.domain.game.tasks.answers.ListSentenceFormingAnswer;
import com.projteam.app.domain.game.tasks.answers.ListWordFillAnswer;
import com.projteam.app.domain.game.tasks.answers.MultipleChoiceAnswer;
import com.projteam.app.domain.game.tasks.answers.SingleChoiceAnswer;
import com.projteam.app.domain.game.tasks.answers.WordConnectAnswer;
import com.projteam.app.domain.game.tasks.answers.WordFillAnswer;

public class AnswerGettersSettersTests
{
	@Test
	public void wordFillAnswerDataTests()
	{
		List<String> answers = List.of("Lorem", "ipsum", "dolor", "sit", "amet");
		WordFillAnswer wfa = new WordFillAnswer(List.of());
		wfa.setAnswers(answers);
		assertEquals(wfa.getAnswers(), answers);
		assertEquals(wfa, wfa);
		assertNotEquals(wfa, null);
		assertNotNull(wfa.toString());
	}
	@Test
	public void choiceWordFillAnswerDataTests()
	{
		List<String> answers = List.of("Lorem", "ipsum", "dolor", "sit", "amet");
		ChoiceWordFillAnswer cwfa = new ChoiceWordFillAnswer(List.of());
		cwfa.setAnswers(answers);
		assertEquals(cwfa.getAnswers(), answers);
		assertEquals(cwfa, cwfa);
		assertNotEquals(cwfa, null);
		assertNotNull(cwfa.toString());
	}
	@Test
	public void listWordFillAnswerDataTests()
	{
		List<List<String>> answers = List.of(
				List.of("Lorem", "ipsum"),
				List.of("dolor"),
				List.of("sit"),
				List.of("amet"));
		ListWordFillAnswer lwfa = new ListWordFillAnswer(List.of());
		lwfa.setAnswers(answers);
		assertEquals(lwfa.getAnswers(), answers);
		assertEquals(lwfa, lwfa);
		assertNotEquals(lwfa, null);
		assertNotNull(lwfa.toString());
	}
	@Test
	public void listChoiceWordFillAnswerDataTests()
	{
		List<List<String>> answers = List.of(
				List.of("Lorem", "ipsum"),
				List.of("dolor"),
				List.of("sit"),
				List.of("amet"));
		ListChoiceWordFillAnswer lcwfa = new ListChoiceWordFillAnswer(List.of());
		lcwfa.setAnswers(answers);
		assertEquals(lcwfa.getAnswers(), answers);
		assertEquals(lcwfa, lcwfa);
		assertNotEquals(lcwfa, null);
		assertNotNull(lcwfa.toString());
	}
	@Test
	public void chronologicalOrderAnswerDataTests()
	{
		List<String> answers = List.of("Lorem", "ipsum", "dolor", "sit", "amet");
		ChronologicalOrderAnswer coa = new ChronologicalOrderAnswer(List.of());
		coa.setAnswers(answers);
		assertEquals(coa.getAnswers(), answers);
		assertEquals(coa, coa);
		assertNotEquals(coa, null);
		assertNotNull(coa.toString());
	}
	@Test
	public void listSentenceFormingAnswerDataTests()
	{
		List<List<String>> answers = List.of(
				List.of("Lorem", "ipsum"),
				List.of("dolor"),
				List.of("sit"),
				List.of("amet"));
		ListSentenceFormingAnswer lsfa = new ListSentenceFormingAnswer(List.of());
		lsfa.setAnswers(answers);
		assertEquals(lsfa.getAnswers(), answers);
		assertEquals(lsfa, lsfa);
		assertNotEquals(lsfa, null);
		assertNotNull(lsfa.toString());
	}
	@Test
	public void singleChoiceAnswerDataTests()
	{
		String answer = "Lorem ipsum dolor sit amet";
		SingleChoiceAnswer sca = new SingleChoiceAnswer("");
		sca.setAnswer(answer);
		assertEquals(sca.getAnswer(), answer);
		assertEquals(sca, sca);
		assertNotEquals(sca, null);
		assertNotNull(sca.toString());
	}
	@Test
	public void multipleChoiceAnswerDataTests()
	{
		List<String> answers = List.of("Lorem", "ipsum", "dolor", "sit", "amet");
		MultipleChoiceAnswer mca = new MultipleChoiceAnswer(List.of());
		mca.setAnswers(answers);
		assertEquals(mca.getAnswers(), answers);
		assertEquals(mca, mca);
		assertNotEquals(mca, null);
		assertNotNull(mca.toString());
	}
	@Test
	public void wordConnectAnswerDataTests()
	{
		Map<String, String> answerMapping = Map.of(
				"Lorem", "ipsum",
				"dolor", "sit",
				"amet", "consectetur",
				"adipiscing", "elit",
				"sed", "do");
		WordConnectAnswer wca = new WordConnectAnswer(Map.of());
		wca.setAnswerMapping(answerMapping);
		assertEquals(wca.getAnswerMapping(), answerMapping);
		assertEquals(wca, wca);
		assertNotEquals(wca, null);
		assertNotNull(wca.toString());
	}
}
