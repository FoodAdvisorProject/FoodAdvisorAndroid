package pro.rane.foodadvisor;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.Manifest;
import android.widget.Button;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class StartScreen extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
        pro.rane.foodadvisor.SessionManager session = new pro.rane.foodadvisor.SessionManager(getApplicationContext());
        if (session.isLoggedIn()){
            Intent i = new Intent(getApplicationContext(), NavigationActivity.class);
            startActivity(i);
            finish();
        }
        setContentView(R.layout.start_screen);
        TextView tx = (TextView)findViewById(R.id.titleView);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Sketch_Block.ttf");

        tx.setTypeface(custom_font);

        Button userButton = (Button) findViewById(R.id.user);
        Button prodButton = (Button) findViewById(R.id.producer);

        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPath(v);
            }
        });

        prodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                producerPath(v);
            }
        });

        // richiesta permesso GPS
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)  {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    //Gestore risposta GPS
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Ok")
                            .setContentText("I permessi sono stati accordati")
                            .setConfirmText("Fantastico!").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    }).show();
                } else {
                    new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Errore!")
                            .setContentText("I permessi non sono stati accordati\nL'applicazione non può funzionare senza")
                            .setConfirmText("Fantastico!").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            ActivityCompat.requestPermissions(StartScreen.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                        }
                    }).show();
                }
            }
        }
    }

    public void userPath(View view){
        Intent intent = new Intent(this, TrackActivity.class);
        startActivity(intent);
    }
    public void producerPath(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
