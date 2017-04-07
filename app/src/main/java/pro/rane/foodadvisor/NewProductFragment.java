package pro.rane.foodadvisor;


import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.content.ContentResolver;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationListener;
import android.provider.Settings;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;


public class NewProductFragment extends Fragment{

    pro.rane.foodadvisor.SessionManager session;
    float latitude = 0.0f;
    float longitude = 0.0f;
    private TextView txtLat;
    private TextView txtLng;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private EditText editTextproductName;
    private EditText editTextproductDesc;

    private Button btnGetLocation = null;

    private ProgressBar pb = null;

    private static final String TAG = "Debug";
    private Boolean flag = false;
    private Button btnNewProduct;


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

        session = new pro.rane.foodadvisor.SessionManager(getContext());

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        pb = (ProgressBar) rootView.findViewById(R.id.loadingBar);
        pb.setVisibility(View.INVISIBLE);

        editTextproductName = (EditText) rootView.findViewById(R.id.prodName);
        editTextproductDesc = (EditText) rootView.findViewById(R.id.prodDesc);

        txtLat = (TextView) rootView.findViewById(R.id.latitude);
        txtLng = (TextView) rootView.findViewById(R.id.longitude);
        txtLat.setText(getString(R.string.latitude).concat(Float.toString(latitude)));
        txtLng.setText(getString(R.string.longitude).concat(Float.toString(longitude)));

        btnGetLocation = (Button) rootView.findViewById(R.id.btnLocation);
        btnGetLocation.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    flag = displayGpsStatus();
                    if (flag) {

                        //Log.w(TAG, "onClick");
                        pb.setVisibility(View.VISIBLE);
                        btnGetLocation.setVisibility(View.INVISIBLE);
                        locationListener = new MyLocationListener();
                        // TODO: 07/04/17 testare con 0 0 (valori iniziali 1000 5)
                        try {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, locationListener);
                        }catch (SecurityException e){
                            e.printStackTrace();
                        }
                    } else {
                        Utility.alert(getContext(), "Il GPS è spento!\nAccendi il GPS per continuare");
                    }
                }
            });

        btnNewProduct = (Button) rootView.findViewById(R.id.new_product_button);
        btnNewProduct.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(editTextproductName.getText().toString())){
                    editTextproductName.setError("Campo obbligatorio");
                    return;
                }

                if (TextUtils.isEmpty(editTextproductDesc.getText().toString())){
                    editTextproductDesc.setError("Campo obbligatorio");
                    return;
                }

                if (latitude== 0.0f && longitude == 0.0f){
                    Toast.makeText(getActivity().getApplicationContext(),"Attendere valore coordinate",Toast.LENGTH_SHORT).show();
                    return;
                }

                btnNewProduct.setVisibility(View.INVISIBLE);
                btnGetLocation.setVisibility(View.INVISIBLE);
                pb.setVisibility(View.VISIBLE);


                String productName = editTextproductName.getText().toString();
                String productDesc = editTextproductDesc.getText().toString();
                HashMap<String, String> user = session.getUserDetails();
                String id = user.get(SessionManager.KEY_ID);

                String url = "http://foodadvisor.rane.pro:8080/addArticle";

                Toast.makeText(getActivity().getBaseContext(),"Url : "+url,Toast.LENGTH_SHORT).show();
                // TODO: 07/04/17 perchè sti due secondi di stop?
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
                    req.put("longitude",longitude);
                    req.put("latitude",latitude);
                    req.put("photo","null");
                }catch (JSONException e){
                    e.printStackTrace();
                }

                //Toast.makeText(getBaseContext(),req.toString(),Toast.LENGTH_SHORT).show();

                pb.setVisibility(View.INVISIBLE);
                btnNewProduct.setVisibility(View.VISIBLE);
                btnGetLocation.setVisibility(View.VISIBLE);

                Intent startPostAct= new Intent(getActivity(), PostActivity.class);
                startPostAct.putExtra("url", url);
                startPostAct.putExtra("req",req.toString());
                startActivity(startPostAct);

            }
        });


        return rootView;
    }

    /*----Method to Check GPS is enable or disable ----- */
    private Boolean displayGpsStatus() {
        ContentResolver contentResolver = getActivity().getBaseContext()
                .getContentResolver();
        return Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);
    }



    /*----------Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            pb.setVisibility(View.INVISIBLE);
            btnGetLocation.setVisibility(View.VISIBLE);
            /*Toast.makeText(getActivity().getBaseContext(),"Coordinate settate:\nLat: " +
                            loc.getLatitude()+ "\nLng: " + loc.getLongitude(),
                    Toast.LENGTH_SHORT).show();*/
            longitude = (float) loc.getLongitude();
            latitude = (float) loc.getLatitude();
            txtLat.setText(getString(R.string.latitude).concat(Float.toString(latitude)));
            txtLng.setText(getString(R.string.longitude).concat(Float.toString(longitude)));
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
