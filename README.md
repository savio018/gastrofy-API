# 🍰 Gastrofy API

O **Gastrofy** é um microSaaS desenvolvido para auxiliar confeiteiros na gestão do seu negócio.

O objetivo do sistema é centralizar as principais operações de uma confeitaria em uma única plataforma, permitindo que o usuário organize e controle suas atividades diárias de forma simples e eficiente.

Cada usuário possui sua própria conta dentro do sistema e pode gerenciar seus próprios dados, garantindo isolamento entre contas.

O sistema foi pensado para ajudar confeiteiros a gerenciar:

- pedidos
- clientes
- produtos
- receitas
- estoque de ingredientes
- controle financeiro

Este projeto foi desenvolvido como parte de um estudo prático de **desenvolvimento backend com Java e Spring Boot**, aplicando conceitos utilizados em sistemas SaaS reais.

## 🚀 Tecnologias utilizadas

- Java
- Spring Boot
- Spring Security
- JWT (JSON Web Token)
- Spring Data JPA
- MySQL
- BCrypt (criptografia de senha)
- Maven

## 🧱 Arquitetura

O backend foi desenvolvido utilizando arquitetura em camadas:

Controller → Service → Repository → Entity

Essa estrutura permite melhor organização do código, separação de responsabilidades e maior facilidade de manutenção.

## 🔐 Segurança

O sistema possui um módulo completo de autenticação com:

- autenticação via JWT
- criptografia de senha com BCrypt
- verificação de email
- recuperação de senha
- rotas protegidas

Todas as rotas sensíveis exigem autenticação através de token JWT.
