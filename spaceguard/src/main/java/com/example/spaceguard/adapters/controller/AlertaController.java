package com.example.spaceguard.adapters.controller;

import com.example.spaceguard.application.service.AlertaService;
import com.example.spaceguard.domain.alerta.dto.AlertaDTO;
import com.example.spaceguard.domain.alerta.dto.AlertaResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/alerta")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService alertaService;

    @PostMapping
    public ResponseEntity<EntityModel<AlertaResponseDTO>> createAlerta(@Valid @RequestBody AlertaDTO data) {

        AlertaResponseDTO response = alertaService.criarAlerta(data);

        EntityModel<AlertaResponseDTO> model = EntityModel.of(response,
                linkTo(methodOn(AlertaController.class).buscarAlerta(response.idAlerta())).withSelfRel(),
                linkTo(methodOn(AlertaController.class).buscarTodosAlertas()).withRel("alertas")
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(model);

    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<AlertaResponseDTO>> buscarAlerta(@PathVariable String id) {

        try {
            AlertaResponseDTO response = alertaService.buscarAlerta(id);

            EntityModel<AlertaResponseDTO> model = EntityModel.of(response,
                    linkTo(methodOn(AlertaController.class).buscarAlerta(id)).withSelfRel(),
                    linkTo(methodOn(AlertaController.class).buscarTodosAlertas()).withRel("alertas")
            );

            return ResponseEntity.ok(model);
        } catch (RuntimeException e) {
            return ResponseEntity.noContent().build();
        }

    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<AlertaResponseDTO>>> buscarTodosAlertas() {

        List<EntityModel<AlertaResponseDTO>> alertas = alertaService.buscarTodosAlertas().stream()
                .map(alerta -> EntityModel.of(alerta,
                        linkTo(methodOn(AlertaController.class).buscarAlerta(alerta.idAlerta())).withSelfRel()
                ))
                .toList();

        CollectionModel<EntityModel<AlertaResponseDTO>> model = CollectionModel.of(alertas,
                linkTo(methodOn(AlertaController.class).buscarTodosAlertas()).withSelfRel()
        );

        return ResponseEntity.ok(model);

    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<AlertaResponseDTO>> atualizarAlerta(@Valid @RequestBody AlertaDTO data, @PathVariable String id) {

        if (id.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        AlertaResponseDTO response = alertaService.atualizarAlerta(id, data);

        EntityModel<AlertaResponseDTO> model = EntityModel.of(response,
                linkTo(methodOn(AlertaController.class).buscarAlerta(id)).withSelfRel(),
                linkTo(methodOn(AlertaController.class).buscarTodosAlertas()).withRel("alertas")
        );

        return ResponseEntity.ok(model);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAlerta(@PathVariable String id) {

        alertaService.deletarAlerta(id);

        return ResponseEntity.noContent().build();

    }

}