package be.ehb.rebruxdef.util;

import android.app.Activity;
import android.os.Handler;

import java.lang.ref.WeakReference;

public class MyHandler extends Handler {
    private WeakReference<Activity> activityRef;

    public MyHandler(Activity activity) {
        this.activityRef = new WeakReference<>(activity);
    }

    public WeakReference<Activity> getActivityRef() {
        return activityRef;
    }
}
