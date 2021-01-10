package com.projteam.app.dao.game.tasks;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projteam.app.domain.game.tasks.ChoiceWordFillElement;

public interface ChoiceWordFillElementDAO extends JpaRepository<ChoiceWordFillElement, UUID>
{}
