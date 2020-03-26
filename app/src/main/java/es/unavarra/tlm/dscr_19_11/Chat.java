package es.unavarra.tlm.dscr_19_11;

import android.app.Activity;
import android.app.Application;

public class Chat extends Application {

    private Activity currentActivity = null;

    public void onCreate() {
        super.onCreate();
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }
}
