package es.unavarra.tlm.dscr_19_11.ResponseHandlers;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import android.widget.Toast;


import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.greenrobot.greendao.database.Database;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import es.unavarra.tlm.dscr_19_11.Activities.ChatsListActivity;
import es.unavarra.tlm.dscr_19_11.Adapters.ChatsListAdapter;
import es.unavarra.tlm.dscr_19_11.Entities.Access;
import es.unavarra.tlm.dscr_19_11.Entities.AccessDao;
import es.unavarra.tlm.dscr_19_11.Entities.DaoMaster;
import es.unavarra.tlm.dscr_19_11.Entities.DaoSession;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.Chat;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.ChatListResponseObject;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.ErrorRegisterObject;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.MessageListResponseObject;
import es.unavarra.tlm.dscr_19_11.R;

public class GetChatListResponseHandler extends AsyncHttpResponseHandler {
    private Gson gson;
    private ChatsListActivity activity;
    private String token;
    private AccessDao accessDao;
    private ChatsListAdapter chatsListAdapter;



    public GetChatListResponseHandler (Gson gson, ChatsListActivity activity,String token) {
        this.gson = gson;
        this.activity = activity;
        this.token = token;
    }

    public ChatsListAdapter getChatListAdapter() {
        return chatsListAdapter;
    }

    private void guardarDB(ArrayList<Chat> chats){

        for(int i = 0; i<chats.size();i++){
            int id = chats.get(i).getId();

            lastMessage(id, this.token);

        }
    }

    private void meterDB(int id, String lastmsg, int numeromsg){

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(activity, "access_db");
        Database db = helper.getWritableDb();
        DaoSession daoSession = new DaoMaster(db).newSession();
        accessDao = daoSession.getAccessDao();


        try { //Almacenamos en la base de datos el ultimo mensaje para poder mostrarlo en el listado de chats.

            Access temporal = new Access();
            temporal.setChat_id(id); // chat ID
            temporal.setLast_msg(lastmsg); //ultimo mensaje
            temporal.setNew_num(numeromsg);//numero de mensajes que hay en el chat actualmentr


            if(accessDao.queryBuilder().where(AccessDao.Properties.Chat_id.eq(id)).list().size()==0){

                accessDao.insert(temporal);
            }else{


                List<Access> list =  accessDao.queryBuilder().where(AccessDao.Properties.Chat_id.eq(id)).list();

                list.get(0).setNew_num(numeromsg);
                list.get(0).setLast_msg(lastmsg);
                accessDao.update(list.get(0));

               if( this.activity.isIniciar()==false){
                   activity.onResume();
                   this.activity.setIniciar(true);
               }

            }

        } catch (Exception e) {
            Log.d("prueba","2º "+e.getMessage());
        }

        try {
            List<Access> list = accessDao.queryBuilder().list();
        }catch (Exception e){
            Log.d("prueba",e.getMessage());

        }

    }

    private void lastMessage(final int id, String token ){

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("X-AUTH-TOKEN",token);
        client.get(activity, "https://api.messenger.tatai.es/v3/chat/" + id + "/message", new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200){
                    Gson gson = new Gson();
                    MessageListResponseObject messageListResponseObject=  gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), MessageListResponseObject.class);

                    String lastmsg;

                    int numeromsg;
                    if(messageListResponseObject.getCount() == 0){
                        lastmsg = "No hay mensajes en este chat";
                        numeromsg = 0;
                    }else{

                        lastmsg = messageListResponseObject.getMessages().get(messageListResponseObject.getCount()-1).getText();
                        numeromsg = messageListResponseObject.getCount();
                    }
                    meterDB(id,lastmsg,numeromsg);


                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 400){
                    ErrorRegisterObject errorRegisterObject = gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), ErrorRegisterObject.class);
                    Toast.makeText(activity,errorRegisterObject.getDescription(),Toast.LENGTH_LONG).show();
                }else if (statusCode == 409){
                    ErrorRegisterObject errorRegisterObject = gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), ErrorRegisterObject.class);
                    Toast.makeText(activity,errorRegisterObject.getDescription(),Toast.LENGTH_LONG).show();
                }else{
                    ErrorRegisterObject errorRegisterObject = gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), ErrorRegisterObject.class);
                    Toast.makeText(activity,errorRegisterObject.getDescription(),Toast.LENGTH_LONG).show();
                }

            }

        });


    }




    @Override
    public void onSuccess (int statusCode, Header[] headers, byte[] responseBody) {

        if (statusCode == 200) {
            ChatListResponseObject chatListResponseObject = gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), ChatListResponseObject.class);
            guardarDB(chatListResponseObject.getChats());
            final List<Chat> chatsList = chatListResponseObject.getChats();
            ListView chatsRecords = activity.findViewById(R.id.chatsList);

            chatsListAdapter = new ChatsListAdapter(chatsList, this.activity, this.accessDao);
            chatsRecords.setAdapter(chatsListAdapter);

            chatsRecords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    activity.openChatActivity(chatsList.get(position));
                }
            });

            chatsRecords.setVisibility(View.VISIBLE);
        } else {

        }

    }

    @Override
    public void onFailure (int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        if (statusCode == 400){
            ErrorRegisterObject errorRegisterObject = this.gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), ErrorRegisterObject.class);
            Toast.makeText(this.activity,errorRegisterObject.getDescription(),Toast.LENGTH_LONG).show();
        } else if (statusCode == 409){
            ErrorRegisterObject errorRegisterObject = this.gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), ErrorRegisterObject.class);
            Toast.makeText(this.activity,errorRegisterObject.getDescription(),Toast.LENGTH_LONG).show();
        } else if (statusCode == 403){ activity.finish();
        } else {

        }
    }
}
