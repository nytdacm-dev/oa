package com.nytdacm.oa.model.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "t_groups")
public class Group extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id", nullable = false, updatable = false)
    private Long groupId;

    @Column(name = "name")
    private String name;

    @ManyToMany(
        mappedBy = "groups",
        fetch = FetchType.EAGER,
        cascade = {CascadeType.MERGE}
    )
    private Set<User> users = new HashSet<>();

    public Long getGroupId() {
        return groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Group group = (Group) o;
        return Objects.equals(groupId, group.groupId) && Objects.equals(name, group.name) && Objects.equals(users, group.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), groupId, name);
    }
}
