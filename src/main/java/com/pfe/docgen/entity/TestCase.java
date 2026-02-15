package com.pfe.docgen.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "testcase")
public class TestCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private List<String> steps;

    private String expectedresult;

    @OneToMany(mappedBy = "testcase", cascade = CascadeType.ALL)
    private List<TestExecution> testexecutions;

    @ManyToOne
    @JoinColumn(name = "test_plan_id")
    private TestPlan testPlan;


}
