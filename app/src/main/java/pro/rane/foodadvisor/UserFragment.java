package pro.rane.foodadvisor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;



public class UserFragment extends Fragment{

    public UserFragment(){
        //must be empty
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_mode, container, false);

        Button userButton = (Button)rootView.findViewById(R.id.user_button);
        userButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TrackActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }

}
