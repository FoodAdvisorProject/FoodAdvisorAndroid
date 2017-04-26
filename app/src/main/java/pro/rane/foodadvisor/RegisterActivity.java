package pro.rane.foodadvisor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;


public class RegisterActivity extends AppCompatActivity {
    private EditText aziendaName;
    private EditText nomeTit;
    private EditText cognomeTit;
    private EditText passText;
    private EditText emailTit;
    private EditText passConfirmText;
    private EditText phoneText;
    private EditText ivaText;
    private EditText description;
    private Button loadPhotoBtn;
    private Button registerBtn;
    private ProgressBar progressBar;
    byte img[];
    Bitmap bitmap=null;
    private String stringedPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        progressBar = (ProgressBar) findViewById((R.id.progressBarReg));
        aziendaName = (EditText) findViewById(R.id.aziendaName);
        nomeTit = (EditText) findViewById(R.id.nomeTit);
        cognomeTit = (EditText) findViewById(R.id.cognomeTit);
        passText = (EditText) findViewById(R.id.passText);
        emailTit = (EditText) findViewById(R.id.emailTit);
        passConfirmText = (EditText) findViewById(R.id.passConfirmText);
        phoneText = (EditText) findViewById(R.id.phoneText);
        ivaText = (EditText) findViewById(R.id.ivaText);
        description = (EditText) findViewById(R.id.descriptionText);

        loadPhotoBtn = (Button) findViewById(R.id.btnPhoto);

        loadPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iob = new Intent(Intent.ACTION_PICK,

                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iob, 0);
            }
        });

        registerBtn = (Button) findViewById(R.id.btnReg);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    register(v);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        aziendaName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Utility.hideKeyboard(v);
            }
        });

        nomeTit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Utility.hideKeyboard(v);
            }
        });

        cognomeTit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Utility.hideKeyboard(v);
            }
        });

        passText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Utility.hideKeyboard(v);
            }
        });

        emailTit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Utility.hideKeyboard(v);
            }
        });

        passConfirmText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Utility.hideKeyboard(v);
            }
        });

        phoneText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Utility.hideKeyboard(v);
            }
        });

        ivaText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Utility.hideKeyboard(v);
            }
        });

        description.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Utility.hideKeyboard(v);
            }
        });
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    private boolean controll() {
        boolean ret = true;

        if(TextUtils.isEmpty(aziendaName.getText().toString())) {
            aziendaName.setError("Il campo non può essere vuoto");
            ret = false;
        }

        if (TextUtils.isEmpty(nomeTit.getText().toString())){
            nomeTit.setError("Il campo non può essere vuoto");
            ret = false;
        }

        if (TextUtils.isEmpty(cognomeTit.getText().toString())){
            cognomeTit.setError("Il campo non può essere vuoto");
            ret = false;
        }

        if (TextUtils.isEmpty(emailTit.getText().toString())){
            emailTit.setError("Il campo non può essere vuoto");
            ret = false;
        }else if (!isEmailValid(emailTit.getText().toString())){
            emailTit.setError("Email non valida");
            ret = false;
        }

        if (!TextUtils.isDigitsOnly(phoneText.getText().toString())){
            phoneText.setError("Il campo non può contenere caratteri alfabetici");
            ret = false;
        }

        if(TextUtils.isEmpty(passText.getText().toString())){
            passText.setError("Il campo non può essere vuoto");
            ret = false;
        } else if (passText.getText().toString().length()< 8){
            passText.setError("La password è troppo corta (min 8 caratteri)");
            ret = false;
        }

        if(TextUtils.isEmpty(passConfirmText.getText().toString())){
            passConfirmText.setError("Il campo non può essere vuoto");
            ret = false;
        } else if (!passText.getText().toString().equals(passConfirmText.getText().toString())){
            passConfirmText.setError("Deve essere la stessa del campo password");
            ret = false;
        }

        return ret;
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK && data!=null){


            Uri selectedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);
                img = bos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Toasty.success(this,"Immagine selezionata").show();
            loadPhotoBtn.setText(R.string.edit_img);
            stringedPhoto = Utility.BitMapToString(bitmap);
        }else{
            Toasty.error(this,"Immagine non selezionata").show();
        }

    }


    public void register(View view) throws UnsupportedEncodingException {
        if(controll()){
            registerBtn.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            JSONObject user = new JSONObject();
            try {
                user.put("login_name", aziendaName.getText().toString());
                user.put("login_passw", Utility.md5(passText.getText().toString()) );
                user.put("email", emailTit.getText().toString().replace("@","%40") );
                user.put("name", nomeTit.getText().toString() );
                user.put("second_name", cognomeTit.getText().toString() );
                user.put("is_enterprise","1");
                user.put("enterprise_description","Telefono azienda "+phoneText.getText().toString()+"%0AIVA"+ivaText.getText().toString()+"%0A"+description.getText().toString());
                // TODO: 10/03/2017  fotografie implementare
                user.put("photo",/*stringedPhoto*/"null");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL="http://foodadvisor.rane.pro:8080/addUser";
            final String requestBody = Utility.toCorrectCase(user.toString());
            Log.e("VOLLEY",requestBody);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("VOLLEY", "Response:"+ response);
                    progressBar.setVisibility(View.INVISIBLE);
                    registerBtn.setVisibility(View.VISIBLE);
                    new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Ok")
                            .setContentText("Registazione effettuata!\nId nuovo utente:"+response)
                            .setConfirmText("Ho capito!").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Errore comunicazione server")
                            .setContentText("Qualcosa non ha funzionato!\n")
                            .setConfirmText("Ho capito!").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    }).show();

                    String body="";
                    //get status code here
                    String statusCode = String.valueOf(error.networkResponse.statusCode);
                    //get response body and parse with appropriate encoding
                    if(error.networkResponse.data!=null) {
                        try {
                            body = new String(error.networkResponse.data,"UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                    registerBtn.setVisibility(View.VISIBLE);
                    Log.e("VOLLEY","Status code:"+statusCode+" Body: "+body);
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };
            requestQueue.add(stringRequest);
        }
    }
}
