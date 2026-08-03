import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Auth, LoginRequest } from '../../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  form: LoginRequest = {
    email: '',
    senha: ''
  };

  loading = false;
  erro = '';

  constructor(
    private authService: Auth,
    private router: Router
  ) {}

  onSubmit(): void {
    this.erro = '';
    this.loading = true;

    this.authService.login(this.form).subscribe({
      next: (response) => {
        localStorage.setItem('token', response.token);
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        console.error('Erro no login:', error);
        this.erro = error?.error?.message || 'Não foi possível fazer login.';
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      }
    });
  }
}