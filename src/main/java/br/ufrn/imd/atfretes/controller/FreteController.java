package br.ufrn.imd.atfretes.controller;

import br.ufrn.imd.atfretes.model.Frete;
import br.ufrn.imd.atfretes.service.FreteService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/frete")
public class FreteController {

    private final FreteService freteService;

    public FreteController(FreteService freteService) {
        this.freteService = freteService;
    }

    @PostMapping("/calcular")
    public Frete calcular(@RequestBody Frete pedido) {
        return freteService.calcularFrete(pedido);
    }
}
