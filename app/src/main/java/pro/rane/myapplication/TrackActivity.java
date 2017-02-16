package pro.rane.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class TrackActivity extends AppCompatActivity {
    Button scan ;


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

                final String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

              goToMapsActivity(contents);


            } else if (resultCode == RESULT_CANCELED) {
             //  Log.i("App","Scan unsuccessful");
            }
        }
    }



    private void goToMapsActivity(String info){

        /*fase di alert (questo va cancellato*/
        final String prova=info;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (!isFinishing()){
                    new AlertDialog.Builder(TrackActivity.this)
                            .setTitle("Your Alert")
                            .setMessage(prova)
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Whatever...
                                }
                            }).show();
                }
            }
        });
        /*fine fase aler*/
        /*richiamo a maps*/
         Intent startMapsActivity = new Intent(this, MapsActivity.class);
         startMapsActivity.putExtra("qrCodeInformation", info);
         startActivity(startMapsActivity);
    }



}
