package com.example.identitylab.lsp;

import android.util.Log;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.UUID;
import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.XposedModuleInterface;

/**
 * Learning/test module for the developer-authorized TunNet test environment.
 *
 * Observation-only: hooks MainActivity.k(), records machine_id, and generates
 * a fresh 16-hex-character test candidate for comparison. The candidate is
 * never written into TunNet files and the original return value is preserved.
 */
public final class IdentityHook extends XposedModule {
    private static final String TAG = "TunNetIdentityLab";
    private static final String TARGET = "com.tunnet.client";
    private static final String ACTIVITY = "com.tunnet.client.MainActivity";
    private boolean hookInstalled;

    @Override
    public void onModuleLoaded(XposedModuleInterface.ModuleLoadedParam param) {
        log(Log.INFO, TAG, "module loaded; process=" + param.getProcessName()
                + ", api=" + getApiVersion() + ", framework=" + getFrameworkName());
    }

    @Override
    public void onPackageReady(XposedModuleInterface.PackageReadyParam param) {
        if (!TARGET.equals(param.getPackageName()) || hookInstalled) return;
        try {
            ClassLoader loader = param.getClassLoader();
            Class<?> activity = Class.forName(ACTIVITY, false, loader);
            Method method = activity.getDeclaredMethod("k");
            hook(method)
                    .setPriority(PRIORITY_DEFAULT)
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        Object result = chain.proceed();
                        String current = null;
                        if (result instanceof LinkedHashMap<?, ?> map) {
                            Object machineId = map.get("machine_id");
                            current = machineId == null ? null : String.valueOf(machineId);
                            log(Log.INFO, TAG,
                                    "MainActivity.k intercepted; machine_id=" + machineId);
                        } else {
                            log(Log.INFO, TAG,
                                    "MainActivity.k intercepted; result=" + result);
                        }

                        String candidate = newRandom16Hex();
                        log(Log.INFO, TAG,
                                "random identity test candidate=" + candidate
                                        + ", current=" + current
                                        + ", replacement=false");
                        return result;
                    });
            hookInstalled = true;
            log(Log.INFO, TAG, "hook installed for " + TARGET + "#k()");
        } catch (Throwable t) {
            log(Log.ERROR, TAG, "hook installation failed for " + TARGET, t);
        }
    }

    private static String newRandom16Hex() {
        String raw = UUID.randomUUID().toString().replace("-", "");
        return raw.substring(0, 16).toLowerCase(Locale.ROOT);
    }
}
