# Regras para o agente (backend Gastrofy)
- Toda funcionalidade nova ou correção vem com teste (unidade ou
  integração).
- Antes de dizer que terminou, rode ./mvnw clean test e mostre a saída
  completa. Só diga "pronto" se passar.
- Testes de integração NÃO usam @Transactional, porque isso esconde
  LazyInitializationException.
- Todo acesso a dados filtra pelo usuário logado
  (findByIdAndUsuarioIdUsuario). Todo endpoint novo ganha um teste de
  isolamento entre usuários.
- Não faça commit nem push. Liste os arquivos alterados.
- Não leia src/main/resources/application.properties nem arquivos .env.
  Os testes usam application-test.properties.
- Não adicione dependências sem avisar.
- Mostre o plano antes de mexer em SecurityConfig, CorsConfig, Usuario
  e EmailService.
