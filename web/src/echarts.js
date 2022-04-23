// 引入 echarts 核心模块，核心模块提供了 echarts 使用必须要的接口。
import * as echarts from "echarts/core";
import { PieChart } from "echarts/charts";
import {
    TooltipComponent,
    LegendComponent
} from "echarts/components";
import { CanvasRenderer } from "echarts/renderers";
// 注册必须的组件
echarts.use([
    TooltipComponent,
    LegendComponent,
    PieChart,
    CanvasRenderer,
]);

const $echarts = echarts;

export default $echarts;