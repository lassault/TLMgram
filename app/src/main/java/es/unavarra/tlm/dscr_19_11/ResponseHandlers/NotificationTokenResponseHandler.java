package es.unavarra.tlm.dscr_19_11.ResponseHandlers;


import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.ErrorRegisterObject;

public class NotificationTokenResponseHandler extends AsyncHttpResponseHandler {
    private Gson gson;

    public NotificationTokenResponseHandler(Gson gson) {
        this.gson = gson;
    }


    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        if (statusCode == 200) {
            Log.d("firebase", "sended");
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        if (statusCode == 400) {
            ErrorRegisterObject errorRegisterObject = this.gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), ErrorRegisterObject.class);
            Log.d("firebase", errorRegisterObject.getDescription());
        }
    }
}