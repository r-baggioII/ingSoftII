import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { createContext, useContext, useMemo, useState } from 'react';
const ToastContext = createContext(undefined);
export function ToastProvider({ children }) {
    const [toast, setToast] = useState(null);
    const value = useMemo(() => ({
        showToast: (message, type = 'success') => {
            setToast({ message, type });
            setTimeout(() => setToast(null), 4000);
        },
        hideToast: () => setToast(null)
    }), []);
    return (_jsxs(ToastContext.Provider, { value: value, children: [children, toast && (_jsx("div", { className: `toast toast-${toast.type}`, role: "status", onClick: () => setToast(null), children: toast.message }))] }));
}
export function useToast() {
    const context = useContext(ToastContext);
    if (!context) {
        throw new Error('useToast debe usarse dentro de ToastProvider');
    }
    return context;
}
