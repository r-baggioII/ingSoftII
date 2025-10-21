import { createContext, useContext, useMemo, useState } from 'react';

type ToastType = 'success' | 'error' | 'info' | 'warning';

interface ToastState {
  type: ToastType;
  message: string;
}

interface ToastContextValue {
  showToast: (message: string, type?: ToastType) => void;
  hideToast: () => void;
}

const ToastContext = createContext<ToastContextValue | undefined>(undefined);

export function ToastProvider({ children }: { children: React.ReactNode }) {
  const [toast, setToast] = useState<ToastState | null>(null);

  const value = useMemo<ToastContextValue>(
    () => ({
      showToast: (message: string, type: ToastType = 'success') => {
        setToast({ message, type });
        setTimeout(() => setToast(null), 4000);
      },
      hideToast: () => setToast(null)
    }),
    []
  );

  return (
    <ToastContext.Provider value={value}>
      {children}
      {toast && (
        <div
          className={`toast toast-${toast.type}`}
          role="status"
          onClick={() => setToast(null)}
        >
          {toast.message}
        </div>
      )}
    </ToastContext.Provider>
  );
}

export function useToast() {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error('useToast debe usarse dentro de ToastProvider');
  }
  return context;
}
