package com.projteam.competico.service.game.tasks;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.projteam.competico.dao.game.tasks.WordConnectDAO;
import com.projteam.competico.domain.game.tasks.Task;
import com.projteam.competico.domain.game.tasks.WordConnect;
import com.projteam.competico.utils.Initializable;

@Service
public class WordConnectService implements TaskService
{
	private WordConnectDAO wcDao;
	
	public WordConnectService(WordConnectDAO wcDao)
	{
		this.wcDao = wcDao;
	}

	@Override
	@Transactional
	public boolean genericExistsById(UUID taskId)
	{
		return wcDao.existsById(taskId);
	}
	@Override
	@Transactional
	public Task genericFindById(UUID taskId)
	{
		return findById(taskId);
	}
	private WordConnect findById(UUID taskId)
	{
		return wcDao.findById(taskId)
				.map(Initializable::init)
				.orElse(null);
	}
	@Override
	@Transactional
	public long count()
	{
		return wcDao.count();
	}
	@Override
	@Transactional
	public List<Task> genericFindAll()
	{
		return wcDao.findAll()
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
		WordConnect newTask = (WordConnect) task;
		WordConnect oldTask = findById(taskId);
		
		oldTask.setCorrectMapping(newTask.getCorrectMapping());
		oldTask.setDifficulty(newTask.getDifficulty());
		oldTask.setInstruction(newTask.getInstruction());
		oldTask.setLeftWords(newTask.getLeftWords());
		oldTask.setRightWords(newTask.getRightWords());
		oldTask.setTags(newTask.getTags());
		
		wcDao.save(oldTask);
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
		wcDao.flush();
	}
	private void save(Task task, boolean flush)
	{
		ensureApplicable(task);
		WordConnect wc = (WordConnect) task;
		if (flush)
			wcDao.saveAndFlush(wc);
		else
			wcDao.save(wc);
	}
	@Override
	@Transactional
	public void genericDeleteById(UUID id)
	{
		wcDao.deleteById(id);
	}
	@Override
	public boolean canAccept(Task task)
	{
		return task instanceof WordConnect;
	}
	
	private void ensureApplicable(Task task)
	{
		if (!canAccept(task))
			throw new IllegalArgumentException("Invalid task for this service: " + task
					.getClass().getTypeName());
	}
}