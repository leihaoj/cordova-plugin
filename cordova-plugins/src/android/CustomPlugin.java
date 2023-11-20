package cordova.plugin.customer;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import java.util.ArrayList;
import android.provider.Settings;
import android.content.Context;
import android.os.Build;
import android.content.Intent;
import android.net.Uri;
import android.os.PowerManager;

import java.util.HashMap;
import java.util.Map;

import cordova.plugin.customer.AudioPlugin;
import cordova.plugin.customer.DevicePlugin;

/**
 * This class echoes a string called from JavaScript.
 */
public class CustomPlugin extends CordovaPlugin {
    // 音频功能
    public AudioPlugin audioPlugin = null;
    // 设备模块
    public DevicePlugin devicePlugin = null;

    @Override
    protected void pluginInitialize() {
        super.pluginInitialize();
        this.audioPlugin = new AudioPlugin();
        this.audioPlugin.init(this.cordova, this.webView);
        // 初始化device
        this.devicePlugin = new DevicePlugin();
        // 申请白名单
        final Context context = this.cordova.getActivity().getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        } else if (action.equals("audio.getAudioFocus")) {
            // 获取音频焦点
            this.audioPlugin.getAudioFocus(callbackContext);
            return true;
        } else if (action.equals("audio.addEventListener")) {
            // 监听音频焦点
            this.audioPlugin.startListener();
            return true;
        } else if (action.equals("audio.chooser")) {
            // 音频文件选择
            this.audioChooser(callbackContext);
            return true;
        } else if (action.equals("device.getSN")) {
            // 获取设备序列号
            JSONObject serial = this.devicePlugin.getDeviceSN();
            Boolean status = serial.optBoolean("status");
            if (status) {
                callbackContext.success(serial);
            } else {
                callbackContext.error(serial);
            }
            return true;
        }
        return false;
    }

    // 获取音频文件列表
    private void audioChooser(CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                ArrayList<HashMap<String, String>> audioFiles = new ArrayList<>();

                Cursor cursor = cordova.getActivity().getContentResolver().query(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                        String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));

                        HashMap<String, String> audioFile = new HashMap<>();
                        audioFile.put("id", String.valueOf(id));
                        audioFile.put("filePath", filePath);
                        audioFile.put("title", title);

                        audioFiles.add(audioFile);
                    }
                    cursor.close();
                }

                // 将音频文件信息发送给前端
                JSONArray jsonAudioFiles = new JSONArray(audioFiles);
                callbackContext.success(jsonAudioFiles);
            }
        });
        return;
    }

    // 获取视频列表
    private void videoChooser(CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                // 视频列表
                ArrayList<HashMap<String, String>> videoFiles = new ArrayList<>();
                // 要获取的视频内容
                String[] projection = { MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA,
                        MediaStore.Video.Media.DISPLAY_NAME };
                Cursor cursor = cordova.getActivity().getContentResolver().query(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        null,
                        null,
                        null);

                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        long videoId = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                        String videoPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        String videoName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                        // 获取视频封面图像
                        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(videoPath,
                                MediaStore.Images.Thumbnails.MINI_KIND);

                        HashMap<String, String> videoFile = new HashMap<>();
                        videoFile.put("id", String.valueOf(videoId));
                        videoFile.put("filePath", videoPath);
                        videoFile.put("title", videoName);

                        videoFiles.add(videoFile);
                    }
                    cursor.close();
                }

                // 将音频文件信息发送给前端
                JSONArray jsonVideoFiles = new JSONArray(videoFiles);
                callbackContext.success(jsonVideoFiles);
            }
        });
        return;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {

        if (message != null && message.length() > 0) {
            // 成功回调
            JSONObject params = new JSONObject();
            try {
                params.put("name", "John");
                params.put("age", 30);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            this.success(callbackContext, params);
            // callbackContext.success(message.length());
        } else {
            callbackContext.error("message不是字符串");
        }
    }

    private void success(CallbackContext callbackContext, JSONObject params) {
        PluginResult result = new PluginResult(PluginResult.Status.OK, params);
        callbackContext.sendPluginResult(result);
        // callbackContext.success(params);
    }
}
