<template>
    <el-card class="chart-container" shadow="never">

        <!-- 指标选择弹窗 -->
        <el-dialog v-model="metricSelectorVisible" title="选择展示指标" width="500px" append-to-body>
            <div class="metric-selector-content">
                <div class="metric-selector-tip">
                    最多可选择 4 个指标进行展示
                </div>
                <el-checkbox-group v-model="selectedMetrics" @change="onMetricsChange" class="metric-checkbox-group">
                    <el-checkbox
v-for="metric in availableMetrics" :key="metric.key" :label="metric.key"
                        :disabled="selectedMetrics.length >= 4 && !selectedMetrics.includes(metric.key)">
                        {{ metric.label }}
                    </el-checkbox>
                </el-checkbox-group>
            </div>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="metricSelectorVisible = false">取消</el-button>
                    <el-button type="primary" @click="confirmMetrics">确定</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- 坐标轴配置弹窗 -->
        <el-dialog v-model="axisConfigVisible" title="坐标轴配置" width="600px" append-to-body>
            <div class="axis-config-content">
                <div class="axis-config-section">
                    <div class="axis-config-title">
                        <Icon icon="ep:promotion" class="mr-2" /> 左轴指标配置
                    </div>
                    <el-select
v-model="leftAxisMetrics" multiple placeholder="请选择左轴指标（最多2个）" class="axis-select"
                        @change="updateChartAxis">
                        <el-option
v-for="metric in selectedMetrics" :key="metric" :label="getMetricLabel(metric)"
                            :value="metric" />
                    </el-select>
                    <div class="axis-tip">建议选择数值量级相近的指标</div>
                </div>

                <div class="axis-config-section">
                    <div class="axis-config-title">
                        <Icon icon="ep:rank" class="mr-2" /> 右轴指标配置
                    </div>
                    <el-select
v-model="rightAxisMetrics" multiple placeholder="请选择右轴指标（最多2个）" class="axis-select"
                        @change="updateChartAxis">
                        <el-option
v-for="metric in selectedMetrics.filter(m => !leftAxisMetrics.includes(m))"
                            :key="metric" :label="getMetricLabel(metric)" :value="metric" />
                    </el-select>
                    <div class="axis-tip">右轴将自动排除左轴已选指标</div>
                </div>
            </div>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="axisConfigVisible = false">关闭</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- 图表区域 -->
        <div class="chart-area">
            <div ref="chartContainer" class="chart-wrapper"></div>
        </div>
    </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { useResizeObserver } from '@vueuse/core'

// 定义组件属性
interface Props {
    accountId?: string | number
    shopId?: string | number
    entityType?: 'CAMPAIGN' | 'ADGROUP' | 'AD' | 'ACCOUNT'
    queryParams?: {
        campaignIds?: number[]
        adGroupIds?: number[]
        adIds?: number[]
    }
    dateRange?: [string, string] | null
    timeUnit?: 'day' | 'week' | 'month'
}

const props = withDefaults(defineProps<Props>(), {
    entityType: 'CAMPAIGN',
    queryParams: () => ({}),
    timeUnit: 'day'
})

// 定义 emits
const emit = defineEmits<{
    (e: 'data-loaded', data: any): void
    (e: 'update:dateRange', dates: [string, string]): void
}>()

// Tab 标签映射
const entityLabels: Record<string, string> = {
    CAMPAIGN: '广告活动',
    ADGROUP: '广告组',
    AD: '广告',
    ACCOUNT: '广告账号'
}

// 根据 tab 获取选中的 IDs
const getSelectedIds = () => {
    switch (props.entityType) {
        case 'CAMPAIGN':
            return props.queryParams?.campaignIds || []
        case 'ADGROUP':
            return props.queryParams?.adGroupIds || []
        case 'AD':
            return props.queryParams?.adIds || []
        default:
            return []
    }
}

// 计算图表标题
const chartTitle = computed(() => {
    const label = entityLabels[props.entityType as string] || '数据'
    return `${label}数据趋势`
})

// 响应式数据
const chartContainer = ref<HTMLDivElement | null>(null)
const chartInstance = ref<echarts.ECharts | null>(null)

// 新增：高度相关的响应式数据
const isCompactHeight = computed(() => {
    return window.innerHeight < 800
})

const windowSize = ref({
    width: window.innerWidth,
    height: window.innerHeight
})



// 防抖函数
const debounce = (func: Function, wait: number) => {
    let timeout: NodeJS.Timeout
    return function executedFunction(...args: any[]) {
        const later = () => {
            clearTimeout(timeout)
            func(...args)
        }
        clearTimeout(timeout)
        timeout = setTimeout(later, wait)
    }
}

// 弹窗控制
const metricSelectorVisible = ref(false)
const axisConfigVisible = ref(false)

import { AdsReportApi } from '@/app/erplus/api/adv/report'

// 可用指标配置
const availableMetrics = [
    { key: 'impressions', label: '展示量', unit: '次' },
    { key: 'clicks', label: '点击量', unit: '次' },
    { key: 'ctr', label: '点击率', unit: '%' },
    { key: 'spend', label: '花费', unit: '元' }, // Changed from cost to spend
    { key: 'sales', label: '销售额', unit: '元' },
    { key: 'acos', label: 'ACOS', unit: '%' },
    { key: 'roas', label: 'ROAS', unit: '' },
    { key: 'orders', label: '订单数', unit: '单' }
]

// 选中的指标
const selectedMetrics = ref<string[]>(['impressions', 'clicks', 'spend', 'sales'])

// 轴设置
const leftAxisMetrics = ref<string[]>(['impressions', 'clicks'])
const rightAxisMetrics = ref<string[]>(['spend', 'sales'])

// 图表数据
const chartData = ref<{ dates: string[], series: Record<string, number[]> }>({ dates: [], series: {} })

// 检测是否为暗黑模式
const isDark = ref(document.documentElement.classList.contains('dark'))
const observer = new MutationObserver(() => {
    isDark.value = document.documentElement.classList.contains('dark')
})

onMounted(() => {
    observer.observe(document.documentElement, {
        attributes: true,
        attributeFilter: ['class']
    })
})

onUnmounted(() => {
    observer.disconnect()
})

watch(() => isDark.value, () => {
    if (chartInstance.value) {
        updateChart()
    }
})

// 类型定义
interface ChartSeriesItem {
    name: string
    type: string
    yAxisIndex: number
    data: any[]
    smooth: boolean
    symbol: string
    symbolSize: number
    showSymbol: boolean
    lineStyle: any
    emphasis: any
}

interface ChartYAxisItem {
    type: string
    name: string
    position: string
    axisLine: any
    axisLabel: any
    axisTick: any
    splitLine: any
}

// 方法
function handleConfigCommand(command: string) {
    switch (command) {
        case 'selectMetrics':
            metricSelectorVisible.value = true
            break
        case 'resetMetrics':
            resetMetrics()
            break
        case 'configureAxes':
            axisConfigVisible.value = true
            break
    }
}

function confirmMetrics() {
    metricSelectorVisible.value = false
    updateChart()
}

function resetMetrics() {
    selectedMetrics.value = ['impressions', 'clicks', 'spend', 'sales']
    leftAxisMetrics.value = ['impressions', 'clicks']
    rightAxisMetrics.value = ['spend', 'sales']
    updateChart()
}

function onMetricsChange() {
    // 确保轴设置与选中指标一致
    leftAxisMetrics.value = leftAxisMetrics.value.filter(metric =>
        selectedMetrics.value.includes(metric)
    )
    rightAxisMetrics.value = rightAxisMetrics.value.filter(metric =>
        selectedMetrics.value.includes(metric)
    )

    // 如果某个轴为空，自动分配指标
    if (leftAxisMetrics.value.length === 0 && selectedMetrics.value.length > 0) {
        leftAxisMetrics.value = [selectedMetrics.value[0]]
    }
    if (rightAxisMetrics.value.length === 0 && selectedMetrics.value.length > 1) {
        rightAxisMetrics.value = [selectedMetrics.value[1]]
    }
}

function getMetricLabel(key: string) {
    const metric = availableMetrics.find(m => m.key === key)
    return metric ? metric.label : key
}

function getMetricUnit(key: string) {
    const metric = availableMetrics.find(m => m.key === key)
    return metric ? metric.unit : ''
}

async function loadChartData() {
    if (!props.shopId || !props.dateRange || !props.dateRange[0] || !props.dateRange[1]) {
        return
    }
    // 防止并发调用（ResizeObserver 和 watcher 可能同时触发）
    if (isLoading) return
    isLoading = true

    try {
        const dimMap: Record<string, string> = {
            day: 'date',
            week: 'week',
            month: 'month'
        }
        const dimension = dimMap[props.timeUnit || 'day']

        const res = await AdsReportApi.queryAdsReport({
            shopId: props.shopId as any,
            startDate: props.dateRange[0],
            endDate: props.dateRange[1],
            dimensions: [dimension],
            metrics: availableMetrics.map(m => m.key),
            orderBy: dimension,
            isAsc: true
        })

        let dates: string[] = []
        const series: Record<string, number[]> = {}

        // 如果是天维度，自动补全日期范围内缺失的数据行，防止 ECharts 渲染或 Tooltip 计算错位
        if (props.timeUnit === 'day' || !props.timeUnit) {
            dates = getDatesInRange(props.dateRange[0], props.dateRange[1])
            availableMetrics.forEach(m => {
                series[m.key] = new Array(dates.length).fill(0)
            })

            res.rows.forEach(row => {
                const dateDim = row.dimensions.find(d => d.key === dimension)
                if (dateDim) {
                    const dateIndex = dates.indexOf(dateDim.value)
                    if (dateIndex !== -1) {
                        row.metrics.forEach(m => {
                            if (series[m.key]) series[m.key][dateIndex] = m.value
                        })
                    }
                }
            })
        } else {
            // 周/月维度暂时直接使用返回的数据行
            res.rows.forEach(row => {
                const dateDim = row.dimensions.find(d => d.key === dimension)
                if (dateDim) {
                    dates.push(dateDim.value)
                    availableMetrics.forEach(m => {
                        if (!series[m.key]) series[m.key] = []
                        const metricData = row.metrics.find(rm => rm.key === m.key)
                        series[m.key].push(metricData ? metricData.value : 0)
                    })
                }
            })
        }

        chartData.value = {
            dates,
            series
        }
        emit('data-loaded', res)
        updateChart()
    } catch (error) {
        console.error('加载图表数据失败:', error)
        ElMessage.error('加载数据失败')
    } finally {
        isLoading = false
    }
}

/**
 * 获取日期范围内的所有日期字符串 (YYYY-MM-DD)
 */
function getDatesInRange(startDate: string, endDate: string) {
    const dates = []
    const start = new Date(startDate)
    const end = new Date(endDate)
    const curr = new Date(start)
    
    // 设置为 UTC/本地时间统一处理，避免跨天问题
    while (curr <= end) {
        dates.push(curr.toISOString().split('T')[0])
        curr.setDate(curr.getDate() + 1)
    }
    return dates
}

function updateChartAxis() {
    updateChart()
}

// ===== 核心状态标记 =====
let chartReady = false       // 图表是否已完成首次渲染
let resizeLocked = false     // setOption 动画期间锁定 resize，防止 ResizeObserver 干扰
let pendingResize = false    // 锁定期间是否有 resize 请求需要延迟执行
let isLoading = false        // loadChartData 并发锁

function updateChart() {
    if (!chartInstance.value || !chartContainer.value) return

    const option = generateChartOption()

    // 在 setOption 前锁定 resize —— el-collapse 过渡期间 ResizeObserver 会高频触发,
    // 如果在 setOption 动画(600ms)期间被 resize()，会破坏 ECharts 内部 coordinateSystem
    // 绑定，导致后续点击图例时 seriesTaskReset 读取到 undefined 而崩溃
    resizeLocked = true
    pendingResize = false

    if (!chartReady) {
        // 首次渲染：使用 notMerge 做一次干净的全量初始化
        chartInstance.value.setOption(option, { notMerge: true })
    } else {
        // 后续更新：使用 replaceMerge 精确控制组件更新策略
        chartInstance.value.setOption(option, { replaceMerge: ['series', 'xAxis', 'yAxis'] })
    }
    chartReady = true

    // 动画结束后解除 resize 锁定，并执行被拦截的 pending resize
    setTimeout(() => {
        resizeLocked = false
        if (pendingResize && chartInstance.value) {
            pendingResize = false
            chartInstance.value.resize()
        }
    }, 650) // 略大于 animationDuration(600ms)，确保动画完全结束
}

// 生命周期
onMounted(() => {
    if (!chartContainer.value) return

    // 核心：el-collapse 展开时有 CSS 过渡动画，onMounted 触发时容器宽高可能为 0
    // 必须等容器有真实尺寸后再 init ECharts，否则内部状态会损坏
    let chartInited = false
    let resizeTimer: ReturnType<typeof setTimeout> | null = null

    useResizeObserver(chartContainer, (entries) => {
        const { width, height } = entries[0].contentRect
        if (width <= 0 || height <= 0) return

        if (!chartInited) {
            // 首次检测到非零尺寸：安全地初始化 ECharts
            chartInited = true
            chartInstance.value = echarts.init(chartContainer.value!)
            loadChartData()

            // 添加窗口大小监听
            window.addEventListener('resize', debouncedResize)
        } else if (chartReady) {
            // 后续尺寸变化：防抖 + 锁定检查，避免过渡期间冲击 setOption 动画
            if (resizeLocked) {
                pendingResize = true
                return
            }
            if (resizeTimer) clearTimeout(resizeTimer)
            resizeTimer = setTimeout(() => {
                chartInstance.value?.resize()
            }, 100)
        }
    })
})

onUnmounted(() => {
    // 移除事件监听
    window.removeEventListener('resize', debouncedResize)
    chartReady = false
    resizeLocked = false
    pendingResize = false
    isLoading = false

    // 销毁图表实例
    if (chartInstance.value) {
        chartInstance.value.dispose()
        chartInstance.value = null
    }
})

// 监听属性变化 —— 均添加 chartInstance 守卫，防止在图表未初始化时发起无效请求
watch(() => [props.accountId, props.shopId], () => {
    if (chartInstance.value) loadChartData()
})

// 监听 tab 切换，重新加载数据
watch(() => props.entityType, () => {
    if (chartInstance.value) loadChartData()
})

// 监听 selectedIds、dateRange、timeUnit 变化
watch(() => [
    props.queryParams?.campaignIds, 
    props.queryParams?.adGroupIds, 
    props.queryParams?.adIds,
    props.dateRange,
    props.timeUnit
], () => {
    if (chartInstance.value) loadChartData()
}, { deep: true })

// 新增：监听窗口高度变化
watch(() => isCompactHeight.value, () => {
    if (chartReady) {
        updateChart()
    }
})

// 根据屏幕大小动态调整基础高度比例
const getBaseHeightRatio = () => {
    const width = window.innerWidth;
    if (width >= 1441) return 0.38;  // 大屏幕38%
    if (width >= 1024) return 0.35;  // 中等屏幕35%
    return 0.36;  // 其他屏幕36%
}

// 窗口大小监听函数（使用动态高度比例）
const handleResize = () => {
    windowSize.value = {
        width: window.innerWidth,
        height: window.innerHeight
    }

    // 使用动态高度比例调整图表容器高度
    if (chartContainer.value) {
        const baseHeightRatio = getBaseHeightRatio();
        const baseHeight = window.innerHeight * baseHeightRatio;
        const cardPadding = 3;         // el-card body padding (2px + 1px)
        const headerHeight = 36;       // card header高度
        const legendSpace = 40;        // 图例空间
        const marginSpace = 3;         // 进一步减少margin和边距
        const totalReserved = cardPadding + headerHeight + legendSpace + marginSpace;

        const availableHeight = baseHeight - totalReserved;
        const minChartHeight = isCompactHeight.value ? 250 : 280;
        const maxChartHeight = 700;    // 大屏幕允许更高
        const finalHeight = Math.min(Math.max(availableHeight, minChartHeight), maxChartHeight);

        const chartArea = chartContainer.value.closest('.chart-area') as HTMLElement;
        if (chartArea) {
            chartArea.style.height = `${finalHeight}px`;
        }
    }

    // 只有在chart初始化并完成首次渲染后才能 resize
    if (chartReady && chartInstance.value) {
        chartInstance.value.resize();
    }
}

// 防抖的resize处理（在handleResize定义之后）
const debouncedResize = debounce(handleResize, 300)

// 暴露方法给父组件
defineExpose({
    refresh: loadChartData,
    handleConfigCommand
})

function generateChartOption() {
    const dates = chartData.value.dates || []
    const seriesData = chartData.value.series || {}

    // 颜色主题配置
    const colorPalette = [
        '#5470c6', '#91cc75', '#fac858', '#ee6666',
        '#73c0de', '#3ba272', '#fc8452', '#9a60b4'
    ]

    const series: ChartSeriesItem[] = []
    const yAxis: ChartYAxisItem[] = []

    const getMetricType = (key: string) => {
        // 展示和点击使用折线图，销售额和花费使用柱状图
        if (['impressions', 'clicks'].includes(key)) return 'line'
        if (['sales', 'spend'].includes(key)) return 'bar'
        return 'line' // 默认其他使用折线
    }

    // 左轴系列
    leftAxisMetrics.value.forEach((metric, index) => {
        const type = getMetricType(metric)
        series.push({
            id: metric,
            name: getMetricLabel(metric),
            type: type,
            yAxisIndex: 0,
            data: seriesData[metric] || [],
            smooth: true,
            symbol: type === 'bar' ? undefined : 'none',
            symbolSize: 6,
            showSymbol: false,
            barMaxWidth: 30, // 限制柱子宽度
            itemStyle: {
                color: colorPalette[index],
                borderRadius: type === 'bar' ? [4, 4, 0, 0] : undefined
            },
            lineStyle: type === 'line' ? {
                width: 3,
                color: colorPalette[index]
            } : undefined,
            emphasis: {
                focus: 'series',
                showSymbol: true,
                symbol: 'circle',
                symbolSize: 8,
                itemStyle: {
                    borderWidth: 2,
                    borderColor: '#fff',
                    shadowColor: 'rgba(0, 0, 0, 0.3)',
                    shadowBlur: 6
                }
            }
        } as any)
    })

    // 右轴系列
    rightAxisMetrics.value.forEach((metric, index) => {
        const type = getMetricType(metric)
        const colorIndex = leftAxisMetrics.value.length + index
        series.push({
            id: metric,
            name: getMetricLabel(metric),
            type: type,
            yAxisIndex: 1,
            data: seriesData[metric] || [],
            smooth: true,
            symbol: type === 'bar' ? undefined : 'none',
            symbolSize: 6,
            showSymbol: false,
            barMaxWidth: 30,
            itemStyle: {
                color: colorPalette[colorIndex],
                borderRadius: type === 'bar' ? [4, 4, 0, 0] : undefined
            },
            lineStyle: type === 'line' ? {
                width: 2,
                color: colorPalette[colorIndex],
                type: 'dashed'
            } : undefined,
            emphasis: {
                focus: 'series',
                showSymbol: true,
                symbol: 'circle',
                symbolSize: 8,
                itemStyle: {
                    borderWidth: 2,
                    borderColor: '#fff',
                    shadowColor: 'rgba(0, 0, 0, 0.3)',
                    shadowBlur: 6
                }
            }
        } as any)
    })

    // Y轴配置: 固定提供两个轴（索引0和1），防止 yAxisIndex 越界导致 ECharts 崩溃
    yAxis.push({
        type: 'value',
        position: 'left',
        show: leftAxisMetrics.value.length > 0,
        axisLine: { show: false },
        axisLabel: {
            color: isDark.value ? '#aaa' : '#999',
            fontSize: 12
        },
        axisTick: { show: false },
        splitLine: {
            lineStyle: {
                type: 'dashed',
                color: isDark.value ? '#333' : '#eee'
            }
        }
    })

    yAxis.push({
        type: 'value',
        position: 'right',
        show: rightAxisMetrics.value.length > 0,
        axisLine: { show: false },
        axisLabel: {
            color: isDark.value ? '#aaa' : '#999',
            fontSize: 12
        },
        axisTick: { show: false },
        splitLine: { show: false }
    })

    return {
        backgroundColor: 'transparent',
        color: colorPalette,
        textStyle: {
            fontFamily: 'Arial, sans-serif',
            fontSize: 12,
            color: isDark.value ? '#eee' : '#333'
        },
        tooltip: {
            show: true,
            trigger: 'axis',
            confine: true, 
            enterable: true,
            transitionDuration: 0,
            backgroundColor: isDark.value ? '#222' : 'rgba(255, 255, 255, 0.98)',
            borderColor: isDark.value ? '#444' : '#ddd',
            borderWidth: 1,
            padding: 12,
            z: 100, // ECharts internal Z-index for tooltip components
            extraCssText: `box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15); border-radius: 8px; z-index: 99999; pointer-events: auto !important;`,
            axisPointer: {
                type: 'shadow',
                shadowStyle: {
                    color: isDark.value ? 'rgba(200, 200, 200, 0.05)' : 'rgba(0, 0, 0, 0.05)'
                }
            },
            formatter: function (params: any) {
                try {
                    if (!params || !params.length) return ''
                    const dataIndex = params[0].dataIndex
                    if (dataIndex === undefined) return ''

                    const titleColor = isDark.value ? '#fff' : '#333'
                    const textColor = isDark.value ? '#ccc' : '#666'
                    
                    let result = `<div style="font-weight: 600; font-size: 13px; margin-bottom: 10px; border-bottom: 1px solid ${isDark.value ? '#444' : '#eee'}; padding-bottom: 6px; color: ${titleColor};">${params[0].name}</div>`
                    
                    // 动态构建全指标列表
                    let hasContent = false
                    availableMetrics.forEach((metric) => {
                        const unit = metric.unit
                        const seriesData = chartData.value.series?.[metric.key]
                        if (!seriesData || seriesData[dataIndex] === undefined) return
                        
                        hasContent = true
                        const value = seriesData[dataIndex]
                        const displayValue = (typeof value === 'number') 
                            ? (unit === '%' ? value.toFixed(2) : value.toLocaleString())
                            : (value !== null && value !== undefined ? value : '0')
                        
                        // 匹配当前指标在图表中的颜色
                        const paramItem = params.find((p: any) => p.seriesName === metric.label)
                        const dotColor = paramItem ? paramItem.color : (isDark.value ? '#555' : '#ccc')
                        
                        result += `
                            <div style="display: flex; align-items: center; justify-content: space-between; margin: 5px 0; min-width: 180px;">
                                <div style="display: flex; align-items: center;">
                                    <span style="display: inline-block; width: 8px; height: 8px; border-radius: 50%; background-color: ${dotColor}; margin-right: 10px;"></span>
                                    <span style="color: ${textColor}; font-size: 12px;">${metric.label}</span>
                                </div>
                                <span style="font-weight: 600; margin-left: 15px; color: ${titleColor}; font-family: 'Roboto Mono', monospace; font-size: 12px;">${displayValue}${unit}</span>
                            </div>
                        `
                    })
                    
                    return hasContent ? result : params[0].name
                } catch (e) {
                    console.error('Tooltip Formatter Error:', e)
                    return 'Data Error'
                }
            }
        },
        legend: {
            data: [...leftAxisMetrics.value, ...rightAxisMetrics.value].map(m => ({
                name: getMetricLabel(m),
                icon: getMetricType(m) === 'bar' ? 'roundRect' : 'circle' 
            })),
            top: 20,
            left: 'center',
            textStyle: {
                color: isDark.value ? '#bbb' : '#666',
                fontSize: isCompactHeight.value ? 10 : 12
            },
            itemWidth: isCompactHeight.value ? 10 : 12,
            itemHeight: isCompactHeight.value ? 10 : 12,
            itemGap: 20
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            top: isCompactHeight.value ? 60 : 80,
            containLabel: true
        },
        xAxis: {
            type: 'category',
            boundaryGap: true, // 有柱状图时需要设为 true
            data: dates,
            axisLine: {
                lineStyle: {
                    color: isDark.value ? '#333' : '#eee'
                }
            },
            axisTick: { show: false },
            axisLabel: {
                color: isDark.value ? '#aaa' : '#999',
                fontSize: 12
            },
            splitLine: { show: false }
        },
        yAxis: yAxis,
        series: series,
        animationDuration: 600,
        animationEasing: 'cubicOut' as const
    }
}



</script>

<style scoped>
.chart-container {
    margin-top: 20px;
}


.chart-title {
    font-size: 16px;
    font-weight: 600;
    color: var(--el-text-color-primary);
}

.chart-controls {
    display: flex;
    gap: 12px;
    align-items: center;
}

.date-picker {
    width: 280px;
}

/* 指标选择弹窗样式 */
.metric-selector-content {
    padding: 20px 0;
}

.metric-selector-tip {
    color: #666;
    font-size: 14px;
    margin-bottom: 20px;
    text-align: center;
}

.metric-checkbox-group {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;
}

/* 坐标轴配置弹窗样式 */
.axis-config-content {
    padding: 20px 0;
}

.axis-config-section {
    margin-bottom: 25px;
}

.axis-config-section:last-child {
    margin-bottom: 0;
}

.axis-config-title {
    font-weight: 500;
    margin-bottom: 12px;
    color: #333;
    display: flex;
    align-items: center;
}

.axis-select {
    width: 100%;
    margin-bottom: 8px;
}

.axis-tip {
    color: #999;
    font-size: 12px;
    text-align: center;
}

.chart-area {
    height: calc(35vh - 15px);
    margin: 0 0 1px 0;
    min-height: 280px;
    max-height: 600px;
}

.chart-wrapper {
    width: 100%;
    height: 100%;
    min-height: 250px;
}

.chart-container {
    margin-top: 2px;
}

/* 减少el-card body内边距 - 使用deep穿透scoped样式 */
:deep(.el-card__body) {
    padding: 2px 20px 1px 20px !important;
}

/* 中等屏幕优化 */
@media (min-width: 1024px) and (max-width: 1440px) {
    .chart-area {
        height: calc(35vh - 10px);
        min-height: 260px;
    }
}

/* 大屏幕优化 */
@media (min-width: 1441px) {
    .chart-area {
        height: calc(38vh - 12px);
        max-height: 700px;
    }
}

/* 紧凑高度下的优化 */
@media (max-height: 800px) {
    .chart-area {
        height: calc(36vh - 10px);
        min-height: 250px;
    }

    .chart-wrapper {
        min-height: 230px;
    }
}

/* 大屏幕优化 */
@media (min-width: 1441px) {
    .chart-area {
        height: calc(32vh - 110px);
        max-height: 600px;
    }
}

/* 紧凑高度下的优化 */
@media (max-height: 800px) {
    .chart-area {
        height: calc(32vh - 95px);
        min-height: 260px;
    }

    .chart-wrapper {
        min-height: 240px;
    }
}

/* 响应式设计 */
@media (max-width: 768px) {
    .chart-controls {
        flex-direction: column;
        gap: 10px;
        align-items: stretch;
    }

    .date-picker {
        width: 100%;
    }

    .metric-checkbox-group {
        grid-template-columns: 1fr;
    }

    .axis-setting-item {
        flex-direction: column;
        align-items: flex-start;
        gap: 8px;
    }

    .axis-label {
        width: auto;
    }
}
</style>