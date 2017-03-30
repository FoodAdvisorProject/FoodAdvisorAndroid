package pro.rane.foodadvisor;


import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


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


public class NewProductFragment extends Fragment{
    protected Context context;

    SessionManager session;
    double latitude = 0.0f;
    double longitude = 0.0f;
    TextView txtLat;
    TextView txtLng;

    private LocationManager locationManager = null;
    private LocationListener locationListener = null;

    private Button btnGetLocation = null;

    private ProgressBar pb = null;

    private static final String TAG = "Debug";
    private Boolean flag = false;

    public NewProductFragment(){
        //must be empty
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_new_product, container, false);
        //if you want to lock screen for always Portrait mode
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        pb = (ProgressBar) rootView.findViewById(R.id.loadingBar);
        pb.setVisibility(View.INVISIBLE);

        txtLat = (TextView) rootView.findViewById(R.id.latitude);
        txtLng = (TextView) rootView.findViewById(R.id.longitude);

        btnGetLocation = (Button) rootView.findViewById(R.id.btnLocation);
        btnGetLocation.setOnClickListener(this);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);


        return rootView;
    }


    @Override
    public void onClick(View v) {
        flag = displayGpsStatus();
        if (flag) {

            Log.e(TAG, "onClick");

            pb.setVisibility(View.VISIBLE);
            locationListener = new MyLocationListener();

            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2500, 5, locationListener);
            }catch (SecurityException e){
                e.printStackTrace();
            }


        } else {
            Utility.alert("GPS spento!", "Il GPS è spento!\nAccendi il GPS per continuare");
        }

    }

    public void getValues(View v){
        if (latitude== 0.0f && longitude == 0.0f) return;
        float floatlat = (float)latitude;
        float floatlng = (float) longitude;
        EditText editTextproductName = (EditText) getActivity().findViewById(R.id.productName);
        EditText editTextproductDesc = (EditText) getActivity().findViewById(R.id.productDesc);
        String productName = editTextproductName.getText().toString();
        String productDesc = editTextproductDesc.getText().toString();
        HashMap<String, String> user = session.getUserDetails();
        String id = user.get(SessionManager.KEY_ID);

        String url = "http://foodadvisor.rane.pro:8080/addArticle";

        Toast.makeText(getActivity().getBaseContext(),"Url : "+url,Toast.LENGTH_SHORT).show();

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

        Intent startPostAct= new Intent(this.getActivity(), PostActivity.class);
        startPostAct.putExtra("url", url);
        startPostAct.putExtra("req",req.toString());
        startActivity(startPostAct);
        //this.getActivity().finish();

    }

    /*----Method to Check GPS is enable or disable ----- */
    private Boolean displayGpsStatus() {
        ContentResolver contentResolver = getActivity().getBaseContext()
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



    /*----------Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            pb.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity().getBaseContext(),"Coordinate settate:\nLat: " +
                            loc.getLatitude()+ "\nLng: " + loc.getLongitude(),
                    Toast.LENGTH_SHORT).show();
            longitude = loc.getLongitude();
            latitude = loc.getLatitude();
            txtLat.setText("Latitude: " +latitude);
            txtLng.setText("Longitude: " +longitude);
        }

        //Non necessarie ai nostri fini
        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {
        }

    }

}
