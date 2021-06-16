package com.projteam.competico.dao.game;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projteam.competico.domain.game.TaskInfo;

public interface TaskInfoDAO extends JpaRepository<TaskInfo, UUID>
{

}
