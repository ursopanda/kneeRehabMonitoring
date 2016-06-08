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
 * Class for bluetooth device list population and selecting rehab knee device
 */
public class BluetoothFragment extends Fragment implements View.OnClickListener{

    bluetoothListener mCommunicator;

    public BluetoothFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Bluetooth");
        Context context = getContext();
        View view = inflater.inflate(R.layout.fragment_bluetooth, container, false);
        mCommunicator.onCreateBT(view, context);
        mCommunicator.populateBluetoothList(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button scanButton = (Button) view.findViewById(R.id.button_scan);
        Button connectButton = (Button) view.findViewById(R.id.connect_bluetooth_button);
        scanButton.setOnClickListener(this);
        connectButton.setOnClickListener(this);
    }


    @Override
    public void onClick(final View v) {
        Context context = getContext();
        switch(v.getId()){
            case R.id.button_scan:
                mCommunicator.scanBluetoothDevices(v, context);
                break;
            case R.id.connect_bluetooth_button:
                mCommunicator.onConnectClick(v, context);
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof bluetoothListener) {
            mCommunicator = (bluetoothListener) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mCommunicator.onDestroyBT();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCommunicator = null;
    }

    public interface bluetoothListener{
        void scanBluetoothDevices(View view, Context context);
        void populateBluetoothList(View view);
        void onCreateBT(View view, Context context);
        void onDestroyBT();
        void onConnectClick(View view, Context context);
    }

}
