package com.example.spaceguard.domain.user;

import com.example.spaceguard.domain.alerta.Alerta;
import com.example.spaceguard.domain.localUsuario.LocalUsuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "usuario")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idUsuario;

    private String nomeUsuario;

    private String telefone;

    private String email;

    private String senha;

    private LocalDate dataCadastro;

    private UserRole role;

    @OneToMany(mappedBy = "usuario")
    private List<Alerta> alertas;

    @OneToMany(mappedBy = "usuario")
    private List<LocalUsuario> locaisUsuario;

}
