package Drawer_fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.careconnectpatient.R;

/**
 * Class for showing precept information for patient
 */
public class PreceptFragment extends Fragment {

    preceptListener mCommunicator;

    public PreceptFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Precept");
        View view = inflater.inflate(R.layout.fragment_precept, container, false);
        mCommunicator.isPrecept(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof preceptListener) {
            mCommunicator = (preceptListener) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCommunicator = null;
    }

    public interface preceptListener{
        void isPrecept(View view);
    }

}
