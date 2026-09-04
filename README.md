# Gastrofy API

O Gastrofy é um microSaaS desenvolvido para auxiliar confeiteiros na gestão do seu negócio.

O objetivo do sistema é centralizar as principais operações de uma confeitaria em uma única plataforma, permitindo que o usuário organize e controle suas atividades diárias de forma simples e eficiente.

Cada usuário possui sua própria conta dentro do sistema e pode gerenciar seus próprios dados, garantindo isolamento total entre contas (multi-tenant).

**API em produção:** `https://gastrofy-api-production.up.railway.app`

O sistema foi pensado para ajudar confeiteiros a gerenciar:

- estoque de insumos, com alertas automáticos de estoque baixo/crítico e validade próxima
- receitas, com cálculo automático de custo
- produtos, com cálculo automático de custo, lucro e margem, e controle de estoque de produto pronto (separado do estoque de insumos)
- pedidos, vinculados a clientes
- clientes

Este projeto foi desenvolvido como estudo prático de desenvolvimento backend com Java e Spring Boot, aplicando conceitos utilizados em sistemas SaaS reais — incluindo autenticação, segurança multiusuário, envio de email transacional e deploy em produção.

## 🚀 Tecnologias utilizadas

- Java 21
- Spring Boot 4
- Spring Security (JWT customizado)
- Spring Data JPA / Hibernate
- MySQL
- BCrypt (criptografia de senha)
- Resend (API HTTP de envio de email transacional)
- OkHttp (cliente HTTP usado para integração com o Resend)
- Maven

## 🧱 Arquitetura

O backend foi desenvolvido utilizando arquitetura em camadas:

```
Controller → Service → Repository → Entity
```

Essa estrutura permite melhor organização do código, separação de responsabilidades e maior facilidade de manutenção.

Todos os endpoints de negócio filtram os dados pelo usuário autenticado, garantindo que cada confeiteiro só acesse os próprios insumos, receitas, produtos, pedidos e clientes.

## 🔐 Segurança

O sistema possui um módulo completo de autenticação com:

- autenticação via JWT
- criptografia de senha com BCrypt
- verificação de email obrigatória antes do primeiro login, com envio real de email via **Resend**, usando domínio próprio (`usegastrofy.com`)
- reenvio de verificação de email (para o caso do link expirar ou o email não ter chegado)
- recuperação de senha (forgot/reset password), sem revelar se um email existe no sistema
- rotas protegidas por token, com tratamento correto de token ausente, inválido e expirado (401)
- limpeza automática diária de contas não verificadas após 48 horas

Todas as rotas sensíveis exigem autenticação através de token JWT.

## 📦 Módulos e regras de negócio

- **Insumos**: CRUD completo, com reposição manual de estoque e configuração opcional de limites para alertas (estoque mínimo, estoque crítico, dias de aviso de validade).
- **Receitas**: compostas por insumos, com cálculo automático de custo total e por unidade.
- **Produtos**: compostos por uma receita, com cálculo automático de custo, lucro e margem. Possuem um campo de **estoque de produto pronto**, que é incrementado ao produzir (`/produtos/{id}/produzir`, que também desconta os insumos usados) e decrementado quando um pedido é confirmado como produzido — nunca na criação do pedido, para permitir cancelamentos sem afetar o estoque.
- **Pedidos**: vinculados a um cliente, com snapshot dos dados do cliente no momento da criação. Ao confirmar um pedido como produzido, o sistema valida se há estoque de produto suficiente e bloqueia a operação com uma mensagem clara caso não haja.
- **Clientes**: cadastro simples vinculado ao usuário.
- **Alertas**: calculados em tempo real a partir do estado atual dos insumos (sem persistência), cobrindo estoque baixo/crítico/zerado e validade próxima/vencida.

## ⚙️ Ambientes

O projeto roda com dois profiles:

- **Desenvolvimento** (`application.properties`, não versionado): usa banco de dados MySQL local.
- **Produção** (`application-prod.properties`, versionado com placeholders): usa variáveis de ambiente configuradas no Railway para banco de dados, JWT e integração com o Resend.

## 🌐 Deploy

- **Backend + banco de dados**: Railway
- **Email transacional**: Resend, com domínio próprio verificado
- **Frontend**: hospedado separadamente no Vercel (repositório `Gastrofy_FrontEnd`)
