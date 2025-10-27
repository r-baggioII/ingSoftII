export declare function useHydratedForm<T extends object>(defaults: T, value: Partial<T>): {
    form: T;
    setForm: import("react").Dispatch<import("react").SetStateAction<T>>;
    submitting: boolean;
    setSubmitting: import("react").Dispatch<import("react").SetStateAction<boolean>>;
    reset: () => void;
};
