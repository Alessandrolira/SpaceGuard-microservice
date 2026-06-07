package com.example.spaceguard.domain.localUsuario.dto;

import com.example.spaceguard.domain.user.dto.UsuarioDTO;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record AdicionarLocalResponseDTO(

        String idLocal,
        BigDecimal latitude,
        BigDecimal longitude,
        LocalDate dataRegistro,
        UsuarioDTO usuarioDTO

) {
}
