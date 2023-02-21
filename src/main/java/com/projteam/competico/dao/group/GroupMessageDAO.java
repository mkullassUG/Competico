package com.projteam.competico.dao.group;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.projteam.competico.domain.group.GroupMessage;

public interface GroupMessageDAO extends JpaRepository<GroupMessage, UUID>
{
	public List<GroupMessage> findAllByGroup_id(UUID groupId);
	public void deleteAllByGroup_id(UUID id);
	@Query("select count(distinct gm) from GroupMessage as gm "
			+ "join gm.group.lecturers as lecturers "
			+ "join gm.group.players as players where "
			+ "(lecturers.id = :id or players.id = :id) "
			+ "and not :id member of gm.readBy")
	public int countUnreadMessages(UUID id);
	@Query("select distinct gm from GroupMessage as gm "
			+ "join gm.group.lecturers as lecturers "
			+ "join gm.group.players as players where "
			+ "(lecturers.id = :id or players.id = :id) "
			+ "and not :id member of gm.readBy")
	public List<GroupMessage> findAllUnreadMessages(UUID id);
}
