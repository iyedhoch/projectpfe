package com.pfe.docgen.DAO;

import com.pfe.docgen.entity.TestPlan;

import java.util.List;
import java.util.Optional;

public interface TestPlanDao {

    TestPlan save(TestPlan testPlan);

    Optional<TestPlan> findById(Long id);

    List<TestPlan> findAll();

    void deleteById(Long id);

}
