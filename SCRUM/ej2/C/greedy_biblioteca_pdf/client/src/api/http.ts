import axios from 'axios';

export interface ApiError {
  message: string;
  status?: number;
  details?: unknown;
}

const http = axios.create({
  baseURL: 'http://localhost:8080/api'
});

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
  const config =
    typeof FormData !== 'undefined' && body instanceof FormData
      ? { headers: { 'Content-Type': 'multipart/form-data' } }
      : undefined;
  try {
    const { data } = await http.post<T>(path, body, config);
    return data;
  } catch (error) {
    throw buildError(error);
  }
}

export async function apiPut<T, B = unknown>(path: string, body: B): Promise<T> {
  const config =
    typeof FormData !== 'undefined' && body instanceof FormData
      ? { headers: { 'Content-Type': 'multipart/form-data' } }
      : undefined;
  try {
    const { data } = await http.put<T>(path, body, config);
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
