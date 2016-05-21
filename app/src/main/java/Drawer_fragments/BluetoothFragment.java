package Drawer_fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.careconnectpatient.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BluetoothFragment extends Fragment {


    public BluetoothFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Bluetooth");

        return inflater.inflate(R.layout.fragment_bluetooth, container, false);
    }

}
