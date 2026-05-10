package br.ufrn.imd.atfretes.service;

import br.ufrn.imd.atfretes.model.Frete;
import org.springframework.stereotype.Service;

@Service
public class FreteService {

    private final double TAXA_DISTANCIA = 0.50;
    private final double TAXA_PESO = 2.00;

    public Frete calcularFrete(Frete pedido) {
        // Simulação de distância fixa para o cálculo inicial
        double distanciaSimulada = 100.0;

        double valorCalculado = (distanciaSimulada * TAXA_DISTANCIA) + (pedido.peso() * TAXA_PESO);

        return new Frete(
                pedido.cepOrigem(),
                pedido.cepDestino(),
                pedido.peso(),
                valorCalculado
        );
    }
}
