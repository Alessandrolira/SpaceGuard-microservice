package com.example.spaceguard.domain.localUsuario;

import com.example.spaceguard.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocalUsuarioRepository extends JpaRepository<LocalUsuario, String> {

    Optional<LocalUsuario> findTopByUsuarioOrderByDataRegistroDesc(User usuario);

}
