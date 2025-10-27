type ToastType = 'success' | 'error';
interface ToastContextValue {
    showToast: (message: string, type?: ToastType) => void;
    hideToast: () => void;
}
export declare function ToastProvider({ children }: {
    children: React.ReactNode;
}): import("react/jsx-runtime").JSX.Element;
export declare function useToast(): ToastContextValue;
export {};
