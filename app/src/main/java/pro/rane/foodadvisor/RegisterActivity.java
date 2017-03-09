package pro.rane.foodadvisor;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import pro.rane.foodadvisor.Rest;
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
    byte img[];
    Bitmap bitmap=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        aziendaName = (EditText) findViewById(R.id.aziendaName);
        nomeTit = (EditText) findViewById(R.id.nomeTit);
        cognomeTit = (EditText) findViewById(R.id.cognomeTit);
        passText = (EditText) findViewById(R.id.passText);
        emailTit = (EditText) findViewById(R.id.emailTit);
        passConfirmText = (EditText) findViewById(R.id.passConfirmText);
        phoneText = (EditText) findViewById(R.id.phoneText);
        ivaText = (EditText) findViewById(R.id.ivaText);
        description = (EditText) findViewById(R.id.descriptionText);
    }

    // TODO: 03/03/2017 scrivere la logica di check information + la chiamata API di registrazione del con eventuale risposta positiva o negativa
    // TODO: 05/03/2017 la call è /addUser
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

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }


    private boolean isNumberValid(String number){

        return number.matches("\\d+(?:\\.\\d+)?");
    }

    private boolean isPasswordValid(String password,String validation) {

        return password.length() > 8 || password.length()==8 && !password.isEmpty() && password==validation;
    }
    private boolean controll() {
        if (aziendaName.getText().toString() == "" || nomeTit.getText().toString() == "" ||
                cognomeTit.getText().toString() == "" || emailTit.getText().toString() == "" ||
                phoneText.getText().toString() == "" || ivaText.getText().toString() == "" ||
                !isPasswordValid(passText.getText().toString(), passConfirmText.getText().toString()) ||
                !isEmailValid(emailTit.getText().toString()) || !isNumberValid(ivaText.getText().toString())) {
            alert("Some incorrect data, please check");
            return false;
        }
        return true;

    }
    public void loadPhoto(View view){

            Intent iob = new Intent(Intent.ACTION_PICK,

                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(iob, 0);

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK && data!=null){

            Uri selectedImage = data.getData();

            try

            {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);
                img = bos.toByteArray();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void register(View view) {
        if(controll()){
            JSONObject user = new JSONObject();
            try {
                user.put("login_name", aziendaName.getText().toString() );
                user.put("passw_login", passText.getText().toString() );
                user.put("email", emailTit.getText().toString() );
                user.put("name", nomeTit.getText().toString() );
                user.put("second_name", cognomeTit.getText().toString() );
                user.put("is_enterprise",Integer.parseInt(ivaText.getText().toString()));
                user.put("enterprise_description","Phone: "+phoneText.getText().toString()+"\n"+ description.getText().toString());
                user.put("photo",img.toString());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            Rest.postRequest("http://foodadvisor.rane.pro:8080/addUser",user,requestQueue);
            alert("Operation complete");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }


    }

}
