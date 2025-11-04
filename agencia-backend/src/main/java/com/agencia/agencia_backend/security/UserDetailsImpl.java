package com.agencia.agencia_backend.security;

import com.agencia.agencia_backend.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {
    
    private String id;
    private String email;
    private String password;
    private String nombre;
    private String apellido;
    private Boolean isAdmin;
    private Boolean isAgente;
    private Boolean isCliente;
    private Boolean isActive;
    private Collection<? extends GrantedAuthority> authorities;
    
    public static UserDetailsImpl build(Usuario usuario) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        if (usuario.getIsAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        if (usuario.getIsAgente()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_AGENTE"));
        }
        if (usuario.getIsCliente()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_CLIENTE"));
        }
        
        return new UserDetailsImpl(
            usuario.getId(),
            usuario.getEmail(),
            usuario.getPassword(),
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getIsAdmin(),
            usuario.getIsAgente(),
            usuario.getIsCliente(),
            usuario.getIsActive(),
            authorities
        );
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        return email;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
