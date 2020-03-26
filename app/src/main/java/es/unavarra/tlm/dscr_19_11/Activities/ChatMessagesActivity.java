package es.unavarra.tlm.dscr_19_11.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;

import org.greenrobot.greendao.database.Database;

import es.unavarra.tlm.dscr_19_11.Entities.Access;
import es.unavarra.tlm.dscr_19_11.Entities.AccessDao;
import es.unavarra.tlm.dscr_19_11.Entities.DaoMaster;
import es.unavarra.tlm.dscr_19_11.Entities.DaoSession;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.Chat;
import es.unavarra.tlm.dscr_19_11.R;
import es.unavarra.tlm.dscr_19_11.ResponseHandlers.GetChatMessageListResponseHandler;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.entity.StringEntity;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.NewMessageRequestObject;
import es.unavarra.tlm.dscr_19_11.ResponseHandlers.LogOutChatResponseHandler;

import es.unavarra.tlm.dscr_19_11.ResponseHandlers.NewMessageResponseHandler;

public class ChatMessagesActivity extends BaseActivity {

    private Chat chat;
    private String chatAsAString;
    private GetChatMessageListResponseHandler getChatMessageListResponseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "access_db");
        Database db = helper.getWritableDb();
        DaoSession daoSession = new DaoMaster(db).newSession();
        AccessDao accessDao = daoSession.getAccessDao();
//        actualizarNumeroMensajes(accessDao);
        getMessages();

        Gson gson = new Gson();
        chatAsAString = getIntent().getStringExtra("chat");
        chat = gson.fromJson(chatAsAString, Chat.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_messages);

        Toolbar toolbar = findViewById(R.id.toolbarMessages);
        toolbar.setLogo(R.mipmap.chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbarTitle = findViewById(R.id.userprofile);

        String username = getSharedPreferences("credenciales", MODE_PRIVATE).getString("username", null);
        if (chat.getUsers().get(1).getName().equals(username)) {
            toolbarTitle.setText(chat.getUsers().get(0).getName());
        } else {
            toolbarTitle.setText(chat.getUsers().get(1).getName());
        }

        toolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentclick = new Intent(getBaseContext(), UserProfileActivity.class);
                intentclick.putExtra("chat", chatAsAString);
                startActivity(intentclick);
            }
        });

        FloatingActionButton botonenviar = findViewById(R.id.botonenviar);
        botonenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textomensaje = ((EditText) findViewById(R.id.textomensaje)).getText().toString();

                if (!textomensaje.isEmpty()) {
                    sendMessage(textomensaje);
                }
            }
        });

        botonenviar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "Send a message", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 10);
                toast.show();
                return true;
            }
        });

    }
    public void peticionBorrarchat(){
        String token = getSharedPreferences("credenciales", Context.MODE_PRIVATE).getString("token",null);
        Gson gson = new Gson();
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("X-AUTH-TOKEN",token);
        client.get(this,"https://api.messenger.tatai.es/v3/chat/"+chat.getId()+"/exit",new LogOutChatResponseHandler(gson,this,chat.getId()));
    }
    public void actualizarNumeroMensajes(AccessDao accessDao){

        List<Access> list =  accessDao.queryBuilder().where(AccessDao.Properties.Chat_id.eq(chat.getId())).list();

        if(list.size()>0){




            list.get(0).setLast_num(list.get(0).getNew_num());

            accessDao.update(list.get(0));

        }

    }

    public void sendMessage(String textomensaje){
        Date today = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());

        NewMessageRequestObject messageRequestObject = new NewMessageRequestObject(textomensaje, formatter.format(today));
////////////////////// COSAS DB
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "access_db");
        Database db = helper.getWritableDb();
        DaoSession daoSession = new DaoMaster(db).newSession();
        AccessDao accessDao = daoSession.getAccessDao();
        actualizarNumeroMensajes(accessDao);
        /////////
        Gson gson = new Gson();
        String jsonenviar = gson.toJson(messageRequestObject);

        AsyncHttpClient client = new AsyncHttpClient();

        String token = getSharedPreferences("credenciales", Context.MODE_PRIVATE).getString("token",null);


        client.addHeader("X-AUTH-TOKEN",token);


        try {
            client.post(
                    this,
                    "https://api.messenger.tatai.es/v3/chat/"+chat.getId()+"/message",
                    new StringEntity(jsonenviar),
                    "application/json",
                    new NewMessageResponseHandler(this, gson, getChatMessageListResponseHandler.getMessageListAdapter())
            );
            ((EditText) findViewById(R.id.textomensaje)).setText("");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    public void salir(){
        this.finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
        String token = getSharedPreferences("credenciales", Context.MODE_PRIVATE).getString("token", null);
        if(token.isEmpty() || token.equals("")){
            salir();

        }
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "access_db");
        Database db = helper.getWritableDb();
        DaoSession daoSession = new DaoMaster(db).newSession();
        AccessDao accessDao = daoSession.getAccessDao();
        actualizarNumeroMensajes(accessDao);
        getMessages();
    }

    public void getMessages () {
        Gson gson = new Gson();

        AsyncHttpClient client = new AsyncHttpClient();
        String token = getSharedPreferences("credenciales", Context.MODE_PRIVATE).getString("token", null);

        getChatMessageListResponseHandler = new GetChatMessageListResponseHandler(gson, this);

        try {
            client.addHeader("X-AUTH-TOKEN", token);
            client.get(
                    "https://api.messenger.tatai.es/v3/chat/" + chat.getId() + "/message",
                    getChatMessageListResponseHandler
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Chat getChat() {
        return chat;
    }

    public void openChatInfo (String chatAsAString) {
        Intent intent = new Intent(this, ChatInfoActivity.class);
        intent.putExtra("chat", chatAsAString);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_messages, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals("Chat info")) {
            openChatInfo(chatAsAString);
        }

        if (item.getTitle().equals("Delete chat")) {
            peticionBorrarchat();
        }

        if (item.getTitle().equals("Search a message")) {
            SearchView searchMessage = (SearchView) item.getActionView();
            searchMessage.setQueryHint("Search a message");
            searchMessage.setIconifiedByDefault(false);
            searchMessage.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    getChatMessageListResponseHandler.getMessageListAdapter().filter(newText);
                    return true;
                }
            });
        }

        return true;
    }
}
