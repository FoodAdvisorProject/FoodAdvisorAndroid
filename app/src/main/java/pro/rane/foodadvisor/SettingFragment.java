package pro.rane.foodadvisor;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class SettingFragment extends Fragment{
    pro.rane.foodadvisor.SessionManager session;
    public SettingFragment(){
    }

    private Switch gpsPerm;
    private Switch cameraPerm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        session = new pro.rane.foodadvisor.SessionManager(getActivity().getApplicationContext());
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        gpsPerm = (Switch) rootView.findViewById(R.id.checkedGPS);
        cameraPerm = (Switch) rootView.findViewById(R.id.checkedCamera);


        gpsPerm.setChecked(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED);
        cameraPerm.setChecked(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED);
        
        gpsPerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 12/04/17 richiesta permessi GPS
                Toasty.success(getContext(),"Richiesta permessi", Toast.LENGTH_SHORT).show();
                gpsPerm.setClickable(false);
            }
        });
        
        cameraPerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 12/04/17 richiesta permessi fotocamera
                Toasty.success(getContext(),"Richiesta permessi", Toast.LENGTH_SHORT).show();
                cameraPerm.setClickable(false);
            }
        });

        return rootView;
    }

}
