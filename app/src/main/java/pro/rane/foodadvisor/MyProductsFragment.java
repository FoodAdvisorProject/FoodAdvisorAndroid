package pro.rane.foodadvisor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Andrea on 08/03/2017.
 */

public class MyProductsFragment extends Fragment {


    public MyProductsFragment() {
        //must be empty
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // TODO: 08/03/2017 fare adeguate call sui prodotti
        
        
        View rootView = inflater.inflate(R.layout.fragment_my_products, container, false);

        return rootView;
    }
}