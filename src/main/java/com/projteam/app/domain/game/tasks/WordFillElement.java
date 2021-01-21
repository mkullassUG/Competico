package com.projteam.app.domain.game.tasks;

import java.util.List;
import java.util.UUID;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
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
public class WordFillElement implements Initializable
{
	private @Id UUID id;
	private @ElementCollection @OrderColumn List<String> text;
	private @ElementCollection @OrderColumn List<EmptySpace> emptySpaces;
	private boolean startWithText;
	private @ElementCollection List<String> possibleAnswers;
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Embeddable
	public static class EmptySpace implements Initializable
	{
		private String answer;
		
		@Override
		public void initialize()
		{}
	}

	public void initialize()
	{
		Initializable.initialize(text, possibleAnswers, emptySpaces);
	}
}
