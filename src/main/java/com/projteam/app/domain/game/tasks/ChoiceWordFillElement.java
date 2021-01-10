package com.projteam.app.domain.game.tasks;

import java.util.List;
import java.util.UUID;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ChoiceWordFillElement
{
	private @Id UUID id;
	private @ElementCollection List<String> text;
	private @ManyToMany List<WordChoice> wordChoices;
	private boolean startWithText;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Entity
	public static class WordChoice
	{
		private @Id UUID id;
		private String correctAnswer;
		private @ElementCollection List<String> inncorrectAnswers;
	}
}