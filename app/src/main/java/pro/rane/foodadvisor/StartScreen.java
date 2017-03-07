package pro.rane.foodadvisor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_screen);

    }

    public void userPath(View view){
        Intent intent = new Intent(this, TrackActivity.class);
        startActivity(intent);
        finish();
    }
    public void producerPath(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
