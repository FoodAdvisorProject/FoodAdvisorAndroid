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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import static com.nostra13.universalimageloader.core.assist.QueueProcessingType.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnInfoWindowClickListener,OnMapReadyCallback {

    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

    private GoogleMap mMap;
    private String info;
    private String[][] coordinates;
    private Marker[] markerT;
    private Marker marker;
    private Hashtable<String, JSONObject> markers;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private JSONArray trip;
    public static JSONObject article;
    private GoogleApiClient client;
   private JSONObject creator=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Bundle b = getIntent().getExtras();
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        if (b != null)
            info = b.getString("info");

        try {
            JSONArray temp= new JSONArray(info);
            trip = new JSONArray();
            for (int i = temp.length()-1; i>=0; i--) {
                trip.put(temp.get(i));
            }
            getInfoArticle(trip.getJSONObject(0).getString("article_id"));
            SystemClock.sleep(4000);
            coordinates = new String[trip.length()][2];
           // Toast.makeText(getApplicationContext(),getInfoArticle(trip.getJSONObject(0).getString("article_id")), Toast.LENGTH_SHORT).show();
            for (int i = 0;i<trip.length();i++){
                coordinates[i][0] = trip.getJSONObject(i).getString(LATITUDE);
                coordinates[i][1] = trip.getJSONObject(i).getString(LONGITUDE);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initImageLoader();
        imageLoader = ImageLoader.getInstance();
        markers = new Hashtable<String, JSONObject>();
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
                        try {
                            MapsActivity.article=new JSONObject(new String(response.toString()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        Volley.newRequestQueue(getApplicationContext()).add(jsonObjReq);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Integer a;

        JSONObject buyer=null;
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
        mMap.setOnInfoWindowClickListener(this);
        View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
        TextView numTxt = (TextView) marker.findViewById(R.id.info_marker);
        markerT=new Marker[trip.length()];
        Polyline line;
       // Toast.makeText(getApplicationContext(),"INFO_ART 2 "+info_art, Toast.LENGTH_SHORT).show();
        try {
            buyer=trip.getJSONObject(0).getJSONObject("buyer");
            creator=trip.getJSONObject(0).getJSONObject("buyer");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            numTxt.setText("Start Point");
            markerT[0]=mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(coordinates[0][0]), Double.parseDouble(coordinates[0][1])))
                    .title("Creation of "+article.getString("name"))
                    .snippet("Description: "+article.getString("description")+"\n"+"Made by: "+creator.getString("name")+"\n"+"Company description: "+creator.getString("enterprise_description"))
                    .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker)))
                   // .anchor(0.2f, 0f)
            );
            markers.put(markerT[0].getId(),creator);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (a = 1; a <trip.length(); a++) {
            numTxt.setText("Point " + a.toString());

           try {
                markerT[a]=mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(coordinates[a][0]), Double.parseDouble(coordinates[a][1])))
                        .title("Transaction number "+a.toString()+" of "+article.getString("name"))
                        .snippet("Description: "+article.getString("description")+"\n"+"Made by: "+creator.getString("name")+"\n"+"Company description: "+creator.getString("enterprise_description"))
                        .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker)))
                );
              line = mMap.addPolyline(new PolylineOptions()
                       .add(new LatLng(Double.parseDouble(coordinates[a-1][0]), Double.parseDouble(coordinates[a-1][1])),new LatLng(Double.parseDouble(coordinates[a][0]), Double.parseDouble(coordinates[a][1])))
                       .width(5)
                       .color(Color.BLUE));

               buyer=trip.getJSONObject(a).getJSONObject("buyer");
               markers.put(markerT[a].getId(),buyer);
             //  Toast.makeText(getApplicationContext(),"partenza "+markers.get(markerT[a].getId()).toString(), Toast.LENGTH_SHORT).show();

           } catch (JSONException e) {
                e.printStackTrace();
           }
        }

       mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerT[0].getPosition(), 5));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);


    }
    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();
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

        client.connect();
    }

    @Override
    public void onStop() {
        super.onStop();

        client.disconnect();
    }
    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
             {

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

           String url = "http://foodadvisor.rane.pro:8080/getUserImage?user_id=";
            String urlCreator= null;
            try {
                urlCreator = "http://foodadvisor.rane.pro:8080/getUserImage?user_id="+creator.getString("user_id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String urlArticle= null;
            String id="1";
            JSONObject buyer=null;
            if (marker.getId() != null && markers != null && markers.size() > 0) {
                if ( markers.get(marker.getId()) != null &&
                        markers.get(marker.getId()) != null) {
                    try {
                        buyer=markers.get(marker.getId());
                        id =buyer.getString("user_id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            url=url+id;
            final ImageView image = ((ImageView) view.findViewById(R.id.badge));
            final ImageView imgSeller = ((ImageView) view.findViewById(R.id.seller));
           final ImageView imgCreator = ((ImageView) view.findViewById(R.id.creator));
            if (url != null && !url.equalsIgnoreCase("null")
                    && !url.equalsIgnoreCase("")) {
                //TODO:aggiunta della immagine del articolo
               /* imageLoader.displayImage(url, imgSeller, options,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri,
                                                          View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view,
                                        loadedImage);
                                getInfoContents(marker);

                            }
                        });*/
                imageLoader.displayImage(url, imgSeller, options,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri,
                                                          View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view,
                                        loadedImage);
                            }
                        });
                imageLoader.displayImage(urlCreator, imgCreator, options,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri,
                                                          View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view,
                                        loadedImage);
                                getInfoContents(marker);
                            }
                        });
                image.setImageResource(R.drawable.logo);
            } else {
                image.setImageResource(R.drawable.logo);
                imgSeller.setImageResource(R.drawable.logo);
                imgCreator.setImageResource(R.drawable.logo);
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
            final TextView info=((TextView) view.findViewById(R.id.txt_info));
            if (info != null) {
                try {
                    info.setText("\nActual Seller: "+buyer.getString("name")+"\nDescription: "+buyer.getString("enterprise_description"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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