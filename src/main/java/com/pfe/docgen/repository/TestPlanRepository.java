package com.pfe.docgen.repository;

import com.pfe.docgen.entity.TestPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestPlanRepository extends JpaRepository<TestPlan, Long> {
}
