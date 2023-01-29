package com.nytdacm.oa.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;

import java.util.Objects;

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
    private boolean superAdmin;

    @Column(name = "admin")
    private boolean admin;

    @Column(name = "active")
    private boolean active;

    @Column(name = "social_account")
    @JdbcTypeCode(SqlTypes.JSON)
    private SocialAccount socialAccount = new SocialAccount();

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public boolean isSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(boolean superAdmin) {
        this.superAdmin = superAdmin;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public SocialAccount getSocialAccount() {
        return socialAccount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return superAdmin == user.superAdmin && admin == user.admin && active == user.active && Objects.equals(userId, user.userId) && Objects.equals(username, user.username) && Objects.equals(password, user.password) && Objects.equals(passwordSalt, user.passwordSalt) && Objects.equals(name, user.name) && Objects.equals(socialAccount, user.socialAccount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userId, username, password, passwordSalt, name, superAdmin, admin, active, socialAccount);
    }
}
