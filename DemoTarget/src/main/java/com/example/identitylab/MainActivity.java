package com.example.identitylab;

import android.app.Activity;
import android.os.Bundle;
import android.os.Process;
import android.widget.*;
import java.io.IOException;

public final class MainActivity extends Activity {
    private IdentityStore store;
    private TextView value;
    @Override public void onCreate(Bundle b) {
        super.onCreate(b); store = new IdentityStore(this);
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(32,32,32,32);
        value = new TextView(this); value.setTextSize(18); root.addView(value);
        Button reset = new Button(this); reset.setText("重置测试身份"); root.addView(reset);
        Button refresh = new Button(this); refresh.setText("读取 machine_id"); root.addView(refresh);
        refresh.setOnClickListener(v -> showId());
        reset.setOnClickListener(v -> { try { store.resetIdentity(); showId(); } catch (IOException e) { value.setText("reset failed: " + e); } });
        setContentView(root); showId();
    }
    private void showId() { value.setText("machine_id = " + store.getMachineId() + "\nPID = " + Process.myPid()); }
}
