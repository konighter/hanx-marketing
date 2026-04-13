import { ref, watch, type Ref } from 'vue';
import { get } from 'lodash-es';

export interface LogicExpression {
  operator: 'AND' | 'OR' | 'NOT' | 'EQ' | 'IN' | 'EMPTY' | 'NOT_EMPTY';
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
 * Recursively evaluate a LogicExpression against form data.
 */
export function evaluateLogic(expr: LogicExpression, data: any): boolean {
  if (!expr) return true;

  const { operator, field, value, children } = expr;
  
  // Support both flat keys (e.g. 'a.0.b') and nested access
  const fieldValue = field ? (data[field] !== undefined ? data[field] : get(data, field)) : undefined;

  switch (operator) {
    case 'EQ':
      // Loose string comparison for form inputs
      return String(fieldValue ?? '') === String(value ?? '');
    case 'IN':
      return Array.isArray(value) && value.map(String).includes(String(fieldValue ?? ''));
    case 'EMPTY':
      return fieldValue === undefined || fieldValue === null || fieldValue === '' || (Array.isArray(fieldValue) && fieldValue.length === 0);
    case 'NOT_EMPTY':
      return fieldValue !== undefined && fieldValue !== null && fieldValue !== '' && (!Array.isArray(fieldValue) || fieldValue.length > 0);
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
      // Initialize with default states from schema
      vMap[field.id] = true;
      rMap[field.id] = !!field.required;

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
