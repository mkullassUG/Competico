package com.projteam.app.dao.game.tasks;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projteam.app.domain.game.tasks.MultipleChoice;

public interface MultipleChoiceDAO extends JpaRepository<MultipleChoice, UUID>
{}
