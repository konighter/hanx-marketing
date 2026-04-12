import { ElInput, ElSelect, ElOption, ElCheckbox, ElCheckboxGroup, ElInputNumber } from 'element-plus'

export const ComponentMap: Record<string, any> = {
  'INPUT': ElInput,
  'SELECT': ElSelect,
  'CHECKBOX': ElCheckbox,
  'CHECKBOX_GROUP': ElCheckboxGroup,
  'NUMBER': ElInputNumber
}

export const DefaultProps: Record<string, any> = {
  'INPUT': { clearable: true },
  'SELECT': { clearable: true, filterable: true },
  'NUMBER': { controlsPosition: 'right' }
}
