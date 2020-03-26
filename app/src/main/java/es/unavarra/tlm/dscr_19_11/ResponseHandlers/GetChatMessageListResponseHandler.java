package es.unavarra.tlm.dscr_19_11.ResponseHandlers;

import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.nio.charset.StandardCharsets;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import es.unavarra.tlm.dscr_19_11.Activities.ChatMessagesActivity;
import es.unavarra.tlm.dscr_19_11.Adapters.MessageListAdapter;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.ErrorRegisterObject;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.Message;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.MessageListResponseObject;
import es.unavarra.tlm.dscr_19_11.R;

public class GetChatMessageListResponseHandler extends AsyncHttpResponseHandler {
    private Gson gson;
    private ChatMessagesActivity activity;
    private MessageListAdapter messageListAdapter;

    public  GetChatMessageListResponseHandler (Gson gson, ChatMessagesActivity activity) {
        this.gson = gson;
        this.activity = activity;
    }

    public MessageListAdapter getMessageListAdapter() {
        return messageListAdapter;
    }

    @Override
    public void onSuccess (int statusCode, Header[] headers, byte[] responseBody) {
        if (statusCode == 200) {
            MessageListResponseObject messageListResponseObject = gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), MessageListResponseObject.class);
            List<Message> messages = messageListResponseObject.getMessages();
            ListView messageRecords = activity.findViewById(R.id.messagesList);

            messageListAdapter = new MessageListAdapter(activity, messages);
            messageRecords.setAdapter(messageListAdapter);

            messageRecords.setVisibility(View.VISIBLE);
            messageRecords.setSelection(messageListAdapter.getCount() - 1);

        }
    }

    @Override
    public void onFailure (int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

        if (statusCode == 400){
            ErrorRegisterObject errorRegisterObject = this.gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), ErrorRegisterObject.class);
            Toast.makeText(activity,errorRegisterObject.getDescription(),Toast.LENGTH_LONG).show();
        }else if (statusCode == 409){
            ErrorRegisterObject errorRegisterObject = this.gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), ErrorRegisterObject.class);
            Toast.makeText(activity,errorRegisterObject.getDescription(),Toast.LENGTH_LONG).show();
        }else{
            //no idea de que poner aquiiiiiii
        }
    }
}
