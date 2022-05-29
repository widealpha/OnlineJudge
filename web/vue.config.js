const webpack = require('webpack');
module.exports = {
  configureWebpack: {
    plugins: [
      new webpack.ProvidePlugin({
        $: "jquery",
        jQuery: "jquery",
        "window.jQuery": "jquery",
        Popper: ["popper.js", "default"]
      })
    ]
  }
}

// module.exports = {
//     devServer: {
//         port: 8080,
//         proxy: {
//             "/online-judge": {
//                 target: 'http://121.196.101.7:8080',
//                 changeOrigin: true,
//                 pathRewrite: {
//                     '^/online-judge': ''
//                 },
//                 secure: true,
//             },
//         }
//     },
//     // 如果你不需要生产环境的 source map，可以将其设置为 false 以加速生产环境构建。
//     productionSourceMap: false,
// }
// module.exports = {
//     devServer: {
//       host: "0.0.0.0",
//       port: 8080, //Vue运行的端口号
//       https: false,
//       open: true,
//       // 配置多个代理
//       proxy: {
//         "/online-judge": {
//           target: "http://121.196.101.7:8080", //服务器域名( + 端口号)
//           ws: true,//开启websocket,根据情况而定
//           changeOrigin: true, //是否需要改变浏览器访问域名
//           pathRewrite: {
//             "^/online-judge": "",
//           },
//         },
        
//       },
//     },
//   };
  