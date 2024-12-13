<template>
  <div>
    <!-- 按钮区 -->
    <div>
      <button @click="fetchData('error')">Error</button>
      <button @click="fetchData('exception')">Exception</button>
    </div>

    <!-- 表格区，仅当 data 存在时显示 -->
    <div v-if="data.length">
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
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "Errors",
  data() {
    return {
      data: [], // 用于存储后端返回的数据
      tableTitle: "", // 表格标题，根据按钮类型动态设置
    };
  },
  methods: {
    async fetchData(type) {
      try {
        // 根据按钮类型动态设置标题
        this.tableTitle = type === "error" ? "Error Data" : "Exception Data";

        // 发送后端请求
        const response = await axios.get(`/api/forum/threads/search/${type}`);
        this.data = response.data;
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    },
  },
};
</script>

<style>
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

/* 表格样式 */
table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

th,
td {
  border: 1px solid #ddd;
  padding: 10px;
  text-align: left;
}

th {
  background-color: #f2f2f2;
  font-weight: bold;
}

h3 {
  margin-top: 20px;
  text-align: center;
}
</style>
