package com.pfe.docgen.DAO;

import com.pfe.docgen.entity.TestPlan;
import com.pfe.docgen.repository.TestPlanRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TestPlanDaoImpl implements TestPlanDao {

    private final TestPlanRepository testPlanRepository;

    public TestPlanDaoImpl(TestPlanRepository testPlanRepository) {
        this.testPlanRepository = testPlanRepository;
    }

    @Override
    public TestPlan save(TestPlan testPlan) {
        return testPlanRepository.save(testPlan);
    }

    @Override
    public Optional<TestPlan> findById(Long id) {
        return testPlanRepository.findById(id);
    }

    @Override
    public List<TestPlan> findAll() {
        return testPlanRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        testPlanRepository.deleteById(id);
    }
}