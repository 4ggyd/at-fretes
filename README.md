# 🚚 A&T Fretes: Inteligência Logística Web

> **Disciplina:** Linguagem de Programação II  
> **Professor:** João Anisio Marinho da Nobrega  
> **Unidade Curricular:** Instituto Metrópole Digital (IMD / UFRN)  
> **Caminho Escolhido:** Caminho A – API Robusta com Interface Gráfica Integrada

---

## 👥 Identificação dos Integrantes
* **Nome:** Lucas Toshio Nascimento da Silva | **Matrícula:** 20240001500
* **Nome:** Amanda Gabrielly Duarte Silva | **Matrícula:** 20240045757

---

## 🎯 1. O Problema
No cenário atual do e-commerce, pequenos lojistas enfrentam um gargalo crítico: a precificação manual de fretes. A dependência de tabelas estáticas e conferências humanas consome tempo operacional e gera erros que impactam a lucratividade. O **A&T Fretes** automatiza a lógica logística, garantindo cálculos dinâmicos combinados à renderização visual de rotas rodoviárias reais.

## 👥 2. O Público-Alvo
Desenvolvedores de e-commerce, operadores logísticos e pequenos empreendedores que necessitam de um motor de cálculo centralizado com interface visual intuitiva e em tempo real.

## 🚀 3. O Escopo (User Stories)
* **Cálculo Dinâmico:** Submeter peso e valor declarado para obter o cálculo de frete com taxas fracionadas de forma imediata.
* **Estimativa e Rota:** Informar os CEPs de origem e destino para visualizar a distância e o trajeto exato sobre a malha rodoviária.
* **Gestão de Parâmetros:** Processamento de taxas básicas, adicionais por distância/quilo e precificação de seguro ad valorem gerenciados de forma centralizada pelo servidor.

## 🛠️ 4. Decisão Técnica e Arquitetura
O sistema utiliza uma arquitetura MVC Web integrada nativamente pelo Spring Boot, eliminando dependências estáticas de execução no cliente.

* **Back-end:** Spring Boot 3.2.2 (Java 17, RestTemplate, ObjectMapper e DTOs de transferência estruturados).
* **Front-end:** HTML5 dinâmico com Thymeleaf, Bootstrap 5.3.2 para layout responsivo justo e Leaflet 1.9.4.
* **Provedor de Mapas:** Stadia Maps (Alidade Smooth) acoplado via token de API.

---

## 🎨 5. Estrutura de Integração
+--------------------------------------------------------+
|                 CAMADA VISUAL (FRONT)                  |
|  Thymeleaf Engine <-> Bootstrap 5 <-> Leaflet Map      |
+--------------------------------------------------------+
|
| JSON Seguro (HTML5 data-attributes)
v
+--------------------------------------------------------+
|                LOGICA DE SERVIÇO (BACK)                |
|  Spring Boot MVC Controller <-> ResultadoFrete DTO    |
+--------------------------------------------------------+
|
| HTTP Requests (ViaCEP / Nominatim)
v
+--------------------------------------------------------+
|              MALHA DE ROTEAMENTO (OSRM)                |
|  Chamadas Assíncronas de Rota Rodoviária Curva a Curva |
+--------------------------------------------------------+

---

## 🚀 6. Instruções para Rodar a Aplicação

### Pré-requisitos
* Java JDK 17+ instalado.
* Maven Wrapper (`mvnw`) disponível na raiz do projeto.

### Passo a Passo

1. **Desimpedimento da Porta de Rede:**
   No terminal do PowerShell, encerre processos que possam reter a porta de execução padrão do servidor:
   `Stop-Process -Id (Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue).OwningProcess -Force -ErrorAction SilentlyContinue`

2. **Compilação e Inicialização:**
   Execute a diretiva abaixo para limpar os caches do projeto, compilar os fontes e iniciar o servidor embutido Tomcat:
   `./mvnw clean spring-boot:run`

3. **Homologação e Teste:**
   Abra uma guia em modo anônimo no navegador e acesse:
   `http://localhost:8080/`
   
   * **Teste sugerido:** Origem `59147395`, Destino `59141610`, Peso `2`, Valor `25`.
   * Clique em **Calcular Rota e Frete**. O sistema processará os dados no back-end e renderizará o trajeto rodoviário real curva a curva no mapa, sem gerar barras de rolagem.

---

## 📡 7. Integrações de Rede
* **ViaCEP:** Validação estrutural de endereços postais baseada nos CEPs de entrada.
* **Nominatim (OpenStreetMap):** Geocodificação assíncrona de endereços em coordenadas cartográficas.
* **OSRM:** Motor externo de roteamento para cálculo de distâncias rodoviárias reais e envio da malha de caminhos.