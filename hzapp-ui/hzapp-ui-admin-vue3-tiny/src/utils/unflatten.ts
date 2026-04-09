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
  const result: Record<string, any> = {};

  for (const key in data) {
    if (!Object.prototype.hasOwnProperty.call(data, key)) continue;

    const keys = key.split('.');
    let current = result;

    for (let i = 0; i < keys.length; i++) {
      const part = keys[i];
      const nextPart = keys[i + 1];

      // If it's the last part, assign the value
      if (i === keys.length - 1) {
        current[part] = data[key];
      } else {
        // If the next part is a number string, handle as array (optional, but good for Amazon)
        // For now, treat everything as object as Amazon SP-API expects objects for multi-valued fields sometimes
        // But if keys are like "item_name.0.value", we might want to handle the "0".
        // However, Amazon's schema often uses "items.0.value", so we better handle array indices.
        
        const isNextAnArray = !isNaN(Number(nextPart));
        
        if (!current[part]) {
          current[part] = isNextAnArray ? [] : {};
        }
        current = current[part];
      }
    }
  }

  return deepClean(result);
}
