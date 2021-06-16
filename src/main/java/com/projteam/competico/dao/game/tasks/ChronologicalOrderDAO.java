package com.projteam.competico.dao.game.tasks;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projteam.competico.domain.game.tasks.ChronologicalOrder;

public interface ChronologicalOrderDAO extends JpaRepository<ChronologicalOrder, UUID>
{}