export function mergeWithDefaults(defaults, value) {
    if (!isMergeable(defaults)) {
        return (value ?? defaults);
    }
    const result = { ...defaults };
    if (!value) {
        return result;
    }
    if (!isMergeable(value)) {
        return value ?? result;
    }
    Object.entries(value).forEach(([key, val]) => {
        if (val === undefined) {
            return;
        }
        const current = result[key];
        if (isMergeable(current) && isMergeable(val)) {
            result[key] = mergeWithDefaults(current, val);
        }
        else {
            result[key] = val;
        }
    });
    return result;
}
function isMergeable(value) {
    return typeof value === 'object' && value !== null && !Array.isArray(value);
}
