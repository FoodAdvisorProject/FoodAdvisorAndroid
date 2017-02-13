package pro.rane.myapplication;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Bundle b = getIntent().getExtras();
        if(b != null)
            info = b.getString("qrCodeInformation");
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
    private static String[][] getCoordinates(){
        String resive="";
        /*use string info for get request*/
        // Create http cliient object to send request to server
        HttpClient Client = new DefaultHttpClient();
        // Create URL string
        String URL = "qui va http://... seguito dalla richiesta con il codice" ;
        //Log.i("httpget", URL);
        try
        {
            String SetServerString = "";

            // Create Request to server and get response

            HttpGet httpget = new HttpGet(URL);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            SetServerString = Client.execute(httpget, responseHandler);

            // Show response on activity

            resive=SetServerString;
        }
        catch(Exception ex)
        {
            Log.i("Fail!","Fail!");
        }
        /*conversion from resive to two dimentional array string*/
        /*{latitudine, longitudine}*/

        String [][] coordinates={{"45.465454","9.186515999999983"},{"41.9027835","12.496365500000024"}};
        return coordinates;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        String[][] coordinates=getCoordinates();
        Integer a;
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(Double.parseDouble(coordinates[0][0]), Double.parseDouble(coordinates[0][1])))
                .title("Start point"));
        for(a = 0; a < coordinates.length; a++){
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(coordinates[a][0]), Double.parseDouble(coordinates[a][1])))
                    .title("Point " + a.toString()));
        }

    }
}
