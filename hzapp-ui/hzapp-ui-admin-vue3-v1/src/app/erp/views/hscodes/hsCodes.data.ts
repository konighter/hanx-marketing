import type { CrudSchema } from '@/hooks/web/useCrudSchemas'
import { dateFormatter } from '@/utils/formatTime'

// 表单校验
export const rules = reactive({
  code: [required]
})

// CrudSchema https://help.h2z.ltd/vue3/crud-schema/
const crudSchemas = reactive<CrudSchema[]>([
  {
    label: 'ID',
    field: 'id',
    isForm: false
  },
  {
    label: '海关编码',
    field: 'code',
    isSearch: true
  },
  {
    label: '编码描述',
    field: 'description',
    isSearch: true,
    form: {
      component: 'Editor',
      componentProps: {
        valueHtml: '',
        height: 200
      }
    }
  },
  {
    label: '品类',
    field: 'categoryId',
    isSearch: true,
    form: {
      component: 'InputNumber',
      value: 0
    }
  },
  {
    label: '创建时间',
    field: 'createTime',
    formatter: dateFormatter,
    isSearch: true,
    search: {
      component: 'DatePicker',
      componentProps: {
        valueFormat: 'YYYY-MM-DD HH:mm:ss',
        type: 'daterange',
        defaultTime: [new Date('1 00:00:00'), new Date('1 23:59:59')]
      }
    },
    isForm: false
  },
  {
    label: '操作',
    field: 'action',
    isForm: false
  }
])
export const { allSchemas } = useCrudSchemas(crudSchemas)
