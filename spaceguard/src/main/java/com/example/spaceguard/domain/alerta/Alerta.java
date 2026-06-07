package com.example.spaceguard.domain.alerta;

import com.example.spaceguard.domain.foco.FocoIncendio;
import com.example.spaceguard.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "alerta")
@Builder
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idAlerta;

    private String tituloAlerta;

    private String msgAlerta;

    private BigDecimal distancia;

    private LocalDate dataEmissao;

    private LocalDate dataExpir;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id_usuario", nullable = false)
    private User usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "foco_incendio_id_foco", nullable = false)
    private FocoIncendio focoIncendio;

}
