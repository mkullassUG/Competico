package com.projteam.app.service.game.tasks;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.projteam.app.dao.game.tasks.OptionSelectDAO;
import com.projteam.app.dao.game.tasks.OptionSelectElementDAO;
import com.projteam.app.domain.game.tasks.OptionSelect;
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.utils.Initializable;

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
	public boolean genericExistsById(Task task)
	{
		ensureApplicable(task);
		return osDao.existsById(((OptionSelect) task).getId());
	}
	@Override
	@Transactional
	public long count()
	{
		return osDao.count();
	}
	@Override
	@Transactional
	public Task genericFindRandom(Random r)
	{
		long count = count();
		if (count < 1)
			return null;
		
		int pos = r.nextInt((int) count);
		
		return osDao.findAll(PageRequest.of(pos, 1))
				.stream()
				.findFirst()
				.map(t -> Initializable.init(t))
				.orElse(null);
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
	public void genericSave(Task task)
	{
		ensureApplicable(task);
		OptionSelect os = (OptionSelect) task;
		oseDao.save(os.getContent());
		osDao.save(os);
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