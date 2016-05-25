package Drawer_fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.careconnectpatient.R;

public class DoctorHistoryFragment extends Fragment implements View.OnClickListener{


    DoctorHistoryListener mCommunicator;

    public DoctorHistoryFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("History");
        View view = inflater.inflate(R.layout.fragment_doctor_history, container, false);
        Spinner spinner = (Spinner) view.findViewById(R.id.doctor_history_spinner);
        mCommunicator.populateHistorySpinner(spinner);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button mAddButton = (Button) view.findViewById(R.id.doctor_history_button);
        mAddButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Spinner spinner = (Spinner) getView().findViewById(R.id.doctor_history_spinner);
        ListView historyList = (ListView) getView().findViewById(R.id.doctor_history_list);
        String patient_key = spinner.getSelectedItem().toString();
        mCommunicator.populateDoctorHistoryList(historyList, patient_key);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DoctorHistoryListener) {
            mCommunicator = (DoctorHistoryListener) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCommunicator = null;
    }



    public interface DoctorHistoryListener {
        void populateDoctorHistoryList(ListView listview, String patient_key);
        void populateHistorySpinner(Spinner spinner);
    }
}
