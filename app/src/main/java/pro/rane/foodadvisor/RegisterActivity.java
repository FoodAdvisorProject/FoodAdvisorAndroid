package pro.rane.foodadvisor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    // TODO: 03/03/2017 scrivere la logica di check information + la chiamata API di registrazione del con eventuale risposta positiva o negativa
    // TODO: 05/03/2017 la call è /addUser

    public boolean checkData(View view){

        return true;
    }


    public void register(View view) {

    }
}
