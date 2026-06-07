package com.example.spaceguard.domain.foco;

import com.example.spaceguard.domain.alerta.Alerta;
import com.example.spaceguard.domain.risco.Risco;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "foco_incendio")
@Builder
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class FocoIncendio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idFoco;

    private LocalDate dataDeteccao;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private BigDecimal riscoFogo;

    private String bioma;

    private String municipio;

    private String estado;

    private Boolean focoAtivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "risco_id_risco", nullable = false)
    private Risco risco;

    @OneToMany(mappedBy = "focoIncendio")
    private List<Alerta> alertas;

}
