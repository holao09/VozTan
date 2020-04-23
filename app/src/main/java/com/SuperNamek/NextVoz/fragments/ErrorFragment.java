package com.SuperNamek.NextVoz.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.SuperNamek.NextVoz.R;
import com.SuperNamek.NextVoz.events.EventDisplayError;
import com.SuperNamek.NextVoz.events.EventUseBrowserFragment;

/**
 * @Author: SuperNamek
 */

public class ErrorFragment extends Fragment {

    public static final String TAG = "ERROR_FRAGMENT";

    private Button btn_retry;
    private TextView lbl_error;

    public ErrorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }

    @Subscribe(sticky = true)
    public void onDisplayError(EventDisplayError event) {
        lbl_error.setText(getString(R.string.error) + " " + event.getErrorDescription());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_error, container, false);
        btn_retry = (Button) view.findViewById(R.id.btn_retry_fragment_error);
        lbl_error = (TextView) view.findViewById(R.id.lbl_error_fragment_error);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventUseBrowserFragment());
            }
        });
    }
}
