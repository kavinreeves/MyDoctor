package com.example.mydoctor.mydoctor.Doctor;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.example.mydoctor.mydoctor.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PrescriptionFullView extends Fragment {

    private WebView webView;


    public PrescriptionFullView() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_prescription_full_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        webView = (WebView) view.findViewById(R.id.webview_prescription);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://wenso-sms.co.uk/prescriptions/pres_chat_0.pdf");

    }
}
