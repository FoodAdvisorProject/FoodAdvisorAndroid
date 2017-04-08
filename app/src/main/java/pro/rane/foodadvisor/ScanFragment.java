package pro.rane.foodadvisor;


import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
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

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.google.android.gms.location.LocationServices;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;

import net.glxn.qrgen.android.QRCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class ScanFragment extends Fragment implements QRCodeReaderView.OnQRCodeReadListener {

    private static final int MY_PERMISSION_REQUEST_CAMERA = 0;


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
        locationListener = new MyLocationListener();
        saveBitmap = (Button) rootView.findViewById(R.id.saveBitmap);
        imageQr = (ImageView) rootView.findViewById(R.id.bitmapQr);


        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
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
                //   getLastBestLocation();
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
                    Toast.makeText(getContext(), "Salvataggio avvenuto, controllare nelle immagini", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), "ERROR Salvataggio non avvenuto", Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(getContext(),"seller: "+seller_id,Toast.LENGTH_LONG).show();
                    addTransaction();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //solo per debug
                //Toast.makeText(getContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsonRequest);


    }

    // TODO: 05/04/2017 finire di implementare
    private void addTransaction() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        final String url = "http://foodadvisor.rane.pro:8080/addTransaction?";


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("VOLLEY", "OnResponse " + response);
                cameraLayout.setVisibility(View.INVISIBLE);

                if (response.contains("Error")) {
                    codeQr="error";
                    Toast.makeText(getContext(), "risposta server " + response.toString(), Toast.LENGTH_LONG).show();
                    Toast.makeText(getContext(), "Impossibile aggiungere transazione" + response.toString(), Toast.LENGTH_LONG).show();

                }else {
                    codeQr = response;
                    saveBitmap.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.INVISIBLE);
                    try {
                        QR = generateQrCode(codeQr);
                        imageQr.setImageBitmap(QR);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
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
        qrCodeReaderView.stopCamera();
    }


  /*  private void getLastBestLocation() {

            latitude =(float)45.465454;
            longitude =(float)9.186515999999983;

    }*/
    /*----------Listener class to get coordinates ------------- */
   private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {

            Toast.makeText(getActivity().getBaseContext(),"Coordinate settate:\nLat: " +
                            loc.getLatitude()+ "\nLng: " + loc.getLongitude(),
                    Toast.LENGTH_SHORT).show();
            latitude = ((float) loc.getLatitude());
            longitude = ((float) loc.getLongitude());
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

    private void saveToInternalStorage(Bitmap bitmapImage){


        ContextWrapper wrapper = new ContextWrapper(getContext().getApplicationContext());

        File file = wrapper.getDir("Images",MODE_PRIVATE);

        file = new File(file, codeQr+".jpg");

        try{

            OutputStream stream = null;
            stream = new FileOutputStream(file);

            bitmapImage.compress(Bitmap.CompressFormat.JPEG,100,stream);
            stream.flush();
            stream.close();

        }catch (IOException e) // Catch the exception
        {
            e.printStackTrace();
        }

        // Parse the gallery image url to uri
        Uri savedImageURI = Uri.parse(file.getAbsolutePath());
        Toast.makeText(getContext(), "File Salvato in: " + savedImageURI.toString(), Toast.LENGTH_LONG).show();


    }

    public static Bitmap generateQrCode(String myCodeText) throws WriterException {
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // H = 30% damage

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        int size = 256;

        BitMatrix bitMatrix = qrCodeWriter.encode(myCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);
        int width = bitMatrix.getWidth();
        Bitmap bmp = Bitmap.createBitmap(width, width, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < width; y++) {
                bmp.setPixel(y, x, bitMatrix.get(x,y)==false ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }
}
