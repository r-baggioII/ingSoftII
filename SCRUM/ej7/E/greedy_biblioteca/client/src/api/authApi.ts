import { apiPost } from './http';

interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  access_token: string;
  token_type: string;
  expires_in: number;
  scope: string;
}

export async function login(credentials: LoginRequest): Promise<LoginResponse> {
  return apiPost<LoginResponse, LoginRequest>('/auth/token', credentials);
}
