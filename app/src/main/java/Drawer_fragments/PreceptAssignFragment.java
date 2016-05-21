package Drawer_fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.careconnectpatient.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreceptAssignFragment extends Fragment implements View.OnClickListener {

    preceptAssignListener mCommunicator;

    public PreceptAssignFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Assign Precept");
        View view = inflater.inflate(R.layout.fragment_precept_assign, container, false);
        Spinner spinner = (Spinner) view.findViewById(R.id.precept_spinner);
        mCommunicator.populateSpinner(spinner);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button mAddButton = (Button) view.findViewById(R.id.assign_precept_button);
        mAddButton.setOnClickListener(this);
    }

    //Override method from View.OnClickListener
    @Override
    public void onClick(final View v) {
        //TODO: create all editTexts
        Spinner mPatientSpinnerView = (Spinner) getView().findViewById(R.id.precept_spinner);
        EditText mOptAngleView = (EditText) getView().findViewById(R.id.precept_opt_angle);
        EditText mMaxAngleView = (EditText) getView().findViewById(R.id.precept_max_angle);
        EditText mDurationView = (EditText) getView().findViewById(R.id.precept_duration);
        EditText mFrequencyView = (EditText) getView().findViewById(R.id.precept_frequency);

        String patient = mPatientSpinnerView.getSelectedItem().toString();
        int o_angle = Integer.parseInt(mOptAngleView.getText().toString());
        int m_angle = Integer.parseInt(mMaxAngleView.getText().toString());
        int duration = Integer.parseInt(mDurationView.getText().toString());
        int frequency = Integer.parseInt(mFrequencyView.getText().toString());

        mCommunicator.assignPrecept(patient, o_angle, m_angle, duration, frequency);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof preceptAssignListener) {
            mCommunicator = (preceptAssignListener) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCommunicator = null;
    }

    public interface preceptAssignListener{
        //TODO:assign all parameters to function
        void populateSpinner(Spinner spinner);
        void assignPrecept(String patient, int o_angle, int m_angle, int duration, int frequency);
    }

}
