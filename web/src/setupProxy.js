const { createProxyMiddleware } = require('http-proxy-middleware');
module.exports = function(app) {
  app.use(
    '/biller/api',
    createProxyMiddleware({
     target: 'https://core1.moadbusglobal.com',
      changeOrigin: true,
    })
  );
  app.listen(3000);
};