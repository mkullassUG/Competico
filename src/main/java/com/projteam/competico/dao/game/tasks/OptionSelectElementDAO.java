package com.projteam.competico.dao.game.tasks;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projteam.competico.domain.game.tasks.OptionSelectElement;

public interface OptionSelectElementDAO extends JpaRepository<OptionSelectElement, UUID>
{}
