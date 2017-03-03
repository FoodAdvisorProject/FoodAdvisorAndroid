package pro.rane.foodadvisor;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: 02/03/2017 in competizione diretta con il tema xml, decidere in base a User experience and feedback (entro fine weekend si risolve) 
        //Remove title bar
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
       // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.setContentView(R.layout.activity_main);
    }


    // TODO: 02/03/2017 decidere i punti di chiamata a questa funzione
    public void userPath(View view){
        Intent startTrackActivity = new Intent(this,TrackActivity.class);
        startActivity(startTrackActivity);
    }

    public void producerPath(View view){
        Intent startLoginActivity = new Intent(this,LoginActivity.class);
        startActivity(startLoginActivity);
    }




}
