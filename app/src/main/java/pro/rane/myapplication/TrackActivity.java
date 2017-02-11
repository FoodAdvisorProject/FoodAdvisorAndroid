package pro.rane.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


/*qui va implementata la parte di lettura da parte di una applicazione in questo caso una app esterna
quella della zxing
 */
public class TrackActivity extends AppCompatActivity {
    Button btn = (Button) findViewById(R.id.trackbutton);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
    }
    public void onClick(View v){
        Intent intent = new Intent(
                "com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 1);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                String format = data.getStringExtra("SCAN_RESULT_FORMAT");
                goToMapsActivity(contents);
            }
    }

    private void goToMapsActivity(String info){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("qrCodeInformation", info);
        startActivity(intent);
    }



}
