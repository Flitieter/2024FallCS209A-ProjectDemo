<template>
  <div class="analyze">
    <!-- 过滤按钮区域 -->
    <div class="filters">
      <button @click="setCategory('duration')">Duration</button>
      <button @click="setCategory('reputation')">Reputation</button>
      <button @click="setCategory('comments')">Comments</button>
    </div>

    <div class="filters">
      <button @click="setType('total')">Total</button>
      <button @click="setType('good')">Good</button>
    </div>

    <!-- 切换图表/表格视图 -->
    <div class="view-toggle">
      <button @click="toggleView">Toggle to {{ viewMode === 'table' ? 'Chart' : 'Table' }}</button>
    </div>

    <!-- 表格展示 -->
    <div v-if="viewMode === 'table' && data.length">
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

    <!-- 折线图展示 -->
    <div v-if="viewMode === 'chart' && data.length">
      <h3>{{ tableTitle }}</h3>
      <canvas ref="lineChart"></canvas>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import { Chart, LineController, LineElement, PointElement, CategoryScale, LinearScale, LogarithmicScale, Title, Tooltip, Legend } from 'chart.js';

// 注册相关组件
Chart.register(
    LineController,
    LineElement,
    PointElement,   // 注册 PointElement
    CategoryScale,
    LinearScale,    // 注册 LinearScale
    LogarithmicScale, // 注册 LogarithmicScale
    Title,
    Tooltip,
    Legend
);

export default {
  data() {
    return {
      category: 'duration',  // 默认选择 duration
      type: 'total',         // 默认选择 total
      data: [],              // 存储从后端获取的数据
      topN: 10000000,
      chartData: [],
      tableTitle: "Analysis Data", // 表格标题
      viewMode: 'table',     // 默认视图为表格
      chartInstance: null,   // 用于保存图表实例
    };
  },
  methods: {
    setCategory(category) {
      this.category = category;
      this.fetchData();
    },
    setType(type) {
      this.type = type;
      this.fetchData();
    },
    fetchData() {
      const apiUrl = `/api/forum/analyze/${this.category}/${this.type}`;
      axios.get(`http://localhost:9091${apiUrl}`)
          .then(response => {
            const data = response.data;
            console.log('Fetched data:', data);  // 打印数据以确认结构
            this.data = data;
            if (this.viewMode === 'chart') {
              this.generateChartData(); // 如果是图表视图，渲染图表
            }
          })
          .catch(error => {
            console.error("Failed to fetch data:", error);
          });
    },
    toggleView() {
      this.viewMode = this.viewMode === 'table' ? 'chart' : 'table';
      if (this.viewMode === 'chart') {
        this.renderChart(); // 切换到图表时渲染折线图
      }
    },
    generateChartData() {
      if (this.category === 'duration') {
        const sortedData = [...this.data]
            .slice(0, this.topN); // 使用 topN 控制显示数量

        this.chartData = sortedData.map(row => ({
          tag: row.tag, // 替换为数据中对应的字段
          count: row.count, // 替换为数据中对应的字段
        }));
      }
    },
    renderChart() {
      // 使用 $nextTick 确保 DOM 更新完成
      this.$nextTick(() => {
        const canvas = this.$refs.lineChart;
        const ctx = canvas?.getContext('2d');
        if (ctx) {
          // 获取表格数据并直接使用
          const labels = this.data.map(row => row[Object.keys(row)[1]]); // 获取第二列作为横坐标
          const counts = this.data.map(row => row[Object.keys(row)[0]]); // 获取第一列作为纵坐标

          // 销毁已有图表实例
          if (this.chartInstance) {
            this.chartInstance.destroy();
          }

          // 创建新的图表实例
          this.chartInstance = new Chart(ctx, {
            type: 'line', // 设置为折线图
            data: {
              labels: labels,
              datasets: [{
                label: 'Count',
                data: counts,
                borderColor: 'rgba(75, 192, 192, 1)',
                fill: false,
                tension: 0.1
              }]
            },
            options: {
              responsive: true,
              scales: {
                x: {
                  type: this.category === 'comments' ? 'linear' : 'logarithmic', // 设置横轴为对数轴
                  title: { display: true, text: this.category }
                },
                y: {
                  type: 'linear', // 设置纵轴为线性轴
                  title: { display: true, text: 'count' },
                  beginAtZero: true
                }
              }
            }
          });
        } else {
          console.error('Canvas element not found!');
        }
      });
    }

  },
  mounted() {
    this.fetchData();
  }
};

</script>

<style scoped>
.analyze {
  width: 100%;
  padding: 20px;
}

.filters {
  margin-bottom: 20px;
}

button {
  margin-right: 10px;
  padding: 8px 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}

table {
  width: 80%;
  max-width: 1200px;
  margin: 20px auto;
  border-collapse: collapse;
  font-size: 14px;
}

th,
td {
  border: 1px solid #ddd;
  padding: 8px 12px;
  text-align: left;
}

th {
  background-color: #f2f2f2;
  font-weight: bold;
}

h3 {
  margin-top: 20px;
  text-align: center;
  font-size: 18px;
}

.view-toggle {
  margin-bottom: 20px;
  text-align: center;
}

canvas {
  width: 80%;      /* 使画布宽度填满容器 */
  height: 400px;    /* 设置固定高度，调整为你想要的大小 */
  margin: 20px auto;
}

</style>
