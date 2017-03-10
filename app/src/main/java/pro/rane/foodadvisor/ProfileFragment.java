package pro.rane.foodadvisor;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    pro.rane.foodadvisor.SessionManager session;
    TextView nome_azienda;
    TextView email;
    TextView descrizione_azienda;
    TextView first_name;
    TextView second_name;
    TextView id_azienda;


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

        nome_azienda.setText(user.get(SessionManager.KEY_AZIENDA));
        email.setText(user.get(SessionManager.KEY_EMAIL));
        descrizione_azienda.setText(user.get(SessionManager.KEY_DESCRIPTION));
        first_name.setText(user.get(SessionManager.KEY_NAME));
        second_name.setText(user.get(SessionManager.KEY_SECOND_NAME));
        id_azienda.setText(user.get(SessionManager.KEY_ID));

        return rootView;
    }

}
