package io.languify.platform.settings.repository;

import io.languify.platform.settings.model.Settings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsRepo extends JpaRepository<Settings, Long> {}
