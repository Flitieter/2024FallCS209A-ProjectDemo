const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true
})

module.exports = {
  devServer: {
    host: '0.0.0.0',  // 允许外部设备访问
    port: 80,       // 你可以选择不同的端口
  }
}
