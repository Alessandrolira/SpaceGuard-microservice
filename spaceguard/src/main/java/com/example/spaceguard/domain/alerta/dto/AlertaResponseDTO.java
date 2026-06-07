package com.example.spaceguard.domain.alerta.dto;

import com.example.spaceguard.domain.foco.dto.FocoIncendioDTO;
import com.example.spaceguard.domain.foco.dto.FocoIncendioResponseDTO;
import com.example.spaceguard.domain.user.dto.UsuarioDTO;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record AlertaResponseDTO(

        String idAlerta,
        String tituloAlerta,
        String msgAlerta,
        BigDecimal distancia,
        LocalDate dataEmissao,
        LocalDate dataExpir,
        UsuarioDTO usuario,
        FocoIncendioResponseDTO focoIncendio

) {
}
