package com.projteam.app.domain.game.tasks;

import com.projteam.app.domain.game.tasks.answers.TaskAnswer;

public interface Task
{
	public double getDifficulty();
	public double acceptAnswer(TaskAnswer answer);
}