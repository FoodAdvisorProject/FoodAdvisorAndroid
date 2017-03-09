package pro.rane.foodadvisor;


import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.LocationListener;


public class NewProductFragment extends Fragment{
    protected Context context;

    public NewProductFragment(){
        //must be empty
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_new_product, container, false);



        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);



        // TODO: 09/03/2017 scrivere la presa della fotografia 

        Button userButton = (Button)rootView.findViewById(R.id.new_product_button);
        userButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO: 09/03/2017 scrivere logica di aggiunta nuovo prodotto
                Intent intent = new Intent(getActivity(),GetCurrentLocation.class);
                startActivity(intent);
            }
        });
        return rootView;
    }

}
