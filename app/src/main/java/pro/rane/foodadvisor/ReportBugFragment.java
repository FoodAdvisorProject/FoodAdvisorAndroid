package pro.rane.foodadvisor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Andrea on 08/03/2017.
 */

public class ReportBugFragment extends Fragment{
    public ReportBugFragment() {
        // Required empty public constructor
    }

    // TODO: 05/03/2017 creare attività con form di report bug alla nostra mail

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_bug_report, container, false);


        return rootView;
    }
}
