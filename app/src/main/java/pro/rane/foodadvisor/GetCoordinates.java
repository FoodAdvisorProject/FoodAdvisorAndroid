package pro.rane.foodadvisor;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Andrea Lacava on 26/02/2017.
 */


//TODO Far funzionare la funzione doInBackground, una volta che quella funziona abbiamo svoltato
public class GetCoordinates extends AsyncTask<Void, Void, String[][]>  {

    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private String tran_id;

    public GetCoordinates(String tran_id){
        this.tran_id = tran_id;
    }

    @Override
    protected String[][] doInBackground(Void ...voids) {
        String dummy_tran_id = "1";

        String richiesta = "http://foodadvisor.rane.pro:8080/getArticleTravel?tran_id=" + tran_id;

        //TODO la libreria apache è deprecata, dobbiamo trovare un'alternativa migliore

        DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
        HttpGet request = new HttpGet(richiesta);
        // Depends on your web service
        request.setHeader("Content-type", "application/json");

        InputStream inputStream = null;
        String result = "";
        try {
            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            inputStream = entity.getContent();
            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            result = sb.toString();
        } catch (Exception e) {
            Log.i("Errore http request",""+e.getMessage());
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (Exception squish) {
                Log.i(squish.getMessage(), squish.getMessage());
            }
        }

        JSONArray jObject = null;
        String[][] coordinates;

        try {
            jObject = new JSONArray(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String[] latitude = new String[jObject.length()];
        String[] longitude = new String[jObject.length()];



        for (int i = 0; i < jObject.length(); i++) {
            try {
                latitude[i] = jObject.getJSONObject(i).getString(LATITUDE);
                longitude[i] = jObject.getJSONObject(i).getString(LONGITUDE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        coordinates = new String[latitude.length][longitude.length];
            for(int i =0; i < latitude.length;i++){
                coordinates[i][0] = latitude[i];
                coordinates[i][1] = longitude[i];
            }


       //String[][] dummy_coordinates = {{"45.465454", "9.186515999999983"}, {"41.9027835", "12.496365500000024"}};


       return coordinates;
        //return dummy_coordinates;
    }

    public interface OnFinishListener{
        void onFinish(String[][] result);
    }

    private OnFinishListener mListener;

    public OnFinishListener setOnFinishListener(OnFinishListener l){
        mListener = l;
        return l;
    }


    // This will get called automatically after doInBackground finishes
    @Override
    protected void onPostExecute(String[][] result) {
        // If listener is set
        if(mListener != null){
            mListener.onFinish(result); // Return the returnValue
        }
    }
}
