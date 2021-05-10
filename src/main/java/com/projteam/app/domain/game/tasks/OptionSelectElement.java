package com.projteam.app.domain.game.tasks;

import java.util.List;
import java.util.UUID;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import com.projteam.app.utils.Initializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Access(AccessType.FIELD)
public class OptionSelectElement implements Initializable
{
	private @Id UUID id;
	private String content;
	private @ElementCollection List<String> correctAnswers;
	private @ElementCollection List<String> incorrectAnswers;
	
	@Override
	public void initialize()
	{
		Initializable.initialize(correctAnswers, incorrectAnswers);
	}
}
