package com.nytdacm.oa.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

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

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "show_in_home_page")
    private Boolean showInHomepage = false;

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

    public Boolean getShowInHomepage() {
        return showInHomepage;
    }

    public void setShowInHomepage(Boolean showInHomepage) {
        this.showInHomepage = showInHomepage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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
        return Objects.equals(groupId, group.groupId) && Objects.equals(name, group.name) && Objects.equals(displayName, group.displayName) && Objects.equals(showInHomepage, group.showInHomepage) && Objects.equals(users, group.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), groupId, name, displayName, showInHomepage);
    }
}
