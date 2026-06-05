# 🚚 A&T Fretes: Inteligência Logística

> **Disciplina:** Linguagem de Programação 2  
> **Professor:** João Anisio Marinho da Nobrega  
> **Unidade Curricular:** Instituto Metrópole Digital (IMD / UFRN)  
> **Caminho Escolhido:** Caminho A – API Robusta com Interface Gráfica Integrada

---

## 👥 Identificação dos Integrantes
* **Nome:** Lucas Toshio Nascimento da Silva | **Matrícula:** 20240001500
* **Nome:** Amanda Gabrielly Duarte Silva | **Matrícula:** 20240045757

---

## 🎯 1. O Problema
No cenário atual do e-commerce, pequenos lojistas enfrentam um gargalo crítico: a precificação manual de fretes. A dependência de tabelas estáticas e conferências humanas consome tempo operacional e gera erros que impactam a lucratividade. O **A&T Fretes** automatiza a lógica logística, garantindo cálculos instantâneos combinados à renderização visual de rotas rodoviárias.

## 👥 2. O Público-Alvo
Desenvolvedores de e-commerce, operadores logísticos e pequenos empreendedores que necessitam de um motor de cálculo centralizado com interface visual intuitiva.

## 🚀 3. O Escopo (User Stories)
* **Cálculo Dinâmico:** Submeter peso e valor para obter o cálculo de frete com taxas fracionadas.
* **Estimativa e Rota:** Informar CEP de destino para visualizar prazo de entrega e trajeto em mapa.
* **Gestão de Parâmetros:** Configuração de taxa base, adicionais por distância/quilo e seguro *Ad Valorem*.

## 🛠️ 4. Decisão Técnica e Arquitetura
O sistema utiliza uma arquitetura MVC integrada entre Spring Boot (Back-end) e JavaFX (Front-end).



[Image of MVC architectural pattern]


* **Back-end:** Spring Boot 3+ (Services/Records).
* **Front-end:** JavaFX 21+ com `WebView` para renderização de mapas via Leaflet/CartoDB.
* **Persistência:** Repositório em memória (HashMap/ArrayList) para alta performance de tempo de execução.

---

## 🎨 5. Estrutura de Integração
+--------------------------------------------------------+
|                  CAMADA VISUAL (FRONT)                 |
|   JavaFX Desktop (Scene Graph)  <->  WebView Component |
+--------------------------------------------------------+
|
Ponte Nativa (JavaBridge JS)
|
+--------------------------------------------------------+
|                 LOGICA DE SERVIÇO (BACK)               |
|   Spring Boot Framework Core <-> Services/Records      |
+--------------------------------------------------------+
|
HTTP Requests (ViaCEP / OSRM)

## 🚀 6. Instruções para Rodar a Aplicação

### Pré-requisitos
* Java JDK 17+ instalado (`JAVA_HOME` configurado).
* Maven Wrapper (`mvnw`) disponível na raiz do projeto.

### Passo a Passo

1. **Limpeza do Workspace:**
   Abra o terminal na raiz do projeto e execute:
   `./mvnw clean` (ou `mvnw clean` no Windows).

2. **Execução:**
   Execute a diretiva abaixo para compilar e iniciar o sistema integrado:
   `./mvnw spring-boot:run` (ou `mvnw spring-boot:run` no Windows).

3. **Homologação e Teste:**
   A janela **"Sistema de Fretes IMD"** abrirá automaticamente.
   * **Teste sugerido:** Origem `59147395`, Destino `04475380`, Peso `2`, Valor `25`.
   * Clique em **"Calcular Rota e Frete"**. O sistema processará os dados e renderizará o trajeto interestadual no mapa.

---

## 📡 7. Integrações de Rede
* **ViaCEP:** Validação de endereços.
* **Nominatim (OpenStreetMap):** Geocodificação (conversão de endereços em coordenadas).
* **OSRM:** Motor de roteamento viário para cálculo de distância e traçado de rotas.