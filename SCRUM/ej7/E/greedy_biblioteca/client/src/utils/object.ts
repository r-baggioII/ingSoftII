export function mergeWithDefaults<T>(defaults: T, value: Partial<T> | undefined): T {
  if (!isMergeable(defaults)) {
    return (value ?? defaults) as T;
  }

  const result: Record<string, unknown> = { ...(defaults as Record<string, unknown>) };
  if (!value) {
    return result as T;
  }

  if (!isMergeable(value)) {
    return (value as unknown as T) ?? (result as T);
  }

  Object.entries(value).forEach(([key, val]) => {
    if (val === undefined) {
      return;
    }
    const current = result[key];
    if (isMergeable(current) && isMergeable(val)) {
      result[key] = mergeWithDefaults(current, val as Record<string, unknown>);
    } else {
      result[key] = val;
    }
  });

  return result as T;
}

function isMergeable(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null && !Array.isArray(value);
}
