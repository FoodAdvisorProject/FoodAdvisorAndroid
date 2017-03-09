package pro.rane.foodadvisor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
    private int timeout =2000;
    private String res;
    private String req;
    private static final String TAG = "Debug";

    private String toCorrectCase(String req){
        String ret;
        ret = req.replace(':','=').replace(',','&').replaceAll(Pattern.quote("{"),"").replaceAll(Pattern.quote("}"),"").replaceAll(Pattern.quote(""),"").replace("\"", "");
        Toast.makeText(getBaseContext(),ret, Toast.LENGTH_SHORT).show();
        return ret;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        String url = "";
        Bundle b = getIntent().getExtras();
        if (b != null){
            url = b.getString("url");
            req = b.getString("req");
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String requestBody = toCorrectCase(req);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("VOLLEY", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
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
                    res =responseString;
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        requestQueue.add(stringRequest);


        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                while(res==null){
                    try {
                        this.wait(timeout);
                    } catch (InterruptedException e) {
                        Log.e(TAG,"res = "+res);
                        e.printStackTrace();
                    }
                }
                // TODO: 09/03/2017 dove deve andare una volta evasa la richiesta?
                //Intent startActivity = new Intent();
               // startActivity(startActivity);
                //finish();
            }
        }, timeout);

    }



}
