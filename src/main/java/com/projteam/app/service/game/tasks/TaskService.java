package com.projteam.app.service.game.tasks;

import java.util.List;
import java.util.Random;
import com.projteam.app.domain.game.tasks.Task;

public interface TaskService
{
	public boolean genericExistsById(Task task);
	public void genericSave(Task task);
	public List<Task> genericFindAll();
	public Task genericFindRandom(Random r);
	public long count();
	public boolean canAccept(Task task);
}
