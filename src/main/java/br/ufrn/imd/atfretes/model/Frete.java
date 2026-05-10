package br.ufrn.imd.atfretes.model;

public record Frete(
        String cepOrigem,
        String cepDestino,
        double peso,
        double valor
) {}
