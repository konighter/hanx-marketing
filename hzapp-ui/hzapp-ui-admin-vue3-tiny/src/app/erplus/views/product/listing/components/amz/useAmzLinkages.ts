import { ref, watch, type Ref } from 'vue';
import { get } from 'lodash-es';

export interface LogicExpression {
  operator: 'AND' | 'OR' | 'NOT' | 'EQ' | 'IN' | 'EMPTY' | 'NOT_EMPTY' | 'REQUIRED';
  field?: string;
  value?: any;
  children?: LogicExpression[];
}

export interface LinkageRule {
  type: 'visibility' | 'requirement';
  action: 'show' | 'hide' | 'required' | 'optional';
  conditionLogic: LogicExpression;
  condition?: string; // Human-readable string from backend
}

/**
 * Resolves a field value from data, handling both flattened paths and indexed paths.
 * If 'a.b' is not found and 'a' is an array, it tries 'a.0.b'.
 * This is crucial for Amazon SP-API schemas where composite fields are often arrays-of-objects.
 */
function resolveFieldValue(path: string, data: any): any {
  if (!path) return undefined;
  
  // 1. Try direct lookup (literal key)
  if (data[path] !== undefined) return data[path];
  
  // 2. Try standard nested lookup (lodash get)
  const val = get(data, path);
  if (val !== undefined) return val;
  
  // 3. Intelligent path resolution for indexed arrays
  // Handles 'variation_theme.name' -> 'variation_theme[0].name'
  const parts = path.split('.');
  if (parts.length > 1) {
    let current = data;
    for (const part of parts) {
      if (current === undefined || current === null) return undefined;
      
      // If current is an array and the part is a property name, assume index 0
      if (Array.isArray(current) && isNaN(Number(part))) {
        current = current[0];
      }
      
      if (current === undefined || current === null) return undefined;
      current = current[part];
    }
    return current;
  }
  
  return undefined;
}

/**
 * Recursively evaluate a LogicExpression against form data.
 */
export function evaluateLogic(expr: LogicExpression, data: any): boolean {
  if (!expr) return true;

  const { operator, field, value, children } = expr;
  
  // Resolve field value using the smart helper
  const fieldValue = field ? resolveFieldValue(field, data) : undefined;

  switch (operator) {
    case 'EQ':
      if (fieldValue === undefined || fieldValue === null) return false;
      return String(fieldValue) === String(value ?? '');
    case 'IN':
      if (fieldValue === undefined || fieldValue === null) return false;
      return Array.isArray(value) && value.map(String).includes(String(fieldValue));
    case 'EMPTY':
      return fieldValue === undefined || fieldValue === null || fieldValue === '' || (Array.isArray(fieldValue) && fieldValue.length === 0);
    case 'REQUIRED':
    case 'NOT_EMPTY':
      return fieldValue !== undefined && 
             fieldValue !== null && 
             fieldValue !== '' && 
             (!Array.isArray(fieldValue) || fieldValue.length > 0);
    case 'AND':
      return children ? children.every(c => evaluateLogic(c, data)) : true;
    case 'OR':
      return children ? children.some(c => evaluateLogic(c, data)) : false;
    case 'NOT':
      return children && children.length > 0 ? !evaluateLogic(children[0], data) : true;
    default:
      return true;
  }
}

/**
 * Composable to manage field visibility and requirement status based on linkage rules.
 */
export function useAmzLinkages(fields: Ref<any[]>, formData: any) {
  const visibilityMap = ref<Record<string, boolean>>({});
  const requirementMap = ref<Record<string, boolean>>({});

  const updateMaps = () => {
    const vMap: Record<string, boolean> = {};
    const rMap: Record<string, boolean> = {};

    fields.value.forEach(field => {
      // Initialize visibility
      vMap[field.id] = true;
      
      // Important: If a field has dynamic requirement rules, its initial required state 
      // should be false (or the rules should explicitly drive it).
      // This prevents fields that are marked 'required' in the static schema (like Amazon's .value wrapper)
      // from staying required even when a linkage rule doesn't trigger.
      const hasRequirementRules = field.linkages?.some((l: LinkageRule) => l.type === 'requirement');
      rMap[field.id] = hasRequirementRules ? false : !!field.required;

      // Apply rules if present
      if (field.linkages && field.linkages.length > 0) {
        const data = formData.value || formData;
        field.linkages.forEach((rule: LinkageRule) => {
          const isTriggered = evaluateLogic(rule.conditionLogic, data);
          
          if (rule.type === 'visibility') {
            if (rule.action === 'show') vMap[field.id] = isTriggered;
            if (rule.action === 'hide') vMap[field.id] = !isTriggered;
          } else if (rule.type === 'requirement') {
            if (rule.action === 'required') {
                if (isTriggered) rMap[field.id] = true;
            } else if (rule.action === 'optional') {
                if (isTriggered) rMap[field.id] = false;
            }
          }

          // Debug logging when state changes due to a trigger
          if (isTriggered) {
            const stateDesc = rule.type === 'visibility' ? (vMap[field.id] ? 'VISIBLE' : 'HIDDEN') : (rMap[field.id] ? 'REQUIRED' : 'OPTIONAL');
            console.log(`%c[Linkage Triggered] %c${field.id} -> ${stateDesc}`, 'color: #409eff; font-weight: bold', 'color: #333', {
                condition: rule.condition,
                action: rule.action
            });
          }
        });
      }
    });

    visibilityMap.value = vMap;
    requirementMap.value = rMap;
  };

  // Reactively update when data or schema changes
  watch(formData, () => {
    updateMaps();
  }, { deep: true, immediate: true });

  watch(fields, () => {
    updateMaps();
  }, { immediate: true });

  return {
    visibilityMap,
    requirementMap
  };
}
