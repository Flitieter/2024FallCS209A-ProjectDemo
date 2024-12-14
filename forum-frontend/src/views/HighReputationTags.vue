<template>
  <div>
    <!-- 按钮区 -->
    <div>
      <button @click="fetchData">Fetch Tags</button>
      <button @click="toggleView">{{ showTable ? 'Show Chart' : 'Show Table' }}</button>
    </div>

    <!-- Reputation 输入框及确认按钮 -->
    <div style="margin-top: 20px;">
      <label for="reputation">Enter Reputation Score: </label>
      <input
          id="reputation"
          type="number"
          v-model.number="reputationScoreInput"
          min="1"
          style="width: 80px; margin-left: 10px;"
      />
      <button @click="navigateToReputation">Go to Tags with Score</button>
    </div>

    <!-- TopN 输入框 -->
    <div v-if="!showTable && chartData.length" style="margin-bottom: 10px;">
      <label for="topN">Display Top N Tags: </label>
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
      <h3>{{ tableTitle }} - Tag Frequency Chart</h3>
      <canvas ref="barChart" width="1000" height="800"></canvas>
    </div>
  </div>
</template>

<script>
import { useRoute, useRouter } from 'vue-router'; // 导入 useRoute 和 useRouter
import axios from "axios";
import { Chart, CategoryScale, LinearScale, BarElement, BarController, Title, Tooltip, Legend } from "chart.js";

// 注册所需模块
Chart.register(CategoryScale, LinearScale, BarElement, BarController, Title, Tooltip, Legend);

export default {
  name: "HighReputationTags",
  data() {
    return {
      data: [], // 用于存储从后端获取的数据
      tableTitle: "Tags Data", // 表格标题
      showTable: true, // 控制显示表格或图表
      chartData: [], // 用于存储柱状图的数据
      topN: 10, // 默认显示前 10 个标签
      reputationScoreInput: null, // 用户输入的 Reputation Score
    };
  },
  computed: {
    // 通过 useRoute 获取路由参数中的 score
    reputationScore() {
      try {
        const route = useRoute();
        return route.params.score;
      }
      catch (error) {
        console.log(error);
      }

    }
  },
  watch: {
    // 监听路由变化，触发数据重新加载
    '$route.params.score': 'fetchDataFromRoute', // 每次路由参数变化时调用 fetchDataFromRoute
    reputationScoreInput(newValue) {
      // 如果用户修改了输入的 reputationScore，手动触发数据更新
      if (newValue) {
        this.fetchData(newValue);
      }
    }
  },
  methods: {
    async fetchData(score) {
      try {
        const response = await axios.get(`/api/forum/threads/HigherReputationTags/${score}`);
        this.data = response.data;
        this.generateChartData();
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    },

    // 新的 fetchDataFromRoute 方法，在路由变化时调用
    fetchDataFromRoute() {
      const score = this.reputationScore;
      if (score) {
        this.fetchData(score);
      }
    },

    // 从表格数据生成图表数据
    generateChartData() {
      const sortedData = [...this.data].sort((a, b) => b.count - a.count).slice(0, this.topN);
      this.chartData = sortedData.map(row => ({
        tag: row.tag,
        count: row.count,
      }));

      this.$nextTick(() => {
        this.renderChart();
      });
    },

    // 更新图表数据并重新渲染
    updateChartData() {
      this.generateChartData();
    },

    // 渲染柱状图
    renderChart() {
      const canvas = this.$refs.barChart;
      if (!canvas) return;
      const ctx = canvas.getContext("2d");
      if (!ctx) return;

      if (this.chartInstance) {
        this.chartInstance.destroy();
      }

      this.chartInstance = new Chart(ctx, {
        type: "bar",
        data: {
          labels: this.chartData.map(item => item.tag),
          datasets: [{
            label: "Tag Frequency",
            data: this.chartData.map(item => item.count),
            backgroundColor: "rgba(153, 102, 255, 0.2)",
            borderColor: "rgba(153, 102, 255, 1)",
            borderWidth: 1,
          }],
        },
        options: {
          responsive: true,
          scales: {
            y: {
              beginAtZero: true,
            },
          },
        },
      });
    },

    // 切换显示表格或图表
    toggleView() {
      this.showTable = !this.showTable;
    },

    // 点击 "Go to Tags with Score" 按钮后跳转
    navigateToReputation() {
      if (this.reputationScoreInput) {
        // 跳转到新的 URL 后刷新页面
        this.$router.push(`/tags/reputation/${this.reputationScoreInput}`).then(() => {
          // 刷新页面
          window.location.reload();
        });
      }
    }

  },

  created() {
    // 在组件创建时，检查是否有路由中的 score 参数
    const score = this.reputationScore;
    if (score) {
      this.fetchData(score);
    }
  }
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

/* 图表样式 */
canvas {
  max-width: 100%;
  margin: 20px auto;
}
</style>
