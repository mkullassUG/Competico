package com.projteam.competico.dao.game.tasks;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projteam.competico.domain.game.tasks.ListWordFill;

public interface ListWordFillDAO extends JpaRepository<ListWordFill, UUID>
{}
