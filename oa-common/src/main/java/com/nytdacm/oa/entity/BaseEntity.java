package com.nytdacm.oa.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
public sealed abstract class BaseEntity permits Group, ProblemList, Submission, User {
    private Instant createdAt;
    private Instant updatedAt;

    private void onCreate() {
        updatedAt = Instant.now();
    }

    private void onUpdate() {
        updatedAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdAt, updatedAt);
    }
}
