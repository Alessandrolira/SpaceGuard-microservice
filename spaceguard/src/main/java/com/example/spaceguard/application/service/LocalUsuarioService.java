package com.example.spaceguard.application.service;

import com.example.spaceguard.domain.localUsuario.LocalUsuario;
import com.example.spaceguard.domain.localUsuario.LocalUsuarioRepository;
import com.example.spaceguard.domain.localUsuario.dto.*;
import com.example.spaceguard.domain.user.User;
import com.example.spaceguard.domain.user.UserRepository;
import com.example.spaceguard.domain.user.dto.UsuarioDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocalUsuarioService {

    private final LocalUsuarioRepository localUsuarioRepository;
    private final UserRepository userRepository;

    public LocaisUsuarioDTO buscarUsuario(String id) {

        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("ID INVÁLIDO"));
        List<LocalUsuario> locais = user.getLocaisUsuario();

        if (locais.isEmpty()){
            return LocaisUsuarioDTO.builder().build();
        }

        List<LocalUsuarioDTO> locaisUsuario = new ArrayList<>();

        for (LocalUsuario local : locais) {

            locaisUsuario.add(LocalUsuarioDTO.builder()
                    .idLocal(local.getIdLocal())
                    .latitude(local.getLatitude())
                    .longitude(local.getLongitude())
                    .dataRegistro(local.getDataRegistro())
                    .build()
            );
            
        }

        return LocaisUsuarioDTO.builder()
                .idUsuario(user.getIdUsuario())
                .email(user.getEmail())
                .telefone(user.getTelefone())
                .listaLocaisUsuario(locaisUsuario)
                .build();

    }

    public AdicionarLocalResponseDTO adicionarLocal(@Valid AdicionarLocalDTO data) {

        User user = userRepository.findById(data.idUsuario()).orElseThrow(() -> new RuntimeException("USUÁRIO INVÁLIDO"));

        LocalUsuario local = localUsuarioRepository.save
                (
                    LocalUsuario.builder()
                        .usuario(user)
                        .dataRegistro(LocalDate.now())
                        .latitude(data.latitude())
                        .longitude(data.longitude())
                        .build()
                );

        return AdicionarLocalResponseDTO.builder()
                .idLocal(local.getIdLocal())
                .latitude(local.getLatitude())
                .longitude(local.getLongitude())
                .dataRegistro(local.getDataRegistro())
                .usuarioDTO(UsuarioDTO.builder()
                        .idUsuario(local.getUsuario().getIdUsuario())
                        .nomeUsuario(local.getUsuario().getNomeUsuario())
                        .telefone(local.getUsuario().getTelefone())
                        .email(local.getUsuario().getEmail())
                        .role(local.getUsuario().getRole())
                        .build()
                ).build();

    }

    public void deleteLocal(String idLocal) {

        LocalUsuario local = localUsuarioRepository.findById(idLocal).orElseThrow(() -> new RuntimeException("LOCAL INVÁLIDO"));

        localUsuarioRepository.delete(local);

    }

    public UltimoLocalUsuarioDTO buscarUltimoLocalUsuario(String idUsuario) {

        User user = userRepository.findById(idUsuario).orElseThrow(() -> new RuntimeException("USUÁRIO INVÁLIDO"));

        LocalUsuario ultimoLocal = localUsuarioRepository.findTopByUsuarioOrderByDataRegistroDesc(user)
                .orElseThrow(() -> new RuntimeException("NENHUM LOCAL ENCONTRADO"));

        LocalUsuarioDTO localDTO = LocalUsuarioDTO.builder()
                .idLocal(ultimoLocal.getIdLocal())
                .latitude(ultimoLocal.getLatitude())
                .longitude(ultimoLocal.getLongitude())
                .dataRegistro(ultimoLocal.getDataRegistro())
                .build();

        return UltimoLocalUsuarioDTO.builder()
                .idUsuario(user.getIdUsuario())
                .nomeUsuario(user.getNomeUsuario())
                .telefone(user.getTelefone())
                .email(user.getEmail())
                .local(LocaisUsuarioDTO.builder()
                        .idUsuario(user.getIdUsuario())
                        .telefone(user.getTelefone())
                        .email(user.getEmail())
                        .listaLocaisUsuario(List.of(localDTO))
                        .build())
                .build();

    }
}
