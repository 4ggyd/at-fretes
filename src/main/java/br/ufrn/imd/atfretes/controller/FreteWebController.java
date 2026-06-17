package br.ufrn.imd.atfretes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;

@Controller
public class FreteWebController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/")
    public String exibirFormulario(Model model) {
        model.addAttribute("calculado", false);
        return "calculo-frete";
    }

    @PostMapping("/calcular")
    public String handleCalcularFrete(
            @RequestParam("cepOrigem") String cepOrigem,
            @RequestParam("cepDestino") String cepDestino,
            @RequestParam("peso") String pesoRaw,
            @RequestParam("valorDeclarado") String valorDeclaradoRaw,
            Model model) {
        
        String cepOrigemLimpo = cepOrigem.trim().replace("-", "").replace(".", "");
        String cepDestinoLimpo = cepDestino.trim().replace("-", "").replace(".", "");

        model.addAttribute("cepOrigem", cepOrigem);
        model.addAttribute("cepDestino", cepDestino);
        model.addAttribute("peso", pesoRaw);
        model.addAttribute("valorDeclarado", valorDeclaradoRaw);

        try {
            if (cepOrigemLimpo.isEmpty() || cepDestinoLimpo.isEmpty() || pesoRaw.isEmpty() || valorDeclaradoRaw.isEmpty()) {
                model.addAttribute("erro", "Erro: Preencha todos os campos do formulário.");
                model.addAttribute("calculado", false);
                return "calculo-frete";
            }

            double pesoBruto = Double.parseDouble(pesoRaw.replace(",", "."));
            double valorDeclarado = Double.parseDouble(valorDeclaradoRaw.replace(",", "."));

            if (pesoBruto <= 0 || valorDeclarado < 0) {
                model.addAttribute("erro", "Erro: Os valores inseridos devem ser maiores que zero.");
                model.addAttribute("calculado", false);
                return "calculo-frete";
            }

            double pesoFinal = (pesoBruto > 100.0) ? (pesoBruto / 1000.0) : pesoBruto;

            Coordenada coordOrigem = buscarCoordenadasSeguras(cepOrigemLimpo);
            if (coordOrigem == null) {
                model.addAttribute("erro", "Erro: O serviço postal não conseguiu validar o CEP de Origem.");
                model.addAttribute("calculado", false);
                return "calculo-frete";
            }

            Coordenada coordDestino = buscarCoordenadasSeguras(cepDestinoLimpo);
            if (coordDestino == null) {
                model.addAttribute("erro", "Erro: O serviço postal não conseguiu validar o CEP de Destino.");
                model.addAttribute("calculado", false);
                return "calculo-frete";
            }

            double distanciaKm = calcularDistanciaHaversine(coordOrigem.lat, coordOrigem.lng, coordDestino.lat, coordDestino.lng);
            double taxaEnvioFixa = 15.90;
            double fatorDistancia = (distanciaKm > 250.0) ? 0.015 : 0.45;
            double distanciaFinal = (distanciaKm <= 0.0) ? 1.0 : distanciaKm;
            
            double taxaPorQuiloERota = pesoFinal * (fatorDistancia + (distanciaFinal * 0.005));
            double taxaSeguroAdValorem = valorDeclarado * 0.005;
            double valorTotalFrete = taxaEnvioFixa + taxaPorQuiloERota + taxaSeguroAdValorem;

            ResultadoFrete resultado = new ResultadoFrete(
                coordOrigem.nome, coordDestino.nome,
                coordOrigem.lat, coordOrigem.lng,
                coordDestino.lat, coordDestino.lng,
                String.format(Locale.US, "%.2f", distanciaFinal),
                String.format(Locale.US, "%.2f", pesoFinal),
                String.format(Locale.US, "%.2f", taxaEnvioFixa),
                String.format(Locale.US, "%.2f", taxaPorQuiloERota),
                String.format(Locale.US, "%.2f", taxaSeguroAdValorem),
                String.format(Locale.US, "%.2f", valorTotalFrete)
            );

            model.addAttribute("calculado", true);
            model.addAttribute("resultado", resultado);

            // Uso explícito de Locale.US para forçar pontos decimais e evitar quebra no JavaScript
            String jsonCoordenadas = String.format(Locale.US,
                "{\"latOrigem\":%.6f,\"lngOrigem\":%.6f,\"latDestino\":%.6f,\"lngDestino\":%.6f,\"nomeOrigem\":\"%s\",\"nomeDestino\":\"%s\"}",
                coordOrigem.lat, coordOrigem.lng, coordDestino.lat, coordDestino.lng,
                coordOrigem.nome.replace("\"", "\\\""), coordDestino.nome.replace("\"", "\\\"")
            );
            model.addAttribute("dadosMapaJson", jsonCoordenadas);

        } catch (NumberFormatException e) {
            model.addAttribute("erro", "Erro: Insira dados numéricos válidos nos campos de Peso e Valor.");
            model.addAttribute("calculado", false);
        } catch (Exception e) {
            model.addAttribute("erro", "Erro operacional no cálculo do frete: " + e.getMessage());
            model.addAttribute("calculado", false);
        }

        return "calculo-frete";
    }

    private Coordenada buscarCoordenadasSeguras(String cep) {
        String localidade = "";
        String uf = "";
        String bairro = "";
        String logradouro = "";
        String rotuloExibicao = "CEP " + cep;

        try {
            String urlViaCep = "https://viacep.com.br/ws/" + cep + "/json/";
            String respostaViaCep = restTemplate.getForObject(urlViaCep, String.class);
            JsonNode nodeViaCep = objectMapper.readTree(respostaViaCep);

            if (nodeViaCep != null && !nodeViaCep.has("erro")) {
                localidade = nodeViaCep.get("localidade").asText();
                uf = nodeViaCep.get("uf").asText();
                bairro = nodeViaCep.has("bairro") ? nodeViaCep.get("bairro").asText() : "";
                logradouro = nodeViaCep.has("logradouro") ? nodeViaCep.get("logradouro").asText() : "";
                rotuloExibicao = (bairro.isEmpty() ? "" : bairro + " - ") + localidade + "/" + uf;
            } else {
                return null;
            }
        } catch (Exception e) {
            System.err.println("Aviso: ViaCEP offline ou instável para o CEP " + cep);
        }

        try {
            String queryBusca = logradouro + " " + localidade + " " + uf + " Brazil";
            if (logradouro.isEmpty()) {
                queryBusca = localidade + " " + uf + " Brazil";
            }

            String urlNominatim = "https://nominatim.openstreetmap.org/search?format=json&limit=1&q=" 
                    + URLEncoder.encode(queryBusca, StandardCharsets.UTF_8);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 ATFretes Engine Core Pro (suporte@atfretes.com.br)");
            HttpEntity<String> entity = new HttpEntity<>(headers);
            HttpMethod metodoGet = Objects.requireNonNull(HttpMethod.GET);

            ResponseEntity<String> response = restTemplate.exchange(urlNominatim, metodoGet, entity, String.class);
            JsonNode nodeNominatim = objectMapper.readTree(response.getBody());

            if (nodeNominatim != null && nodeNominatim.isArray() && nodeNominatim.size() > 0) {
                JsonNode primeiro = nodeNominatim.get(0);
                double lat = primeiro.get("lat").asDouble();
                double lon = primeiro.get("lon").asDouble();
                return new Coordenada(lat, lon, rotuloExibicao);
            }
        } catch (Exception e) {
            System.err.println("Aviso: Nominatim offline para o CEP " + cep + ". Aplicando coordenadas padrão.");
        }

        if (cep.startsWith("13106") || cep.startsWith("130") || cep.startsWith("131")) {
            return new Coordenada(-22.9064, -47.0616, (localidade.isEmpty() ? "Campinas - SP" : rotuloExibicao));
        } else if (cep.startsWith("59147") || cep.startsWith("59144") || cep.startsWith("590") || cep.startsWith("591") || cep.startsWith("59141")) {
            return new Coordenada(-5.914395, -35.244040, (localidade.isEmpty() ? "Parnamirim/Natal - RN" : rotuloExibicao));
        } else if (cep.startsWith("59500")) {
            return new Coordenada(-5.1116, -36.6344, (localidade.isEmpty() ? "Macau - RN" : rotuloExibicao));
        } else if (cep.startsWith("04") || cep.startsWith("01") || cep.startsWith("0")) {
            return new Coordenada(-23.5489, -46.6328, (localidade.isEmpty() ? "São Paulo - SP" : rotuloExibicao));
        }

        return new Coordenada(-15.7801, -47.9292, (localidade.isEmpty() ? "Terminal de Carga Central" : rotuloExibicao));
    }

    private double calcularDistanciaHaversine(double lat1, double lon1, double lat2, double lon2) {
        double raioTerra = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return raioTerra * c;
    }

    private static class Coordenada {
        double lat;
        double lng;
        String nome;

        Coordenada(double lat, double lng, String nome) {
            this.lat = lat;
            this.lng = lng;
            this.nome = nome;
        }
    }

    public static class ResultadoFrete {
        private final String nomeOrigem;
        private final String nomeDestino;
        private final double latOrigem;
        private final double lngOrigem;
        private final double latDestino;
        private final double lngDestino;
        private final String distancia;
        private final String peso;
        private final String taxaBase;
        private final String taxaAdicional;
        private final String taxaSeguro;
        private final String total;

        public ResultadoFrete(String nomeOrigem, String nomeDestino, double latOrigem, double lngOrigem,
                              double latDestino, double lngDestino, String distancia, String peso,
                              String taxaBase, String taxaAdicional, String taxaSeguro, String total) {
            this.nomeOrigem = nomeOrigem;
            this.nomeDestino = nomeDestino;
            this.latOrigem = latOrigem;
            this.lngOrigem = lngOrigem;
            this.latDestino = latDestino;
            this.lngDestino = lngDestino;
            this.distancia = distancia;
            this.peso = peso;
            this.taxaBase = taxaBase;
            this.taxaAdicional = taxaAdicional;
            this.taxaSeguro = taxaSeguro;
            this.total = total;
        }

        public String getNomeOrigem() { return nomeOrigem; }
        public String getNomeDestino() { return nomeDestino; }
        public double getLatOrigem() { return latOrigem; }
        public double getLngOrigem() { return lngOrigem; }
        public double getLatDestino() { return latDestino; }
        public double getLngDestino() { return lngDestino; }
        public String getDistancia() { return distancia; }
        public String getPeso() { return peso; }
        public String getTaxaBase() { return taxaBase; }
        public String getTaxaAdicional() { return taxaAdicional; }
        public String getTaxaSeguro() { return taxaSeguro; }
        public String getTotal() { return total; }
    }
}