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
	public Page<Group> findAllDistinctByPlayers_idOrLecturers_id(UUID playerId, UUID lecturerId, Pageable pageable);
	public boolean existsByNameAndLecturers_id(String name, UUID id);
	public boolean existsByGroupCodeAndLecturers_idOrGroupCodeAndPlayers_id(
			String groupCode1, UUID lecturerId, String groupCode2, UUID playerId);
	public List<Group> findAllByLecturers_id(UUID lecturer_id);
	public List<Group> findAllDistinctByLecturers_idOrPlayers_id(UUID lecturer_id, UUID player_id);
}
