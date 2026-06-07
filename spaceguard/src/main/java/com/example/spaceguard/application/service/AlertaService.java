package com.example.spaceguard.application.service;

import com.example.spaceguard.domain.alerta.Alerta;
import com.example.spaceguard.domain.alerta.AlertaRepository;
import com.example.spaceguard.domain.alerta.dto.AlertaDTO;
import com.example.spaceguard.domain.alerta.dto.AlertaResponseDTO;
import com.example.spaceguard.domain.foco.FocoIncendio;
import com.example.spaceguard.domain.foco.FocoIncendioRepository;
import com.example.spaceguard.domain.foco.dto.FocoIncendioResponseDTO;
import com.example.spaceguard.domain.risco.dto.RiscoDTO;
import com.example.spaceguard.domain.user.User;
import com.example.spaceguard.domain.user.UserRepository;
import com.example.spaceguard.domain.user.dto.UsuarioDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final UserRepository userRepository;
    private final FocoIncendioRepository focoIncendioRepository;

    @CacheEvict(value = "alertas", allEntries = true)
    public AlertaResponseDTO criarAlerta(@Valid AlertaDTO data) {

        User usuario = userRepository.findById(data.idUsuario()).orElseThrow(() -> new RuntimeException("USUÁRIO INVÁLIDO"));
        FocoIncendio focoIncendio = focoIncendioRepository.findById(data.idFoco()).orElseThrow(() -> new RuntimeException("FOCO INVÁLIDO"));


        Alerta alerta = alertaRepository.save(

                Alerta.builder()
                        .tituloAlerta(data.tituloAlerta())
                        .msgAlerta(data.msgAlerta())
                        .distancia(data.distancia())
                        .dataEmissao(data.dataEmissao())
                        .dataExpir(data.dataExpir())
                        .usuario(usuario)
                        .focoIncendio(focoIncendio)
                        .build()

        );

        return AlertaResponseDTO.builder()
                .idAlerta(alerta.getIdAlerta())
                .tituloAlerta(alerta.getTituloAlerta())
                .msgAlerta(alerta.getMsgAlerta())
                .dataEmissao(alerta.getDataEmissao())
                .dataExpir(alerta.getDataExpir())
                .usuario(
                        UsuarioDTO.builder()
                                .idUsuario(alerta.getUsuario().getIdUsuario())
                                .nomeUsuario(alerta.getUsuario().getNomeUsuario())
                                .telefone(alerta.getUsuario().getTelefone())
                                .email(alerta.getUsuario().getEmail())
                                .dataCadastro(alerta.getUsuario().getDataCadastro())
                                .role(alerta.getUsuario().getRole())
                                .build()
                )
                .focoIncendio(
                        FocoIncendioResponseDTO.builder()
                                .idFoco(focoIncendio.getIdFoco())
                                .latitude(alerta.getFocoIncendio().getLatitude())
                                .longitude(alerta.getFocoIncendio().getLongitude())
                                .riscoFogo(alerta.getFocoIncendio().getRiscoFogo())
                                .bioma(alerta.getFocoIncendio().getBioma())
                                .municipio(alerta.getFocoIncendio().getMunicipio())
                                .estado(alerta.getFocoIncendio().getEstado())
                                .focoAtivo(alerta.getFocoIncendio().getFocoAtivo())
                                .risco(RiscoDTO.builder()
                                        .nivelRisco(alerta.getFocoIncendio().getRisco().getNivelRisco())
                                        .pontuacao(alerta.getFocoIncendio().getRisco().getPontuacao())
                                        .build()
                                )
                                .build()

                )
                .build();


    }

    @Cacheable(value = "alertas", key = "#id")
    public AlertaResponseDTO buscarAlerta(String id) {

        Alerta alerta = alertaRepository.findById(id).orElseThrow(() -> new RuntimeException("ALERTA INVÁLIDO"));

        return AlertaResponseDTO.builder()
                .idAlerta(alerta.getIdAlerta())
                .tituloAlerta(alerta.getTituloAlerta())
                .msgAlerta(alerta.getMsgAlerta())
                .distancia(alerta.getDistancia())
                .dataEmissao(alerta.getDataEmissao())
                .dataExpir(alerta.getDataExpir())
                .usuario(
                        UsuarioDTO.builder()
                                .idUsuario(alerta.getUsuario().getIdUsuario())
                                .nomeUsuario(alerta.getUsuario().getNomeUsuario())
                                .telefone(alerta.getUsuario().getTelefone())
                                .email(alerta.getUsuario().getEmail())
                                .dataCadastro(alerta.getUsuario().getDataCadastro())
                                .role(alerta.getUsuario().getRole())
                                .build()
                )
                .focoIncendio(
                        FocoIncendioResponseDTO.builder()
                                .idFoco(alerta.getFocoIncendio().getIdFoco())
                                .latitude(alerta.getFocoIncendio().getLatitude())
                                .longitude(alerta.getFocoIncendio().getLongitude())
                                .riscoFogo(alerta.getFocoIncendio().getRiscoFogo())
                                .bioma(alerta.getFocoIncendio().getBioma())
                                .municipio(alerta.getFocoIncendio().getMunicipio())
                                .estado(alerta.getFocoIncendio().getEstado())
                                .focoAtivo(alerta.getFocoIncendio().getFocoAtivo())
                                .risco(RiscoDTO.builder()
                                        .nivelRisco(alerta.getFocoIncendio().getRisco().getNivelRisco())
                                        .pontuacao(alerta.getFocoIncendio().getRisco().getPontuacao())
                                        .build()
                                )
                                .build()
                )
                .build();

    }

    @Cacheable(value = "alertas", key = "'all'")
    public List<AlertaResponseDTO> buscarTodosAlertas() {

        return alertaRepository.findAll().stream()
                .map(alerta -> AlertaResponseDTO.builder()
                        .idAlerta(alerta.getIdAlerta())
                        .tituloAlerta(alerta.getTituloAlerta())
                        .msgAlerta(alerta.getMsgAlerta())
                        .distancia(alerta.getDistancia())
                        .dataEmissao(alerta.getDataEmissao())
                        .dataExpir(alerta.getDataExpir())
                        .usuario(
                                UsuarioDTO.builder()
                                        .idUsuario(alerta.getUsuario().getIdUsuario())
                                        .nomeUsuario(alerta.getUsuario().getNomeUsuario())
                                        .telefone(alerta.getUsuario().getTelefone())
                                        .email(alerta.getUsuario().getEmail())
                                        .dataCadastro(alerta.getUsuario().getDataCadastro())
                                        .role(alerta.getUsuario().getRole())
                                        .build()
                        )
                        .focoIncendio(
                                FocoIncendioResponseDTO.builder()
                                        .idFoco(alerta.getFocoIncendio().getIdFoco())
                                        .latitude(alerta.getFocoIncendio().getLatitude())
                                        .longitude(alerta.getFocoIncendio().getLongitude())
                                        .riscoFogo(alerta.getFocoIncendio().getRiscoFogo())
                                        .bioma(alerta.getFocoIncendio().getBioma())
                                        .municipio(alerta.getFocoIncendio().getMunicipio())
                                        .estado(alerta.getFocoIncendio().getEstado())
                                        .focoAtivo(alerta.getFocoIncendio().getFocoAtivo())
                                        .risco(RiscoDTO.builder()
                                                .nivelRisco(alerta.getFocoIncendio().getRisco().getNivelRisco())
                                                .pontuacao(alerta.getFocoIncendio().getRisco().getPontuacao())
                                                .build()
                                        )
                                        .build()
                        )
                        .build()
                ).toList();

    }

    @CacheEvict(value = "alertas", allEntries = true)
    public AlertaResponseDTO atualizarAlerta(String id, @Valid AlertaDTO data) {

        Alerta alerta = alertaRepository.findById(id).orElseThrow(() -> new RuntimeException("ALERTA INVÁLIDO"));
        User usuario = userRepository.findById(data.idUsuario()).orElseThrow(() -> new RuntimeException("USUÁRIO INVÁLIDO"));
        FocoIncendio focoIncendio = focoIncendioRepository.findById(data.idFoco()).orElseThrow(() -> new RuntimeException("FOCO INVÁLIDO"));

        alerta.setTituloAlerta(data.tituloAlerta());
        alerta.setMsgAlerta(data.msgAlerta());
        alerta.setDistancia(data.distancia());
        alerta.setDataEmissao(data.dataEmissao());
        alerta.setDataExpir(data.dataExpir());
        alerta.setUsuario(usuario);
        alerta.setFocoIncendio(focoIncendio);

        Alerta alertaAtualizado = alertaRepository.save(alerta);

        return AlertaResponseDTO.builder()
                .idAlerta(alertaAtualizado.getIdAlerta())
                .tituloAlerta(alertaAtualizado.getTituloAlerta())
                .msgAlerta(alertaAtualizado.getMsgAlerta())
                .distancia(alertaAtualizado.getDistancia())
                .dataEmissao(alertaAtualizado.getDataEmissao())
                .dataExpir(alertaAtualizado.getDataExpir())
                .usuario(
                        UsuarioDTO.builder()
                                .idUsuario(alertaAtualizado.getUsuario().getIdUsuario())
                                .nomeUsuario(alertaAtualizado.getUsuario().getNomeUsuario())
                                .telefone(alertaAtualizado.getUsuario().getTelefone())
                                .email(alertaAtualizado.getUsuario().getEmail())
                                .dataCadastro(alertaAtualizado.getUsuario().getDataCadastro())
                                .role(alertaAtualizado.getUsuario().getRole())
                                .build()
                )
                .focoIncendio(
                        FocoIncendioResponseDTO.builder()
                                .idFoco(alertaAtualizado.getFocoIncendio().getIdFoco())
                                .latitude(alertaAtualizado.getFocoIncendio().getLatitude())
                                .longitude(alertaAtualizado.getFocoIncendio().getLongitude())
                                .riscoFogo(alertaAtualizado.getFocoIncendio().getRiscoFogo())
                                .bioma(alertaAtualizado.getFocoIncendio().getBioma())
                                .municipio(alertaAtualizado.getFocoIncendio().getMunicipio())
                                .estado(alertaAtualizado.getFocoIncendio().getEstado())
                                .focoAtivo(alertaAtualizado.getFocoIncendio().getFocoAtivo())
                                .risco(RiscoDTO.builder()
                                        .nivelRisco(alertaAtualizado.getFocoIncendio().getRisco().getNivelRisco())
                                        .pontuacao(alertaAtualizado.getFocoIncendio().getRisco().getPontuacao())
                                        .build()
                                )
                                .build()
                )
                .build();

    }

    @CacheEvict(value = "alertas", allEntries = true)
    public void deletarAlerta(String id) {

        Alerta alerta = alertaRepository.findById(id).orElseThrow(() -> new RuntimeException("ALERTA INVÁLIDO"));

        alertaRepository.delete(alerta);

    }

}