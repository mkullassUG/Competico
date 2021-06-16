package com.projteam.competico.dto.game.tasks.create;

import java.util.List;

public interface TaskDTO
{
	public String getInstruction();
	public List<String> getTags();
	public double getDifficulty();
}
