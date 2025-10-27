import axios, { type AxiosRequestHeaders } from 'axios';
import { clearToken, getToken } from '../utils/auth';

export interface ApiError {
  message: string;
  status?: number;
  details?: unknown;
}

const http = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

http.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    const headers = (config.headers ?? {}) as Record<string, string>;
    headers.Authorization = `Bearer ${token}`;
    config.headers = headers as unknown as AxiosRequestHeaders;
  }
  return config;
});

http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      clearToken();
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

function buildError(error: unknown): ApiError {
  if (axios.isAxiosError(error)) {
    const status = error.response?.status;
    const message =
      (typeof error.response?.data === 'string' && error.response.data) ||
      error.response?.data?.message ||
      error.message ||
      'Error inesperado';
    return {
      message,
      status,
      details: error.response?.data
    };
  }

  if (error instanceof Error) {
    return { message: error.message };
  }

  return { message: 'Error desconocido' };
}

export async function apiGet<T>(path: string, params?: Record<string, unknown>): Promise<T> {
  try {
    const { data } = await http.get<T>(path, { params });
    return data;
  } catch (error) {
    throw buildError(error);
  }
}

export async function apiPost<T, B = unknown>(path: string, body: B): Promise<T> {
  try {
    const { data } = await http.post<T>(path, body);
    return data;
  } catch (error) {
    throw buildError(error);
  }
}

export async function apiPut<T, B = unknown>(path: string, body: B): Promise<T> {
  try {
    const { data } = await http.put<T>(path, body);
    return data;
  } catch (error) {
    throw buildError(error);
  }
}

export async function apiDelete(path: string): Promise<void> {
  try {
    await http.delete(path);
  } catch (error) {
    throw buildError(error);
  }
}
