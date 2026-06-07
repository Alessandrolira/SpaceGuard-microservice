package com.example.spaceguard.application.service;

import com.example.spaceguard.domain.risco.Risco;
import com.example.spaceguard.domain.risco.RiscoReposiroty;
import com.example.spaceguard.domain.risco.dto.RiscoDTO;
import com.example.spaceguard.domain.risco.dto.RiscoResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RiscoService {

    private final RiscoReposiroty riscoReposiroty;

    @CacheEvict(value = "riscos", allEntries = true)
    public RiscoResponseDTO criarRisco(@Valid RiscoDTO data) {

        Risco risco = riscoReposiroty.save(
                Risco.builder()
                        .nivelRisco(data.nivelRisco())
                        .pontuacao(data.pontuacao())
                        .build()
        );

        return RiscoResponseDTO.builder()
                .idRisco(risco.getIdRisco())
                .nivelRisco(risco.getNivelRisco())
                .pontuacao(risco.getPontuacao())
                .build();

    }

    @Cacheable(value = "riscos", key = "#id")
    public RiscoResponseDTO buscarRisco(String id) {

        Risco risco = riscoReposiroty.findById(id).orElseThrow(() -> new RuntimeException("RISCO INVÁLIDO"));

        return RiscoResponseDTO.builder()
                .idRisco(risco.getIdRisco())
                .nivelRisco(risco.getNivelRisco())
                .pontuacao(risco.getPontuacao())
                .build();

    }

    @Cacheable(value = "riscos", key = "'all'")
    public List<RiscoResponseDTO> buscarTodosRiscos() {

        return riscoReposiroty.findAll().stream()
                .map(risco -> RiscoResponseDTO.builder()
                        .idRisco(risco.getIdRisco())
                        .nivelRisco(risco.getNivelRisco())
                        .pontuacao(risco.getPontuacao())
                        .build()
                ).toList();

    }

    @CacheEvict(value = "riscos", allEntries = true)
    public RiscoResponseDTO atualizarRisco(String id, @Valid RiscoDTO data) {

        Risco risco = riscoReposiroty.findById(id).orElseThrow(() -> new RuntimeException("RISCO INVÁLIDO"));

        risco.setNivelRisco(data.nivelRisco());
        risco.setPontuacao(data.pontuacao());

        Risco riscoAtualizado = riscoReposiroty.save(risco);

        return RiscoResponseDTO.builder()
                .idRisco(riscoAtualizado.getIdRisco())
                .nivelRisco(riscoAtualizado.getNivelRisco())
                .pontuacao(riscoAtualizado.getPontuacao())
                .build();

    }

    @CacheEvict(value = "riscos", allEntries = true)
    public void deletarRisco(String id) {

        Risco risco = riscoReposiroty.findById(id).orElseThrow(() -> new RuntimeException("RISCO INVÁLIDO"));

        riscoReposiroty.delete(risco);

    }

}