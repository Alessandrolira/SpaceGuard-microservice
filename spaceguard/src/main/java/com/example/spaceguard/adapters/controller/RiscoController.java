package com.example.spaceguard.adapters.controller;

import com.example.spaceguard.application.service.RiscoService;
import com.example.spaceguard.domain.risco.dto.RiscoDTO;
import com.example.spaceguard.domain.risco.dto.RiscoResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RequiredArgsConstructor
@RequestMapping("/risco")
@RestController
public class RiscoController {

    private final RiscoService riscoService;

    @PostMapping
    public ResponseEntity<EntityModel<RiscoResponseDTO>> createRisco(@Valid @RequestBody RiscoDTO data) {

        RiscoResponseDTO response = riscoService.criarRisco(data);

        EntityModel<RiscoResponseDTO> model = EntityModel.of(response,
                linkTo(methodOn(RiscoController.class).buscarRisco(response.idRisco())).withSelfRel(),
                linkTo(methodOn(RiscoController.class).buscarTodosRiscos()).withRel("riscos")
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(model);

    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<RiscoResponseDTO>> buscarRisco(@PathVariable String id) {

        try {
            RiscoResponseDTO response = riscoService.buscarRisco(id);

            EntityModel<RiscoResponseDTO> model = EntityModel.of(response,
                    linkTo(methodOn(RiscoController.class).buscarRisco(id)).withSelfRel(),
                    linkTo(methodOn(RiscoController.class).buscarTodosRiscos()).withRel("riscos")
            );

            return ResponseEntity.ok(model);
        } catch (RuntimeException e) {
            return ResponseEntity.noContent().build();
        }

    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<RiscoResponseDTO>>> buscarTodosRiscos() {

        List<EntityModel<RiscoResponseDTO>> riscos = riscoService.buscarTodosRiscos().stream()
                .map(risco -> EntityModel.of(risco,
                        linkTo(methodOn(RiscoController.class).buscarRisco(risco.idRisco())).withSelfRel()
                ))
                .toList();

        CollectionModel<EntityModel<RiscoResponseDTO>> model = CollectionModel.of(riscos,
                linkTo(methodOn(RiscoController.class).buscarTodosRiscos()).withSelfRel()
        );

        return ResponseEntity.ok(model);

    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<RiscoResponseDTO>> atualizarRisco(@Valid @RequestBody RiscoDTO data, @PathVariable String id) {

        if (id.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        RiscoResponseDTO response = riscoService.atualizarRisco(id, data);

        EntityModel<RiscoResponseDTO> model = EntityModel.of(response,
                linkTo(methodOn(RiscoController.class).buscarRisco(id)).withSelfRel(),
                linkTo(methodOn(RiscoController.class).buscarTodosRiscos()).withRel("riscos")
        );

        return ResponseEntity.ok(model);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarRisco(@PathVariable String id) {

        riscoService.deletarRisco(id);

        return ResponseEntity.noContent().build();

    }

}