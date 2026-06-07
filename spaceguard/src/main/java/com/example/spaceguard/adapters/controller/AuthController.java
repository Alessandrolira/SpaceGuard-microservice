package com.example.spaceguard.adapters.controller;

import com.example.spaceguard.application.security.JwtUtil;
import com.example.spaceguard.application.service.AuthService;
import com.example.spaceguard.domain.user.User;
import com.example.spaceguard.domain.user.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody CreateUserDTO data) {
        try {
            authService.createUser(data);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            System.out.println("Erro ao criar usuario: " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO data) {
        try {
            String email = authService.authenticate(data.email(), data.senha());
            String token = jwtUtil.generateToken(email);
            return ResponseEntity.ok(new LoginResponseDTO(token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UsuarioDTO>> buscarUsuario(@PathVariable String id) {

        try {
            UsuarioDTO response = authService.buscarUsuario(id);

            EntityModel<UsuarioDTO> model = EntityModel.of(response,
                    linkTo(methodOn(AuthController.class).buscarUsuario(id)).withSelfRel(),
                    linkTo(methodOn(AuthController.class).buscarTodosUsuarios()).withRel("usuarios")
            );

            return ResponseEntity.ok(model);
        } catch (RuntimeException e) {
            return ResponseEntity.noContent().build();
        }

    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UsuarioDTO>>> buscarTodosUsuarios() {

        List<EntityModel<UsuarioDTO>> usuarios = authService.buscarTodosUsuarios().stream()
                .map(usuario -> EntityModel.of(usuario,
                        linkTo(methodOn(AuthController.class).buscarUsuario(usuario.idUsuario())).withSelfRel()
                ))
                .toList();

        CollectionModel<EntityModel<UsuarioDTO>> model = CollectionModel.of(usuarios,
                linkTo(methodOn(AuthController.class).buscarTodosUsuarios()).withSelfRel()
        );

        return ResponseEntity.ok(model);

    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ModifyUserResponseDTO>> atualizarUsuario(@Valid @RequestBody ModifyUserDTO data, @PathVariable String id) {

        if (id.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        ModifyUserResponseDTO modifyUser = authService.modifyUser(id, data);

        EntityModel<ModifyUserResponseDTO> model = EntityModel.of(modifyUser,
                linkTo(methodOn(AuthController.class).buscarUsuario(id)).withSelfRel(),
                linkTo(methodOn(AuthController.class).buscarTodosUsuarios()).withRel("usuarios")
        );

        return ResponseEntity.ok(model);

    }

    @PatchMapping("/{id}")
    public ResponseEntity<EntityModel<ModifyUserResponseDTO>> atualizarUsuarioPatch(@RequestBody ModifyUserPatchDTO data, @PathVariable String id) {

        if (id.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        ModifyUserResponseDTO modifyUser = authService.atualizarPatchUser(id, data);

        EntityModel<ModifyUserResponseDTO> model = EntityModel.of(modifyUser,
                linkTo(methodOn(AuthController.class).buscarUsuario(id)).withSelfRel(),
                linkTo(methodOn(AuthController.class).buscarTodosUsuarios()).withRel("usuarios")
        );

        return ResponseEntity.ok(model);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable String id) {

        authService.deletarUsuario(id);

        return ResponseEntity.noContent().build();

    }

    @GetMapping("/me")
    public ResponseEntity<ModifyUserResponseDTO> getMe() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = authService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ModifyUserResponseDTO.builder()
                .idUsuario(user.getIdUsuario())
                .nomeUsuario(user.getNomeUsuario())
                .telefone(user.getTelefone())
                .email(user.getEmail())
                .dataCadastro(user.getDataCadastro())
                .role(user.getRole())
                .build());
    }

}