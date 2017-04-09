package pro.rane.foodadvisor;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.HashMap;



public class ProfileFragment extends Fragment {

    pro.rane.foodadvisor.SessionManager session;
    TextView nome_azienda;
    TextView email;
    TextView descrizione_azienda;
    TextView first_name;
    TextView second_name;
    TextView id_azienda;
    ImageView foto;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        session = new pro.rane.foodadvisor.SessionManager(getActivity().getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();

        // TODO: 10/03/2017 gestione foto 

        nome_azienda = (TextView) rootView.findViewById(R.id.nome_azienda);
        email = (TextView) rootView.findViewById(R.id.email_azienda);
        descrizione_azienda = (TextView) rootView.findViewById(R.id.description_azienda);
        first_name = (TextView) rootView.findViewById(R.id.first_name);
        second_name = (TextView) rootView.findViewById(R.id.second_name);
        id_azienda = (TextView) rootView.findViewById(R.id.id_azienda);
        foto = (ImageView) rootView.findViewById(R.id.fotoProfilo);

        nome_azienda.setText(user.get(SessionManager.KEY_AZIENDA));
        email.setText(user.get(SessionManager.KEY_EMAIL));
        descrizione_azienda.setText(user.get(SessionManager.KEY_DESCRIPTION));
        first_name.setText(user.get(SessionManager.KEY_NAME));
        second_name.setText(user.get(SessionManager.KEY_SECOND_NAME));
        id_azienda.setText(user.get(SessionManager.KEY_ID));
        //Toast.makeText(getContext(),user.get(SessionManager.KEY_ID),Toast.LENGTH_SHORT).show();

        ImageLoader imageLoader = ImageLoader.getInstance();
        // TODO: 08/04/2017 temporaneo
        imageLoader.displayImage("http://foodadvisor.rane.pro:8080/getUserImage?user_id=".concat(user.get(SessionManager.KEY_ID)),foto);

        return rootView;
    }

}
