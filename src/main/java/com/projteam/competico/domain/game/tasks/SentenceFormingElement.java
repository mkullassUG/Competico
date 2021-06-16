package com.projteam.competico.domain.game.tasks;

import java.util.List;
import java.util.UUID;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OrderColumn;
import com.projteam.competico.utils.Initializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Access(AccessType.FIELD)
public class SentenceFormingElement implements Initializable
{
	private @Id UUID id;
	private @ElementCollection @OrderColumn List<String> words;
	
	@Override
	public void initialize()
	{
		Initializable.initialize(words);
	}
}
