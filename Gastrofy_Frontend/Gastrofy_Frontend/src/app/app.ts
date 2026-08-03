import { Component } from '@angular/core';
import { Login } from './pages/login/login';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [Login],
  template: `<app-login></app-login>`,
})
export class App {}