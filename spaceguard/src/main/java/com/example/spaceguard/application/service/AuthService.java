package com.example.spaceguard.application.service;

import com.example.spaceguard.domain.user.dto.*;
import com.example.spaceguard.domain.user.User;
import com.example.spaceguard.domain.user.UserRepository;
import com.example.spaceguard.domain.user.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.jspecify.annotations.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @CacheEvict(value = "usuarios", allEntries = true)
    public void createUser(@Valid CreateUserDTO data) {
        User user = User.builder()
                .nomeUsuario(data.nomeUsuario())
                .telefone(data.telefone())
                .email(data.email())
                .senha(passwordEncoder.encode(data.senha()))
                .dataCadastro(LocalDate.now())
                .role(UserRole.ADMIN)
                .build();
        System.out.println("User created ==>" + data);

        userRepository.save(user);
    }

    public String authenticate(String email, String senha) {
        User user = userRepository.findByEmail(email);

        if (user == null || !passwordEncoder.matches(senha, user.getSenha())) {
            throw new IllegalArgumentException("Email ou senha inválidos");
        }

        return user.getEmail();
    }

    @Cacheable(value = "usuarios", key = "#id")
    public UsuarioDTO buscarUsuario(String id) {

        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("ID INVÁLIDO"));

        return UsuarioDTO.builder()
                .idUsuario(user.getIdUsuario())
                .nomeUsuario(user.getNomeUsuario())
                .telefone(user.getTelefone())
                .email(user.getEmail())
                .dataCadastro(user.getDataCadastro())
                .role(user.getRole())
                .build();

    }

    @Cacheable(value = "usuarios", key = "'all'")
    public List<UsuarioDTO> buscarTodosUsuarios() {

        return userRepository.findAll().stream()
                .map(user -> UsuarioDTO.builder()
                        .idUsuario(user.getIdUsuario())
                        .nomeUsuario(user.getNomeUsuario())
                        .telefone(user.getTelefone())
                        .email(user.getEmail())
                        .dataCadastro(user.getDataCadastro())
                        .role(user.getRole())
                        .build()
                ).toList();

    }

    @CacheEvict(value = "usuarios", allEntries = true)
    public ModifyUserResponseDTO modifyUser(String id, @Valid ModifyUserDTO data) {

        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("ID INVÁLIDO"));

        user.setNomeUsuario(data.nomeUsuario());
        user.setTelefone(data.telefone());
        user.setEmail(data.email());
        user.setSenha(passwordEncoder.encode(data.senha()));
        user.setRole(data.role());

        User userAtualizado = userRepository.save(user);

        return ModifyUserResponseDTO.builder()
                .idUsuario(userAtualizado.getIdUsuario())
                .nomeUsuario(userAtualizado.getNomeUsuario())
                .telefone(userAtualizado.getTelefone())
                .email(userAtualizado.getEmail())
                .dataCadastro(userAtualizado.getDataCadastro())
                .role(userAtualizado.getRole())
                .build();

    }

    @CacheEvict(value = "usuarios", allEntries = true)
    public ModifyUserResponseDTO atualizarPatchUser(String id, ModifyUserPatchDTO data) {

        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("ID INVÁLIDO"));

        if (data.nomeUsuario() != null && !data.nomeUsuario().isEmpty()){
            user.setNomeUsuario(data.nomeUsuario());
        }

        if (data.telefone() != null && !data.telefone().isEmpty()){
            user.setTelefone(data.telefone());
        }

        if (data.email() != null && !data.email().isEmpty()){
            user.setEmail(data.email());
        }

        if (data.senha() != null && !data.senha().isEmpty()){
            user.setSenha(passwordEncoder.encode(data.senha()));
        }

        if (data.role() != null){
            user.setRole(data.role());
        }

        User usuarioAtualizado = userRepository.save(user);

        return ModifyUserResponseDTO.builder()
                .idUsuario(usuarioAtualizado.getIdUsuario())
                .nomeUsuario(usuarioAtualizado.getNomeUsuario())
                .telefone(usuarioAtualizado.getTelefone())
                .email(usuarioAtualizado.getEmail())
                .dataCadastro(usuarioAtualizado.getDataCadastro())
                .role(usuarioAtualizado.getRole())
                .build();

    }

    @CacheEvict(value = "usuarios", allEntries = true)
    public void deletarUsuario(String id) {

        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("ID INVÁLIDO"));

        userRepository.delete(user);

    }

    public @Nullable User findByEmail(String username) {
        return userRepository.findByEmail(username);
    }

}