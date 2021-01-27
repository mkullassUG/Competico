package com.projteam.app.dto.game.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.projteam.app.domain.game.tasks.WordConnect;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WordConnectDTO implements TaskDTO
{
	private List<String> leftWords;
	private List<String> rightWords;

	public WordConnectDTO(WordConnect task)
	{
		leftWords = shuffle(task.getLeftWords());
		rightWords = shuffle(task.getRightWords());
		
	}
	private static <T> List<T> shuffle(List<T> list)
	{
		List<T> ret = new ArrayList<>(list);
		Collections.shuffle(ret);
		return ret;
	}
}
