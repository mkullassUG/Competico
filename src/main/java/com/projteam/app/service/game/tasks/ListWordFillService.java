package com.projteam.app.service.game.tasks;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.projteam.app.dao.game.tasks.ListWordFillDAO;
import com.projteam.app.dao.game.tasks.WordFillElementDAO;
import com.projteam.app.domain.game.tasks.ListWordFill;
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.utils.Initializable;

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
	public boolean genericExistsById(Task task)
	{
		ensureApplicable(task);
		return lwfDao.existsById(((ListWordFill) task).getId());
	}
	@Override
	public long count()
	{
		return lwfDao.count();
	}
	@Override
	@Transactional
	public Task genericFindRandom(Random r)
	{
		long count = count();
		if (count < 1)
			return null;
		
		int pos = r.nextInt((int) count);
		
		return lwfDao.findAll(PageRequest.of(pos, 1))
				.stream()
				.findFirst()
				.map(t -> Initializable.init(t))
				.orElse(null);
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
	public void genericSave(Task task)
	{
		ensureApplicable(task);
		ListWordFill lwf = (ListWordFill) task;
		wfeDao.saveAll(lwf.getRows());
		lwfDao.save(lwf);
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