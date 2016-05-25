package Drawer_fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.careconnectpatient.R;

public class PatientListFragment extends ListFragment {

    patientListListener mCommunicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Your patients");
        View view = inflater.inflate(R.layout.fragment_patient_list, container, false);
        ListView listView = (ListView) view.findViewById(android.R.id.list);

        mCommunicator.populateList(listView);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof patientListListener) {
            mCommunicator = (patientListListener) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCommunicator = null;
    }


    public interface patientListListener {
        void populateList(ListView listview);
    }
}
