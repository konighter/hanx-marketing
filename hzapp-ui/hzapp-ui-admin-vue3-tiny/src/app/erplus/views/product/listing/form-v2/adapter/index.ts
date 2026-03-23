import { DefaultProps } from '../registry'

export interface AttributeSchema {
  field: string
  label: string
  type: string
  props: Record<string, any>
  rules?: any[]
  options?: any[] // For select/checkbox
  description?: string
}

export const normalizeAttribute = (attr: any): AttributeSchema => {
  let type = 'INPUT'
  const props: Record<string, any> = {}
  const rules: any[] = []

  // Determine Component Type
  if (attr.options && attr.options.length > 0) {
    type = 'SELECT'
    if (attr.mulSelection) {
      props.multiple = true
    }
    if (attr.customizable) {
      props.allowCreate = true
    }
  } else if (attr.attrType === 'NUMBER') { // Assuming there might be a number type
    type = 'NUMBER'
  } else {
    type = 'INPUT'
    if (attr.isEditable === false) {
      props.disabled = true
    }
  }

  // Merge Default Props
  Object.assign(props, DefaultProps[type] || {})

  // Placeholder
  props.placeholder = type === 'SELECT' ? `请选择${attr.attrName}` : `请输入${attr.attrName}`

  // Validation Rules
  if (attr.isRequired) {
    rules.push({ required: true, message: props.placeholder, trigger: 'change' })
  }

  return {
    field: attr.attrCode,
    label: attr.attrName,
    type,
    props,
    rules,
    options: attr.options,
    description: attr.attrDescription
  }
}
