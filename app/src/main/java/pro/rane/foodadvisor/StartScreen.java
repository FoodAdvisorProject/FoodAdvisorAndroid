package pro.rane.foodadvisor;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.Manifest;
import android.widget.Button;

public class StartScreen extends AppCompatActivity {
    //pro.rane.foodadvisor.SessionManager session;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION =1;
    private Button userButton;
    private Button prodButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*session = new pro.rane.foodadvisor.SessionManager(getApplicationContext());
        if (session.isLoggedIn()){
            //Se l'utente è già loggato passo direttamente al Navigation Drawer
            Intent i = new Intent(getApplicationContext(), NavigationActivity.class);
            startActivity(i);
            finish();
        }*/
        setContentView(R.layout.start_screen);

        userButton = (Button) findViewById(R.id.user);
        prodButton = (Button) findViewById(R.id.producer);

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
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permesso accordato, sta senza pensieri
                } else {
                    // permission denied, non possiamo disabilitare il GPS quindi dovrebbe continuare chiederlo
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }
                return;
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
        finish();
    }
}
