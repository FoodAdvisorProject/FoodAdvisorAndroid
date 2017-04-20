package pro.rane.foodadvisor;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;


import android.os.Bundle;
import android.text.TextUtils;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import es.dmoral.toasty.Toasty;


public class LoginActivity extends AppCompatActivity  {

    ProgressBar progress;
    EditText txtUsername, txtPassword;
    Button btnLogin , btnRegister;
    pro.rane.foodadvisor.SessionManager session;
    String username;
    String password;
    private final static boolean DEBUG = false;
    private static final String TAG = "LoginActivity";
    private static final String imgUri ="http://foodadvisor.rane.pro:8080/getUserImage?user_id=";


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

        if(DEBUG)Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();

        txtUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Utility.hideKeyboard(v);
            }
        });

        txtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Utility.hideKeyboard(v);
            }
        });

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

                if (TextUtils.isEmpty(txtUsername.getText().toString())){
                    txtUsername.setError("Il campo non può essere vuoto");
                    return;
                }
                if (TextUtils.isEmpty(txtPassword.getText().toString())){
                    txtPassword.setError("Il campo non può essere vuoto");
                    return;
                }
                // Get username, password from EditText
                username = txtUsername.getText().toString();
                password = Utility.md5(txtPassword.getText().toString());

                if (username.trim().length() > 0 && password.trim().length() > 0) {
                    progress.setVisibility(View.VISIBLE);
                    btnLogin.setVisibility(View.INVISIBLE);
                    btnRegister.setVisibility(View.INVISIBLE);
                    try {
                        autentication(username,password);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }else {
                    // user didn't entered username or password
                    // Show alert asking him to enter the details
                    txtUsername.setError("Il campo non può essere vuoto");
                    txtPassword.setError("Il campo non può essere vuoto");
                }
            }
        });
    }

    private void autentication(String uname, String ciph_pwd) throws UnsupportedEncodingException {
        final String url = "http://foodadvisor.rane.pro:8080/getUser?email="+URLEncoder.encode(uname,"UTF-8") + "&password=" + URLEncoder.encode(ciph_pwd, "UTF-8");
        //Toast.makeText(getApplicationContext(),url,Toast.LENGTH_SHORT).show();
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(final JSONObject response) {
                try {
                    String user_id = response.getString("user_id");
                    Log.d("DEBUG","User_id :"+user_id);
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.loadImage(imgUri.concat(user_id), new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                                File directory = cw.getDir("images", Context.MODE_PRIVATE);
                                File mypath=new File(directory,"profile.jpg");

                                FileOutputStream fos = null;
                                try {
                                    fos = new FileOutputStream(mypath);
                                    // Use the compress method on the BitMap object to write image to the OutputStream
                                    loadedImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
                                    String imgPath = "file://".concat(directory.getAbsolutePath()).concat("/profile.jpg");
                                    session.createLoginSession(response.getString("login"), response.getString("name"), response.getString("second_name"), response.getString("email"), response.getString("enterprise_description"),response.getString("user_id"),imgPath);

                                    // Activity start
                                    Intent i = new Intent(getApplicationContext(), NavigationActivity.class);
                                    startActivity(i);
                                    finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        if (fos != null) {
                                            fos.close();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (NullPointerException e){
                                        Log.e(TAG,e.toString());
                                    }
                                }
                            }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            try {
                                session.createLoginSession(response.getString("login"), response.getString("name"), response.getString("second_name"), response.getString("email"), response.getString("enterprise_description"), response.getString("user_id"), "drawable://"+R.drawable.farmer);
                                // Activity start
                                Intent i = new Intent(getApplicationContext(), NavigationActivity.class);
                                startActivity(i);
                                finish();
                            }catch (JSONException e){
                                e.printStackTrace();
                            }

                        }


                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    progress.setVisibility(View.INVISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                    btnRegister.setVisibility(View.VISIBLE);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toasty.error(getApplicationContext(),"Login fallito.\nL'email o la password non sono corrette",Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.INVISIBLE);
                btnLogin.setVisibility(View.VISIBLE);
                btnRegister.setVisibility(View.VISIBLE);
            }
        });

        queue.add(request);

    }

    public void registerActivity(){
        Intent startRegisterActivity = new Intent(this,RegisterActivity.class);
        startActivity(startRegisterActivity);
    }



}

