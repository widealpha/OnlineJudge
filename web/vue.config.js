module.exports = {
    devServer: {
        port: 8081,
        proxy: {
            "/api": {
                target: 'http://api.widealpha.top:8080/sdu-pta',
                changeOrigin: true,
                pathRewrite: {
                    '^/api': ''
                },
                secure: true,
            },
        }
    },
    // 如果你不需要生产环境的 source map，可以将其设置为 false 以加速生产环境构建。
    productionSourceMap: false,
}
