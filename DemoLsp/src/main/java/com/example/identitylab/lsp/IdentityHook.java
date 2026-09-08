package com.example.identitylab.lsp;

import android.util.Log;
import java.lang.reflect.Method;
import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.XposedModuleInterface;

public final class IdentityHook extends XposedModule {
    private static final String TAG = "IdentityLab";
    private static final String TARGET = "com.example.identitylab";
    private static final String STORE = "com.example.identitylab.IdentityStore";
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
            Class<?> store = Class.forName(STORE, false, loader);
            Method method = store.getDeclaredMethod("getMachineId");
            hook(method)
                    .setPriority(PRIORITY_DEFAULT)
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {
                        Object result = chain.proceed();
                        log(Log.INFO, TAG, "getMachineId intercepted; result=" + result);
                        return result;
                    });
            hookInstalled = true;
            log(Log.INFO, TAG, "hook installed for " + TARGET + "#getMachineId()");
        } catch (Throwable t) {
            log(Log.ERROR, TAG, "hook installation failed for " + TARGET, t);
        }
    }
}
