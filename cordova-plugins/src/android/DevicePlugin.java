package cordova.plugin.customer;

import java.lang.reflect.Method;
import android.os.Build;

import org.json.JSONObject;
import org.json.JSONException;

public class DevicePlugin {

    /**
     * 获取sn（序列号）
     * 
     * @return
     */
    public JSONObject getDeviceSN() {
        JSONObject params = new JSONObject();
        // 先添加默认值
        try {
            params.put("sn", "");
            params.put("status", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String serial = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { // 9.0 +
                serial = Build.getSerial();
            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) { // 8.0 +
                serial = Build.SERIAL;
            } else { // 8.0 -
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class);
                serial = (String) get.invoke(c, "ro.serialno");
            }
            try {
                params.put("sn", serial);
                params.put("status", (serial != null && !serial.isEmpty()) ? true : false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage();
            try {
                params.put("message", message);
            } catch (JSONException err) {
                err.printStackTrace();
            }
        }
        return params;
    }
}
