package com.projteam.app.domain.game.tasks;

import java.util.List;
import java.util.UUID;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import com.projteam.app.utils.Initializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Access(AccessType.FIELD)
public class ChoiceWordFillElement implements Initializable
{
	private @Id UUID id;
	private @ElementCollection @OrderColumn List<String> text;
	private @ManyToMany @OrderColumn List<WordChoice> wordChoices;
	private boolean startWithText;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Entity
	@Access(AccessType.FIELD)
	public static class WordChoice implements Initializable
	{
		private @Id UUID id;
		private String correctAnswer;
		private @ElementCollection List<String> inncorrectAnswers;
		
		@Override
		public void initialize()
		{
			Initializable.initialize(inncorrectAnswers);
		}
	}
	@Override
	public void initialize()
	{
		Initializable.initialize(text, wordChoices);
	}
}