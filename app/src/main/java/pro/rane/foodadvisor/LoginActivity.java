package pro.rane.foodadvisor;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.cast.framework.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import pro.rane.foodadvisor.Rest;
import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    // Email, password edittext
    EditText txtUsername, txtPassword;

    // login button
    Button btnLogin;

    // Alert Dialog Manager
    //AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManagement session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Session Manager
        session = new SessionManagement(getApplicationContext());

        // Email, Password input text
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();


        // Login button
        btnLogin = (Button) findViewById(R.id.btnLogin);


        // Login button click event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get username, password from EditText
                String username = txtUsername.getText().toString();
                String password =Rest.sha256( txtPassword.getText().toString());
                String jsonStr = connection(username,password);


                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);

                        // Check if username, password is filled
                        if (username.trim().length() > 0 && password.trim().length() > 0) {

                            if (username.equals(jsonObj.getString("email")) && password.equals(jsonObj.getString("passw"))) {

                                session.createLoginSession(jsonObj.getString("login"),jsonObj.getString("name"),jsonObj.getString("second_name"),jsonObj.getString("email"),jsonObj.getString("enterprise_description"),jsonObj.getString("photo"));

                                // Activity start
                                Intent i = new Intent(getApplicationContext(), NavigationActivity.class);
                                startActivity(i);
                                finish();

                            } else {
                                // username / password doesn't match
                                alert("Login failed..Username/Password is incorrect");
                            }
                        } else {
                            // user didn't entered username or password
                            // Show alert asking him to enter the details
                            alert("Login failed..Please enter username and password");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private String connection(String email, String sha){
        Rest request= new Rest();
        String URL="http://foodadvisor.rane.pro:8080/getUserIdByEmail?email="+email;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String response= request.getRequest(URL,requestQueue);
        if(Integer.parseInt(response)>0) {
            URL = "http://foodadvisor.rane.pro:8080/getUser?user_id="+response;
            return request.getRequest(URL, requestQueue);
        }
        return "";
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


    public void registerActivity(View view){
        Intent startRegisterActivity = new Intent(this,RegisterActivity.class);
        startActivity(startRegisterActivity);
    }



}

