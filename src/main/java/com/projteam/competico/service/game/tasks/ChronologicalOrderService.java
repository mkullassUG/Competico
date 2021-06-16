package com.projteam.competico.service.game.tasks;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.projteam.competico.dao.game.tasks.ChronologicalOrderDAO;
import com.projteam.competico.domain.game.tasks.ChronologicalOrder;
import com.projteam.competico.domain.game.tasks.Task;
import com.projteam.competico.utils.Initializable;

@Service
public class ChronologicalOrderService implements TaskService
{
	private ChronologicalOrderDAO coDao;
	
	public ChronologicalOrderService(ChronologicalOrderDAO coDao)
	{
		this.coDao = coDao;
	}

	@Override
	@Transactional
	public boolean genericExistsById(UUID taskId)
	{
		return coDao.existsById(taskId);
	}
	@Override
	@Transactional
	public Task genericFindById(UUID taskId)
	{
		return findById(taskId);
	}
	private ChronologicalOrder findById(UUID taskId)
	{
		return coDao.findById(taskId)
				.map(Initializable::init)
				.orElse(null);
	}
	@Override
	@Transactional
	public long count()
	{
		return coDao.count();
	}
	@Override
	@Transactional
	public List<Task> genericFindAll()
	{
		return coDao.findAll()
				.stream()
				.map(t -> Initializable.init(t))
				.collect(Collectors.toList());
	}
	@Override
	@Transactional
	public void genericReplace(UUID taskId, Task task)
	{
		replace(taskId, task, false);
	}
	@Override
	@Transactional
	public void genericReplaceAndFlush(UUID taskId, Task task)
	{
		replace(taskId, task, true);
	}
	private void replace(UUID taskId, Task task, boolean flush)
	{
		ensureApplicable(task);
		ChronologicalOrder newTask = (ChronologicalOrder) task;
		ChronologicalOrder oldTask = findById(taskId);
		
		oldTask.setDifficulty(newTask.getDifficulty());
		oldTask.setInstruction(newTask.getInstruction());
		oldTask.setSentences(newTask.getSentences());
		oldTask.setTags(newTask.getTags());
		
		if (flush)
			coDao.saveAndFlush(oldTask);
		else
			coDao.save(oldTask);
	}
	@Override
	@Transactional
	public void genericSave(Task task)
	{
		save(task, false);
	}
	@Override
	@Transactional
	public void genericSaveAndFlush(Task task)
	{
		save(task, true);
	}
	@Override
	@Transactional
	public void flush()
	{
		coDao.flush();
	}
	private void save(Task task, boolean flush)
	{
		ensureApplicable(task);
		if (flush)
			coDao.saveAndFlush((ChronologicalOrder) task);
		else
			coDao.save((ChronologicalOrder) task);
	}
	@Override
	@Transactional
	public void genericDeleteById(UUID id)
	{
		coDao.deleteById(id);
	}
	@Override
	public boolean canAccept(Task task)
	{
		return task instanceof ChronologicalOrder;
	}
	
	private void ensureApplicable(Task task)
	{
		if (!canAccept(task))
			throw new IllegalArgumentException("Invalid task for this service: " + task
					.getClass().getTypeName());
	}
}