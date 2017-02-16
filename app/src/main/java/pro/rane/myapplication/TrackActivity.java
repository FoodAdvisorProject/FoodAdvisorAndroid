package pro.rane.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class TrackActivity extends AppCompatActivity {
    Button scan ;
    String info="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        scan =  (Button) findViewById(R.id.trackbutton);
    }


    public void scanQR(View v) {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        //"SCAN_MODE","SCAN_MODE" --> Permette lo scanner dei BarCode
        //"SCAN_MODE","QR_CODE_MODE" -->Permette lo scanner dei qrcode
        startActivityForResult(intent, 0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                info=contents;

                // Handle successful scan

            } else if (resultCode == RESULT_CANCELED) {
               // Log.i("App","Scan unsuccessful");
            }
        }
    }



    private void goToMapsActivity(String info){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("qrCodeInformation", info);
        startActivity(intent);
    }



}
