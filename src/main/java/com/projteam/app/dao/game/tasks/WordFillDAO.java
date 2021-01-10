package com.projteam.app.dao.game.tasks;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.projteam.app.domain.game.tasks.WordFill;

public interface WordFillDAO extends JpaRepository<WordFill, UUID>
{}
