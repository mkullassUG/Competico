package com.projteam.app.domain.game.tasks;

import java.util.List;
import java.util.UUID;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SentenceFormingElement
{
	private @Id UUID id;
	private @ElementCollection List<String> words;
}
