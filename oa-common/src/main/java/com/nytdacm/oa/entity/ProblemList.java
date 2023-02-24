package com.nytdacm.oa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "t_problem_list")
public final class ProblemList extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "problem_list_id", nullable = false, updatable = false)
    private Long problemListId;

    public Long getProblemListId() {
        return problemListId;
    }
}
