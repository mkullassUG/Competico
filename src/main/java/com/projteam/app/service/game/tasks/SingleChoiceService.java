package com.projteam.app.service.game.tasks;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.projteam.app.dao.game.tasks.SingleChoiceDAO;
import com.projteam.app.domain.game.tasks.SingleChoice;
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.utils.Initializable;

@Service
public class SingleChoiceService implements TaskService
{
	private SingleChoiceDAO scDao;
	
	public SingleChoiceService(SingleChoiceDAO scDao)
	{
		this.scDao = scDao;
	}
	
	@Override
	public boolean genericExistsById(Task task)
	{
		ensureApplicable(task);
		return scDao.existsById(((SingleChoice) task).getId());
	}
	@Override
	public long count()
	{
		return scDao.count();
	}
	@Override
	@Transactional
	public Task genericFindRandom(Random r)
	{
		long count = count();
		if (count < 1)
			return null;
		
		int pos = r.nextInt((int) count);
		
		return scDao.findAll(PageRequest.of(pos, 1))
				.stream()
				.findFirst()
				.map(t -> Initializable.init(t))
				.orElse(null);
	}
	@Override
	@Transactional
	public List<Task> genericFindAll()
	{
		return scDao.findAll()
				.stream()
				.map(t -> Initializable.init(t))
				.collect(Collectors.toList());
	}
	@Override
	public void genericSave(Task task)
	{
		ensureApplicable(task);
		SingleChoice sc = (SingleChoice) task;
		scDao.save(sc);
	}
	@Override
	public boolean canAccept(Task task)
	{
		return task instanceof SingleChoice;
	}
	
	private void ensureApplicable(Task task)
	{
		if (!canAccept(task))
			throw new IllegalArgumentException("Invalid task for this service: " + task
					.getClass().getTypeName());
	}
}