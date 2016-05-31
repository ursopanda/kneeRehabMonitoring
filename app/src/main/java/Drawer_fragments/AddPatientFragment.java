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

import com.careconnectpatient.R;

/**
 * class for adding patient to doctor's patient
 */
public class AddPatientFragment extends Fragment implements View.OnClickListener{

    addPatientListener mCommunicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Add Patient");
        return inflater.inflate(R.layout.fragment_add_patient, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button mAddButton = (Button) view.findViewById(R.id.add_patient_button);
        mAddButton.setOnClickListener(this);
    }

    //Override method from View.OnClickListener
    @Override
    public void onClick(final View v) {
        //getting email from input field
        EditText mEmailView = (EditText) getView().findViewById(R.id.add_patient_email);
        String add_email = mEmailView.getText().toString();
        mCommunicator.isPatient(add_email);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof addPatientListener) {
            mCommunicator = (addPatientListener) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCommunicator = null;
    }

    public interface addPatientListener{
        void isPatient(String email);
    }
}
