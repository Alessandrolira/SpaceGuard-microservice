package com.example.spaceguard.adapters.controller;

import com.example.spaceguard.application.service.LocalUsuarioService;
import com.example.spaceguard.domain.localUsuario.dto.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/local-usuario")
@RequiredArgsConstructor
public class LocalUsuarioController {

    private final LocalUsuarioService localUsuarioService;

    @GetMapping("/usuario/{id}")
    private ResponseEntity<LocaisUsuarioDTO> buscarLocal(@PathVariable String id){

        try {
            LocaisUsuarioDTO response = localUsuarioService.buscarUsuario(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.noContent().build();
        }

    }

    @PostMapping
    private ResponseEntity<AdicionarLocalResponseDTO> adicionarLocal(@Valid @RequestBody AdicionarLocalDTO data){

        AdicionarLocalResponseDTO response = localUsuarioService.adicionarLocal(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deletarLocal(@PathVariable String idLocal){

        localUsuarioService.deleteLocal(idLocal);

        return ResponseEntity.noContent().build();

    }

    @GetMapping("/ultimoLocal/usuario/{id}")
    private ResponseEntity<UltimoLocalUsuarioDTO> buscarUltimoLocal(@PathVariable String id){

        try {
            UltimoLocalUsuarioDTO response = localUsuarioService.buscarUltimoLocalUsuario(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.noContent().build();
        }

    }


}
