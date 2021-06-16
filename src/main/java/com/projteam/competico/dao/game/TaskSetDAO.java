package com.projteam.competico.dao.game;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projteam.competico.domain.game.TaskSet;

public interface TaskSetDAO extends JpaRepository<TaskSet, UUID>
{
	public Optional<TaskSet> findByNameAndLecturerID(String name, UUID lecturerID);
	public List<TaskSet> findAllByLecturerID(UUID lecturerID);
	public long countAllByLecturerID(UUID lecturerID);
	public boolean existsByNameAndLecturerID(String name, UUID lecturerID);
}
