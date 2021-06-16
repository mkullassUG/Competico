package com.projteam.competico.service.game.tasks;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.projteam.competico.dao.game.tasks.ListWordFillDAO;
import com.projteam.competico.dao.game.tasks.WordFillElementDAO;
import com.projteam.competico.domain.game.tasks.ListWordFill;
import com.projteam.competico.domain.game.tasks.Task;
import com.projteam.competico.domain.game.tasks.WordFillElement;
import com.projteam.competico.utils.Initializable;

@Service
public class ListWordFillService implements TaskService
{
	private ListWordFillDAO lwfDao;
	private WordFillElementDAO wfeDao;
	
	public ListWordFillService(ListWordFillDAO lwfDao,
			WordFillElementDAO wfeDao)
	{
		this.lwfDao = lwfDao;
		this.wfeDao = wfeDao;
	}
	
	@Override
	@Transactional
	public boolean genericExistsById(UUID taskId)
	{
		return lwfDao.existsById(taskId);
	}
	@Override
	@Transactional
	public Task genericFindById(UUID taskId)
	{
		return findById(taskId);
	}
	private ListWordFill findById(UUID taskId)
	{
		return lwfDao.findById(taskId)
				.map(Initializable::init)
				.orElse(null);
	}
	@Override
	@Transactional
	public long count()
	{
		return lwfDao.count();
	}
	@Override
	@Transactional
	public List<Task> genericFindAll()
	{
		return lwfDao.findAll()
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
		ListWordFill newTask = (ListWordFill) task;
		ListWordFill oldTask = findById(taskId);
		
		wfeDao.deleteAll(oldTask.getRows());
		oldTask.setRows(newTask.getRows());
		oldTask.setDifficulty(newTask.getDifficulty());
		oldTask.setInstruction(newTask.getInstruction());
		oldTask.setTags(newTask.getTags());
		wfeDao.saveAll(oldTask.getRows());
		if (flush)
		{
			wfeDao.flush();
			lwfDao.saveAndFlush(oldTask);
		}
		else
			lwfDao.save(oldTask);
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
		wfeDao.flush();
		lwfDao.flush();
	}
	private void save(Task task, boolean flush)
	{
		ensureApplicable(task);
		ListWordFill lwf = (ListWordFill) task;
		if (flush)
		{
			wfeDao.saveAll(lwf.getRows());
			wfeDao.flush();
			lwfDao.saveAndFlush(lwf);
		}
		else
		{
			wfeDao.saveAll(lwf.getRows());
			lwfDao.save(lwf);
		}
	}
	@Override
	@Transactional
	public void genericDeleteById(UUID id)
	{
		ListWordFill lwf = lwfDao.findById(id).orElse(null);
		if (lwf == null)
			return;
		List<WordFillElement> wfes = lwf.getRows();
		lwfDao.deleteById(id);
		wfes.forEach(wfe -> wfeDao.deleteById(wfe.getId()));
	}
	@Override
	public boolean canAccept(Task task)
	{
		return task instanceof ListWordFill;
	}
	
	private void ensureApplicable(Task task)
	{
		if (!canAccept(task))
			throw new IllegalArgumentException("Invalid task for this service: " + task
					.getClass().getTypeName());
	}
}