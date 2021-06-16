package com.projteam.competico.dao.group;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projteam.competico.domain.group.GroupMessage;

public interface GroupMessageDAO extends JpaRepository<GroupMessage, UUID>
{
	public List<GroupMessage> findAllByGroup_id(UUID groupId);
	public void deleteAllByGroup_id(UUID id);
}
