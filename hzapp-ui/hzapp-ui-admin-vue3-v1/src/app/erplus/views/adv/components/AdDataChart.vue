<template>
    <el-card class="chart-container" shadow="never">
        <template #header>
            <div class="flex justify-between">

                <span class="chart-title">
                    {{ chartTitle }}
                </span>
                <div class="flex items-center w-[280px] justify-end">


                    <!-- 统一的配置下拉菜单 -->
                    <el-dropdown @command="handleConfigCommand" trigger="click">
                        <el-button type="primary">
                            <Icon icon="ep:setting" class="mr-1" /> 图表配置
                            
                        </el-button>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item command="selectMetrics">
                                    <template #default>
                                        <Icon icon="ep:list" class="mr-2" /> 选择展示指标

                                    </template>

                                </el-dropdown-item>
                                <el-dropdown-item command="configureAxes">
                                    <Icon icon="ep:connection" class="mr-2" /> 坐标轴配置
                                </el-dropdown-item>
                                <el-dropdown-item divided command="resetMetrics">
                                    <Icon icon="ep:refresh" class="mr-2" /> 重置默认配置
                                </el-dropdown-item>

                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </div>
            </div>
        </template>

        <!-- 指标选择弹窗 -->
        <el-dialog v-model="metricSelectorVisible" title="选择展示指标" width="500px" append-to-body>
            <div class="metric-selector-content">
                <div class="metric-selector-tip">
                    最多可选择 4 个指标进行展示
                </div>
                <el-checkbox-group v-model="selectedMetrics" @change="onMetricsChange" class="metric-checkbox-group">
                    <el-checkbox v-for="metric in availableMetrics" :key="metric.key" :label="metric.key"
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
                    <el-select v-model="leftAxisMetrics" multiple placeholder="请选择左轴指标（最多2个）" class="axis-select"
                        @change="updateChartAxis">
                        <el-option v-for="metric in selectedMetrics" :key="metric" :label="getMetricLabel(metric)"
                            :value="metric" />
                    </el-select>
                    <div class="axis-tip">建议选择数值量级相近的指标</div>
                </div>

                <div class="axis-config-section">
                    <div class="axis-config-title">
                        <Icon icon="ep:rank" class="mr-2" /> 右轴指标配置
                    </div>
                    <el-select v-model="rightAxisMetrics" multiple placeholder="请选择右轴指标（最多2个）" class="axis-select"
                        @change="updateChartAxis">
                        <el-option v-for="metric in selectedMetrics.filter(m => !leftAxisMetrics.includes(m))"
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

// 定义组件属性
interface Props {
    accountId?: string | number
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
const chartData = ref<any>({})

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
    if (!props.dateRange || props.dateRange.length !== 2) {
        return
    }

    try {
        const res = await AdsReportApi.getPerformanceTrend({
            accountId: props.accountId as any,
            entityType: props.entityType as any || 'CAMPAIGN',
            entityId: getSelectedIds()[0], // Currently support single entity selection in detail
            startDate: props.dateRange[0],
            endDate: props.dateRange[1],
            timeUnit: props.timeUnit
        })

        chartData.value = {
            dates: res.timeList,
            series: res.seriesData
        }
        emit('data-loaded', res)
        updateChart()
    } catch (error) {
        console.error('加载图表数据失败:', error)
        ElMessage.error('加载数据失败')
    }
}

function updateChartAxis() {
    updateChart()
}

function updateChart() {
    if (!chartInstance.value || !chartContainer.value) return

    const option = generateChartOption()
    chartInstance.value.setOption(option, true)

    // 更新后重新调整大小，确保布局正确
    setTimeout(() => {
        if (chartInstance.value) {
            chartInstance.value.resize()
        }
        // 额外调用一次resize确保grid配置生效
        setTimeout(() => {
            if (chartInstance.value) {
                chartInstance.value.resize()
            }
        }, 50)
    }, 100)
}

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

    // 左轴系列
    leftAxisMetrics.value.forEach((metric, index) => {
        if (seriesData[metric]) {
            series.push({
                name: getMetricLabel(metric),
                type: 'line',
                yAxisIndex: 0,
                data: seriesData[metric],
                smooth: true,
                symbol: 'none',
                symbolSize: 6,
                showSymbol: false,
                lineStyle: {
                    width: 3,
                    color: colorPalette[index]
                },
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
            })
        }
    })

    // 右轴系列
    rightAxisMetrics.value.forEach((metric, index) => {
        if (seriesData[metric]) {
            const colorIndex = leftAxisMetrics.value.length + index
            series.push({
                name: getMetricLabel(metric),
                type: 'line',
                yAxisIndex: 1,
                data: seriesData[metric],
                smooth: true,
                symbol: 'none',
                symbolSize: 6,
                showSymbol: false,
                lineStyle: {
                    width: 2,
                    color: colorPalette[colorIndex],
                    type: 'dashed'
                },
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
            })
        }
    })

    // Y轴配置
    if (leftAxisMetrics.value.length > 0) {
        yAxis.push({
            type: 'value',
            name: '左轴',
            position: 'left',
            axisLine: { show: false },
            axisLabel: {
                color: '#999',
                fontSize: 12
            },
            axisTick: { show: false },
            splitLine: {
                lineStyle: {
                    type: 'dashed',
                    color: '#eee'
                }
            }
        })
    }

    if (rightAxisMetrics.value.length > 0) {
        yAxis.push({
            type: 'value',
            name: '右轴',
            position: 'right',
            axisLine: { show: false },
            axisLabel: {
                color: '#999',
                fontSize: 12
            },
            axisTick: { show: false },
            splitLine: { show: false }
        })
    }

    return {
        backgroundColor: '#fff',
        color: colorPalette,
        textStyle: {
            fontFamily: 'Arial, sans-serif',
            fontSize: 12
        },
        tooltip: {
            trigger: 'axis',
            backgroundColor: 'rgba(255, 255, 255, 0.95)',
            borderColor: '#eee',
            borderWidth: 1,
            padding: 12,
            textStyle: {
                color: '#666',
                fontSize: 12
            },
            extraCssText: 'box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1); border-radius: 4px;',
            formatter: function (params: any) {
                let result = `<div style="font-weight: bold; margin-bottom: 6px; color: #333;">${params[0].name}</div>`
                params.forEach((item: any) => {
                    const unit = getMetricUnit(item.seriesName)
                    result += `
            <div style="display: flex; align-items: center; margin: 2px 0;">
              <span style="display: inline-block; width: 8px; height: 8px; border-radius: 50%; background-color: ${item.color}; margin-right: 6px;"></span>
              <span style="color: #666;">${item.seriesName}:</span>
              <span style="font-weight: 500; margin-left: 4px; color: #333;">${Number(item.value).toLocaleString()}${unit}</span>
            </div>
          `
                })
                return result
            }
        },
        legend: {
            data: [...leftAxisMetrics.value, ...rightAxisMetrics.value].map(getMetricLabel),
            top: 15,               // 固定15px顶部位置
            left: 'center',
            textStyle: {
                color: '#666',
                fontSize: isCompactHeight.value ? 10 : 12
            },
            itemWidth: isCompactHeight.value ? 10 : 12,
            itemHeight: isCompactHeight.value ? 10 : 12,
            itemGap: isCompactHeight.value ? 15 : 20
        },
        grid: {
            left: 50,              // 固定50px左边距给y轴
            right: 50,             // 固定50px右边距给右轴标签
            bottom: 35,           // 进一步减少到底部35px (25px标签 + 8px边距 + 2px缓冲)
            top: isCompactHeight.value ? 70 : 90,  // 固定顶部空间给图例
            containLabel: true
        },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: dates,
            axisLine: {
                lineStyle: {
                    color: '#eee'
                }
            },
            axisTick: { show: false },
            axisLabel: {
                color: '#999',
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

// 生命周期
onMounted(() => {
    if (chartContainer.value) {
        chartInstance.value = echarts.init(chartContainer.value)
        loadChartData()

        // 添加窗口大小监听
        window.addEventListener('resize', debouncedResize)

        // 初始计算高度
        handleResize()

        // 确保图表初始化后布局正确
        setTimeout(() => {
            handleResize()
        }, 200)
    }
})

onUnmounted(() => {
    // 移除事件监听
    window.removeEventListener('resize', debouncedResize)

    // 销毁图表实例
    if (chartInstance.value) {
        chartInstance.value.dispose()
    }
})

// 监听属性变化
watch(() => props.accountId, () => {
    loadChartData()
})

// 监听 tab 切换，重新加载数据
watch(() => props.entityType, () => {
    loadChartData()
})

// 监听 selectedIds、dateRange、timeUnit 变化
watch(() => [
    props.queryParams?.campaignIds, 
    props.queryParams?.adGroupIds, 
    props.queryParams?.adIds,
    props.dateRange,
    props.timeUnit
], () => {
    loadChartData()
}, { deep: true })

// 新增：监听窗口高度变化
watch(() => isCompactHeight.value, () => {
    if (chartInstance.value) {
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

    // 重绘图表
    if (chartInstance.value) {
        chartInstance.value.resize();
    }
}

// 防抖的resize处理（在handleResize定义之后）
const debouncedResize = debounce(handleResize, 300)

// 暴露方法给父组件
defineExpose({
    refresh: loadChartData
})
</script>

<style scoped>
.chart-container {
    margin-top: 20px;
}


.chart-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
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