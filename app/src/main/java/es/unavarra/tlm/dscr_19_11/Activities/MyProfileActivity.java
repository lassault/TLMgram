package es.unavarra.tlm.dscr_19_11.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.ChangeNameRequestObject;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.ChangePassRequestJson;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.ErrorRegisterObject;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.ProfileResponseJson;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.RegisterSuccessfulObject;
import es.unavarra.tlm.dscr_19_11.R;
import es.unavarra.tlm.dscr_19_11.ResponseHandlers.LogOutResponseHandler;

public class MyProfileActivity extends BaseActivity {

    private boolean open = false;

    @Override
    protected void onResume(){
        super.onResume();

        String token = getSharedPreferences("credenciales",Context.MODE_PRIVATE).getString("token",null);

        if(token.isEmpty() || token.equals("")){
            finish();
        }else {
            pedirUser(token);
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        Toolbar toolbar = findViewById(R.id.toolbarMyProfile);
        toolbar.setLogo(R.mipmap.chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        String token = getSharedPreferences("credenciales",Context.MODE_PRIVATE).getString("token",null);
        pedirUser(token);

        FloatingActionButton editName = findViewById(R.id.cambiarnamebutton);
        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editField = findViewById(R.id.editField);
                if (open) {
                    if (editField.getHint().toString().equals(getString(R.string.nuevapass))) {
                        editField.setVisibility(View.INVISIBLE);
                        findViewById(R.id.cambiarnamebutton).animate().translationX(0);
                        findViewById(R.id.exitbutton).animate().translationX(0);
                        ((FloatingActionButton) findViewById(R.id.cambiarpassbutton)).setImageResource(R.drawable.ic_lock_black);
                        findViewById(R.id.cambiarpassbutton).animate().translationX(0);
                        findViewById(R.id.actionButton).animate().translationX(0);
                        open = false;
                    }

                    if (editField.getHint().toString().equals(getString(R.string.nuevonombre))) {
                        editField.setVisibility(View.INVISIBLE);
                        findViewById(R.id.cambiarpassbutton).animate().translationX(0);
                        findViewById(R.id.exitbutton).animate().translationX(0);
                        ((FloatingActionButton) findViewById(R.id.cambiarnamebutton)).setImageResource(R.drawable.ic_edit_black);
                        findViewById(R.id.cambiarnamebutton).animate().translationX(0);
                        findViewById(R.id.actionButton).animate().translationX(0);
                        open = false;
                    }
                } else {
                    editField.setHint(R.string.nuevonombre);
                    editField.setInputType(InputType.TYPE_CLASS_TEXT);
                    editField.setVisibility(View.VISIBLE);
                    ((FloatingActionButton) findViewById(R.id.cambiarnamebutton)).setImageResource(R.drawable.ic_arrow_back_black);
                    findViewById(R.id.cambiarpassbutton).animate().translationX(-(findViewById(R.id.cambiarpassbutton).getX()-findViewById(R.id.cambiarnamebutton).getX()));
                    findViewById(R.id.exitbutton).animate().translationX(-(findViewById(R.id.exitbutton).getX()-findViewById(R.id.cambiarnamebutton).getX()));
                    findViewById(R.id.actionButton).animate().translationX(+800);
                    findViewById(R.id.actionButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            peticionCambiarNombre();
                        }
                    });
                    open = true;
                }
            }
        });

        editName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "Edit name", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 10);
                toast.show();
                return true;
            }
        });

        FloatingActionButton editPassword = findViewById(R.id.cambiarpassbutton);
        editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editField = findViewById(R.id.editField);
                if (!open) {
                    editField.setHint(R.string.nuevapass);
                    editField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    editField.setVisibility(View.VISIBLE);
                    ((FloatingActionButton) findViewById(R.id.cambiarpassbutton)).setImageResource(R.drawable.ic_arrow_back_black);
                    findViewById(R.id.cambiarpassbutton).animate().translationX(-(findViewById(R.id.cambiarpassbutton).getX()-findViewById(R.id.cambiarnamebutton).getX()));
                    findViewById(R.id.exitbutton).animate().translationX(-(findViewById(R.id.exitbutton).getX()-findViewById(R.id.cambiarnamebutton).getX()));
                    findViewById(R.id.actionButton).animate().translationX(+800);
                    findViewById(R.id.actionButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            peticionCambiarPassword();
                        }
                    });
                    open = true;
                }
            }
        });

        editPassword.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "Edit password", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 10);
                toast.show();
                return true;
            }
        });

        FloatingActionButton logOutButton = findViewById(R.id.exitbutton);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit();
            }
        });

        logOutButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "Log out", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 10);
                toast.show();
                return false;
            }
        });
    }

    public void pedirUser(String token){

        AsyncHttpClient client = new AsyncHttpClient();

        client.addHeader("X-AUTH-TOKEN",token);
        client.get(this, "https://api.messenger.tatai.es/v3/profile/me", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200){
                            Gson gson = new Gson();
                            ProfileResponseJson profileResponseJson = gson.fromJson(new String(responseBody, StandardCharsets.UTF_8),ProfileResponseJson.class);

                            TextView my_name = findViewById(R.id.my_profile_name);
                            my_name.setText(profileResponseJson.getUser().getName());

                            TextView email = findViewById(R.id.emailmyprofile);
                            email.setText(String.format(Locale.getDefault(), "EMAIL: %1$s", profileResponseJson.getUser().getEmail()));
                            email.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                            TextView id = findViewById(R.id.idmyprofile);
                            id.setText(String.format(Locale.getDefault(), "ID: %1$02d", profileResponseJson.getUser().getId()));
                            id.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if (statusCode == 400){
                        Gson gson= new Gson();
                        ErrorRegisterObject errorRegisterObject = gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), ErrorRegisterObject.class);
                        Toast.makeText(getApplicationContext(),errorRegisterObject.getDescription(),Toast.LENGTH_LONG).show();

                    }else if (statusCode == 409){
                        Gson gson= new Gson();
                        ErrorRegisterObject errorRegisterObject = gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), ErrorRegisterObject.class);
                        Toast.makeText(getApplicationContext(),errorRegisterObject.getDescription(),Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );

    }

    public void peticionCambiarNombre(){

        String nombre = ((EditText)findViewById(R.id.editField)).getText().toString();

        Gson gson = new Gson();
        ChangeNameRequestObject changeNameRequestObject = new ChangeNameRequestObject(nombre);

        String jsonenviar = gson.toJson(changeNameRequestObject);
        AsyncHttpClient client = new AsyncHttpClient();

        try {
            String token = getSharedPreferences("credenciales", Context.MODE_PRIVATE).getString("token", null);


            client.addHeader("X-AUTH-TOKEN", token);
            client.post(
                    this,
                    "https://api.messenger.tatai.es/v3/profile/me",
                    new StringEntity(jsonenviar),
                    "application/json",
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (statusCode == 200){
                                Gson gson = new Gson();
                                ProfileResponseJson profileResponseJson = gson.fromJson(new String(responseBody,StandardCharsets.UTF_8),ProfileResponseJson.class);
                                Toast toast = Toast.makeText(getApplicationContext(),"Name changed to: ." + profileResponseJson.getUser().getName(),Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP, 0, 10);
                                toast.show();
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            if(statusCode == 400){
                                Gson gson= new Gson();
                                ErrorRegisterObject errorRegisterObject = gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), ErrorRegisterObject.class);
                                Toast.makeText(getApplicationContext(),errorRegisterObject.getDescription(),Toast.LENGTH_LONG).show();
                            }else if (statusCode == 409){
                                Gson gson = new Gson();
                                ErrorRegisterObject errorRegisterObject = gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), ErrorRegisterObject.class);
                                Toast.makeText(getApplicationContext(),errorRegisterObject.getDescription(),Toast.LENGTH_LONG).show();
                            }
                        }
                    }
            );

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void peticionCambiarPassword(){

        String pass = ((EditText)findViewById(R.id.editField)).getText().toString();

        Gson gson = new Gson();
        ChangePassRequestJson changePassRequestJson = new ChangePassRequestJson(pass);

        String jsonenviar = gson.toJson(changePassRequestJson);
        AsyncHttpClient client = new AsyncHttpClient();

        try {
            String token = getSharedPreferences("credenciales", Context.MODE_PRIVATE).getString("token", null);


            client.addHeader("X-AUTH-TOKEN", token);
            client.post(
                    this,
                    "https://api.messenger.tatai.es/v3/profile/me/password",
                    new StringEntity(jsonenviar),
                    "application/json",
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (statusCode == 200){
                                Gson gson = new Gson();
                                RegisterSuccessfulObject registerSuccessfulObject = gson.fromJson( new String(responseBody,StandardCharsets.UTF_8),RegisterSuccessfulObject.class);
                                Toast.makeText(getApplicationContext(),"Your pass has been changed.",Toast.LENGTH_SHORT).show();
                                guardarCredenciales(registerSuccessfulObject);

                                finish();

                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            if(statusCode == 400){
                                Gson gson= new Gson();
                                ErrorRegisterObject errorRegisterObject = gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), ErrorRegisterObject.class);
                                Toast.makeText(getApplicationContext(),errorRegisterObject.getDescription(),Toast.LENGTH_LONG).show();
                            }else if (statusCode == 409){
                                Gson gson = new Gson();
                                ErrorRegisterObject errorRegisterObject = gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), ErrorRegisterObject.class);
                                Toast.makeText(getApplicationContext(),errorRegisterObject.getDescription(),Toast.LENGTH_LONG).show();
                            }
                        }
                    }
            );

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void guardarCredenciales(RegisterSuccessfulObject registerSuccessfulObject){
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token",registerSuccessfulObject.getSession().getToken());
        editor.putString("email",registerSuccessfulObject.getUser().getEmail());
        editor.putString("username",registerSuccessfulObject.getUser().getName());
        editor.putString("validUntil",registerSuccessfulObject.getSession().getValid_until());
        editor.commit();
    }

   public void exit(){

       SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
       SharedPreferences.Editor editor = preferences.edit();
       editor.putString("token", null);
       editor.putString("validUntil", null);
       editor.putString("username", null);
       editor.putString("email", null);

       editor.commit();

       String token = getSharedPreferences("credenciales", Context.MODE_PRIVATE).getString("token",null);

       //Mandamos la petición
       AsyncHttpClient client = new AsyncHttpClient();
       client.addHeader("X-AUTH-TOKEN",token);

       client.get(this,"https://api.messenger.tatai.es/v3/auth/logout",new LogOutResponseHandler());
       this.finish();
   }


    public void deleteAccount(){
        AsyncHttpClient client = new AsyncHttpClient();

        String token = getSharedPreferences("credenciales", Context.MODE_PRIVATE).getString("token", null);


        client.addHeader("X-AUTH-TOKEN", token);
        client.delete(
                this,
                "https://api.messenger.tatai.es/v3/profile",

                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if(statusCode==200){
                            Toast toast = Toast.makeText(getApplicationContext(),"Account deleted",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 10);
                            toast.show();
                            SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("token", null);
                            editor.putString("email", null);
                            editor.putString("username", null);
                            editor.putString("validUntil", null);
                            editor.commit();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Gson gson = new Gson();
                        if (statusCode == 400){
                            ErrorRegisterObject errorRegisterObject = gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), ErrorRegisterObject.class);
                            Toast.makeText(getApplicationContext(),errorRegisterObject.getDescription(),Toast.LENGTH_LONG).show();
                        }else if(statusCode == 409){
                            ErrorRegisterObject errorRegisterObject = gson.fromJson(new String(responseBody, StandardCharsets.UTF_8), ErrorRegisterObject.class);
                            Toast.makeText(getApplicationContext(),errorRegisterObject.getDescription(),Toast.LENGTH_LONG).show();
                        }
                    }
                }

        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getTitle().equals("Delete my profile")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
            builder.setTitle("Delete profile");
            builder.setMessage(getString(R.string.estasseguro));
            builder.setCancelable(true);
            builder.setIcon(R.drawable.ic_warning_black);

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteAccount();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.setInverseBackgroundForced(true);
            alertDialog.show();
        }

        return true;
    }
}
