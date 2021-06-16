package com.projteam.competico.dao.game;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projteam.competico.domain.game.GlobalTask;

public interface GlobalTaskDAO extends JpaRepository<GlobalTask, UUID>
{
	
}
