/**
 * Deeply removes null, undefined, empty objects, and empty arrays from a value.
 */
function deepClean(val: any): any {
  if (Array.isArray(val)) {
    const cleaned = val.map(deepClean).filter(item => {
      if (item === null || item === undefined) return false;
      if (typeof item === 'object' && Object.keys(item).length === 0) return false;
      return true;
    });
    return cleaned.length > 0 ? cleaned : undefined;
  }
  
  if (val !== null && typeof val === 'object') {
    const cleaned: any = {};
    let hasKeys = false;
    for (const key in val) {
      const cleanedVal = deepClean(val[key]);
      if (cleanedVal !== null && cleanedVal !== undefined) {
        cleaned[key] = cleanedVal;
        hasKeys = true;
      }
    }
    return hasKeys ? cleaned : undefined;
  }
  
  return val;
}

/**
 * Unflatten a single-level object with dot-notation keys into a nested object structure.
 * Example: { "a.b.c": 1, "a.b.d": 2 } => { "a": { "b": { "c": 1, "d": 2 } } }
 */
export function unflatten(data: Record<string, any>): any {
  const result: any = {};

  for (const key in data) {
    if (!Object.prototype.hasOwnProperty.call(data, key)) continue;

    const parts = key.split('.');
    let current = result;

    for (let i = 0; i < parts.length; i++) {
      const part = parts[i];
      const isLast = i === parts.length - 1;

      if (isLast) {
        current[part] = data[key];
      } else {
        const nextPart = parts[i + 1];
        const isNextArray = !isNaN(Number(nextPart)) && nextPart !== '';

        if (!current[part]) {
          current[part] = isNextArray ? [] : {};
        } else if (isNextArray && !Array.isArray(current[part])) {
          // If we previously thought it was an object but now see an array index, convert it
          // This is a safety measure for mixed or inconsistent keys
          current[part] = [];
        }

        current = current[part];
      }
    }
  }

  return deepClean(result);
}
