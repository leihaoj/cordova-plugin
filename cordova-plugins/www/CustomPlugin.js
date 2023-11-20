var exec = require('cordova/exec');

const PLUGIN_NAME = 'CustomPlugin';

module.exports = {
  // 测试方法
  coolMethod: (arg0, success, error) => {
    exec(success, error, PLUGIN_NAME, 'coolMethod', [arg0]);
  },

  // 音频模块
  audio: {
    // 获取音频焦点
    getAudioFocus: (arg0, success, error) => {
      exec(success, error, PLUGIN_NAME, 'audio.getAudioFocus', [arg0]);
    },
    // 监听焦点事件
    addEventListener: (arg0, success, error) => {
      exec(success, error, PLUGIN_NAME, 'audio.addEventListener', [arg0]);
    },
    chooser: (arg0, success, error) => {
      exec(success, error, PLUGIN_NAME, 'audio.chooser', [arg0]);
    },
  },

  // 设备模块
  device: {
    getSN: (arg0, success, error) => {
      exec(success, error, PLUGIN_NAME, 'device.getSN', [arg0]);
    },
  },
};
