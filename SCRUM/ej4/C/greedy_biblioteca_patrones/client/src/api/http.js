import axios from 'axios';
const http = axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: {
        'Content-Type': 'application/json'
    }
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
