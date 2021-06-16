package com.projteam.competico.service.game.tasks;

import java.util.List;
import java.util.UUID;
import com.projteam.competico.domain.game.tasks.Task;

public interface TaskService
{
	public boolean genericExistsById(UUID taskId);
	public Task genericFindById(UUID taskId);
	public void genericSave(Task task);
	public void genericSaveAndFlush(Task task);
	public List<Task> genericFindAll();
	public long count();
	public void flush();
	public boolean canAccept(Task task);
	public void genericDeleteById(UUID id);
	public void genericReplace(UUID id, Task task);
	public void genericReplaceAndFlush(UUID id, Task task);
}
