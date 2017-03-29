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

/**
 * Created by Andrea on 08/03/2017.
 */

public class ReportBugFragment extends Fragment{

    private static final String email = "food.advisor.ita@gmail.com";
    private EditText body;
    private Button bug_button;

    public ReportBugFragment() {
        // Required empty public constructor
    }




    private void reportABug(View view){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{email});
        i.putExtra(Intent.EXTRA_SUBJECT, "[BUG REPORT] "+ Build.VERSION.SDK_INT);
        i.putExtra(Intent.EXTRA_TEXT   , body.getText().toString());
        try {
            startActivity(Intent.createChooser(i, "Invio la mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "Non ci sono servizi email installati.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_bug_report, container, false);
        body = (EditText) getActivity().findViewById(R.id.body_email_bug);
        bug_button = (Button) rootView.findViewById(R.id.button_send_bug);
        bug_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                reportABug(v);
            }
        });


        return rootView;
    }
}
