import { apiPost } from './http';
export async function login(credentials) {
    return apiPost('/auth/token', credentials);
}
