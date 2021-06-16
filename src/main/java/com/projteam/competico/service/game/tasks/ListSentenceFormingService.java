package com.projteam.competico.service.game.tasks;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.projteam.competico.dao.game.tasks.ListSentenceFormingDAO;
import com.projteam.competico.dao.game.tasks.SentenceFormingElementDAO;
import com.projteam.competico.domain.game.tasks.ListSentenceForming;
import com.projteam.competico.domain.game.tasks.SentenceFormingElement;
import com.projteam.competico.domain.game.tasks.Task;
import com.projteam.competico.utils.Initializable;

@Service
public class ListSentenceFormingService implements TaskService
{
	private ListSentenceFormingDAO lsfDao;
	private SentenceFormingElementDAO sfeDao;
	
	public ListSentenceFormingService(ListSentenceFormingDAO lsfDao,
			SentenceFormingElementDAO sfeDao)
	{
		this.lsfDao = lsfDao;
		this.sfeDao = sfeDao;
	}
	
	@Override
	@Transactional
	public boolean genericExistsById(UUID taskId)
	{
		return lsfDao.existsById(taskId);
	}
	@Override
	@Transactional
	public Task genericFindById(UUID taskId)
	{
		return findById(taskId);
	}
	private ListSentenceForming findById(UUID taskId)
	{
		return lsfDao.findById(taskId)
				.map(Initializable::init)
				.orElse(null);
	}
	@Override
	@Transactional
	public long count()
	{
		return lsfDao.count();
	}
	@Override
	@Transactional
	public List<Task> genericFindAll()
	{
		return lsfDao.findAll()
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
		ListSentenceForming newTask = (ListSentenceForming) task;
		ListSentenceForming oldTask = findById(taskId);
		
		sfeDao.deleteAll(oldTask.getRows());
		oldTask.setRows(newTask.getRows());
		oldTask.setDifficulty(newTask.getDifficulty());
		oldTask.setInstruction(newTask.getInstruction());
		oldTask.setTags(newTask.getTags());
		sfeDao.saveAll(oldTask.getRows());
		
		if (flush)
		{
			sfeDao.flush();
			lsfDao.saveAndFlush(oldTask);
		}
		else
			lsfDao.save(oldTask);
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
		sfeDao.flush();
		lsfDao.flush();
	}
	private void save(Task task, boolean flush)
	{
		ensureApplicable(task);
		ListSentenceForming lsf = (ListSentenceForming) task;
		if (flush)
		{
			sfeDao.saveAll(lsf.getRows());
			sfeDao.flush();
			lsfDao.saveAndFlush(lsf);
		}
		else
		{
			sfeDao.saveAll(lsf.getRows());
			lsfDao.save(lsf);
		}
	}
	@Override
	@Transactional
	public void genericDeleteById(UUID id)
	{
		ListSentenceForming lsf = lsfDao.findById(id).orElse(null);
		if (lsf == null)
			return;
		List<SentenceFormingElement> sfes = lsf.getRows();
		lsfDao.deleteById(id);
		sfes.forEach(sfe -> sfeDao.deleteById(sfe.getId()));
	}
	@Override
	public boolean canAccept(Task task)
	{
		return task instanceof ListSentenceForming;
	}
	
	private void ensureApplicable(Task task)
	{
		if (!canAccept(task))
			throw new IllegalArgumentException("Invalid task for this service: " + task
					.getClass().getTypeName());
	}
}