package com.projteam.competico.domain.game.tasks;

import java.util.List;
import java.util.UUID;
import com.projteam.competico.domain.game.tasks.answers.TaskAnswer;
import com.projteam.competico.dto.game.tasks.show.TaskInfoDTO;
import com.projteam.competico.utils.Initializable;

public interface Task extends Initializable
{
	public UUID getId();
	public void setId(UUID id);
	public String getInstruction();
	public double getDifficulty();
	public List<String> getTags();
	public double acceptAnswer(TaskAnswer answer);
	public Class<? extends TaskAnswer> getAnswerType();
	public TaskInfoDTO prepareTaskInfo(int currentTaskNumber, int taskCount);
}
