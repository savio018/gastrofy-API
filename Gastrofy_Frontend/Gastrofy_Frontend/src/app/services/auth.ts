import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoginRequest {
  email: string;
  senha: string;
}

export interface LoginResponse {
  token: string;
}

export interface RegisterRequest {
  nome: string;
  email: string;
  senha: string;
}

export interface UsuarioResponse {
  id: number;
  nome: string;
  email: string;
  dataCriacao: string;
}

export interface RegisterResponse {
  usuario: UsuarioResponse;
  tokenVerificacaoEmail: string;
}

@Injectable({
  providedIn: 'root'
})
export class Auth {
  private readonly apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  login(data: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/auth/login`, data);
  }

  register(data: RegisterRequest): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(`${this.apiUrl}/usuarios`, data);
  }
}