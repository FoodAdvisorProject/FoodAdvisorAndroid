package pro.rane.foodadvisor;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


public class SplashScreen extends AppCompatActivity {
    private String scan_res;
    private int timeout =2000;
    private String tran_id;
    private String res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        Bundle b = getIntent().getExtras();
        if (b != null)
            scan_res = b.getString("qrCodeInformation");

        Log.d(this.getClass().getSimpleName() ,"SCAN_RES: "+scan_res);

        RequestQueue queue = Volley.newRequestQueue(this);

        tran_id = scan_res;

        String url = "http://foodadvisor.rane.pro:8080/getArticleTravel?tran_id=" + tran_id;


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
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
                // TODO: 01/03/2017 ripetere scannerizzazione qr in caso di valore di merda NB usare metodo goScanActivity
                goToMapsActivity(res);
                finish();
            }
        }, timeout);


    }

    private void goToMapsActivity(String coordinates){
        Intent startMapsActivity = new Intent(this, MapsActivity.class);
        startMapsActivity.putExtra("coordinates", coordinates);
        startActivity(startMapsActivity);
    }


    private void goScanActivity(){
        Intent startTrackActivity = new Intent(this,TrackActivity.class);
        startActivity(startTrackActivity);
    }


}
