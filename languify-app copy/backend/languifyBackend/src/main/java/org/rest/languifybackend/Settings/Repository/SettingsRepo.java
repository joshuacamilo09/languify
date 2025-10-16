package org.rest.languifybackend.Settings.Repository;

import org.rest.languifybackend.Settings.Model.Settings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsRepo extends JpaRepository<Settings,Long> { }
