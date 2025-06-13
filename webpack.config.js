const PluginWebpackConfig = require('graylog-web-plugin').PluginWebpackConfig;
const loadBuildConfig = require('graylog-web-plugin').loadBuildConfig;
const path = require('path');

console.log("in webpack.config.js - before")

module.exports = new PluginWebpackConfig(__dirname,
    'org.graylog.plugins.jira.JiraNotificationPlugin',
    loadBuildConfig(path.resolve(__dirname, './build.config')), {
  // Here goes your additional webpack configuration.
});

console.log("in webpack.config.js - after")
