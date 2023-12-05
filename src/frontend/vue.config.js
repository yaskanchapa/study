// const { defineConfig } = require('@vue/cli-service')
const path = require('path')

module.exports = {
  outputDir: path.resolve(__dirname, '../../src/main/resources/static'),
  devServer: {
    proxy: {
      '^/api': {
        target: 'http://localhost:9090/',
        changeOrigin: true
      }
    }
  }

}
