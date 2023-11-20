package cordova.plugin.customer;

import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;

import android.content.Context;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.bsc.cordova.CDVBroadcaster;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import static java.lang.String.format;

import org.json.JSONObject;

public class AudioPlugin {

    public CallbackContext audioFocusContext = null;

    public CordovaInterface cordova = null;

    public CordovaWebView webview = null;

    // 初始化
    public void init(CordovaInterface cordova, CordovaWebView webview) {
        this.cordova = cordova;
        this.webview = webview;
    }

    public void pauseAllLostFocus(String type) {
        // 停止播放事件
        // this.successStr(this.audioFocusContext, "失去音频焦点" + type);
    }

    public void resumeAllGainedFocus() {
        // 继续播放
        // this.successStr(this.audioFocusContext, "获取到焦点了");
    }

    /**
     * 监听焦点事件
     */
    private OnAudioFocusChangeListener focusChangeListener = new OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            /*
             * 进入其他app-AUDIOFOCUS_LOSS_TRANSIENT
             */
            switch (focusChange) {
                case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
                    fireEvent("1");
                    break;
                case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT):
                    fireEvent("2");
                    // 获取焦点
                    boolean status = startListener();
                    if (status) {
                        fireEvent("success");
                    }
                    break;
                case (AudioManager.AUDIOFOCUS_LOSS):
                    // 失去焦点了
                    fireEvent("3");
                    break;
                case (AudioManager.AUDIOFOCUS_GAIN):
                    fireEvent("4");
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 监听发送消息
     */
    protected <T> void fireEvent(final String eventName) {
        this.cordova.getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                String method = null;
                method = format("javascript:window.customerEvent.audio( '%s' );", eventName);
                webview.loadUrl(method);
            }
        });
    }

    // 启动监听
    public boolean startListener() {
        AudioManager am = (AudioManager) this.cordova.getActivity().getSystemService(Context.AUDIO_SERVICE);
        int result = am.requestAudioFocus(focusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // 获取焦点成功
            return true;
        }
        return false;
    }

    // 获取音频焦点
    public void getAudioFocus(CallbackContext callbackContext) {
        this.audioFocusContext = callbackContext;
        String TAG2 = "AudioHandler.getAudioFocus(): Error : ";

        AudioManager am = (AudioManager) this.cordova.getActivity().getSystemService(Context.AUDIO_SERVICE);
        int result = am.requestAudioFocus(null,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // 获取焦点失败
            this.error(callbackContext, result + " instead of " + AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
        } else {
            // 获取焦点成功
            this.success(callbackContext);
        }
    }

    private void success(CallbackContext callbackContext) {
        PluginResult result = new PluginResult(PluginResult.Status.OK);
        callbackContext.sendPluginResult(result);
    }

    private void error(CallbackContext callbackContext, String message) {
        callbackContext.error(message);
    }
}
