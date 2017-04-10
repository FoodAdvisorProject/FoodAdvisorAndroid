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

public class SettingFragment extends Fragment{
    pro.rane.foodadvisor.SessionManager session;
    public SettingFragment(){
    }

    private CheckBox gpsPerm;
    private CheckBox cameraPerm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        session = new pro.rane.foodadvisor.SessionManager(getActivity().getApplicationContext());
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        gpsPerm = (CheckBox) rootView.findViewById(R.id.checkedGPS);
        cameraPerm = (CheckBox) rootView.findViewById(R.id.checkedCamera);


        gpsPerm.setChecked(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED);
        cameraPerm.setChecked(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED);

        return rootView;
    }

}
