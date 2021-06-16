package com.projteam.competico.service.game.tasks;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.projteam.competico.dao.game.tasks.ChoiceWordFillElementDAO;
import com.projteam.competico.dao.game.tasks.ChoiceWordFillElementWordChoiceDAO;
import com.projteam.competico.dao.game.tasks.ListChoiceWordFillDAO;
import com.projteam.competico.domain.game.tasks.ChoiceWordFillElement;
import com.projteam.competico.domain.game.tasks.ListChoiceWordFill;
import com.projteam.competico.domain.game.tasks.Task;
import com.projteam.competico.domain.game.tasks.ChoiceWordFillElement.WordChoice;
import com.projteam.competico.utils.Initializable;

@Service
public class ListChoiceWordFillService implements TaskService
{
	private ListChoiceWordFillDAO lcwfDao;
	private ChoiceWordFillElementDAO cwfeDao;
	private ChoiceWordFillElementWordChoiceDAO cwfewcDao;
	
	public ListChoiceWordFillService(ListChoiceWordFillDAO lcwfDao,
			ChoiceWordFillElementDAO cwfeDao,
			ChoiceWordFillElementWordChoiceDAO cwfewcDao)
	{
		this.lcwfDao = lcwfDao;
		this.cwfeDao = cwfeDao;
		this.cwfewcDao = cwfewcDao;
	}
	
	@Override
	@Transactional
	public boolean genericExistsById(UUID taskId)
	{
		return lcwfDao.existsById(taskId);
	}
	@Override
	@Transactional
	public Task genericFindById(UUID taskId)
	{
		return findById(taskId);
	}
	private ListChoiceWordFill findById(UUID taskId)
	{
		return lcwfDao.findById(taskId)
				.map(Initializable::init)
				.orElse(null);
	}
	@Override
	@Transactional
	public long count()
	{
		return lcwfDao.count();
	}
	@Override
	@Transactional
	public List<Task> genericFindAll()
	{
		return lcwfDao.findAll()
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
		ListChoiceWordFill newTask = (ListChoiceWordFill) task;
		ListChoiceWordFill oldTask = findById(taskId);
		
		oldTask.getRows().forEach(row -> 
			cwfewcDao.deleteAll(row.getWordChoices()));
		cwfeDao.deleteAll(oldTask.getRows());
		
		oldTask.setRows(newTask.getRows());
		oldTask.setDifficulty(newTask.getDifficulty());
		oldTask.setInstruction(newTask.getInstruction());
		oldTask.setTags(newTask.getTags());
		
		oldTask.getRows().forEach(row -> 
			cwfewcDao.saveAll(row.getWordChoices()));
		cwfeDao.saveAll(oldTask.getRows());
		if (flush)
		{
			cwfewcDao.flush();
			cwfeDao.flush();
			lcwfDao.saveAndFlush(oldTask);
		}
		else
			lcwfDao.save(oldTask);
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
		cwfewcDao.flush();
		cwfeDao.flush();
		lcwfDao.flush();
	}
	private void save(Task task, boolean flush)
	{
		ensureApplicable(task);
		ListChoiceWordFill lcwf = (ListChoiceWordFill) task;
		for (ChoiceWordFillElement cwfe: lcwf.getRows())
		{
			if (flush)
			{
				cwfewcDao.saveAll(cwfe.getWordChoices());
				cwfewcDao.flush();
				cwfeDao.saveAndFlush(cwfe);
			}
			else
			{
				cwfewcDao.saveAll(cwfe.getWordChoices());
				cwfeDao.save(cwfe);
			}
		}
		if (flush)
			lcwfDao.saveAndFlush(lcwf);
		else
			lcwfDao.save(lcwf);
	}
	@Override
	@Transactional
	public void genericDeleteById(UUID id)
	{
		ListChoiceWordFill lcwf = lcwfDao.findById(id).orElse(null);
		if (lcwf == null)
			return;
		List<ChoiceWordFillElement> cwfes = lcwf.getRows();
		List<WordChoice> wcs = cwfes.stream()
				.flatMap(cwfe -> cwfe.getWordChoices().stream())
				.collect(Collectors.toList());
		lcwfDao.deleteById(id);
		cwfes.forEach(cwfe -> cwfeDao.deleteById(cwfe.getId()));
		wcs.forEach(cwfewc -> cwfewcDao.deleteById(cwfewc.getId()));
	}
	@Override
	public boolean canAccept(Task task)
	{
		return task instanceof ListChoiceWordFill;
	}
	
	private void ensureApplicable(Task task)
	{
		if (!canAccept(task))
			throw new IllegalArgumentException("Invalid task for this service: " + task
					.getClass().getTypeName());
	}
}