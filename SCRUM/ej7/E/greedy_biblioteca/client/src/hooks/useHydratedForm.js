import { useCallback, useEffect, useState } from 'react';
import { mergeWithDefaults } from '../utils/object';
export function useHydratedForm(defaults, value) {
    const computeValue = useCallback(() => mergeWithDefaults(defaults, value), [defaults, value]);
    const [form, setForm] = useState(computeValue);
    const [submitting, setSubmitting] = useState(false);
    useEffect(() => {
        setForm(computeValue());
    }, [computeValue]);
    const reset = useCallback(() => {
        setForm(computeValue());
    }, [computeValue]);
    return { form, setForm, submitting, setSubmitting, reset };
}
