package pro.rane.foodadvisor;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class MainFragment extends Fragment{


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment, container, false);

        TextView tx = (TextView) rootView.findViewById(R.id.titleFragView);
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/Sketch_Block.ttf");
        tx.setTypeface(custom_font);

        return rootView;
    }

}
