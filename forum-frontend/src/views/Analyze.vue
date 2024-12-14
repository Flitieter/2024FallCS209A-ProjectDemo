<template>
  <div class="analyze">
    <div class="filters">
      <button @click="setCategory('duration')">Duration</button>
      <button @click="setCategory('reputation')">Reputation</button>
      <button @click="setCategory('comments')">Comments</button>
    </div>

    <div class="filters">
      <button @click="setType('total')">Total</button>
      <button @click="setType('good')">Good</button>
    </div>

    <div id="chart-container" style="height: 400px;"></div>
  </div>
</template>

<script>
import axios from "axios";
import * as echarts from "echarts";

export default {
  data() {
    return {
      category: 'duration',  // 默认选择 duration
      type: 'total',         // 默认选择 total
      chart: null,
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
            this.renderChart(data);
          })
          .catch(error => {
            console.error("Failed to fetch data:", error);
          });
    },
    renderChart(data) {
      if (this.chart === null) {
        this.chart = echarts.init(document.getElementById('chart-container'));
      }

      // 检查数据结构
      if (!Array.isArray(data) || data.length === 0) {
        console.error('Data format is incorrect or empty');
        return;
      }

      // 提取 xData 和 yData
      let xData = [];
      if (this.category === 'duration') {
        xData = data.map(item => item.time); // 时间
      } else if (this.category === 'reputation') {
        xData = data.map(item => item.reputation); // 声誉
      } else if (this.category === 'comments') {
        xData = data.map(item => item.commentNumber); // 评论数
      }
      let yData = data.map(item => item.count);

      // 同时过滤无效 xData 和 yData
      const validData = xData
          .map((value, index) => ({ x: value, y: yData[index] })) // 合并 xData 和 yData
          .filter(item => item.x > 0); // 过滤掉 x <= 0 的数据
      xData = validData.map(item => item.x); // 更新 xData
      yData = validData.map(item => item.y); // 更新 yData
      console.log(yData);
      console.log(xData);

      // 检查过滤后的数据是否为空
      if (xData.length === 0 || yData.length === 0) {
        console.error('Filtered xData or yData is empty');
        return;
      }

      const option = {
        tooltip: {
          trigger: 'axis',
        },
        xAxis: {
          type: 'log', // 对数坐标轴
          data: xData,
          name: this.category === 'comments' ? '评论数' : (this.category === 'duration' ? '时间（小时）' : '声誉'),
        },
        yAxis: {
          type: 'value',
          name: '答案数量',
        },
        series: [{
          type: 'line',
          data: yData,
          smooth: true,
        }],
      };

      this.chart.setOption(option);
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

#chart-container {
  width: 100%;
  height: 400px;
}
</style>
