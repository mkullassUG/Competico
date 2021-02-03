package com.projteam.app.service.game.tasks;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.projteam.app.dao.game.tasks.MultipleChoiceDAO;
import com.projteam.app.dao.game.tasks.MultipleChoiceElementDAO;
import com.projteam.app.domain.game.tasks.MultipleChoice;
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.utils.Initializable;

@Service
public class MultipleChoiceService implements TaskService
{
	private MultipleChoiceDAO mcDao;
	private MultipleChoiceElementDAO mceDao;

	public MultipleChoiceService(MultipleChoiceDAO mcDao,
			MultipleChoiceElementDAO mceDao)
	{
		this.mcDao = mcDao;
		this.mceDao = mceDao;
	}
	
	@Override
	@Transactional
	public boolean genericExistsById(Task task)
	{
		ensureApplicable(task);
		return mcDao.existsById(((MultipleChoice) task).getId());
	}
	@Override
	@Transactional
	public long count()
	{
		return mcDao.count();
	}
	@Override
	@Transactional
	public Task genericFindRandom(Random r)
	{
		long count = count();
		if (count < 1)
			return null;
		
		int pos = r.nextInt((int) count);
		
		return mcDao.findAll(PageRequest.of(pos, 1))
				.stream()
				.findFirst()
				.map(t -> Initializable.init(t))
				.orElse(null);
	}
	@Override
	@Transactional
	public List<Task> genericFindAll()
	{
		return mcDao.findAll()
				.stream()
				.map(t -> Initializable.init(t))
				.collect(Collectors.toList());
	}
	@Override
	@Transactional
	public void genericSave(Task task)
	{
		ensureApplicable(task);
		MultipleChoice mc = (MultipleChoice) task;
		mceDao.save(mc.getContent());
		mcDao.save(mc);
	}
	@Override
	public boolean canAccept(Task task)
	{
		return task instanceof MultipleChoice;
	}
	
	private void ensureApplicable(Task task)
	{
		if (!canAccept(task))
			throw new IllegalArgumentException("Invalid task for this service: " + task
					.getClass().getTypeName());
	}
}