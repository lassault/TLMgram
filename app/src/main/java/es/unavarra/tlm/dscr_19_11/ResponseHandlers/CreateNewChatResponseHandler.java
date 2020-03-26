package es.unavarra.tlm.dscr_19_11.ResponseHandlers;


import android.app.Activity;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.ErrorRegisterObject;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.NewChatResponseObject;

public class CreateNewChatResponseHandler extends AsyncHttpResponseHandler {
    private Gson gson;
    private Activity activity;

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public CreateNewChatResponseHandler(Gson gson, Activity activity){
        this.gson = gson;
        this.activity = activity;
    }
    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        if (statusCode == 200){
            NewChatResponseObject newChatResponseObject = this.gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), NewChatResponseObject.class);
            Toast.makeText(activity,"tu id de chat es: "+newChatResponseObject.getChat().getId(),Toast.LENGTH_LONG).show();
            activity.finish();
        } else {

        }

    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        if (statusCode == 400){
            ErrorRegisterObject errorRegisterObject = this.gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), ErrorRegisterObject.class);
            Toast.makeText(this.activity,errorRegisterObject.getDescription(),Toast.LENGTH_LONG).show();
        } else if (statusCode == 409){
            ErrorRegisterObject errorRegisterObject = this.gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), ErrorRegisterObject.class);
            Toast.makeText(this.activity,errorRegisterObject.getDescription(),Toast.LENGTH_LONG).show();
        } else if (statusCode == 403){
            activity.finish();
        } else {

        }
    }


}
