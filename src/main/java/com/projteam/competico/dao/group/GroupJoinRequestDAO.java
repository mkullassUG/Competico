package com.projteam.competico.dao.group;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projteam.competico.domain.group.GroupJoinRequest;

public interface GroupJoinRequestDAO extends JpaRepository<GroupJoinRequest, UUID>
{
	public void deleteAllByGroup_id(UUID id);
	public boolean existsByAccount_idAndGroup_id(UUID account_id, UUID group_id);
	public Optional<GroupJoinRequest> findByAccount_idAndGroup_id(UUID account_id, UUID group_id);
	public List<GroupJoinRequest> findAllByGroup_id(UUID id);
	public List<GroupJoinRequest> findAllByAccount_id(UUID id);
	public Page<GroupJoinRequest> findAllByAccount_id(UUID id, Pageable pageable);
	public List<GroupJoinRequest> findAllByGroup_Lecturers_id(UUID id);
	public Page<GroupJoinRequest> findAllByGroup_Lecturers_id(UUID id, Pageable pageable);
	public int countByGroup_Lecturers_id(UUID id);
}
