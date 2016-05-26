package Drawer_fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.careconnectpatient.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoctorViewFragment extends Fragment implements View.OnClickListener{


    doctorViewListener mCommunicator;

    public DoctorViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_view, container, false);
        mCommunicator.populateDoctorView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button deleteButton = (Button) view.findViewById(R.id.doc_view_delete_button);
        Button changeButton = (Button) view.findViewById(R.id.doc_view_change_button);
        deleteButton.setOnClickListener(this);
        changeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        Context context = getContext();
        switch(v.getId()){
            case R.id.doc_view_delete_button:
                mCommunicator.deleteDocAccount(context);
                break;
            case R.id.doc_view_change_button:
                mCommunicator.changeDocPassword(context);
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof doctorViewListener) {
            mCommunicator = (doctorViewListener) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCommunicator = null;
    }

    public interface doctorViewListener{
        void deleteDocAccount(Context context);
        void changeDocPassword(Context context);
        void populateDoctorView(View view);
    }

}
