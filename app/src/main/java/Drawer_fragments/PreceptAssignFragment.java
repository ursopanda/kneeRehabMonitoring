package Drawer_fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
    View.OnClickListener removeListener;

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
        Button mRemoveButton = (Button) view.findViewById(R.id.remove_precept_button);
        mAddButton.setOnClickListener(this);
        mRemoveButton.setOnClickListener(this);
    }

    public View.OnClickListener getRemoveListener() {
        return removeListener;
    }

    //Override method from View.OnClickListener
    @Override
    public void onClick(final View v) {
        //TODO: create all editTexts
        Spinner mPatientSpinnerView = (Spinner) getView().findViewById(R.id.precept_spinner);
        String patient = mPatientSpinnerView.getSelectedItem().toString();
        Context context = getContext();

        switch(v.getId()){
            case R.id.assign_precept_button:

                EditText mOptAngleView = (EditText) getView().findViewById(R.id.precept_opt_angle);
                EditText mMaxAngleView = (EditText) getView().findViewById(R.id.precept_max_angle);
                EditText mDurationView = (EditText) getView().findViewById(R.id.precept_duration);
                EditText mFrequencyView = (EditText) getView().findViewById(R.id.precept_frequency);

                int o_angle = 0;
                int m_angle = 0;
                int duration = 0;
                int frequency = 0;
                boolean cancel = false;
                View focusView = null;

                if(!TextUtils.isEmpty(mOptAngleView.getText())) {
                    o_angle = Integer.parseInt(mOptAngleView.getText().toString());
                }
                else {
                    cancel = true;
                    mOptAngleView.setError(getString(R.string.error_field_required));
                    focusView = mOptAngleView;
                }

                if(!TextUtils.isEmpty(mMaxAngleView.getText())) {
                    m_angle = Integer.parseInt(mMaxAngleView.getText().toString());
                }
                else{
                    cancel = true;
                    mMaxAngleView.setError(getString(R.string.error_field_required));
                    focusView = mMaxAngleView;
                }

                if(!TextUtils.isEmpty(mDurationView.getText())) {
                    duration = Integer.parseInt(mDurationView.getText().toString());
                }
                else{
                    cancel = true;
                    mDurationView.setError(getString(R.string.error_field_required));
                    focusView = mDurationView;
                }

                if(!TextUtils.isEmpty(mFrequencyView.getText())) {
                    frequency = Integer.parseInt(mFrequencyView.getText().toString());
                }
                else{
                    cancel = true;
                    mFrequencyView.setError(getString(R.string.error_field_required));
                    focusView = mFrequencyView;
                }

                if(cancel){
                    focusView.requestFocus();
                }
                else{
                    mCommunicator.assignPrecept(patient, o_angle, m_angle, duration, frequency);
                }
                break;

            case R.id.remove_precept_button:
                mCommunicator.removeAssignment(patient, context);
                break;
        }



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
        void removeAssignment(String patient, Context context);
    }

}
