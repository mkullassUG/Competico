package com.projteam.app.domain.game.tasks;

import java.util.List;
import java.util.UUID;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class WordFillElement
{
	private @Id UUID id;
	private @ElementCollection List<String> text;
	private @ElementCollection List<EmptySpace> emptySpaces;
	private boolean startWithText;
	private @ElementCollection List<String> possibleAnswers;
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Embeddable
	public static class EmptySpace
	{
		private String answer;
	}
}
