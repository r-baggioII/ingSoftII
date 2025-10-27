import { useCallback, useRef, useState } from 'react';
import { mergeWithDefaults } from '../utils/object';
export function useCrudDialog(defaults) {
    const defaultsRef = useRef(defaults);
    const buildDefault = useCallback(() => {
        return mergeWithDefaults(defaultsRef.current, {});
    }, []);
    const [current, setCurrent] = useState(buildDefault);
    const [isOpen, setIsOpen] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const openForCreate = useCallback(() => {
        setCurrent(buildDefault());
        setIsEditing(false);
        setIsOpen(true);
    }, [buildDefault]);
    const openForEdit = useCallback((value) => {
        setCurrent(mergeWithDefaults(defaultsRef.current, value));
        setIsEditing(true);
        setIsOpen(true);
    }, []);
    const close = useCallback(() => {
        setIsOpen(false);
        setIsEditing(false);
    }, []);
    const setCurrentValue = useCallback((value) => {
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
