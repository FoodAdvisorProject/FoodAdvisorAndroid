package pro.rane.myapplication;

import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

    private GoogleMap mMap;
    private String info;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Bundle b = getIntent().getExtras();
        if (b != null)
            info = b.getString("qrCodeInformation");
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    /*connection to obtain the array  of positions*/
    private static String[][] getCoordinates(/*String tran_id  in attesa del lettoreQR*/) throws JSONException {

        String dummy_tran_id = "1";

        String richiesta = "http://foodadvisor.rane.pro:8080/getArticleTravel?tran_id=" + dummy_tran_id;

        DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
        HttpGet request = new HttpGet(richiesta);
        // Depends on your web service
        request.setHeader("Content-type", "application/json");

        InputStream inputStream = null;
        String result = null;
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
            // Oops
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (Exception squish) {
                Log.i(squish.getMessage(), squish.getMessage());
            }
        }

        JSONArray jObject;
        String[][] coordinates;

        jObject = new JSONArray(result);
        String[] latitude = new String[jObject.length()];
        String[] longitude = new String[jObject.length()];

        for (int i = 0; i < jObject.length(); i++) {

            latitude[i] = jObject.getJSONObject(i).getString(LATITUDE);
            longitude[i] = jObject.getJSONObject(i).getString(LONGITUDE);
        }

        /* Decommentare ad implementazione finita
        coordinates = new String[latitude.length][longitude.length];

            for(int i =0; i < latitude.length;i++){
                coordinates[i][0] = latitude[i];
                coordinates[i][1] = longitude[i];
            }
            */

        String[][] dummy_coordinates = {{"45.465454", "9.186515999999983"}, {"41.9027835", "12.496365500000024"}};

        //return coordinates;
        return dummy_coordinates;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        String[][] coordinates = new String[0][];
        try {
            coordinates = getCoordinates();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Integer a;
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(Double.parseDouble(coordinates[0][0]), Double.parseDouble(coordinates[0][1])))
                .title("Start point"));
        for (a = 0; a < coordinates.length; a++) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(coordinates[a][0]), Double.parseDouble(coordinates[a][1])))
                    .title("Point " + a.toString()));
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
