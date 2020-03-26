package es.unavarra.tlm.dscr_19_11.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.Chat;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.CreateChatRequestObject;
import es.unavarra.tlm.dscr_19_11.R;
import es.unavarra.tlm.dscr_19_11.ResponseHandlers.CreateNewChatResponseHandler;
import es.unavarra.tlm.dscr_19_11.ResponseHandlers.GetChatListResponseHandler;

public class ChatsListActivity extends BaseActivity {


    private GetChatListResponseHandler getChatListResponseHandler;
    private boolean open = false;
    private boolean iniciar;

    public boolean isIniciar() {
        return iniciar;
    }

    public void setIniciar(boolean iniciar) {
        this.iniciar = iniciar;
    }

    @Override
    public void onResume(){
        super.onResume();
        getChats();

        String token = getSharedPreferences("credenciales",Context.MODE_PRIVATE).getString("token",null);

        if(token == null || token.isEmpty()){
            this.finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.iniciar= false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats_list);
        getChats();
        Toolbar toolbar = findViewById(R.id.toolbarChats);
        toolbar.setLogo(R.mipmap.chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getFirebaseToken();

        FloatingActionButton createChatButton = findViewById(R.id.switchemailtext);
        createChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText emailText = findViewById(R.id.emailnewchat);
                if (open) {
                    emailText.setVisibility(View.INVISIBLE);
                    ((FloatingActionButton) findViewById(R.id.switchemailtext)).setImageResource(R.drawable.ic_add_black);
                    findViewById(R.id.createchatbutton).animate().translationX(0);
                    open = false;
                } else {
                    emailText.setVisibility(View.VISIBLE);
                    ((FloatingActionButton) findViewById(R.id.switchemailtext)).setImageResource(R.drawable.ic_clear_black);
                    findViewById(R.id.createchatbutton).animate().translationX(+800);
                    findViewById(R.id.createchatbutton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            createChat();
                        }
                    });
                    open = true;
                }
            }
        });

        createChatButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "Create new chat", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 10);
                toast.show();
                return true;
            }
        });

    }

    public void getFirebaseToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    return;
                }

                String firebaseToken = task.getResult().getToken();
                Log.d("firebase", "Token: "+ firebaseToken);
            }
        });
    }

    public void getChats() {
        Gson gson = new Gson();

        AsyncHttpClient client = new AsyncHttpClient();
        String token = getSharedPreferences("credenciales", Context.MODE_PRIVATE).getString("token", null);


        getChatListResponseHandler = new GetChatListResponseHandler(gson, this, token);

        try {
            client.addHeader("X-AUTH-TOKEN", token);
            client.get(
                    "https://api.messenger.tatai.es/v3/chat",
                    getChatListResponseHandler
            );
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void createChat() {
        String emailChat = ((EditText) findViewById(R.id.emailnewchat)).getText().toString();
        if(emailChat == null || emailChat.equals("")){
            Toast toast = Toast.makeText(getApplicationContext(), "Tienes que escribir un email", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 10);
            toast.show();
        }else {
            CreateChatRequestObject createChatRequestObject = new CreateChatRequestObject(emailChat);
            hacerPeticion(createChatRequestObject);
        }
    }

    public void openMyProfileActivity() {
        Intent intent = new Intent(this, MyProfileActivity.class);
        startActivity(intent);
    }

    public void openChatActivity (Chat chat) {
        Intent intent = new Intent(this, ChatMessagesActivity.class);
        Gson gson = new Gson();

        String chatAsAString = gson.toJson(chat);
        intent.putExtra("chat", chatAsAString);

        startActivity(intent);
    }

    public void hacerPeticion(CreateChatRequestObject createChatRequestObject){
        //aqui hacer la peticion

        Gson gson = new Gson();

        String jsonenviar = gson.toJson(createChatRequestObject);
        //Mandamos la petición
        AsyncHttpClient client = new AsyncHttpClient();
        String token = getSharedPreferences("credenciales", Context.MODE_PRIVATE).getString("token",null);

        try {
            client.addHeader("X-AUTH-TOKEN",token);
            client.post(
                    this,
                    "https://api.messenger.tatai.es/v3/chat/invite",
                    new StringEntity(jsonenviar),
                    "application/json",
                    new CreateNewChatResponseHandler(gson, this)

            );
            onResume();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chats_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals("My profile")) {
            openMyProfileActivity();
        }

        if (item.getTitle().equals("Search a chat")) {
            SearchView searchChat = (SearchView) item.getActionView();
            searchChat.setQueryHint("Search a chat");
            searchChat.setIconifiedByDefault(false);
            searchChat.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    getChatListResponseHandler.getChatListAdapter().filter(newText);
                    return true;
                }
            });
        }

        return true;
    }
}
