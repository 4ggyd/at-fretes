# 🚚 A&T Fretes: Inteligência Logística

> **Disciplina:** Linguagem de Programação 2  
> **Professor:** João Anisio Marinho da Nobrega  
> **Caminho Escolhido:** Caminho A – API Robusta 

---

## 👥 Identificação dos Integrantes
* **Nome:** Lucas Toshio Nascimento da Silva |**Matrícula:** 20240001500
* **Nome:** Amanda Gabrielly Duarte Silva | **Matrícula:** 20240045757 

---

## 🎯 1. O Problema
No cenário atual do e-commerce, pequenos lojistas enfrentam um gargalo crítico: a precificação manual de fretes. A dependência de tabelas estáticas e conferências humanas não apenas consome um tempo operacional valioso, mas gera erros de cotação que impactam diretamente a lucratividade e a confiança do consumidor. O **A&T Fretes** surge para eliminar essa ineficiência, oferecendo uma solução de backend que automatiza a lógica logística, garantindo cálculos instantâneos e precisos.

## 👥 2. O Público-Alvo
A API é direcionada a desenvolvedores de ecossistemas de vendas e pequenos empreendedores que buscam um motor de cálculo centralizado. Ao fornecer uma interface programável, permitimos que esses profissionais integrem inteligência logística diretamente em seus checkouts, abstraindo a complexidade dos algoritmos de transporte.

## 🚀 3. O Escopo (User Stories)
Para atender às necessidades do negócio, o sistema será pautado nas seguintes funcionalidades:

* **Cálculo Dinâmico por Peso:** Como um sistema de vendas, eu quero submeter o peso de um pacote para receber o valor do frete calculado automaticamente.
* **Estimativa de Prazo:** Como um usuário final, eu quero informar meu CEP de destino para visualizar o prazo estimado de entrega.
* **Gestão de Parâmetros Logísticos:** Como administrador do sistema, eu quero atualizar o valor da taxa por quilômetro para ajustar os preços conforme variações externas, como o aumento do combustível.

## 🛠️ 4. Decisão do Projeto e Desafio Técnico
A escolha pelo desenvolvimento de uma API Robusta justifica-se pela complexidade inerente ao processamento de variáveis dinâmicas em tempo real. Diferente de uma aplicação de cadastro simples (CRUD), o **A&T Fretes** exige a implementação de uma lógica de serviço que correlaciona peso e distância para gerar resultados precisos. A utilização de uma arquitetura dividida em camadas de **Controle, Serviço e Repositório** demonstra o domínio de padrões de projeto essenciais para o ecossistema Spring.

## 💻 5. Stack de Tecnologia Proposta
O projeto será construído com tecnologias modernas que garantem performance e manutenibilidade:

* **Linguagem:** Java 17+ (utilizando Records para imutabilidade de dados).
* **Framework:** Spring Boot 3+.
* **Documentação:** Springdoc (Swagger UI) para visualização e testes dos endpoints.
* **Arquitetura:** Divisão em Camadas de Controle, Serviço e Repositório.

## 💾 6. Persistência de Dados
Para esta etapa do desenvolvimento, os dados serão gerenciados por meio de um **Repositório em Memória**. Utilizaremos estruturas de dados nativas do Java, como `HashMap` para indexação rápida de rotas e `ArrayList` para o gerenciamento de registros enquanto a aplicação estiver em execução, garantindo alta performance sem a necessidade inicial de um banco de dados SQL.
