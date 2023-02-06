package com.jiangnane.abus.demo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ToggleButton;

import com.jiangnane.abus.ABus;
import com.jiangnane.abus.utils.Logger;


public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToggleButton tb = findViewById(R.id.btn);
        tb.setChecked(getHvacSwitch());
        tb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Bundle params = new Bundle();
            params.putBoolean("hvac_switch", isChecked);
            ABus.INS.set("hvac", params);

            Logger.i(TAG, "The hvac_switch was set to " + getHvacSwitch() + ". ");

            ABus.INS.post("hvac", params);
        });

    }

    private boolean getHvacSwitch() {
        Bundle b = ABus.INS.get("hvac", null);
        return b != null && b.getBoolean("hvac_switch");
    }
}
