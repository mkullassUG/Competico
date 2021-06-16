package com.projteam.competico.dao.group;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projteam.competico.domain.group.GroupGameResult;

public interface GroupGameResultDAO extends JpaRepository<GroupGameResult, UUID>
{
	public void deleteAllByGroup_id(UUID id);
	public Page<GroupGameResult> findAllByGroup_id(UUID group_id, Pageable req);
}
