package com.projteam.app.dao.game.tasks;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projteam.app.domain.game.tasks.ListChoiceWordFill;

public interface ListChoiceWordFillDAO extends JpaRepository<ListChoiceWordFill, UUID>
{}