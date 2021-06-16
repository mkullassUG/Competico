package com.projteam.competico.dao.game.tasks;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projteam.competico.domain.game.tasks.ChoiceWordFillElement;

public interface ChoiceWordFillElementDAO extends JpaRepository<ChoiceWordFillElement, UUID>
{}
