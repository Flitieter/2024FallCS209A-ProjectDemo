<template>
  <div class="main-container">
    <h1>欢迎来到 Main 页面</h1>
    <div class="buttons-container">
      <button @click="navigateTo('Errors')">错误页面</button>
      <button @click="navigateTo('Tags')">标签页面</button>
      <button @click="navigateTo('HighReputationTags', 100)">高声誉标签</button>
      <button @click="navigateTo('Analyze')">分析页面</button>
    </div>
  </div>
</template>

<script>
export default {
  name: "Main",
  methods: {
    navigateTo(pageName, score = null) {
      if (score) {
        this.$router.push({ name: pageName, params: { score } });
      } else {
        this.$router.push({ name: pageName });
      }
    }
  }
};
</script>

<style scoped>
.main-container {
  text-align: center;
  padding: 20px;
}

.buttons-container {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

button {
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
  border: none;
  border-radius: 5px;
  background-color: #007bff;
  color: white;
  transition: background-color 0.3s ease;
}

button:hover {
  background-color: #0056b3;
}
</style>
