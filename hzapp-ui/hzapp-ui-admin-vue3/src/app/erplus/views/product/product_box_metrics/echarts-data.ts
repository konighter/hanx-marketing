import { EChartsOption } from 'echarts'


export const lineOptions: EChartsOption = {
  title: {
    text: '商品指标趋势',
    left: 'center'
  },
  xAxis: {
    data: [],
    boundaryGap: false,
    axisTick: {
      show: false
    }
  },
  grid: {
    left: 20,
    right: 20,
    bottom: 20,
    top: 80,
    containLabel: true
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'cross'
    },
    padding: [5, 10]
  },
  yAxis: {
    axisTick: {
      show: false
    }
  },
  legend: {
    data: [],
    top: 50
  },
  series: []
}


