# Gastrofy — Contexto do Projeto

## Visão Geral

O Gastrofy é um sistema de gestão para confeitarias, focado principalmente em pequenas confeiteiras que trabalham com produção sob encomenda.

O objetivo do sistema é centralizar e organizar processos que normalmente são feitos manualmente, como:

- controle de pedidos
- controle de clientes
- gerenciamento de ingredientes
- cálculo de custos de produção
- controle de estoque

O sistema funciona como um **ERP simplificado para confeitaria**, priorizando simplicidade e automação de processos.

---

# Problema que o sistema resolve

Muitas confeiteiras trabalham apenas com:

- WhatsApp
- caderno
- planilhas simples
- notas no celular

Isso gera problemas como:

- pedidos esquecidos
- falta de ingredientes no momento da produção
- dificuldade de calcular custo de produção
- dificuldade de calcular lucro
- desorganização da produção

O Gastrofy busca resolver esses problemas automatizando o controle de pedidos, produção e estoque.

---

# Conceito central do sistema

O núcleo do Gastrofy é o **controle de estoque conectado aos pedidos**.

Fluxo principal:

Estoque de Ingredientes  
↓  
Receitas / Produtos  
↓  
Pedidos  
↓  
Consumo automático de ingredientes  
↓  
Atualização do estoque

Quando um pedido é criado, o sistema calcula automaticamente:

- consumo de ingredientes
- custo de produção
- lucro estimado

---

# Entidades principais do sistema

Usuario  
Cliente  
Ingrediente  
Produto  
Pedido  
ItemPedido

Relacionamento principal:

Usuario
├── Clientes
├── Ingredientes
├── Produtos
└── Pedidos

---

# Descrição das entidades

## Usuario
Representa o dono da conta no sistema (confeiteira).

Cada usuário possui seus próprios dados no sistema.

---

## Cliente
Pessoa que realiza pedidos.

Campos principais:

- nome
- telefone
- histórico de pedidos

Relacionamento:

Usuario → Clientes

---

## Ingrediente
Representa insumos utilizados na confeitaria.

Exemplos:

- farinha
- ovos
- açúcar
- chocolate

Campos principais:

- nome
- quantidade disponível
- unidade de medida
- custo

---

## Produto
Itens vendidos pela confeitaria.

Exemplos:

- bolo de chocolate
- cupcake
- brownie

Cada produto possui uma **receita**, que define:

- ingredientes utilizados
- quantidade de cada ingrediente

---

## Pedido

Representa encomendas feitas pelos clientes.

Campos principais:

- cliente
- produtos
- data de entrega
- valor total
- status

Status possíveis:

- pendente
- em produção
- finalizado
- entregue

---

## ItemPedido

Representa um produto dentro de um pedido.

Relacionamento:

Pedido → ItemPedido → Produto

---

# Arquitetura do sistema

Backend e frontend separados.

Backend: API REST.

Tecnologias utilizadas:

- Java
- Spring Boot
- Spring Data JPA
- MySQL
- Spring Security
- JWT
- Maven

---

# Arquitetura de código

O projeto segue arquitetura em camadas:

Controller  
Service  
Repository  
Entity

Fluxo:

Controller → Service → Repository → Banco de dados

---

# Uso de DTO

O projeto utiliza DTOs para separar:

- estrutura da API
- estrutura do banco

Tipos usados:

Request DTO → entrada de dados  
Response DTO → saída de dados

---

# Segurança

A autenticação utiliza:

- Spring Security
- JWT
- BCrypt para criptografia de senha

Fluxo de autenticação:

Login  
↓  
Validação de email e senha  
↓  
Geração de JWT  
↓  
Token usado nas requisições

---

# Multi-tenancy

O sistema é multiusuário.

Cada usuário possui seus próprios dados isolados.

Estrutura:

Usuario
├── Clientes
├── Ingredientes
├── Produtos
└── Pedidos

---

# Estado atual do desenvolvimento

O projeto atualmente possui:

- CRUD de usuários
- DTOs
- validações
- criptografia de senha com BCrypt
- arquitetura em camadas
- tratamento global de erros

Pacotes principais do backend:

controller  
service  
repository  
model  
dto  
security  
exception

---

# Próximos passos de desenvolvimento

- autenticação completa com JWT
- entidade Cliente
- entidade Ingrediente
- entidade Produto
- entidade Pedido
- cálculo automático de consumo de ingredientes
- desenvolvimento do frontend