import axios from 'axios';
import { clearToken, getToken } from '../utils/auth';
const http = axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: {
        'Content-Type': 'application/json'
    }
});
http.interceptors.request.use((config) => {
    const token = getToken();
    if (token) {
        const headers = (config.headers ?? {});
        headers.Authorization = `Bearer ${token}`;
        config.headers = headers;
    }
    return config;
});
http.interceptors.response.use((response) => response, (error) => {
    if (error.response?.status === 401) {
        clearToken();
        if (window.location.pathname !== '/login') {
            window.location.href = '/login';
        }
    }
    return Promise.reject(error);
});
function buildError(error) {
    if (axios.isAxiosError(error)) {
        const status = error.response?.status;
        const message = (typeof error.response?.data === 'string' && error.response.data) ||
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
export async function apiGet(path, params) {
    try {
        const { data } = await http.get(path, { params });
        return data;
    }
    catch (error) {
        throw buildError(error);
    }
}
export async function apiPost(path, body) {
    try {
        const { data } = await http.post(path, body);
        return data;
    }
    catch (error) {
        throw buildError(error);
    }
}
export async function apiPut(path, body) {
    try {
        const { data } = await http.put(path, body);
        return data;
    }
    catch (error) {
        throw buildError(error);
    }
}
export async function apiDelete(path) {
    try {
        await http.delete(path);
    }
    catch (error) {
        throw buildError(error);
    }
}
