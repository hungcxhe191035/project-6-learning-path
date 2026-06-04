package org.swp.my_learning_path.security;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.swp.my_learning_path.entity.User;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority(
                        "ROLE_" + user.getRole().name()
                )
        );
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Trả về false nếu tài khoản bị INACTIVE (bị khóa bởi admin).
     * Spring Security sẽ ném LockedException và redirect về /login?error=true
     * thay vì cho phép đăng nhập.
     */
    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus() != org.swp.my_learning_path.constant.EAccountStatus.INACTIVE;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == org.swp.my_learning_path.constant.EAccountStatus.ACTIVE;
    }
}
