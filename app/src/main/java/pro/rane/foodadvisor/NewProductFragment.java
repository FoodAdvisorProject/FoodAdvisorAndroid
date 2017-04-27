package pro.rane.foodadvisor;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationListener;
import android.provider.Settings;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;


public class NewProductFragment extends Fragment {

    pro.rane.foodadvisor.SessionManager session;
    float latitude = 0.0f;
    float longitude = 0.0f;
    private TextView txtLatLng;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private EditText editTextproductName;
    private EditText editTextproductDesc;

    private ProgressBar pb;
    private Button btnNewProduct;
    private Button btnPhoto;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private ImageView imgNewPrd;
    String photo = "null";

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION =1;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 2;
    // TODO: 26/04/2017 cambiare a 256 quando il server è fixato
    private static final int MAX_SIZE_IMG = 256;

    public NewProductFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_new_product, container, false);
        //if you want to lock screen for always Portrait mode
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        txtLatLng = (TextView) rootView.findViewById(R.id.coordinates);
        txtLatLng.setText(getString(R.string.latitude).concat(Float.toString(latitude)).concat(" ").concat(getString(R.string.longitude).concat(Float.toString(longitude))));


        session = new pro.rane.foodadvisor.SessionManager(getContext());

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        editTextproductName = (EditText) rootView.findViewById(R.id.prodName);
        editTextproductDesc = (EditText) rootView.findViewById(R.id.prodDesc);

        pb = (ProgressBar) rootView.findViewById(R.id.loadingBar);
        pb.setVisibility(View.INVISIBLE);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            Toasty.error(getContext(),"Permessi archiviazione non abilitati.\nControlla le impostazioni!",Toast.LENGTH_LONG).show();


        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            //Toasty.error(getContext(),"Permessi non pervenuti",Toast.LENGTH_LONG).show();
        }
        Location loc;
        try {
            loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }catch (Exception e){
            loc = null;
            e.printStackTrace();
        }

        if(loc!=null){
            longitude = (float) loc.getLongitude();
            latitude = (float) loc.getLatitude();
            txtLatLng.setText(getString(R.string.latitude).concat(Float.toString(latitude)).concat(" ").concat(getString(R.string.longitude).concat(Float.toString(longitude))));
        }else{
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                pb.setVisibility(View.VISIBLE);
                locationListener = new MyLocationListener();
                try {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }catch (SecurityException e){
                    new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Mancano i permessi")
                            .setContentText("FoodAdvisor ha bisogno del GPS per funzionare correttamente!")
                            .setConfirmText("Ho capito!").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    }).show();
                }
            } else {
                new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Il GPS è spento")
                        .setContentText("FoodAdvisor ha bisogno del GPS per funzionare correttamente!")
                        .setConfirmText("Ho capito!").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                }).show();
            }
        }

        imgNewPrd = (ImageView) rootView.findViewById(R.id.imageLoaded);


        editTextproductName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Utility.hideKeyboard(v);
                }
            }
        });

        editTextproductDesc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Utility.hideKeyboard(v);
                }
            }
        });




        btnPhoto = (Button) rootView.findViewById(R.id.btnPhoto);
        btnPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Per proseguire è necessario avere i permessi della fotocamera!")
                            .setConfirmText("Ho capito!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    ActivityCompat.requestPermissions(getActivity(),
                                            new String[]{Manifest.permission.CAMERA},
                                            MY_PERMISSIONS_REQUEST_CAMERA);
                                }
                            })
                            .show();
                }else{
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
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

                if (latitude == 0.0f && longitude == 0.0f){
                    Toasty.warning(getActivity().getApplicationContext(),"Attendere valore coordinate",Toast.LENGTH_SHORT).show();
                    return;
                }

                btnNewProduct.setVisibility(View.INVISIBLE);
                pb.setVisibility(View.VISIBLE);


                String productName = editTextproductName.getText().toString();
                String productDesc = editTextproductDesc.getText().toString();
                HashMap<String, String> user = session.getUserDetails();
                String id = user.get(SessionManager.KEY_ID);

                final String url = "http://foodadvisor.rane.pro:8080/addArticle";

                //Toast.makeText(getActivity().getBaseContext(),"Url : "+url,Toast.LENGTH_SHORT).show();

                JSONObject req = new JSONObject();
                try{
                    req.put("name",productName);
                    req.put("creator_id",id);
                    req.put("description",productDesc);
                    req.put("longitude",longitude);
                    req.put("latitude",latitude);
                    req.put("photo",photo);
                }catch (JSONException e){
                    e.printStackTrace();
                }


                pb.setVisibility(View.INVISIBLE);
                btnNewProduct.setVisibility(View.VISIBLE);

                Intent startPostAct= new Intent(getActivity(), PostActivity.class);
                startPostAct.putExtra("url", url);
                startPostAct.putExtra("req",req.toString());
                startActivity(startPostAct);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap src = (Bitmap) extras.get("data");
            Bitmap imageBitmap = Bitmap.createScaledBitmap(src,MAX_SIZE_IMG,MAX_SIZE_IMG,false);
            photo = Utility.BitMapToString(imageBitmap);
            imgNewPrd.setImageBitmap(src);
            imgNewPrd.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Permessi accordati")
                            .setContentText("Grazie mille!")
                            .setConfirmText("Ho capito!").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    }).show();

                } else {
                    // permission denied, non possiamo disabilitare il GPS quindi dovrebbe continuare chiederlo
                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Errore!")
                            .setContentText("L'applicazione non può funzionare senza GPS!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    ActivityCompat.requestPermissions(getActivity(),
                                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                                }
                            })
                            .show();
                }
            }
        }
    }


    /*----------Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            pb.setVisibility(View.INVISIBLE);
            longitude = (float) loc.getLongitude();
            latitude = (float) loc.getLatitude();
            txtLatLng.setText(getString(R.string.latitude).concat(Float.toString(latitude)).concat(" ").concat(getString(R.string.longitude).concat(Float.toString(longitude))));
        }

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
