package es.unavarra.tlm.dscr_19_11.ResponseHandlers;


import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.greenrobot.greendao.database.Database;

import java.nio.charset.StandardCharsets;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import es.unavarra.tlm.dscr_19_11.Activities.ChatMessagesActivity;
import es.unavarra.tlm.dscr_19_11.Entities.Access;
import es.unavarra.tlm.dscr_19_11.Entities.AccessDao;
import es.unavarra.tlm.dscr_19_11.Entities.DaoMaster;
import es.unavarra.tlm.dscr_19_11.Entities.DaoSession;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.ErrorRegisterObject;

public class LogOutChatResponseHandler extends AsyncHttpResponseHandler {
    private Gson gson;
    private int id;

    public LogOutChatResponseHandler(Gson gson, ChatMessagesActivity chatMessagesActivity, int id) {
        this.gson = gson;
        this.chatMessagesActivity = chatMessagesActivity;
        this.id = id;
    }

    private ChatMessagesActivity chatMessagesActivity;

    public LogOutChatResponseHandler(ChatMessagesActivity chatMessagesActivity) {
        this.chatMessagesActivity = chatMessagesActivity;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        if (statusCode ==200){


            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(chatMessagesActivity, "access_db");
            Database db = helper.getWritableDb();
            DaoSession daoSession = new DaoMaster(db).newSession();
            AccessDao accessDao = daoSession.getAccessDao();

            List<Access> list =  accessDao.queryBuilder().where(AccessDao.Properties.Chat_id.eq(this.id)).list();
            accessDao.delete(list.get(0));
            chatMessagesActivity.finish();
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

    }
}
