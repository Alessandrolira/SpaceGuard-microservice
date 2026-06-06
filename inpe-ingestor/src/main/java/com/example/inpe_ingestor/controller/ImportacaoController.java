package com.example.inpe_ingestor.controller;

import com.example.inpe_ingestor.service.ImportacaoResultado;
import com.example.inpe_ingestor.service.ImportacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Gatilho manual da importação.
 * <p>
 * POST /importar -> baixa o CSV do INPE, estima os valores, cria os Riscos por
 * bioma e insere os focos no spaceguard (tudo via Feign). Devolve o resumo.
 */
@RestController
@RequestMapping("/importar")
public class ImportacaoController {

    private final ImportacaoService importacaoService;

    public ImportacaoController(ImportacaoService importacaoService) {
        this.importacaoService = importacaoService;
    }

    @PostMapping
    public ResponseEntity<ImportacaoResultado> importar() {
        ImportacaoResultado resultado = importacaoService.importar();
        return ResponseEntity.ok(resultado);
    }
}
