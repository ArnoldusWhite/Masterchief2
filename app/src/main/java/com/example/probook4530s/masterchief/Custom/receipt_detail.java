package com.example.probook4530s.masterchief.Custom;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.probook4530s.masterchief.Custom.custom.CustomActivity;
import com.example.probook4530s.masterchief.Custom.ui.receiptdetail.ReceiptDetailFragment;
import com.example.probook4530s.masterchief.R;

public class receipt_detail extends CustomActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //setContentView(R.layout.activity_main);
        setContentView(R.layout.receipt_detail_activity);
        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, ReceiptDetailFragment.newInstance())
                    .commitNow();
        }
    }
}
