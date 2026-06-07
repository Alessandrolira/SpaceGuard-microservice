package com.example.spaceguard.adapters.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/versao")
public class VersaoController {
    @GetMapping
    public Map<String, String> versao() {
        return Map.of(
                "aplicacao", "SpaceGuard",
                "versao", "3.0",
                "mensagem", "Deploy automatizado via Azure DevOps CI/CD"
        );
    }
}
