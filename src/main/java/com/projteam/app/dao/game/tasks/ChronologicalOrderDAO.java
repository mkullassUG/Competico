package com.projteam.app.dao.game.tasks;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projteam.app.domain.game.tasks.ChronologicalOrder;

public interface ChronologicalOrderDAO extends JpaRepository<ChronologicalOrder, UUID>
{}