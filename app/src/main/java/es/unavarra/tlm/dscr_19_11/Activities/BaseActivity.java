package es.unavarra.tlm.dscr_19_11.Activities;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import es.unavarra.tlm.dscr_19_11.Chat;

public class BaseActivity extends AppCompatActivity {

    protected Chat chat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chat = (Chat) this.getApplicationContext();
    }

    @Override
    protected void onResume() {
        super.onResume();
        chat.setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        clearReferences();
        super.onPause();
    }

    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences() {
        Activity currentActivity = chat.getCurrentActivity();
        if (this.equals(currentActivity)) {
            chat.setCurrentActivity(null);
        }
    }
}
