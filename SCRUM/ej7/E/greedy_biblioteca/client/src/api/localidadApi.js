import { apiGet, apiPost } from './http';
export async function fetchLocalidades() {
    return apiGet('/localidades');
}
export async function createLocalidad(denominacion) {
    return apiPost('/localidades', { denominacion });
}
