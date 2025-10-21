export interface ApiError {
    message: string;
    status?: number;
    details?: unknown;
}
export declare function apiGet<T>(path: string, params?: Record<string, unknown>): Promise<T>;
export declare function apiPost<T, B = unknown>(path: string, body: B): Promise<T>;
export declare function apiPut<T, B = unknown>(path: string, body: B): Promise<T>;
export declare function apiDelete(path: string): Promise<void>;
