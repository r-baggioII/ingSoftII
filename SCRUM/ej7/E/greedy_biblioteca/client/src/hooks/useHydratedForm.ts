import { useCallback, useEffect, useState } from 'react';
import { mergeWithDefaults } from '../utils/object';

export function useHydratedForm<T extends object>(defaults: T, value: Partial<T>) {
  const computeValue = useCallback(() => mergeWithDefaults(defaults, value), [defaults, value]);
  const [form, setForm] = useState<T>(computeValue);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    setForm(computeValue());
  }, [computeValue]);

  const reset = useCallback(() => {
    setForm(computeValue());
  }, [computeValue]);

  return { form, setForm, submitting, setSubmitting, reset };
}
