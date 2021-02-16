package com.projteam.app.domain.game.tasks;

import java.util.List;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;
import com.projteam.app.dto.game.tasks.show.TaskInfoDTO;
import com.projteam.app.utils.Initializable;

public interface Task extends Initializable
{
	public String getInstruction();
	public double getDifficulty();
	public List<String> getTags();
	public double acceptAnswer(TaskAnswer answer);
	public Class<? extends TaskAnswer> getAnswerType();
	public TaskInfoDTO prepareTaskInfo(int currentTaskNumber, int taskCount);
}
