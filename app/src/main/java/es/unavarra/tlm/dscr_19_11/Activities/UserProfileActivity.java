package es.unavarra.tlm.dscr_19_11.Activities;

import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.Chat;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.ErrorRegisterObject;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.ProfileResponseJson;
import es.unavarra.tlm.dscr_19_11.R;

public class UserProfileActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Toolbar toolbar = findViewById(R.id.toolbarProfileInfo);
        toolbar.setLogo(R.mipmap.chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FloatingActionButton back = findViewById(R.id.backProfileInfo);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salir();
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

        String chatAsAString = getIntent().getStringExtra("chat");
        Gson gson = new Gson();
        Chat chat = gson.fromJson(chatAsAString, Chat.class);
        String username = getSharedPreferences("credenciales", MODE_PRIVATE).getString("username", null);
        String url = "https://api.messenger.tatai.es/v3/profile/";
        if (chat.getUsers().get(1).getName().equals(username)) {
            url += chat.getUsers().get(0).getId();
        } else {
            url += chat.getUsers().get(1).getId();
        }

        AsyncHttpClient client = new AsyncHttpClient();

        String token = getSharedPreferences("credenciales", Context.MODE_PRIVATE).getString("token", null);

        client.addHeader("X-AUTH-TOKEN", token);
        client.get(
                getApplicationContext(),
                url,
                new AsyncHttpResponseHandler(){

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200){
                            Gson gson = new Gson();
                            ProfileResponseJson profileResponseJson = gson.fromJson(new String(responseBody, StandardCharsets.UTF_8),ProfileResponseJson.class);

                            TextView username = findViewById(R.id.user_name);
                            username.setText(profileResponseJson.getUser().getName());

                            TextView userid = findViewById(R.id.user_id);
                            userid.setText(String.format(Locale.getDefault(), "ID: %1$02d", profileResponseJson.getUser().getId()));

                            TextView useremail = findViewById(R.id.user_email);
                            useremail.setText(profileResponseJson.getUser().getEmail());
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if (statusCode == 400){
                            Gson gson= new Gson();
                            ErrorRegisterObject errorRegisterObject = gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), ErrorRegisterObject.class);
                            Toast.makeText(getApplicationContext(),errorRegisterObject.getDescription(),Toast.LENGTH_LONG).show();
                        }else if (statusCode == 409){
                            Gson gson= new Gson();
                            ErrorRegisterObject errorRegisterObject = gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), ErrorRegisterObject.class);
                            Toast.makeText(getApplicationContext(),errorRegisterObject.getDescription(),Toast.LENGTH_LONG).show();

                        }
                    }
                }


        );


    }
    public void salir(){
        this.finish();
    }
}
