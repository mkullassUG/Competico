package com.projteam.competico.service.game.tasks;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.projteam.competico.dao.game.tasks.WordFillDAO;
import com.projteam.competico.dao.game.tasks.WordFillElementDAO;
import com.projteam.competico.domain.game.tasks.Task;
import com.projteam.competico.domain.game.tasks.WordFill;
import com.projteam.competico.domain.game.tasks.WordFillElement;
import com.projteam.competico.utils.Initializable;

@Service
public class WordFillService implements TaskService
{
	private WordFillDAO wfDao;
	private WordFillElementDAO wfeDao;
	
	public WordFillService(WordFillDAO wfDao,
			WordFillElementDAO wfeDao)
	{
		this.wfDao = wfDao;
		this.wfeDao = wfeDao;
	}

	@Override
	@Transactional
	public boolean genericExistsById(UUID taskId)
	{
		return wfDao.existsById(taskId);
	}
	@Override
	@Transactional
	public Task genericFindById(UUID taskId)
	{
		return findById(taskId);
	}
	private WordFill findById(UUID taskId)
	{
		return wfDao.findById(taskId)
				.map(Initializable::init)
				.orElse(null);
	}
	@Override
	@Transactional
	public long count()
	{
		return wfDao.count();
	}
	@Override
	@Transactional
	public List<Task> genericFindAll()
	{
		return wfDao.findAll()
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
		WordFill newTask = (WordFill) task;
		WordFill oldTask = findById(taskId);
		
		WordFillElement oldWfe = oldTask.getContent();
		WordFillElement newWfe = newTask.getContent();
		
		oldWfe.setEmptySpaces(newWfe.getEmptySpaces());
		oldWfe.setPossibleAnswers(newWfe.getPossibleAnswers());
		oldWfe.setStartWithText(newWfe.isStartWithText());
		oldWfe.setText(newWfe.getText());
		if (flush)
			wfeDao.saveAndFlush(oldWfe);
		else
			wfeDao.save(oldWfe);
		oldTask.setContent(oldWfe);
		oldTask.setDifficulty(newTask.getDifficulty());
		oldTask.setInstruction(newTask.getInstruction());
		oldTask.setTags(newTask.getTags());
		if (flush)
			wfDao.saveAndFlush(oldTask);
		else
			wfDao.save(oldTask);
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
		wfDao.flush();
	}
	private void save(Task task, boolean flush)
	{
		ensureApplicable(task);
		WordFill wf = (WordFill) task;
		if (flush)
		{
			wfeDao.saveAndFlush(wf.getContent());
			wfDao.saveAndFlush(wf);
		}
		else
		{
			wfeDao.save(wf.getContent());
			wfDao.save(wf);
		}
	}
	@Override
	@Transactional
	public void genericDeleteById(UUID id)
	{
		WordFill wf = wfDao.findById(id).orElse(null);
		WordFillElement wfe = wf.getContent();
		wfDao.deleteById(id);
		wfeDao.deleteById(wfe.getId());
	}
	@Override
	public boolean canAccept(Task task)
	{
		return task instanceof WordFill;
	}
	
	private void ensureApplicable(Task task)
	{
		if (!canAccept(task))
			throw new IllegalArgumentException("Invalid task for this service: " + task
					.getClass().getTypeName());
	}
}