package com.pfe.docgen.repository;

import com.pfe.docgen.template.TemplateConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TemplateConfigRepository
        extends JpaRepository<TemplateConfig, Long> {

    Optional<TemplateConfig> findByName(String name);

    Optional<TemplateConfig> findByActiveTrue();

    @Modifying
    @Query("UPDATE TemplateConfig t SET t.active = false WHERE t.active = true")
    void deactivateAll();

}