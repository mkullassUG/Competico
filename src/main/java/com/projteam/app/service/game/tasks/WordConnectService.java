package com.projteam.app.service.game.tasks;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.projteam.app.dao.game.tasks.WordConnectDAO;
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.domain.game.tasks.WordConnect;
import com.projteam.app.utils.Initializable;

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
	public boolean genericExistsById(Task task)
	{
		ensureApplicable(task);
		return wcDao.existsById(((WordConnect) task).getId());
	}
	@Override
	@Transactional
	public long count()
	{
		return wcDao.count();
	}
	@Override
	@Transactional
	public Task genericFindRandom(Random r)
	{
		long count = count();
		if (count < 1)
			return null;
		
		int pos = r.nextInt((int) count);
		
		return wcDao.findAll(PageRequest.of(pos, 1))
				.stream()
				.findFirst()
				.map(t -> Initializable.init(t))
				.orElse(null);
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
	public void genericSave(Task task)
	{
		ensureApplicable(task);
		WordConnect wc = (WordConnect) task;
		wcDao.save(wc);
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