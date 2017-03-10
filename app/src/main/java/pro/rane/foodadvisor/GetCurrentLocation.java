package pro.rane.foodadvisor;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class GetCurrentLocation extends Activity implements OnClickListener {
    SessionManager session;
    double latitude = 1.0f;
    double longitude = 1.0f;
    TextView txtLat;
    TextView txtLng;

    private LocationManager locationMangaer = null;
    private LocationListener locationListener = null;

    private Button btnGetLocation = null;

    private ProgressBar pb = null;

    private static final String TAG = "Debug";
    private Boolean flag = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_location);
        session = new SessionManager(getApplicationContext());


        //if you want to lock screen for always Portrait mode
        setRequestedOrientation(ActivityInfo
                .SCREEN_ORIENTATION_PORTRAIT);

        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);

        txtLat = (TextView) findViewById(R.id.latitude);
        txtLng = (TextView) findViewById(R.id.longitude);


        btnGetLocation = (Button) findViewById(R.id.btnLocation);
        btnGetLocation.setOnClickListener(this);

        locationMangaer = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onClick(View v) {
        flag = displayGpsStatus();
        if (flag) {

            Log.e(TAG, "onClick");

            pb.setVisibility(View.VISIBLE);
            locationListener = new MyLocationListener();

            try {
                locationMangaer.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            }catch (SecurityException e){
                e.printStackTrace();
            }


        } else {
            alertbox("Gps Status!!", "Your GPS is: OFF");
        }

    }

    public void getValues(View v){
        if (latitude== 0.0f && longitude == 0.0f) return;
        float floatlat = (float)latitude;
        float floatlng = (float) longitude;
        EditText editTextproductName = (EditText) findViewById(R.id.productName);
        EditText editTextproductDesc = (EditText) findViewById(R.id.productDesc);
        String productName = editTextproductName.getText().toString();
        String productDesc = editTextproductDesc.getText().toString();
        HashMap<String, String> user = session.getUserDetails();
        String id = user.get(SessionManager.KEY_ID);

        String url = "http://foodadvisor.rane.pro:8080/addArticle";

       Toast.makeText(getBaseContext(),"Url : "+url,Toast.LENGTH_SHORT).show();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONObject req = new JSONObject();
        try{
            req.put("name",productName);
            req.put("creator_id",id);
            req.put("description",productDesc);
            req.put("longitude",floatlng);
            req.put("latitude",floatlat);
            req.put("photo","null");
        }catch (JSONException e){
            e.printStackTrace();
        }

        //Toast.makeText(getBaseContext(),req.toString(),Toast.LENGTH_SHORT).show();
        // TODO: 10/03/2017 i beneamati permessi e la permission request 
        // TODO: 09/03/2017 per ora esiste post activity, più in là esisterà una cosa migliore
        Intent startPostAct= new Intent(this, PostActivity.class);
        startPostAct.putExtra("url", url);
        startPostAct.putExtra("req",req.toString());
        startActivity(startPostAct);
        finish();

    }

    /*----Method to Check GPS is enable or disable ----- */
    private Boolean displayGpsStatus() {
        ContentResolver contentResolver = getBaseContext()
                .getContentResolver();
        boolean gpsStatus = Settings.Secure
                .isLocationProviderEnabled(contentResolver,
                        LocationManager.GPS_PROVIDER);
        if (gpsStatus) {
            return true;

        } else {
            return false;
        }
    }

    /*----------Method to create an AlertBox ------------- */
    protected void alertbox(String title, String mymessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your Device's GPS is Disable")
                .setCancelable(false)
                .setTitle("** Gps Status **")
                .setPositiveButton("Gps On",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // finish the current activity
                                // AlertBoxAdvance.this.finish();
                                Intent myIntent = new Intent(
                                        Settings.ACTION_SECURITY_SETTINGS);
                                startActivity(myIntent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel the dialog box
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /*----------Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            pb.setVisibility(View.INVISIBLE);
            Toast.makeText(getBaseContext(),"Coordinate settate:\nLat: " +
                            loc.getLatitude()+ "\nLng: " + loc.getLongitude(),
                    Toast.LENGTH_SHORT).show();
            longitude = loc.getLongitude();
            latitude = loc.getLatitude();
            txtLat.setText("Latitude: " +latitude);
            txtLng.setText("Longitude: " +longitude);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    }
}
