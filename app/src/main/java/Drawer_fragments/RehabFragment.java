package Drawer_fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.careconnectpatient.R;

public class RehabFragment extends Fragment {


    public RehabFragment() {
        // Required empty public constructor
    }

//    public static RehabFragment newInstance(Bundle b) {
//        RehabFragment fragment = new RehabFragment();
//        fragment.setArguments(b);
//        return fragment;
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Rehab");

        return inflater.inflate(R.layout.fragment_rehab, container, false);
    }

}
