package com.projteam.app.dto.game.tasks;

import java.util.List;
import com.projteam.app.domain.game.tasks.WordConnect;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordConnectDTO implements TaskDTO
{
	private List<String> leftWords;
	private List<String> rightWords;

	public WordConnectDTO(WordConnect task)
	{
		leftWords = task.getLeftWords();
		rightWords = task.getRightWords();
	}
}
