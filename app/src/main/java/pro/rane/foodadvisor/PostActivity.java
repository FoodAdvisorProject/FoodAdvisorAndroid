package pro.rane.foodadvisor;

import android.graphics.Bitmap;
import android.os.Bundle;
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
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import net.glxn.qrgen.android.QRCode;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class PostActivity  extends AppCompatActivity {
    private String req;
    private static final String TAG = "Debug";
    private TextView loadingText;
    private ProgressBar progressBar;
    private Button backButton;
    private ImageView qrcode;
    private final static int qrDisplayedSize = 1000;

    // TODO: 13/04/2017 il QRCode va salvato all'interno della memoria 

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
        //Decommentare solo per il debug
        //Toast.makeText(getBaseContext(),requestBody, Toast.LENGTH_SHORT).show();
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
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VOLLEY", error.toString());
                new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Errore comunicazione server")
                        .setContentText("Qualcosa non ha funzionato!\nDescrizione errore:".concat(error.toString()))
                        .show();
                finish();
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

                if(response.statusCode==400){
                    new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Errore comunicazione server")
                            .setContentText("Qualcosa non ha funzionato!\nCodice errore:"+response.statusCode)
                            .show();
                    finish();
                }
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(stringRequest);
    }

    public void comeBack(View view){
        finish();
    }


}
