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
export declare function login(credentials: LoginRequest): Promise<LoginResponse>;
export {};
