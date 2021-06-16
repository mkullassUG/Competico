package com.projteam.competico.service.game.tasks;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.projteam.competico.dao.game.tasks.ChoiceWordFillDAO;
import com.projteam.competico.dao.game.tasks.ChoiceWordFillElementDAO;
import com.projteam.competico.dao.game.tasks.ChoiceWordFillElementWordChoiceDAO;
import com.projteam.competico.domain.game.tasks.ChoiceWordFill;
import com.projteam.competico.domain.game.tasks.ChoiceWordFillElement;
import com.projteam.competico.domain.game.tasks.Task;
import com.projteam.competico.domain.game.tasks.ChoiceWordFillElement.WordChoice;
import com.projteam.competico.utils.Initializable;

@Service
public class ChoiceWordFillService implements TaskService
{
	private ChoiceWordFillDAO cwfDao;
	private ChoiceWordFillElementDAO cwfeDao;
	private ChoiceWordFillElementWordChoiceDAO cwfewcDao;
	
	public ChoiceWordFillService(ChoiceWordFillDAO cwfDao,
			ChoiceWordFillElementDAO cwfeDao,
			ChoiceWordFillElementWordChoiceDAO cwfewcDao)
	{
		this.cwfDao = cwfDao;
		this.cwfeDao = cwfeDao;
		this.cwfewcDao = cwfewcDao;
	}

	@Override
	@Transactional
	public boolean genericExistsById(UUID taskId)
	{
		return cwfDao.existsById(taskId);
	}
	@Override
	@Transactional
	public Task genericFindById(UUID taskId)
	{
		return findById(taskId);
	}
	private ChoiceWordFill findById(UUID taskId)
	{
		return cwfDao.findById(taskId)
				.map(Initializable::init)
				.orElse(null);
	}
	@Override
	@Transactional
	public long count()
	{
		return cwfDao.count();
	}
	@Override
	@Transactional
	public List<Task> genericFindAll()
	{
		return cwfDao.findAll()
				.stream()
				.map(t -> Initializable.init(t))
				.collect(Collectors.toList());
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
		ChoiceWordFill newTask = (ChoiceWordFill) task;
		ChoiceWordFill oldTask = findById(taskId);
		if (oldTask == null)
			throw new IllegalArgumentException("Cannot replace data; entity not found.");
		
		ChoiceWordFillElement newCwfe = newTask.getContent();
		ChoiceWordFillElement oldCwfe = oldTask.getContent();
		oldCwfe.setStartWithText(newCwfe.isStartWithText());
		oldCwfe.setText(newCwfe.getText());
		cwfewcDao.deleteAll(oldCwfe.getWordChoices());
		oldCwfe.setWordChoices(newCwfe.getWordChoices());
		oldTask.setContent(oldCwfe);
		oldTask.setDifficulty(newTask.getDifficulty());
		oldTask.setInstruction(newTask.getInstruction());
		oldTask.setTags(newTask.getTags());
		
		cwfewcDao.saveAll(oldCwfe.getWordChoices());
		if (flush)
		{
			cwfewcDao.flush();
			cwfeDao.saveAndFlush(oldCwfe);
			cwfDao.saveAndFlush(oldTask);
		}
		else
		{
			cwfeDao.save(oldCwfe);
			cwfDao.save(oldTask);
		}
	}
	@Override
	@Transactional
	public void flush()
	{
		cwfewcDao.flush();
		cwfeDao.flush();
		cwfDao.flush();
	}
	private void save(Task task, boolean flush)
	{
		ensureApplicable(task);
		ChoiceWordFill cwf = (ChoiceWordFill) task;
		ChoiceWordFillElement cwfe = cwf.getContent();
		if (flush)
		{
			cwfewcDao.saveAll(cwfe.getWordChoices());
			cwfewcDao.flush();
			cwfeDao.saveAndFlush(cwfe);
			cwfDao.saveAndFlush(cwf);
		}
		else
		{
			cwfewcDao.saveAll(cwfe.getWordChoices());
			cwfeDao.save(cwfe);
			cwfDao.save(cwf);
		}
	}
	@Override
	@Transactional
	public void genericDeleteById(UUID id)
	{
		ChoiceWordFill cwf = cwfDao.findById(id).orElse(null);
		if (cwf == null)
			return;
		ChoiceWordFillElement cwfe = cwf.getContent();
		List<WordChoice> wcs = cwfe.getWordChoices();
		cwfDao.deleteById(id);
		cwfeDao.deleteById(cwfe.getId());
		wcs.forEach(cwfewc -> cwfewcDao.deleteById(cwfewc.getId()));
	}
	@Override
	public boolean canAccept(Task task)
	{
		return task instanceof ChoiceWordFill;
	}
	
	private void ensureApplicable(Task task)
	{
		if (!canAccept(task))
			throw new IllegalArgumentException("Invalid task for this service: " + task
					.getClass().getTypeName());
	}
}