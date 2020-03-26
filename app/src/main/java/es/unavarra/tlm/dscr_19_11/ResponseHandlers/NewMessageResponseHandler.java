package es.unavarra.tlm.dscr_19_11.ResponseHandlers;

import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.greenrobot.greendao.database.Database;

import java.nio.charset.StandardCharsets;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import es.unavarra.tlm.dscr_19_11.Activities.ChatMessagesActivity;
import es.unavarra.tlm.dscr_19_11.Adapters.MessageListAdapter;
import es.unavarra.tlm.dscr_19_11.Entities.Access;
import es.unavarra.tlm.dscr_19_11.Entities.AccessDao;
import es.unavarra.tlm.dscr_19_11.Entities.DaoMaster;
import es.unavarra.tlm.dscr_19_11.Entities.DaoSession;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.ErrorRegisterObject;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.NewMessageResponseObject;
import es.unavarra.tlm.dscr_19_11.R;

public class NewMessageResponseHandler extends AsyncHttpResponseHandler {

    private ChatMessagesActivity chatMessagesActivity;
    private Gson gson ;
    private MessageListAdapter messageListAdapter;

    public NewMessageResponseHandler(ChatMessagesActivity chatMessagesActivity, Gson gson, MessageListAdapter messageListAdapter) {
        this.chatMessagesActivity = chatMessagesActivity;
        this.gson = gson;
        this.messageListAdapter = messageListAdapter;
    }
    public void actualizarNumeroMensajes(AccessDao accessDao,int id,String msg){

        List<Access> list =  accessDao.queryBuilder().where(AccessDao.Properties.Chat_id.eq(id)).list();

        if(list.size()>0){
            list.get(0).setLast_num(list.get(0).getLast_num()+1);
            list.get(0).setNew_num(list.get(0).getNew_num()+1);
            list.get(0).setLast_msg(msg);

            accessDao.update(list.get(0));
        }

    }

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        if (statusCode == 200){
            NewMessageResponseObject newMessageResponseObject = this.gson.fromJson(new String(responseBody,StandardCharsets.UTF_8),NewMessageResponseObject.class);

            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(chatMessagesActivity, "access_db");
            Database db = helper.getWritableDb();
            DaoSession daoSession = new DaoMaster(db).newSession();
            AccessDao accessDao = daoSession.getAccessDao();
            actualizarNumeroMensajes(accessDao,newMessageResponseObject.getMessages().get(0).getChat().getId(),newMessageResponseObject.getMessages().get(0).getText());
            messageListAdapter.addMessage(newMessageResponseObject.getMessages().get(0));

            ListView listView = chatMessagesActivity.findViewById(R.id.messagesList);
            listView.setSelection(messageListAdapter.getCount() - 1);

        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        if (statusCode == 400) {
            ErrorRegisterObject errorRegisterObject = this.gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), ErrorRegisterObject.class);
            Toast.makeText(this.chatMessagesActivity, errorRegisterObject.getDescription(), Toast.LENGTH_LONG).show();
        } else if (statusCode == 409) {
            ErrorRegisterObject errorRegisterObject = this.gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), ErrorRegisterObject.class);
            Toast.makeText(this.chatMessagesActivity, errorRegisterObject.getDescription(), Toast.LENGTH_LONG).show();
        }else if(statusCode == 403){
            ErrorRegisterObject errorRegisterObject = this.gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), ErrorRegisterObject.class);
            Toast.makeText(this.chatMessagesActivity, errorRegisterObject.getDescription(), Toast.LENGTH_LONG).show();
        }
    }
}
