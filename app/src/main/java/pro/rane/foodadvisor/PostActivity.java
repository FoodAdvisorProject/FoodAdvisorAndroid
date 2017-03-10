package pro.rane.foodadvisor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

/**
 * Created by Andrea on 09/03/2017.
 */


public class PostActivity  extends AppCompatActivity {
    private String req;
    private static final String TAG = "Debug";
    private TextView loadingText;
    private ProgressBar progressBar;
    private Button backButton;

    private String toCorrectCase(String req){
        String ret;
        ret = req.replace(':','=').replace(',','&').replaceAll(Pattern.quote("{"),"").replaceAll(Pattern.quote("}"),"").replaceAll(Pattern.quote(""),"").replace("\"", "").replace(" ","_");
        Toast.makeText(getBaseContext(),ret, Toast.LENGTH_SHORT).show();
        return ret;
    }

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
        backButton.setVisibility(View.INVISIBLE);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String requestBody = toCorrectCase(req);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("VOLLEY", response);
                loadingText.setText(R.string.ok_response);
                backButton.setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
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
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        requestQueue.add(stringRequest);

        // TODO: 09/03/2017 dove deve andare una volta evasa la richiesta?
        //Intent startActivity = new Intent();
        // startActivity(startActivity);
        //finish();
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void comeBack(View view){
        finish();
    }


}
