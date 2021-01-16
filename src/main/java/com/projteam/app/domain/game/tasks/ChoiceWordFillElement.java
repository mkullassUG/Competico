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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Access(AccessType.FIELD)
public class ChoiceWordFillElement
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
	public static class WordChoice
	{
		private @Id UUID id;
		private String correctAnswer;
		private @ElementCollection List<String> inncorrectAnswers;
	}
}