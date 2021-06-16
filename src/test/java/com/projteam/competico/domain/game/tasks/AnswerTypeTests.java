package com.projteam.competico.domain.game.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.projteam.competico.domain.game.tasks.answers.ChoiceWordFillAnswer;
import com.projteam.competico.domain.game.tasks.answers.ChronologicalOrderAnswer;
import com.projteam.competico.domain.game.tasks.answers.ListChoiceWordFillAnswer;
import com.projteam.competico.domain.game.tasks.answers.ListSentenceFormingAnswer;
import com.projteam.competico.domain.game.tasks.answers.ListWordFillAnswer;
import com.projteam.competico.domain.game.tasks.answers.OptionSelectAnswer;
import com.projteam.competico.domain.game.tasks.answers.WordConnectAnswer;
import com.projteam.competico.domain.game.tasks.answers.WordFillAnswer;

public class AnswerTypeTests
{
	@Test
	public void wordFillConvertsToDTO()
	{
		WordFill wf = new WordFill();
		assertEquals(wf.getAnswerType(), WordFillAnswer.class);
	}
	@Test
	public void choiceWordFillConvertsToDTO()
	{
		ChoiceWordFill cwf = new ChoiceWordFill();
		assertEquals(cwf.getAnswerType(), ChoiceWordFillAnswer.class);
	}
	@Test
	public void listWordFillConvertsToDTO()
	{
		ListWordFill lwf = new ListWordFill();
		assertEquals(lwf.getAnswerType(), ListWordFillAnswer.class);
	}
	@Test
	public void listChoiceWordFillConvertsToDTO()
	{
		ListChoiceWordFill lcwf = new ListChoiceWordFill();
		assertEquals(lcwf.getAnswerType(), ListChoiceWordFillAnswer.class);
	}
	@Test
	public void chronologicalOrderConvertsToDTO()
	{
		ChronologicalOrder co = new ChronologicalOrder();
		assertEquals(co.getAnswerType(), ChronologicalOrderAnswer.class);
	}
	@Test
	public void listSentenceFormingConvertsToDTO()
	{
		ListSentenceForming lsf = new ListSentenceForming();
		assertEquals(lsf.getAnswerType(), ListSentenceFormingAnswer.class);
	}
	@Test
	public void optionSelectConvertsToDTO()
	{
		OptionSelect os = new OptionSelect();
		assertEquals(os.getAnswerType(), OptionSelectAnswer.class);
	}
	@Test
	public void wordConnectConvertsToDTO()
	{
		WordConnect wc = new WordConnect();
		assertEquals(wc.getAnswerType(), WordConnectAnswer.class);
	}
}
