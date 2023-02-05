package com.nytdacm.oa.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "t_users")
@SQLDelete(sql = "UPDATE t_users SET deleted = true WHERE user_id = ?")
@FilterDef(name = "deletedProductFilter", parameters = @ParamDef(name = "deleted", type = boolean.class))
@Filter(name = "deletedProductFilter", condition = "deleted = :deleted")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "username", nullable = false, unique = true, updatable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "password_salt", nullable = false)
    private String passwordSalt;

    @Column(name = "name")
    private String name;

    @Column(name = "super_admin")
    private Boolean superAdmin = false;

    @Column(name = "admin")
    private Boolean admin = false;

    @Column(name = "active")
    private Boolean active = false;

    @Column(name = "social_account")
    @JdbcTypeCode(SqlTypes.JSON)
    private SocialAccount socialAccount = new SocialAccount();

    @ManyToMany(
        fetch = FetchType.EAGER,
        cascade = {CascadeType.MERGE})
    @JoinTable(
        name = "user_group",
        joinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "user_id")
        },
        inverseJoinColumns = {
            @JoinColumn(name = "group_id", referencedColumnName = "group_id")
        })
    private Set<Group> groups = new HashSet<>();

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(Boolean superAdmin) {
        this.superAdmin = superAdmin;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public SocialAccount getSocialAccount() {
        return socialAccount;
    }

    public void setSocialAccount(SocialAccount socialAccount) {
        this.socialAccount = socialAccount;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) && Objects.equals(username, user.username) && Objects.equals(password, user.password) && Objects.equals(passwordSalt, user.passwordSalt) && Objects.equals(name, user.name) && Objects.equals(superAdmin, user.superAdmin) && Objects.equals(admin, user.admin) && Objects.equals(active, user.active) && Objects.equals(socialAccount, user.socialAccount) && Objects.equals(groups, user.groups);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userId, username, password, passwordSalt, name, superAdmin, admin, active, socialAccount, groups);
    }
}
