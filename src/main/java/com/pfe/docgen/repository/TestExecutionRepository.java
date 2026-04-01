package com.pfe.docgen.repository;

import com.pfe.docgen.entity.TestExecution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestExecutionRepository extends JpaRepository<TestExecution, Long> {
}
