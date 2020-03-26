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



public class LoginResponseHandler extends AsyncHttpResponseHandler {
    private Gson gson;
    private Activity activity;

    public LoginResponseHandler(Gson gson, Activity activity){
        this.gson = gson;
        this.activity = activity;
    }

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



    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        if(statusCode == 200){
            RegisterSuccessfulObject registerSuccessfulObject = this.gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), RegisterSuccessfulObject.class);

            SharedPreferences preferences = activity.getSharedPreferences("credenciales", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            //guardar variables de sesion en shared preferences
            editor.putString("token",registerSuccessfulObject.getSession().getToken());
            editor.putString("validUntil",registerSuccessfulObject.getSession().getValid_until());
            editor.putString("username",registerSuccessfulObject.getUser().getName());
            editor.putString("email",registerSuccessfulObject.getUser().getEmail());
            editor.commit();
            //ir a la pantalla de chats

            activity.recreate();

        } else {
            //no idea de que poner aqui
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        if (statusCode == 400){
            ErrorRegisterObject errorRegisterObject = this.gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), ErrorRegisterObject.class);
            Toast.makeText(getActivity(),errorRegisterObject.getDescription(),Toast.LENGTH_LONG).show();
        } else if (statusCode == 409){
            ErrorRegisterObject errorRegisterObject = this.gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), ErrorRegisterObject.class);
            Toast.makeText(getActivity(),errorRegisterObject.getDescription(),Toast.LENGTH_LONG).show();
        } else {
        //no idea de que poner aquiiiiiii
        }
    }
}
