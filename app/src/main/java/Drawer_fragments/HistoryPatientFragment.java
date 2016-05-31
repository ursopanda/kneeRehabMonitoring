package Drawer_fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.careconnectpatient.R;

/**
 * Class for populating rehab history list for patient
 */
public class HistoryPatientFragment extends Fragment {

    patientHistoryListener mCommunicator;

    public HistoryPatientFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("History");
        View view = inflater.inflate(R.layout.fragment_patient_history, container, false);
        ListView historyList = (ListView) view.findViewById(R.id.patient_history_list);
        mCommunicator.populateHistoryList(historyList);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof patientHistoryListener) {
            mCommunicator = (patientHistoryListener) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCommunicator = null;
    }



    public interface patientHistoryListener {
        void populateHistoryList(ListView listview);
    }
}
