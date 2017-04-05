package pro.rane.foodadvisor;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;


public class ScanFragment extends Fragment implements QRCodeReaderView.OnQRCodeReadListener{

    private ViewGroup cameraLayout;
    private ProgressBar pb;
    private Button tran;

    private TextView resultTextView;
    private QRCodeReaderView qrCodeReaderView;
    private CheckBox flashlightCheckBox;
    private PointsOverlayView pointsOverlayView;


    private float latitude;
    private float longitude;


    private static final int MY_PERMISSION_REQUEST_CAMERA = 0;

    // TODO: 05/04/2017 get /getTransaction
    // TODO: 05/04/2017 post /addTransaction
    // TODO: 05/04/2017 Coordinate GPS prendere
    // TODO: 05/04/2017 getTransaction prende article_id e seller_id, buyer_id si ricava dal SessionManager, le coordinate dal GPS

    public ScanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scan, container, false);

        pb = (ProgressBar) rootView.findViewById(R.id.progrBar3);
        tran = (Button) rootView.findViewById(R.id.btnTran);

        cameraLayout = (ViewGroup) rootView.findViewById(R.id.camera_layout);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            View content =  inflater.inflate(R.layout.content_decoder, cameraLayout, true);
            qrCodeReaderView = (QRCodeReaderView) content.findViewById(R.id.qrdecoderview);
            resultTextView = (TextView) content.findViewById(R.id.result_text_view);
            flashlightCheckBox = (CheckBox) content.findViewById(R.id.flashlight_checkbox);
            pointsOverlayView = (PointsOverlayView) content.findViewById(R.id.points_overlay_view);

            qrCodeReaderView.setAutofocusInterval(2000L);
            qrCodeReaderView.setOnQRCodeReadListener(this);
            qrCodeReaderView.setBackCamera();
            flashlightCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    qrCodeReaderView.setTorchEnabled(isChecked);
                }
            });

            qrCodeReaderView.setQRDecodingEnabled(true);
            qrCodeReaderView.startCamera();

        } else {
            requestCameraPermission();
        }


        pb.setVisibility(View.INVISIBLE);

        tran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tran.setVisibility(View.INVISIBLE);
                pb.setVisibility(View.VISIBLE);
            }
        });

        return rootView;
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
            Snackbar.make(cameraLayout, "Per continuare sono necessari i permessi della fotocamera.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override public void onClick(View view) {

                    ActivityCompat.requestPermissions(getActivity(), new String[] {
                            Manifest.permission.CAMERA
                    }, MY_PERMISSION_REQUEST_CAMERA);
                }
            }).show();
        } else {
            Snackbar.make(cameraLayout, "Permessi non disponibili. Richiedo i permessi.",
                    Snackbar.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(), new String[] {
                    Manifest.permission.CAMERA
            }, MY_PERMISSION_REQUEST_CAMERA);
        }
    }


    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        resultTextView.setText(text);
        pointsOverlayView.setPoints(points);
    }
}
