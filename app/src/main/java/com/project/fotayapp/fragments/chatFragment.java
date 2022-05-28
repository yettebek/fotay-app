package com.project.fotayapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.project.fotayapp.R;

public class chatFragment extends Fragment {

    //Declarar variables
    private ImageView iv_send_message;
    private EditText et_message;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        iv_send_message = view.findViewById(R.id.message_send);
        et_message = view.findViewById(R.id.message_edit);

        iv_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_message = et_message.getText().toString().trim();
                if (!user_message.isEmpty()) {
                    Toast.makeText(getContext(), user_message, Toast.LENGTH_SHORT).show();
                    et_message.setText("");
                    et_message.clearFocus();
                } else {
                    Toast.makeText(getContext(), "No se puede enviar un mensaje vacio", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public chatFragment() {
        // Required empty public constructor
    }
}