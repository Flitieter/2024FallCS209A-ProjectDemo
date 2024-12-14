<template>
  <div>
    <!-- 按钮区 -->
    <div>
      <button @click="fetchData('error')">Error</button>
      <button @click="fetchData('exception')">Exception</button>
      <button @click="toggleView">{{ showTable ? 'Show Chart' : 'Show Table' }}</button>
    </div>

    <!-- TopN 输入框 -->
    <div v-if="!showTable && chartData.length" style="margin-bottom: 10px;">
      <label for="topN">Display Top N Words: </label>
      <input
          id="topN"
          type="number"
          v-model.number="topN"
          min="1"
          max="100"
          @input="updateChartData"
          style="width: 60px; margin-left: 10px;"
      />
    </div>

    <!-- 表格区 -->
    <div v-if="showTable && data.length">
      <h3>{{ tableTitle }}</h3>
      <table>
        <thead>
        <tr>
          <th v-for="(key, index) in Object.keys(data[0])" :key="index">{{ key }}</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(row, index) in data" :key="index">
          <td v-for="(value, key) in row" :key="key">{{ value }}</td>
        </tr>
        </tbody>
      </table>
    </div>

    <!-- 图表区 -->
    <div v-if="!showTable && chartData.length">
      <h3>{{ tableTitle }} - Word Frequency Chart</h3>
      <canvas ref="barChart" width="1000" height="800"></canvas>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import { Chart, CategoryScale, LinearScale, BarElement, BarController, Title, Tooltip, Legend } from "chart.js";

// 注册所需模块
Chart.register(CategoryScale, LinearScale, BarElement, BarController, Title, Tooltip, Legend);

export default {
  name: "Errors",
  data() {
    return {
      data: [], // 用于存储从后端获取的数据
      tableTitle: "", // 表格标题，根据按钮类型动态设置
      showTable: true, // 控制显示表格或图表
      chartData: [], // 用于存储柱状图的数据
      topN: 10, // 默认显示前 10 个单词
    };
  },
  methods: {
    async fetchData(type) {
      try {
        // 设置表格标题
        this.tableTitle = type === "error" ? "Error Data" : "Exception Data";

        // 发送请求并获取数据
        const response = await axios.get(`/api/forum/threads/search/${type}`);
        this.data = response.data;

        // 使用表格数据生成图表
        this.generateChartData();
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    },

    // 从表格数据生成图表数据
    generateChartData() {
      // 按 count 降序排序并截取 topN 数据
      const sortedData = [...this.data]
          .sort((a, b) => b.count - a.count)
          .slice(0, this.topN); // 使用 topN 控制显示数量

      this.chartData = sortedData.map(row => ({
        word: row.word, // 替换为数据中对应的字段
        count: row.count, // 替换为数据中对应的字段
      }));

      // 渲染图表
      this.$nextTick(() => {
        this.renderChart();
      });
    },

    // 更新图表数据并重新渲染
    updateChartData() {
      this.generateChartData(); // 根据新的 topN 值更新图表数据
    },

    // 渲染柱状图
    renderChart() {
      const canvas = this.$refs.barChart; // 获取 canvas 元素
      if (!canvas) return; // 如果 canvas 元素不存在，则退出

      const ctx = canvas.getContext("2d");
      if (!ctx) return; // 如果无法获取上下文，则退出

      // 如果已有图表实例，则销毁
      if (this.chartInstance) {
        this.chartInstance.destroy();
      }

      // 创建新的图表
      this.chartInstance = new Chart(ctx, {
        type: "bar",
        data: {
          labels: this.chartData.map(item => item.word), // X轴显示单词
          datasets: [
            {
              label: "Word Frequency",
              data: this.chartData.map(item => item.count), // Y轴显示计数
              backgroundColor: "rgba(75, 192, 192, 0.2)",
              borderColor: "rgba(75, 192, 192, 1)",
              borderWidth: 1,
            },
          ],
        },
        options: {
          responsive: true,
          scales: {
            y: {
              beginAtZero: true, // 确保从 0 开始
            },
          },
        },
      });
    },

    // 切换显示表格或图表
    toggleView() {
      this.showTable = !this.showTable;
    },
  },
};
</script>

<style scoped>
/* 按钮样式 */
button {
  margin: 10px;
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 5px;
  font-size: 16px;
}

button:hover {
  background-color: #0056b3;
}

/* 输入框样式 */
input[type="number"] {
  padding: 5px;
  font-size: 14px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

/* 表格样式 */
table {
  width: 80%; /* 设置表格宽度为80% */
  max-width: 1200px; /* 最大宽度 */
  margin: 20px auto; /* 居中显示 */
  border-collapse: collapse;
  font-size: 14px; /* 缩小字体 */
}

th,
td {
  border: 1px solid #ddd;
  padding: 8px 12px; /* 减少单元格的内边距 */
  text-align: left;
}

th {
  background-color: #f2f2f2;
  font-weight: bold;
}

h3 {
  margin-top: 20px;
  text-align: center;
  font-size: 18px; /* 缩小标题字体 */
}

/* 图表样式 */
canvas {
  max-width: 100%;
  margin: 20px auto;
}
</style>
