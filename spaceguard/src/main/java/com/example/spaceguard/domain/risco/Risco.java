package com.example.spaceguard.domain.risco;

import com.example.spaceguard.domain.foco.FocoIncendio;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "risco")
@Builder
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Risco {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idRisco;

    @Enumerated(EnumType.STRING)

    private EnumNivelRisco nivelRisco;

    private BigDecimal pontuacao;

    @OneToMany(mappedBy = "risco")
    private List<FocoIncendio> focos;

}
