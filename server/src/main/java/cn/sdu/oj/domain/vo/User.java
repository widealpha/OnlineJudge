package cn.sdu.oj.domain.vo;

import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

public class User extends org.springframework.security.core.userdetails.User {

    private Integer id;

    public User() {
        super("NOBODY", "[PROTECTED]", new ArrayList<>());
    }

    public User(String username, String password) {
        super(username, password, new ArrayList<>());
    }

    public User(Integer id,String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
    }

    public User(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
