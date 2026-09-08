package com.example.identitylab;

import android.content.Context;
import java.io.*;
import java.util.UUID;

public final class IdentityStore {
    private final Context context;
    public IdentityStore(Context context) { this.context = context.getApplicationContext(); }
    public String getMachineId() {
        File f = new File(context.getFilesDir(), "custom_mid");
        if (f.exists()) {
            try {
                String s = readTrimmed(f);
                if (IdentityPolicy.isValidCustomMid(s)) return s;
                f.delete();
            } catch (IOException ignored) { }
        }
        String fallback = android.provider.Settings.Secure.getString(context.getContentResolver(), "android_id");
        return fallback == null ? "" : fallback;
    }
    public void resetIdentity() throws IOException {
        String id = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        write(new File(context.getFilesDir(), "custom_mid"), id);
        deleteIfExists(new File(context.getFilesDir(), "device.key"));
        deleteIfExists(new File(context.getFilesDir(), "device.key.lock"));
    }
    private static String readTrimmed(File f) throws IOException {
        try (FileInputStream in = new FileInputStream(f)) {
            byte[] b = new byte[in.available()]; int n = in.read(b);
            return new String(b, 0, Math.max(0, n)).trim();
        }
    }
    private static void write(File f, String s) throws IOException { try (FileOutputStream out = new FileOutputStream(f)) { out.write(s.getBytes()); } }
    private static void deleteIfExists(File f) { if (f.exists()) f.delete(); }
}
