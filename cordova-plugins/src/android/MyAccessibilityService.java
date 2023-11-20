package cordova.plugin.customer;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

public class MyAccessibilityService extends AccessibilityService {

    @Override
    public void onServiceConnected() {
        // 无障碍服务连接成功
        // Set the type of events that this service wants to listen to. Others
        // won't be passed to this service.
        // info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED |
        // AccessibilityEvent.TYPE_VIEW_FOCUSED;

        // // If you only want this service to work with specific applications, set
        // their
        // // package names here. Otherwise, when the service is activated, it will
        // listen
        // // to events from all applications.
        // info.packageNames = new String[] { "com.example.android.myFirstApp",
        // "com.example.android.mySecondApp" };

        // // Set the type of feedback your service will provide.
        // info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;

        // // Default services are invoked only if no package-specific ones are present
        // // for the type of AccessibilityEvent generated. This service *is*
        // // application-specific, so the flag isn't necessary. If this was a
        // // general-purpose service, it would be worth considering setting the
        // // DEFAULT flag.

        // // info.flags = AccessibilityServiceInfo.DEFAULT;

        // info.notificationTimeout = 100;

        // this.setServiceInfo(info);

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            // 获取当前前台应用程序的包名
            String packageName = event.getPackageName().toString();

            // 指定应用程序的包名
            String targetPackageName = "com.example.targetapp";

            // 判断是否为指定应用程序
            if (packageName.equals(targetPackageName)) {
                // 在指定应用程序启动时执行你的逻辑
                // 例如发送通知、记录日志等
                // 或者直接在这里调用你的方法
            }
        }
    }

    @Override
    public void onInterrupt() {
    }
}