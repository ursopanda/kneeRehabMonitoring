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
 * Class for patients' rehab managing
 */
public class RehabFragment extends Fragment implements View.OnClickListener  {

    rehabListener mCommunicator;

    public RehabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Rehab");
        View view = inflater.inflate(R.layout.fragment_rehab, container, false);
        mCommunicator.timer(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button finishButton = (Button) view.findViewById(R.id.finish_button);
        finishButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Context context = getContext();
        mCommunicator.finishRehab(context);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof rehabListener) {
            mCommunicator = (rehabListener) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCommunicator = null;
    }


    public interface rehabListener{
        void timer(View view);
        void finishRehab(Context context);
    }

}
