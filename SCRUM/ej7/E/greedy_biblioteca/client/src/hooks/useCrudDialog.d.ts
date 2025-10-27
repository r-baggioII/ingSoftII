export declare function useCrudDialog<T extends object>(defaults: T): {
    current: T;
    isOpen: boolean;
    isEditing: boolean;
    openForCreate: () => void;
    openForEdit: (value: Partial<T>) => void;
    close: () => void;
    setCurrentValue: (value: Partial<T>) => void;
};
