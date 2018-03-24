package ru.todo100.activer.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author Igor Bobko <limit-speed@yandex.ru>
 */
@Entity
@Table(name = "authority")
public class AuthorityItem implements java.io.Serializable {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_username", referencedColumnName = "account_username", nullable = false)
    @NotNull
    private AccountItem account;

    @NotNull
    @Id
    @Column(name = "authority_role", nullable = false)
    private String role;

    public AccountItem getAccount() {
        return account;
    }

    public void setAccount(AccountItem account) {
        this.account = account;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AuthorityItem that = (AuthorityItem) o;
        return account.equals(that.account) && role.equals(that.role);
    }

    @Override
    public int hashCode() {
        int result = account.hashCode();
        result = 31 * result + role.hashCode();
        return result;
    }
}
