import { useCallback, useRef, useState } from 'react';
import { mergeWithDefaults } from '../utils/object';

export function useCrudDialog<T extends object>(defaults: T) {
  const defaultsRef = useRef(defaults);

  const buildDefault = useCallback(() => {
    return mergeWithDefaults(defaultsRef.current, {} as Partial<T>);
  }, []);

  const [current, setCurrent] = useState<T>(buildDefault);
  const [isOpen, setIsOpen] = useState(false);
  const [isEditing, setIsEditing] = useState(false);

  const openForCreate = useCallback(() => {
    setCurrent(buildDefault());
    setIsEditing(false);
    setIsOpen(true);
  }, [buildDefault]);

  const openForEdit = useCallback((value: Partial<T>) => {
    setCurrent(mergeWithDefaults(defaultsRef.current, value));
    setIsEditing(true);
    setIsOpen(true);
  }, []);

  const close = useCallback(() => {
    setIsOpen(false);
    setIsEditing(false);
  }, []);

  const setCurrentValue = useCallback((value: Partial<T>) => {
    setCurrent(mergeWithDefaults(defaultsRef.current, value));
  }, []);

  return {
    current,
    isOpen,
    isEditing,
    openForCreate,
    openForEdit,
    close,
    setCurrentValue
  };
}
