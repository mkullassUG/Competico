package com.projteam.competico.service.game.tasks;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.projteam.competico.dao.game.tasks.OptionSelectDAO;
import com.projteam.competico.dao.game.tasks.OptionSelectElementDAO;
import com.projteam.competico.domain.game.tasks.OptionSelect;
import com.projteam.competico.domain.game.tasks.OptionSelectElement;
import com.projteam.competico.domain.game.tasks.Task;
import com.projteam.competico.utils.Initializable;

@Service
public class OptionSelectService implements TaskService
{
	private OptionSelectDAO osDao;
	private OptionSelectElementDAO oseDao;

	public OptionSelectService(OptionSelectDAO osDao,
			OptionSelectElementDAO oseDao)
	{
		this.osDao = osDao;
		this.oseDao = oseDao;
	}

	@Override
	@Transactional
	public boolean genericExistsById(UUID taskId)
	{
		return osDao.existsById(taskId);
	}
	@Override
	@Transactional
	public Task genericFindById(UUID taskId)
	{
		return findById(taskId);
	}
	private OptionSelect findById(UUID taskId)
	{
		return osDao.findById(taskId)
				.map(Initializable::init)
				.orElse(null);
	}
	@Override
	@Transactional
	public long count()
	{
		return osDao.count();
	}
	@Override
	@Transactional
	public List<Task> genericFindAll()
	{
		return osDao.findAll()
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
		OptionSelect newTask = (OptionSelect) task;
		OptionSelect oldTask = findById(taskId);
		
		OptionSelectElement oldOse = oldTask.getContent();
		OptionSelectElement newOse = newTask.getContent();
		
		oldOse.setContent(newOse.getContent());
		oldOse.setCorrectAnswers(newOse.getCorrectAnswers());
		oldOse.setIncorrectAnswers(newOse.getIncorrectAnswers());
		oldTask.setContent(oldOse);
		oldTask.setDifficulty(newTask.getDifficulty());
		oldTask.setInstruction(newTask.getInstruction());
		oldTask.setTags(newTask.getTags());
		
		if (flush)
		{
			oseDao.saveAndFlush(oldOse);
			osDao.saveAndFlush(oldTask);
		}
		else
		{
			oseDao.save(oldOse);
			osDao.save(oldTask);
		}
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
		oseDao.flush();
		osDao.flush();
	}
	private void save(Task task, boolean flush)
	{
		ensureApplicable(task);
		OptionSelect os = (OptionSelect) task;
		if (flush)
		{
			oseDao.saveAndFlush(os.getContent());
			osDao.saveAndFlush(os);
		}
		else
		{
			oseDao.save(os.getContent());
			osDao.save(os);
		}
	}
	@Override
	@Transactional
	public void genericDeleteById(UUID id)
	{
		OptionSelect os = osDao.findById(id).orElse(null);
		if (os == null)
			return;
		OptionSelectElement ose = os.getContent();
		osDao.deleteById(id);
		oseDao.deleteById(ose.getId());
	}
	@Override
	public boolean canAccept(Task task)
	{
		return task instanceof OptionSelect;
	}
	
	private void ensureApplicable(Task task)
	{
		if (!canAccept(task))
			throw new IllegalArgumentException("Invalid task for this service: " + task
					.getClass().getTypeName());
	}
}