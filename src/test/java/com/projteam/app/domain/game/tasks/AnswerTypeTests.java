package com.projteam.app.domain.game.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

public class AnswerTypeTests
{
	@Test
	void wordFillConvertsToDTO()
	{
		WordFill wf = new WordFill();
		assertEquals(wf.getAnswerType(), WordFillAnswer.class);
	}
	@Test
	void choiceWordFillConvertsToDTO()
	{
		ChoiceWordFill cwf = new ChoiceWordFill();
		assertEquals(cwf.getAnswerType(), ChoiceWordFillAnswer.class);
	}
	@Test
	void listWordFillConvertsToDTO()
	{
		ListWordFill lwf = new ListWordFill();
		assertEquals(lwf.getAnswerType(), ListWordFillAnswer.class);
	}
	@Test
	void listChoiceWordFillConvertsToDTO()
	{
		ListChoiceWordFill lcwf = new ListChoiceWordFill();
		assertEquals(lcwf.getAnswerType(), ListChoiceWordFillAnswer.class);
	}
	@Test
	void chronologicalOrderConvertsToDTO()
	{
		ChronologicalOrder co = new ChronologicalOrder();
		assertEquals(co.getAnswerType(), ChronologicalOrderAnswer.class);
	}
	@Test
	void listSentenceFormingConvertsToDTO()
	{
		ListSentenceForming lsf = new ListSentenceForming();
		assertEquals(lsf.getAnswerType(), ListSentenceFormingAnswer.class);
	}
	@Test
	void singleChoiceConvertsToDTO()
	{
		SingleChoice sc = new SingleChoice();
		assertEquals(sc.getAnswerType(), SingleChoiceAnswer.class);
	}
	@Test
	void multipleChoiceConvertsToDTO()
	{
		MultipleChoice mc = new MultipleChoice();
		assertEquals(mc.getAnswerType(), MultipleChoiceAnswer.class);
	}
	@Test
	void wordConnectConvertsToDTO()
	{
		WordConnect wc = new WordConnect();
		assertEquals(wc.getAnswerType(), WordConnectAnswer.class);
	}
}
