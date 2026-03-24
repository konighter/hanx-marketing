import SpuTableSelect from './SpuTableSelect.vue'

export class PropertyAndValues {
  id!: number
  name!: string
  values?: any[]
}

export class RuleConfig {
  name!: string
  rule!: (arg: any) => boolean
  message!: string
}

export { SpuTableSelect }
