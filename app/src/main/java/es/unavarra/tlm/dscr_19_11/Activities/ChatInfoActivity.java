package es.unavarra.tlm.dscr_19_11.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import es.unavarra.tlm.dscr_19_11.ObjectsJson.Chat;
import es.unavarra.tlm.dscr_19_11.R;

public class ChatInfoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_info);

        Toolbar toolbar = findViewById(R.id.toolbarChatInfo);
        toolbar.setLogo(R.mipmap.chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FloatingActionButton back = findViewById(R.id.backChatInfo);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        back.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "Back to chat", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 10);
                toast.show();
                return true;
            }
        });

        final Intent intent = getIntent();
        Gson gson = new Gson();
        final String chatAsAString = getIntent().getStringExtra("chat");
        final Chat chat = gson.fromJson(chatAsAString, Chat.class);

        TextView chatID = findViewById(R.id.chatID);
        chatID.setText(getString(R.string.chatID, chat.getId()));

        TextView numOfParticipants = findViewById(R.id.numOfParticipants);
        numOfParticipants.setText(getString(R.string.participants, chat.getParticipants()));
        numOfParticipants.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'", Locale.getDefault()).parse(chat.getCreated_at());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            TextView created_at = findViewById(R.id.created_at);
            created_at.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d %02d:%02d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
            //created_at.setText(getString(R.string.created_at, String.format(Locale.getDefault(), "%02d/%02d/%04d %02d:%02d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))));
            created_at.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TextView chatOwner = findViewById(R.id.Owner);
        chatOwner.setText(getString(R.string.chatOwner, chat.getUsers().get(0).getName()));
        chatOwner.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TextView chatUser = findViewById(R.id.User);
        chatUser.setText(getString(R.string.chatUser, chat.getUsers().get(1).getName()));
        chatUser.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    }
}
