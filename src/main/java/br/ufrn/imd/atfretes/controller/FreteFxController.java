package br.ufrn.imd.atfretes.controller;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;
import org.springframework.stereotype.Component;
import netscape.javascript.JSObject;

@Component
public class FreteFxController {

    @FXML
    private TextField txtCepOrigem;

    @FXML
    private TextField txtCepDestino;

    @FXML
    private TextField txtPeso;

    @FXML
    private TextField txtValorDeclarado;

    @FXML
    private Button btnCalcular;

    @FXML
    private Label lblResultado;

    @FXML
    private WebView mapView;

    private double pesoKgValido;
    private double valorDeclaradoValido;

    @FXML
    public void handleCalcularFrete(ActionEvent event) {
        try {
            String cepOrigemRaw = txtCepOrigem.getText().trim().replace("-", "").replace(".", "");
            String cepDestinoRaw = txtCepDestino.getText().trim().replace("-", "").replace(".", "");
            
            if (cepOrigemRaw.isEmpty() || cepDestinoRaw.isEmpty() || txtPeso.getText().isEmpty() || txtValorDeclarado.getText().isEmpty()) {
                lblResultado.setText("Erro: Preencha todos os campos.");
                return;
            }

            double pesoBruto = Double.parseDouble(txtPeso.getText().replace(",", "."));
            this.valorDeclaradoValido = Double.parseDouble(txtValorDeclarado.getText().replace(",", "."));

            if (pesoBruto <= 0 || valorDeclaradoValido < 0) {
                lblResultado.setText("Erro: Valores devem ser maiores que zero.");
                return;
            }

            this.pesoKgValido = (pesoBruto > 100.0) ? (pesoBruto / 1000.0) : pesoBruto;

            lblResultado.setText("Buscando localizações e calculando rota rodoviária real...");

            // Injeção de String HTML limpa e contínua com a CDN de alta performance do CartoDB Voyager
            String htmlMapa = "<html>" +
                    "<head>" +
                    "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                    "<link rel='stylesheet' href='https://unpkg.com/leaflet@1.9.4/dist/leaflet.css' />" +
                    "<link rel='stylesheet' href='https://unpkg.com/leaflet-routing-machine@3.2.12/dist/leaflet-routing-machine.css' />" +
                    "<script src='https://unpkg.com/leaflet@1.9.4/dist/leaflet.js'></script>" +
                    "<script src='https://unpkg.com/leaflet-routing-machine@3.2.12/dist/leaflet-routing-machine.js'></script>" +
                    "<style>html, body, #map { height: 100%; margin: 0; padding: 0; background-color: #f1f5f9; } " +
                    ".leaflet-routing-container { display: none !important; }</style>" + 
                    "</head>" +
                    "<body>" +
                    "<div id='map'></div>" +
                    "<script>" +
                    "var map, routingControl; " +
                    "try { " +
                    "   map = L.map('map', {zoomControl: true, fadeAnimation: false}).setView([-15.7801, -47.9292], 4); " + 
                    "   var camadaTiles = L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', { " +
                    "       maxZoom: 19, " +
                    "       updateWhenIdle: false, " +
                    "       keepBuffer: 12 " +
                    "   }).addTo(map); " +
                    "   " +
                    "   function buscarCoordenadas(cep, callback) { " +
                    "       fetch('https://viacep.com.br/ws/' + cep + '/json/') " +
                    "           .then(res => res.json()) " +
                    "           .then(viacep => { " +
                    "               if (viacep.erro) { callback('CEP inexistente'); return; } " +
                    "               var query = viacep.localidade + ' ' + viacep.uf + ' Brazil'; " +
                    "               var rotulo = (viacep.bairro ? viacep.bairro + ' - ' : '') + viacep.localidade + '/' + viacep.uf; " +
                    "               fetch('https://nominatim.openstreetmap.org/search?format=json&q=' + encodeURIComponent(query)) " +
                    "                   .then(r => r.json()) " +
                    "                   .then(geo => { " +
                    "                       if (geo && geo.length > 0) { " +
                    "                           callback(null, { lat: parseFloat(geo[0].lat), lng: parseFloat(geo[0].lon), nome: rotulo }); " +
                    "                       } else { callback('Coordenadas não achadas'); } " +
                    "                   }).catch(() => callback('Falha na geocodificação')); " +
                    "           }).catch(() => callback('Erro ao conectar no ViaCEP')); " +
                    "   } " +
                    "   " +
                    "   buscarCoordenadas('" + cepOrigemRaw + "', function(errA, orig) { " +
                    "       if (errA) { window.JavaBridge.notificarErro('Origem: ' + errA); return; } " +
                    "       buscarCoordenadas('" + cepDestinoRaw + "', function(errB, dest) { " +
                    "           if (errB) { window.JavaBridge.notificarErro('Destino: ' + errB); return; } " +
                    "           " +
                    "           routingControl = L.Routing.control({ " +
                    "               waypoints: [L.latLng(orig.lat, orig.lng), L.latLng(dest.lat, dest.lng)], " +
                    "               router: L.Routing.osrmv1({ serviceUrl: 'https://router.project-osrm.org/route/v1', timeout: 10000 }), " +
                    "               lineOptions: { styles: [{ color: '#1a73e8', weight: 6, opacity: 0.9 }] }, " +
                    "               createMarker: function(i, wp) { " +
                    "                   if (i === 0) return L.marker(wp.latLng).bindPopup('<b>Origem:</b> ' + orig.nome).openPopup(); " +
                    "                   return L.marker(wp.latLng).bindPopup('<b>Destino:</b> ' + dest.nome); " +
                    "               } " +
                    "           }).addTo(map); " +
                    "           " +
                    "           routingControl.on('routesfound', function(e) { " +
                    "               var distanciaKm = e.routes[0].summary.totalDistance / 1000.0; " +
                    "               map.fitBounds(L.polyline(e.routes[0].coordinates).getBounds()); " +
                    "               " +
                    "               var loops = 0; " +
                    "               var intervalId = setInterval(function() { " +
                    "                   if (map) { map.invalidateSize(); } " +
                    "                   if (++loops > 12) { clearInterval(intervalId); } " +
                    "               }, 100); " +
                    "               " +
                    "               window.JavaBridge.completarCalculoLogistico(distanciaKm, orig.nome, dest.nome); " +
                    "           }); " +
                    "           " +
                    "           routingControl.on('routingerror', function() { " +
                    "               window.JavaBridge.completarCalculoLogistico(35.0, orig.nome, dest.nome); " +
                    "           }); " +
                    "       }); " +
                    "   }); " +
                    "} catch (err) { " +
                    "   window.JavaBridge.notificarErro('Erro de script: ' + err.message); " +
                    "} " +
                    "window.onload = function() { " +
                    "   var l = 0; var t = setInterval(function() { if(map){ map.invalidateSize(); } if(++l > 10){ clearInterval(t); } }, 150); " +
                    "};" +
                    "</script>" +
                    "</body>" +
                    "</html>";

            mapView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == Worker.State.SUCCEEDED) {
                    JSObject window = (JSObject) mapView.getEngine().executeScript("window");
                    window.setMember("JavaBridge", new ProvedorLogistico());
                    
                    Platform.runLater(() -> {
                        mapView.getEngine().executeScript("var lp=0; var tm=setInterval(function(){ if(map) map.invalidateSize(); if(++lp>12) clearInterval(tm); }, 100);");
                    });
                }
            });

            mapView.getEngine().loadContent(htmlMapa);

        } catch (NumberFormatException e) {
            lblResultado.setText("Erro: Insira dados numéricos válidos nos campos de Peso e Valor.");
        } catch (Exception e) {
            lblResultado.setText("Erro no processamento: " + e.getMessage());
        }
    }

    public class ProvedorLogistico {
        public void completarCalculoLogistico(double distanciaKm, String origem, String destino) {
            Platform.runLater(() -> {
                double taxaEnvioFixa = 15.90;
                double fatorDistancia = (distanciaKm > 250.0) ? 0.015 : 0.45;
                double distanciaFinal = (distanciaKm <= 0.0) ? 1.0 : distanciaKm;
                
                double taxaPorQuiloERota = pesoKgValido * (fatorDistancia + (distanciaFinal * 0.005));
                double taxaSeguroAdValorem = valorDeclaradoValido * 0.005;
                
                double valorTotalFrete = taxaEnvioFixa + taxaPorQuiloERota + taxaSeguroAdValorem;

                lblResultado.setText(String.format(
                    "Origem: %s\n" +
                    "Destino: %s\n\n" +
                    "Distância por Rodovias: %.2f km\n" +
                    "Peso Considerado: %.2f kg\n\n" +
                    " - Taxa Base de Envio: R$ %.2f\n" +
                    " - Adicional de Rota e Peso: R$ %.2f\n" +
                    " - Seguro de Carga (0.5 por cento): R$ %.2f\n\n" +
                    "Total a Pagar: R$ %.2f",
                    origem, destino, distanciaFinal, pesoKgValido, taxaEnvioFixa, taxaPorQuiloERota, taxaSeguroAdValorem, valorTotalFrete
                ));
            });
        }

        public void notificarErro(String messageError) {
            Platform.runLater(() -> {
                lblResultado.setText("Erro na busca: " + messageError);
            });
        }
    }
}