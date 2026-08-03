import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Auth, RegisterRequest } from '../../services/auth';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  form: RegisterRequest = {
    nome: '',
    email: '',
    senha: ''
  };

  loading = false;
  erro = '';
  sucesso = '';
  tokenVerificacaoEmail = '';

  constructor(
    private authService: Auth,
    private router: Router
  ) {}

  onSubmit(): void {
    this.erro = '';
    this.sucesso = '';
    this.tokenVerificacaoEmail = '';
    this.loading = true;

    this.authService.register(this.form).subscribe({
      next: (response) => {
        this.sucesso = 'Cadastro realizado com sucesso!';
        this.tokenVerificacaoEmail = response.tokenVerificacaoEmail;
      },
      error: (error) => {
        console.error('Erro no cadastro:', error);
        this.erro = error?.error?.message || 'Não foi possível criar a conta.';
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  irParaLogin(): void {
    this.router.navigate(['/login']);
  }
}