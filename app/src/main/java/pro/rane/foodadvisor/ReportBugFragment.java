package pro.rane.foodadvisor;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;


public class ReportBugFragment extends Fragment{

    private static final String email = "food.advisor.ita@gmail.com";
    private EditText body;
    private Button bug_button;
    pro.rane.foodadvisor.SessionManager session;
    public ReportBugFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_bug_report, container, false);
        session = new pro.rane.foodadvisor.SessionManager(getActivity().getApplicationContext());

        final HashMap<String, String> user = session.getUserDetails();

        body = (EditText) rootView.findViewById(R.id.body_mail_bug);
        bug_button = (Button) rootView.findViewById(R.id.button_send_bug);
        bug_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{email});
                i.putExtra(Intent.EXTRA_SUBJECT, "[BUG REPORT] User: " + user.get(SessionManager.KEY_NAME));
                i.putExtra(Intent.EXTRA_TEXT   , body.getText().toString()+"\n\n Version: "+ Build.VERSION.RELEASE +" API: "+ Build.VERSION.SDK_INT + "Model: " + Build.MODEL+" Marca: "+Build.MANUFACTURER);
                try {
                    startActivity(Intent.createChooser(i, getString(R.string.title_app_selector_name)));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "Non ci sono servizi email installati.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return rootView;
    }
}
