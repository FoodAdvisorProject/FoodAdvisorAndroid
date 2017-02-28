package pro.rane.foodadvisor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONArray;
import org.json.JSONException;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

    private GoogleMap mMap;
    private String info;
    private String[][] coordinates;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Bundle b = getIntent().getExtras();
        if (b != null)
            info = b.getString("coordinates");

        try {
            JSONArray trip = new JSONArray(info);
            coordinates = new String[trip.length()][2];
            for (int i = 0;i<trip.length();i++){
                coordinates[i][0] = trip.getJSONObject(i).getString(LATITUDE);
                coordinates[i][1] = trip.getJSONObject(i).getString(LONGITUDE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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


    @Override
    public void onMapReady(GoogleMap googleMap) {

        //String[][] dummy_coordinates  = {{"45.465454", "9.186515999999983"}, {"41.9027835", "12.496365500000024"},{"40.9027835", "15.496365500000024"}};


        Integer a;
        mMap = googleMap;
        View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
        TextView numTxt = (TextView) marker.findViewById(R.id.num_txt);

        for (a = 0; a < coordinates.length; a++) {

            if(a==0){
                numTxt.setText("Go");
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(coordinates[a][0]), Double.parseDouble(coordinates[a][1])))
                        .title("GO")
                        .snippet("Start point "+ a.toString())
                        .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker)))
                );
            }else {
                numTxt.setText(a.toString());
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(coordinates[a][0]), Double.parseDouble(coordinates[a][1])))
                        .title(a.toString())
                        .snippet("Arrival point " + a.toString())
                        .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker)))
                );
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(coordinates[a][0]), Double.parseDouble(coordinates[a][1])),5));
        }



    }
    // Convert a view to bitmap
    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, ViewPager.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }
}
