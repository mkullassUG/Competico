package com.projteam.app.service.game.tasks;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.projteam.app.dao.game.tasks.WordFillDAO;
import com.projteam.app.dao.game.tasks.WordFillElementDAO;
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.domain.game.tasks.WordFill;
import com.projteam.app.utils.Initializable;

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
	public boolean genericExistsById(Task task)
	{
		ensureApplicable(task);
		return wfDao.existsById(((WordFill) task).getId());
	}
	@Override
	@Transactional
	public long count()
	{
		return wfDao.count();
	}
	@Override
	@Transactional
	public Task genericFindRandom(Random r)
	{
		long count = count();
		if (count < 1)
			return null;
		
		int pos = r.nextInt((int) count);
		
		return wfDao.findAll(PageRequest.of(pos, 1))
				.stream()
				.findFirst()
				.map(t -> Initializable.init(t))
				.orElse(null);
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
	public void genericSave(Task task)
	{
		ensureApplicable(task);
		WordFill wf = (WordFill) task;
		wfeDao.save(wf.getContent());
		wfDao.save(wf);
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