package com.projteam.app.dao.game.tasks;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projteam.app.domain.game.tasks.WordFillElement;

public interface WordFillElementDAO extends JpaRepository<WordFillElement, UUID>
{}
