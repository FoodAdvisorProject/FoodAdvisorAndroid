package pro.rane.foodadvisor;

import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import net.glxn.qrgen.android.QRCode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class PostActivity  extends AppCompatActivity {
    private String req;
    private TextView loadingText;
    private ProgressBar progressBar;
    private Button backButton;
    private ImageView qrcode;
    private final static int qrDisplayedSize = 1000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_splash_screen);
        String url = "";
        Bundle b = getIntent().getExtras();
        if (b != null){
            url = b.getString("url");
            req = b.getString("req");
        }
        loadingText = (TextView) findViewById(R.id.loadingText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar22);
        backButton = (Button) findViewById(R.id.backButton);
        qrcode = (ImageView) findViewById(R.id.qr_code);
        qrcode.setVisibility(View.INVISIBLE);
        backButton.setVisibility(View.INVISIBLE);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String requestBody = Utility.toCorrectCase(req);

        //Toast.makeText(this,requestBody, Toast.LENGTH_SHORT).show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("VOLLEY", response);
                Bitmap myBitmap = QRCode.from(response).bitmap();
                qrcode.setImageBitmap(myBitmap);
                ViewGroup.LayoutParams params = qrcode.getLayoutParams();
                params.width = qrDisplayedSize;
                params.height = qrDisplayedSize;
                qrcode.setLayoutParams(params);
                loadingText.setText(getString(R.string.prod_id_desc).concat(response));
                qrcode.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                saveToInternalStorage(myBitmap,response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VOLLEY", error.toString());
                new SweetAlertDialog(PostActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Errore comunicazione server")
                        .setContentText("Qualcosa non ha funzionato!")
                        .setConfirmText("Ok").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        finish();
                    }
                }).show();
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

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {

                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(stringRequest);
    }

    public void comeBack(View view){
        finish();
    }

    private void saveToInternalStorage(Bitmap bitmapImage,String name){

        ContextWrapper wrapper = new ContextWrapper(this.getApplicationContext());

        File file = wrapper.getDir("Images",MODE_PRIVATE);

        file = new File(file, name+".jpg");

        try{
            OutputStream stream;
            stream = new FileOutputStream(file);

            bitmapImage.compress(Bitmap.CompressFormat.JPEG,100,stream);
            stream.flush();
            stream.close();

        }catch (IOException e){
            e.printStackTrace();
        }
        MediaStore.Images.Media.insertImage(getContentResolver(), bitmapImage, "Id transazione:"+ name , "Created by FoodAdvisor");

        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("File Salvato!")
                .setContentText("Stampa dalla galleria il QRcode e mettilo sul tuo prodotto!")
                .setConfirmText("Ho capito").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
            }
        }).show();

    }


}
