package pro.rane.foodadvisor;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {

    ProgressBar progress;
    EditText txtUsername, txtPassword;
    Button btnLogin , btnRegister;
    // Alert Dialog Manager
    //AlertDialogManager alert = new AlertDialogManager();
    // Session Manager Class
    pro.rane.foodadvisor.SessionManager session;
    String username;
    String password;

    // TODO: 30/03/2017 RISCRIVERE COMPLETAMENTE LA LOGICA DI AUTENTICAZIONE. ASPETTARE NUOVE CHIAMATE REST
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Session Manager
        session = new pro.rane.foodadvisor.SessionManager(getApplicationContext());


        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        progress = (ProgressBar) findViewById(R.id.progressBar7);
        progress.setVisibility(View.INVISIBLE);

        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();
        if (session.isLoggedIn()){
            // Activity start
            Intent i = new Intent(getApplicationContext(), NavigationActivity.class);
            startActivity(i);
            finish();
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
              registerActivity();
            }
        });



        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get username, password from EditText
                username = txtUsername.getText().toString();
                password = Utility.md5(txtPassword.getText().toString());
                connection(username);
                progress.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);
                btnRegister.setVisibility(View.INVISIBLE);
            }
        });
    }
    private String res = "";
    private int timeout =2000;
    private void connection(String email){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://foodadvisor.rane.pro:8080/getUserIdByEmail?email="+email,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        res = response;
                        Log.d(this.getClass().getSimpleName() ,"RESPONSE VALUE: "+ response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(this.getClass().getSimpleName() ,"Errore su volley : " + error.toString());
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                while(res.equals("")){
                    try {
                        this.wait(timeout);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                auxConnection(Integer.parseInt(res));
            }
        }, timeout);


    }
    private void auxConnection(Integer user_id){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://foodadvisor.rane.pro:8080/getUser?user_id="+user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        res = response;
                        Log.d(this.getClass().getSimpleName() ,"RESPONSE VALUE: "+ response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(this.getClass().getSimpleName() ,"Errore su volley : " + error.toString());
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                while(res==null){
                    try {
                        this.wait(timeout);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                autentication(res);
            }
        }, timeout);
    }
    private void autentication(String jsonStr){
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);

                // Check if username, password is filled
                if (username.trim().length() > 0 && password.trim().length() > 0) {

                    if (username.equals(jsonObj.getString("email")) && password.equals(jsonObj.getString("passw"))) {

                        session.createLoginSession(jsonObj.getString("login"),jsonObj.getString("name"),jsonObj.getString("second_name"),jsonObj.getString("email"),jsonObj.getString("enterprise_description"),jsonObj.getString("photo"),jsonObj.getString("user_id"));

                        // Activity start
                        Intent i = new Intent(getApplicationContext(), NavigationActivity.class);
                        startActivity(i);
                        finish();

                    } else {
                        // username / password doesn't match
                        alert("Login fallito.\nL'email o la password non sono corrette");
                    }
                } else {
                    // user didn't entered username or password
                    // Show alert asking him to enter the details
                    alert("Login fallito.\nPerfavore inserisci email e password");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void alert(String text){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Attention");
        alertDialog.setMessage(text);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //TODO: in questo punto aggiungere altro
            }
        });
        alertDialog.show();
    }


    public void registerActivity(){
        Intent startRegisterActivity = new Intent(this,RegisterActivity.class);
        startActivity(startRegisterActivity);
    }



}

