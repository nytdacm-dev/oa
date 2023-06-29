package com.nytdacm.oa.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "t_groups")
@Getter
@Setter
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

    @Column(name = "homepage_order")
    private Integer homepageOrder;

    @ManyToMany(
        mappedBy = "groups",
        fetch = FetchType.EAGER,
        cascade = {CascadeType.MERGE}
    )
    private Set<User> users = new HashSet<>();
}
