package pro.rane.foodadvisor;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class SettingFragment extends Fragment{
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 2;
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
        gpsPerm.setClickable(!(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED));
        cameraPerm.setClickable(!(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED));

        gpsPerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            }
        });
        
        cameraPerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        });

        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toasty.success(getContext(),"Permessi garantiti", Toast.LENGTH_LONG).show();
                    gpsPerm.setClickable(false);
                } else {
                    Toasty.error(getContext(),"Permessi negati", Toast.LENGTH_LONG).show();
                    gpsPerm.setChecked(false);
                    gpsPerm.setClickable(true);
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toasty.success(getContext(),"Permessi garantiti", Toast.LENGTH_LONG).show();
                   cameraPerm.setClickable(false);
                } else {
                    Toasty.error(getContext(),"Permessi negati", Toast.LENGTH_LONG).show();
                    cameraPerm.setChecked(false);
                    cameraPerm.setClickable(true);
                }
            }
        }
    }

}
