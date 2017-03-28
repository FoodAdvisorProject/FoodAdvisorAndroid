package pro.rane.foodadvisor;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.util.DisplayMetrics;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import static com.nostra13.universalimageloader.core.assist.QueueProcessingType.*;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

    private GoogleMap mMap;
    private String info;
    private String[][] coordinates;
    private Marker[] markerT;
    private Marker marker;
    private Hashtable<String, String> markers;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private JSONArray trip;
    private JSONObject article;
    public static String info_art;
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
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        if (b != null)
            info = b.getString("info");

        try {
            trip = new JSONArray(info);
            getInfoArticle(trip.getJSONObject(0).getString("article_id"));
            SystemClock.sleep(4000);

            coordinates = new String[trip.length()][2];
           // Toast.makeText(getApplicationContext(),getInfoArticle(trip.getJSONObject(0).getString("article_id")), Toast.LENGTH_SHORT).show();
            Integer n=trip.length()-1;
            for (int i = 0;i<trip.length();i++){
                coordinates[n-i][0] = trip.getJSONObject(i).getString(LATITUDE);
                coordinates[n-i][1] = trip.getJSONObject(i).getString(LONGITUDE);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initImageLoader();
        imageLoader = ImageLoader.getInstance();
        markers = new Hashtable<String, String>();
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.logo)        //    Display Stub Image
                .showImageForEmptyUri(R.drawable.logo)    //    If Empty image found
                .cacheInMemory()
                .cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }
    public void getInfoArticle(String id){

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET, "http://foodadvisor.rane.pro:8080/getArticle?article_id="+id, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                      //  Toast.makeText(getApplicationContext(),"Response "+response.toString(), Toast.LENGTH_SHORT).show();
                        MapsActivity.info_art= new String(response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        Volley.newRequestQueue(getApplicationContext()).add(jsonObjReq);
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
        Integer a;
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
        TextView numTxt = (TextView) marker.findViewById(R.id.num_txt);
        markerT=new Marker[trip.length()];

        Toast.makeText(getApplicationContext(),"INFO_ART 2 "+info_art, Toast.LENGTH_SHORT).show();
        try {
            article=new JSONObject(info_art);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            numTxt.setText("Start Point");
            markerT[0]=mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(coordinates[0][0]), Double.parseDouble(coordinates[0][1])))
                    .title("Creation of "+article.getString("name"))
                    .snippet("Description: "+article.getString("description"))
                    .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker)))
            );
            markers.put(markerT[0].getId(),"https://is5-ssl.mzstatic.com/image/thumb/Purple18/v4/03/41/a2/0341a26c-b318-ac3d-94e5-c8213036bd7d/source/256x256bb.jpg");//article.getString("photo")
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (a = 1; a <trip.length(); a++) {
            numTxt.setText("Point " + a.toString());

           try {
                markerT[a]=mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(coordinates[a][0]), Double.parseDouble(coordinates[a][1])))
                        .title("Transaction of "+article.getString("name"))
                        .snippet("Description: "+article.getString("description"))
                        .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker)))
                );
               markers.put(markerT[a].getId(),"https://is5-ssl.mzstatic.com/image/thumb/Purple18/v4/03/41/a2/0341a26c-b318-ac3d-94e5-c8213036bd7d/source/256x256bb.jpg");//article.getString("photo")

           } catch (JSONException e) {
                e.printStackTrace();
           }
        }

       mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerT[0].getPosition(), 15));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);


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
    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private View view;

        public CustomInfoWindowAdapter() {
            view = getLayoutInflater().inflate(R.layout.custom_info_window,
                    null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            if (MapsActivity.this.marker != null
                    && MapsActivity.this.marker.isInfoWindowShown()) {
                MapsActivity.this.marker.hideInfoWindow();
                MapsActivity.this.marker.showInfoWindow();
            }
            return null;
        }

        @Override
        public View getInfoWindow(final Marker marker) {
            MapsActivity.this.marker = marker;

            String url = null;

            if (marker.getId() != null && markers != null && markers.size() > 0) {
                if ( markers.get(marker.getId()) != null &&
                        markers.get(marker.getId()) != null) {
                    url = markers.get(marker.getId());
                }
            }
            final ImageView image = ((ImageView) view.findViewById(R.id.badge));

            if (url != null && !url.equalsIgnoreCase("null")
                    && !url.equalsIgnoreCase("")) {
                imageLoader.displayImage(url, image, options,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri,
                                                          View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view,
                                        loadedImage);
                                getInfoContents(marker);
                            }
                        });
            } else {
                image.setImageResource(R.drawable.logo);
            }

            final String title = marker.getTitle();
            final TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                titleUi.setText(title);
            } else {
                titleUi.setText("");
            }

            final String snippet = marker.getSnippet();
            final TextView snippetUi = ((TextView) view
                    .findViewById(R.id.snippet));
            if (snippet != null) {
                snippetUi.setText(snippet);
            } else {
                snippetUi.setText("");
            }

            return view;
        }
    }
    private void initImageLoader() {
        int memoryCacheSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            int memClass = ((ActivityManager)
                    getSystemService(Context.ACTIVITY_SERVICE))
                    .getMemoryClass();
            memoryCacheSize = (memClass / 8) * 1024 * 1024;
        } else {
            memoryCacheSize = 2 * 1024 * 1024;
        }

        final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this).threadPoolSize(5)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCacheSize(memoryCacheSize)
                .memoryCache(new FIFOLimitedMemoryCache(memoryCacheSize-1000000))
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(LIFO)
                .build();

        ImageLoader.getInstance().init(config);
    }
}