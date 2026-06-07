package com.example.spaceguard.adapters.controller;

import com.example.spaceguard.application.service.FocoIncendioService;
import com.example.spaceguard.domain.foco.dto.FocoIncendioDTO;
import com.example.spaceguard.domain.foco.dto.FocoIncendioResponseDTO;
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
@RequestMapping("/foco-incendio")
@RequiredArgsConstructor
public class FocoIncendioController {

    private final FocoIncendioService focoIncendioService;

    @PostMapping
    public ResponseEntity<EntityModel<FocoIncendioResponseDTO>> createFocoIncencio(@Valid @RequestBody FocoIncendioDTO data) {

        FocoIncendioResponseDTO response = focoIncendioService.criarFocoIncencio(data);

        EntityModel<FocoIncendioResponseDTO> model = EntityModel.of(response,
                linkTo(methodOn(FocoIncendioController.class).buscarFocoIncendio(response.idFoco())).withSelfRel(),
                linkTo(methodOn(FocoIncendioController.class).buscarTodosFocos()).withRel("focos")
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(model);

    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<FocoIncendioResponseDTO>> buscarFocoIncendio(@PathVariable String id) {

        try {
            FocoIncendioResponseDTO response = focoIncendioService.buscarFocoIncendio(id);

            EntityModel<FocoIncendioResponseDTO> model = EntityModel.of(response,
                    linkTo(methodOn(FocoIncendioController.class).buscarFocoIncendio(id)).withSelfRel(),
                    linkTo(methodOn(FocoIncendioController.class).buscarTodosFocos()).withRel("focos")
            );

            return ResponseEntity.ok(model);
        } catch (RuntimeException e) {
            return ResponseEntity.noContent().build();
        }

    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<FocoIncendioResponseDTO>>> buscarTodosFocos() {

        List<EntityModel<FocoIncendioResponseDTO>> focos = focoIncendioService.buscarTodosFocos().stream()
                .map(foco -> EntityModel.of(foco,
                        linkTo(methodOn(FocoIncendioController.class).buscarFocoIncendio(foco.idFoco())).withSelfRel()
                ))
                .toList();

        CollectionModel<EntityModel<FocoIncendioResponseDTO>> model = CollectionModel.of(focos,
                linkTo(methodOn(FocoIncendioController.class).buscarTodosFocos()).withSelfRel()
        );

        return ResponseEntity.ok(model);

    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<FocoIncendioResponseDTO>> atualizarFocoIncendio(@Valid @RequestBody FocoIncendioDTO data, @PathVariable String id) {

        if (id.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        FocoIncendioResponseDTO response = focoIncendioService.atualizarFocoIncendio(id, data);

        EntityModel<FocoIncendioResponseDTO> model = EntityModel.of(response,
                linkTo(methodOn(FocoIncendioController.class).buscarFocoIncendio(id)).withSelfRel(),
                linkTo(methodOn(FocoIncendioController.class).buscarTodosFocos()).withRel("focos")
        );

        return ResponseEntity.ok(model);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarFocoIncendio(@PathVariable String id) {

        focoIncendioService.deletarFocoIncendio(id);

        return ResponseEntity.noContent().build();

    }

}