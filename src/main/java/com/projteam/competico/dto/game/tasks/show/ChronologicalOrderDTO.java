package com.projteam.competico.dto.game.tasks.show;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChronologicalOrderDTO implements TaskDTO
{
	private List<String> sentences;
	
	public ChronologicalOrderDTO(List<String> sentences)
	{
		this.sentences = new ArrayList<>(sentences);
		Collections.shuffle(this.sentences);
	}
}
