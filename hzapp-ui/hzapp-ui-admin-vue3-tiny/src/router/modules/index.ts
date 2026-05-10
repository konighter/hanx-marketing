const modules = import.meta.glob(['./**/*.ts', '../../app/**/router/*.ts'], { eager: true })

const remainingRouter: AppRouteRecordRaw[] = []

Object.keys(modules).forEach((key) => {
  if (key === './index.ts' || key === './modules/index.ts') return
  const mod = (modules[key] as any).default
  if (mod) {
    const modList = Array.isArray(mod) ? [...mod] : [mod]
    remainingRouter.push(...modList)
  }
})

export default remainingRouter
