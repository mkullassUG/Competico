package com.projteam.competico.dao.group;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projteam.competico.domain.group.Group;

public interface GroupDAO extends JpaRepository<Group, UUID>
{
	public boolean existsByGroupCode(String groupCode);
	public Optional<Group> findByGroupCode(String groupCode);
	public Page<Group> findAllByPlayers_idOrLecturers_id(UUID playerId, UUID lecturerId, Pageable pageable);
	public boolean existsByNameAndLecturers_id(String name, UUID id);
	public List<Group> findAllByLecturers_id(UUID id);
}
