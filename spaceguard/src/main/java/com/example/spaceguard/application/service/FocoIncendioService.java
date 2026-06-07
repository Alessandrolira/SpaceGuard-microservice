package com.example.spaceguard.application.service;

import com.example.spaceguard.domain.foco.FocoIncendio;
import com.example.spaceguard.domain.foco.FocoIncendioRepository;
import com.example.spaceguard.domain.foco.dto.FocoIncendioDTO;
import com.example.spaceguard.domain.foco.dto.FocoIncendioResponseDTO;
import com.example.spaceguard.domain.risco.Risco;
import com.example.spaceguard.domain.risco.RiscoReposiroty;
import com.example.spaceguard.domain.risco.dto.RiscoDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FocoIncendioService {

    private final FocoIncendioRepository focoIncendioRepository;
    private final RiscoReposiroty riscoReposiroty;

    @CacheEvict(value = "focos", allEntries = true)
    public FocoIncendioResponseDTO criarFocoIncencio(@Valid FocoIncendioDTO data) {

        Risco risco = riscoReposiroty.findById(data.idRisco()).orElseThrow(() -> new RuntimeException("RISCO INVÁLIDO"));

        FocoIncendio focoIncendio = focoIncendioRepository.save(
                FocoIncendio.builder()
                        .dataDeteccao(data.dataDeteccao())
                        .latitude(data.latitude())
                        .longitude(data.longitude())
                        .riscoFogo(data.riscoFogo())
                        .bioma(data.bioma())
                        .municipio(data.municipio())
                        .estado(data.estado())
                        .focoAtivo(data.focoAtivo())
                        .risco(risco)
                        .build()
        );

        return FocoIncendioResponseDTO.builder()
                .idFoco(focoIncendio.getIdFoco())
                .latitude(focoIncendio.getLatitude())
                .longitude(focoIncendio.getLongitude())
                .riscoFogo(focoIncendio.getRiscoFogo())
                .bioma(focoIncendio.getBioma())
                .municipio(focoIncendio.getMunicipio())
                .estado(focoIncendio.getEstado())
                .focoAtivo(focoIncendio.getFocoAtivo())
                .risco(RiscoDTO.builder()
                        .nivelRisco(risco.getNivelRisco())
                        .pontuacao(risco.getPontuacao())
                        .build())
                .build();

    }

    @Cacheable(value = "focos", key = "#id")
    public FocoIncendioResponseDTO buscarFocoIncendio(String id) {

        FocoIncendio focoIncendio = focoIncendioRepository.findById(id).orElseThrow(() -> new RuntimeException("FOCO INVÁLIDO"));

        return FocoIncendioResponseDTO.builder()
                .idFoco(focoIncendio.getIdFoco())
                .latitude(focoIncendio.getLatitude())
                .longitude(focoIncendio.getLongitude())
                .riscoFogo(focoIncendio.getRiscoFogo())
                .bioma(focoIncendio.getBioma())
                .municipio(focoIncendio.getMunicipio())
                .estado(focoIncendio.getEstado())
                .focoAtivo(focoIncendio.getFocoAtivo())
                .risco(RiscoDTO.builder()
                        .nivelRisco(focoIncendio.getRisco().getNivelRisco())
                        .pontuacao(focoIncendio.getRisco().getPontuacao())
                        .build())
                .build();

    }

    @Cacheable(value = "focos", key = "'all'")
    public List<FocoIncendioResponseDTO> buscarTodosFocos() {

        return focoIncendioRepository.findAll().stream()
                .map(focoIncendio -> FocoIncendioResponseDTO.builder()
                        .idFoco(focoIncendio.getIdFoco())
                        .latitude(focoIncendio.getLatitude())
                        .longitude(focoIncendio.getLongitude())
                        .riscoFogo(focoIncendio.getRiscoFogo())
                        .bioma(focoIncendio.getBioma())
                        .municipio(focoIncendio.getMunicipio())
                        .estado(focoIncendio.getEstado())
                        .focoAtivo(focoIncendio.getFocoAtivo())
                        .risco(RiscoDTO.builder()
                                .nivelRisco(focoIncendio.getRisco().getNivelRisco())
                                .pontuacao(focoIncendio.getRisco().getPontuacao())
                                .build())
                        .build()
                ).toList();

    }

    @CacheEvict(value = "focos", allEntries = true)
    public FocoIncendioResponseDTO atualizarFocoIncendio(String id, @Valid FocoIncendioDTO data) {

        FocoIncendio focoIncendio = focoIncendioRepository.findById(id).orElseThrow(() -> new RuntimeException("FOCO INVÁLIDO"));
        Risco risco = riscoReposiroty.findById(data.idRisco()).orElseThrow(() -> new RuntimeException("RISCO INVÁLIDO"));

        focoIncendio.setDataDeteccao(data.dataDeteccao());
        focoIncendio.setLatitude(data.latitude());
        focoIncendio.setLongitude(data.longitude());
        focoIncendio.setRiscoFogo(data.riscoFogo());
        focoIncendio.setBioma(data.bioma());
        focoIncendio.setMunicipio(data.municipio());
        focoIncendio.setEstado(data.estado());
        focoIncendio.setFocoAtivo(data.focoAtivo());
        focoIncendio.setRisco(risco);

        FocoIncendio focoAtualizado = focoIncendioRepository.save(focoIncendio);

        return FocoIncendioResponseDTO.builder()
                .idFoco(focoAtualizado.getIdFoco())
                .latitude(focoAtualizado.getLatitude())
                .longitude(focoAtualizado.getLongitude())
                .riscoFogo(focoAtualizado.getRiscoFogo())
                .bioma(focoAtualizado.getBioma())
                .municipio(focoAtualizado.getMunicipio())
                .estado(focoAtualizado.getEstado())
                .focoAtivo(focoAtualizado.getFocoAtivo())
                .risco(RiscoDTO.builder()
                        .nivelRisco(focoAtualizado.getRisco().getNivelRisco())
                        .pontuacao(focoAtualizado.getRisco().getPontuacao())
                        .build())
                .build();

    }

    @CacheEvict(value = "focos", allEntries = true)
    public void deletarFocoIncendio(String id) {

        FocoIncendio focoIncendio = focoIncendioRepository.findById(id).orElseThrow(() -> new RuntimeException("FOCO INVÁLIDO"));

        focoIncendioRepository.delete(focoIncendio);

    }

}