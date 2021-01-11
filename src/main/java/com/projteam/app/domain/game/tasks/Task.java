package com.projteam.app.domain.game.tasks;

import com.projteam.app.domain.game.tasks.answers.TaskAnswer;
import com.projteam.app.dto.game.tasks.TaskInfoDTO;

public interface Task
{
	public double getDifficulty();
	public double acceptAnswer(TaskAnswer answer);
	public Class<? extends TaskAnswer> getAnswerType();
	public TaskInfoDTO toDTO(int taskNumber);
}
