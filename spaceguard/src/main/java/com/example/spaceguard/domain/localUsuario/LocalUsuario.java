package com.example.spaceguard.domain.localUsuario;

import com.example.spaceguard.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "local_usuario")
@Builder
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class LocalUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idLocal;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private LocalDate dataRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id_usuario", nullable = false)
    private User usuario;

}
