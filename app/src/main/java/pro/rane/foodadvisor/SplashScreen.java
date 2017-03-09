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

/*NOTA:quando viene invocato splashScreen bisogno inserire due put extra
uno contenente le info un l'altro contenente l'url a cui fare la richiesta.

es:
in Track Activity:
         Intent startSplashScreen = new Intent(this, SplashScreen.class);
         startSplashScreen.putExtra("info", "pro.rane.foodadvisor.MapsActivity");<--- si passo il nome del activity a cui passare poi le info come fosse una stringa
         startSplashScreen.putExtra("url", "http://foodadvisor.rane.pro:8080/getArticleTravel?tran_id="+codice);
         startActivity(startSplashScreen);
 in MapsActivity:
  Bundle b = getIntent().getExtras();
        if (b != null)
            info = b.getString("info"); <--- da qui lui prende la risposta che è stata data dal server
 */

public class SplashScreen extends AppCompatActivity {
    private String nameActivity;
    private int timeout =2000;
    private String res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        String url;
        Bundle b = getIntent().getExtras();
        if (b != null)
            nameActivity = b.getString("info");
            url = b.getString("url");
      //  Log.d(this.getClass().getSimpleName() ,"SCAN_RES: "+scan_res);

        RequestQueue queue = Volley.newRequestQueue(this);



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
                try {
                    goToActivity(res);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                finish();
            }
        }, timeout);


    }

    private void goToActivity(String info) throws ClassNotFoundException {
        Class<?> c = null;
        if(nameActivity != null) {
            try {
                c = Class.forName(nameActivity );
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        Intent startActivity = new Intent(this, c);
        startActivity.putExtra("info", info);
        startActivity(startActivity);
    }


 /*   non usato, quando si chiude la activity torna a quella precendete
 private void goScanActivity(){
        Intent startTrackActivity = new Intent(this,TrackActivity.class);
        startActivity(startTrackActivity);
    }
*/

}
