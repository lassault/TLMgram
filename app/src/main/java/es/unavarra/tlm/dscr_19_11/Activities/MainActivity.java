package es.unavarra.tlm.dscr_19_11.Activities;

import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.entity.StringEntity;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.LoginRequestObject;
import es.unavarra.tlm.dscr_19_11.ObjectsJson.RegisterRequestObject;
import es.unavarra.tlm.dscr_19_11.R;
import es.unavarra.tlm.dscr_19_11.ResponseHandlers.LoginResponseHandler;
import es.unavarra.tlm.dscr_19_11.ResponseHandlers.RegisterResponseHandler;

public class MainActivity extends BaseActivity {

    private boolean open = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (SesionIniciada()){
            openChatsActivity();
        }


        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        toolbar.setLogo(R.mipmap.chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        final FloatingActionButton registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText registerName = findViewById(R.id.registerName);
                if (open) {
                    registerName.setVisibility(View.INVISIBLE);
                    ((FloatingActionButton) findViewById(R.id.registerButton)).setImageResource(R.drawable.ic_add_black);
                    findViewById(R.id.sendRegisterButton).animate().translationX(0);
                    open = false;
                } else {
                    registerName.setVisibility(View.VISIBLE);
                    ((FloatingActionButton) findViewById(R.id.registerButton)).setImageResource(R.drawable.ic_clear_black);
                    findViewById(R.id.sendRegisterButton).animate().translationX(findViewById(R.id.loginButton).getX()-findViewById(R.id.registerButton).getX());
                    findViewById(R.id.sendRegisterButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            register();
                        }
                    });
                    open = true;
                }

            }
        });
        registerButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "Register", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 10);
                toast.show();
                return true;
            }
        });

        FloatingActionButton loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });

        loginButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Toast toast = Toast.makeText(getApplicationContext(), "Login", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 10);
                toast.show();
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (SesionIniciada()){
            openChatsActivity();
        }

        EditText emailField = findViewById(R.id.loginEmail);
        emailField.getText().clear();

        EditText passwordField = findViewById(R.id.loginPassword);
        passwordField.getText().clear();

    }

    public boolean SesionIniciada(){
        boolean inicio;

        String token = getSharedPreferences("credenciales", Context.MODE_PRIVATE).getString("token",null);
        String validUntil = getSharedPreferences("credenciales", Context.MODE_PRIVATE).getString("validUntil",null);

        if (token == null){
            inicio = false;
        } else {
            inicio = false;
            try {
                 Date date = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'", Locale.getDefault()).parse(validUntil);
                 Date now = new Date();
                 if (!now.after(date)) {
                     inicio = true; //token expirado
                 }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return inicio;
    }

    public void openChatsActivity() {
        Intent intent = new Intent(this, ChatsListActivity.class);
        startActivity(intent);
    }


    public void register() {

        String email = ((EditText)findViewById(R.id.loginEmail)).getText().toString();
        String password = ((EditText)findViewById(R.id.loginPassword)).getText().toString();
        String nombre = ((EditText)findViewById(R.id.registerName)).getText().toString();

        if (email.isEmpty()) {
            Toast.makeText(this, R.string.emailError, Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty()){
            Toast.makeText(this, R.string.passwordError, Toast.LENGTH_SHORT).show();
        } else  if(nombre.isEmpty()){
            Toast.makeText(this, "Introduce un nombre", Toast.LENGTH_SHORT).show();
        }else {

            Gson gson = new Gson();
            RegisterRequestObject registerRequestObject = new RegisterRequestObject(email, password, nombre);

            String jsonenviar = gson.toJson(registerRequestObject);
            //Mandamos la peticion
            AsyncHttpClient client = new AsyncHttpClient();


            try {
                client.post(
                        this,
                        "https://api.messenger.tatai.es/v3/auth/register",
                        new StringEntity(jsonenviar),
                        "application/json",
                        new RegisterResponseHandler(gson, this)
                );

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void Login() {
        String email = ((EditText) findViewById(R.id.loginEmail)).getText().toString();
        String password = ((EditText) findViewById(R.id.loginPassword)).getText().toString();

        if (email.isEmpty()) {
            Toast.makeText(this, R.string.emailError, Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty()) {
            Toast.makeText(this, R.string.passwordError, Toast.LENGTH_SHORT).show();
        } else {

            Gson gson = new Gson();
            LoginRequestObject loginRequestObject = new LoginRequestObject(email, password);

            String jsonenviar = gson.toJson(loginRequestObject);
            AsyncHttpClient client = new AsyncHttpClient();

            try {
                client.post(
                        this,
                        "https://api.messenger.tatai.es/v3/auth/login",
                        new StringEntity(jsonenviar),
                        "application/json",
                        new LoginResponseHandler(gson, this)

                );
                this.onResume();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }


    }
}

