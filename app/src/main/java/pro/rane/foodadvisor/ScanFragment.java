package pro.rane.foodadvisor;


import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import net.glxn.qrgen.android.QRCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

import static android.content.Context.MODE_PRIVATE;


public class ScanFragment extends Fragment implements QRCodeReaderView.OnQRCodeReadListener {

    private static final int MY_PERMISSION_REQUEST_CAMERA = 0;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;


    private pro.rane.foodadvisor.SessionManager session;

    private ViewGroup cameraLayout;
    private ProgressBar pb;
    private Button newTranBtn;

    private TextView resultTextView;
    private QRCodeReaderView qrCodeReaderView;
    private CheckBox flashlightCheckBox;
    private PointsOverlayView pointsOverlayView;

    private String tran_id;
    private String buyer_id;
    private String seller_id;
    private String article_id;
    private float latitude = 0.0f;
    private float longitude = 0.0f;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private String codeQr = "";
    private Button saveBitmap;
    private ImageView imageQr;
    private Bitmap QR;


    public ScanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scan, container, false);

        session = new pro.rane.foodadvisor.SessionManager(getActivity().getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        buyer_id = user.get(SessionManager.KEY_ID);

        pb = (ProgressBar) rootView.findViewById(R.id.progrBar3);
        newTranBtn = (Button) rootView.findViewById(R.id.btnTran);
        cameraLayout = (ViewGroup) rootView.findViewById(R.id.camera_layout);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        saveBitmap = (Button) rootView.findViewById(R.id.saveBitmap);
        imageQr = (ImageView) rootView.findViewById(R.id.bitmapQr);

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            Toasty.error(getContext(),"Permessi non pervenuti",Toast.LENGTH_LONG).show();
        }
        Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if(loc!=null){
            longitude = (float) loc.getLongitude();
            latitude = (float) loc.getLatitude();
            Toasty.success(getContext(),"Coordinate settate",Toast.LENGTH_SHORT).show();
        }else{
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                pb.setVisibility(View.VISIBLE);
                locationListener = new MyLocationListener();
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000,1, locationListener);
            } else {
                new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Il GPS è spento")
                        .setContentText("FoodAdvisor ha bisogno del GPS per funzionanre correttamente!")
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

        pb.setVisibility(View.INVISIBLE);
        newTranBtn.setVisibility(View.INVISIBLE);


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            View content = inflater.inflate(R.layout.content_decoder, cameraLayout, true);
            qrCodeReaderView = (QRCodeReaderView) content.findViewById(R.id.qrdecoderview);
            resultTextView = (TextView) content.findViewById(R.id.result_text_view);
            flashlightCheckBox = (CheckBox) content.findViewById(R.id.flashlight_checkbox);
            pointsOverlayView = (PointsOverlayView) content.findViewById(R.id.points_overlay_view);

            qrCodeReaderView.setAutofocusInterval(2000L);
            qrCodeReaderView.setOnQRCodeReadListener(this);
            qrCodeReaderView.setBackCamera();
            flashlightCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    qrCodeReaderView.setTorchEnabled(isChecked);
                }
            });

            qrCodeReaderView.setQRDecodingEnabled(true);
            qrCodeReaderView.startCamera();

        } else {
            requestCameraPermission();
        }

        newTranBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latitude == 0.0f && longitude == 0.0f) {
                    Toast.makeText(getContext(), "Coordinate ancora non caricate, Attendere", Toast.LENGTH_SHORT).show();
                    return;
                }
                newTranBtn.setVisibility(View.INVISIBLE);
                pb.setVisibility(View.VISIBLE);
                tran_id = resultTextView.getText().toString();
                try {
                    getTransaction(tran_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        saveBitmap.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!codeQr.equals("error")) {
                    saveToInternalStorage(QR);
                }else{
                    Toasty.error(getContext(), "Salvataggio non avvenuto", Toast.LENGTH_SHORT).show();

                }

            }
        });


        return rootView;
    }

    private void getTransaction(String transaction_id) throws JSONException {
        final String url = "http://foodadvisor.rane.pro:8080/getTransaction?tran_id=";
        RequestQueue queue = Volley.newRequestQueue(getContext());
        final JSONObject[] getResponse = new JSONObject[1];

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url.concat(transaction_id), null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                //Debug
                //Toast.makeText(getContext(),response.toString(),Toast.LENGTH_LONG).show();
                getResponse[0] = response;
                try {
                    seller_id = getResponse[0].getString("buyer_id");
                    article_id = getResponse[0].getString("article_id");
                    //Toast.makeText(getContext(),"seller: "+seller_id,Toast.LENGTH_LONG).show();
                    addTransaction();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(getContext(),"Transazione non riconosciuta!",Toast.LENGTH_SHORT).show();

                qrCodeReaderView.setQRDecodingEnabled(true);
                cameraLayout.setVisibility(View.VISIBLE);
                pb.setVisibility(View.INVISIBLE);
                newTranBtn.setVisibility(View.VISIBLE);
            }
        });

        queue.add(jsonRequest);


    }


    private void addTransaction() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        final String url = "http://foodadvisor.rane.pro:8080/addTransaction";


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("VOLLEY", "OnResponse " + response);
                cameraLayout.setVisibility(View.INVISIBLE);

                if (response.contains("Error")) {
                    codeQr="error";
                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Errore")
                            .setContentText("Impossibile aggiungere la transazione.\n Descrizione errore: "+response)
                            .show();

                    qrCodeReaderView.setQRDecodingEnabled(true);
                    cameraLayout.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.INVISIBLE);
                    newTranBtn.setVisibility(View.VISIBLE);

                }else {

                    new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Transazione eseguita")
                            .setContentText("La transazione è stata eseguita!\nSalva il tuo qrcode, stampalo \n e mettilo sul prodotto!")
                            .show();

                    codeQr = response;
                    saveBitmap.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.INVISIBLE);
                    QR  = QRCode.from(codeQr).bitmap();
                    imageQr.setImageBitmap(QR);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Errore")
                        .setContentText("Impossibile aggiungere la transazione.\n Descrizione errore: "+error.toString())
                        .show();

                qrCodeReaderView.setQRDecodingEnabled(true);
                cameraLayout.setVisibility(View.VISIBLE);
                pb.setVisibility(View.INVISIBLE);
                newTranBtn.setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=utf-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> req = new HashMap<>();
                req.put("article_id", article_id);
                req.put("buyer_id", buyer_id);
                req.put("seller_id", seller_id);
                req.put("longitude", Float.toString(longitude));
                req.put("latitude", Float.toString(latitude));
                return req;
            }
        };
        queue.add(stringRequest);
    }


    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
            Snackbar.make(cameraLayout, "Per continuare sono necessari i permessi della fotocamera.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.CAMERA
                    }, MY_PERMISSION_REQUEST_CAMERA);
                }
            }).show();
        } else {
            Snackbar.make(cameraLayout, "Permessi non disponibili. Richiedo i permessi.",
                    Snackbar.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.CAMERA
            }, MY_PERMISSION_REQUEST_CAMERA);
        }
    }


    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        resultTextView.setText(text);
        pointsOverlayView.setPoints(points);
        newTranBtn.setVisibility(View.VISIBLE);
    }


    /*----------Listener class to get coordinates ------------- */
   private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {

            Toasty.success(getActivity().getBaseContext(),"Coordinate settate:\nLat: " +
                            loc.getLatitude()+ "\nLng: " + loc.getLongitude(),
                    Toast.LENGTH_SHORT).show();
            latitude = ((float) loc.getLatitude());
            longitude = ((float) loc.getLongitude());
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

    private void saveToInternalStorage(Bitmap bitmapImage){

        ContextWrapper wrapper = new ContextWrapper(getContext().getApplicationContext());

        File file = wrapper.getDir("Images",MODE_PRIVATE);

        file = new File(file, codeQr+".jpg");

        try{
            OutputStream stream;
            stream = new FileOutputStream(file);

            bitmapImage.compress(Bitmap.CompressFormat.JPEG,100,stream);
            stream.flush();
            stream.close();

        }catch (IOException e){
            e.printStackTrace();
        }
        MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmapImage, "Id transazione:"+ codeQr , "Created by FoodAdvisor");
        Toasty.success(getContext(), "File Salvato, controlla la galleria", Toast.LENGTH_LONG).show();
        saveBitmap.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permesso accordato, sta senza pensieri
                    Toasty.success(getContext(),"Permesso GPS accordato",Toast.LENGTH_SHORT).show();
                } else {
                    // permission denied, non possiamo disabilitare il GPS quindi dovrebbe continuare chiederlo
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }
                return;
            }
            case MY_PERMISSION_REQUEST_CAMERA:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toasty.success(getContext(),"Permesso fotocamera accordato",Toast.LENGTH_SHORT).show();
                } else {
                    // permission denied, non possiamo disabilitare il GPS quindi dovrebbe continuare chiederlo
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSION_REQUEST_CAMERA);
                }

            }
        }
    }

}
