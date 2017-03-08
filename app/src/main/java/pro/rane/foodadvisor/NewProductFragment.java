package pro.rane.foodadvisor;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;

public class NewProductFragment extends Fragment{

    public NewProductFragment(){
        //must be empty
    }

    private float[] getCoordinates(){
        float[] coordinates = new float[2];

        // TODO: 09/03/2017 deve restituire le coordinate attuali

        float lat = 0.0f;

        float lng = 0.0f;

        coordinates[0] = lat;
        coordinates[1] = lng;
        return coordinates;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_new_product, container, false);

        float[] coordinates = getCoordinates();

        // TODO: 09/03/2017 scrivere la presa della fotografia 

        Button userButton = (Button)rootView.findViewById(R.id.new_product_button);
        userButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO: 09/03/2017 scrivere logica di aggiunta nuovo prodotto
                Intent intent = new Intent(getActivity(), SplashScreen.class);
                startActivity(intent);
            }
        });
        return rootView;
    }
}
