package es.unavarra.tlm.dscr_19_11.ResponseHandlers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.ErrorRegisterObject;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.RegisterSuccessfulObject;

public class RegisterResponseHandler extends AsyncHttpResponseHandler {

private Gson gson;
private Activity activity;

public RegisterResponseHandler(Gson gson, Activity activity){
    this.gson = gson;
    this.activity = activity;

}
    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        if (statusCode == 200){
            RegisterSuccessfulObject registerSuccessfulObject  = this.gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), RegisterSuccessfulObject.class);

            SharedPreferences preferences = activity.getSharedPreferences("credenciales", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString("token", registerSuccessfulObject.getSession().getToken());
            editor.putString("email", registerSuccessfulObject.getUser().getEmail());
            editor.putString("username", registerSuccessfulObject.getUser().getName());
            editor.putString("validUntil", registerSuccessfulObject.getSession().getValid_until());
            editor.commit();

            activity.recreate();
        } else {
            //no se que pasa aqui
        }

    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        if (statusCode == 400){
            ErrorRegisterObject errorRegisterObject = this.gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), ErrorRegisterObject.class);
            Toast.makeText(activity,errorRegisterObject.getDescription(),Toast.LENGTH_SHORT).show();
        } else if (statusCode == 409){
            ErrorRegisterObject errorRegisterObject = this.gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), ErrorRegisterObject.class);
            Toast.makeText(activity,errorRegisterObject.getDescription(),Toast.LENGTH_SHORT).show();

        } else {
            //no se que pasa aqui
        }
    }
}
