package pro.rane.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void userPath(View view){
        Intent startTrackActivity = new Intent(this,TrackActivity.class);

        startActivity(startTrackActivity);
    }

    public void producerPath(View view){
        Intent startLoginActivity = new Intent(this,LoginActivity.class);

        startActivity(startLoginActivity);
    }




}
