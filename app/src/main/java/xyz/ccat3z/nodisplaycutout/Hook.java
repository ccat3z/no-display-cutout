package xyz.ccat3z.nodisplaycutout;

import android.app.Activity;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hook implements IXposedHookLoadPackage {
    private static final String LOG_TAG = "NoDisplayCutout";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;
                WindowManager.LayoutParams params = activity.getWindow().getAttributes();
                params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                Log.d(LOG_TAG, "Set WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES");
            }
        });

        XposedHelpers.findAndHookMethod(WindowInsets.class, "getInsets", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.args[0] = (int) param.args[0] & ~WindowInsets.Type.displayCutout();
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Log.d(LOG_TAG, "getInsets(" + param.args[0] + ") = " + param.getResult().toString());
//                Log.d(LOG_TAG, Log.getStackTraceString(new Exception()));
            }
        });

        XposedHelpers.findAndHookMethod(WindowInsets.class, "getInsetsIgnoringVisibility", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.args[0] = (int) param.args[0] & ~WindowInsets.Type.displayCutout();
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Log.d(LOG_TAG, "getInsetsIgnoringVisibility(" + param.args[0] + ") = " + param.getResult().toString());
            }
        });

//        XposedHelpers.findAndHookMethod(WindowInsets.class, "getSystemWindowInsets", new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                param.setResult(((WindowInsets) param.getResult()).consumeDisplayCutout());
////                Log.d(LOG_TAG, "getSystemWindowInsets() = " + param.getResult().toString());
////                Log.d(LOG_TAG, Log.getStackTraceString(new Exception()));
////                Log.d(LOG_TAG, param.thisObject.getClass().toString());
//            }
//        });

//        XposedHelpers.findAndHookMethod(WindowInsets.class, "getSystemWindowInsets", new XC_MethodReplacement() {
//            @Override
//            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
//                return ((WindowInsets) param.thisObject).getInsets(WindowInsets.Type.systemBars());
//            }
//        });

//        XposedHelpers.findAndHookMethod("com.android.internal.policy.DecorView", lpparam.classLoader, "onApplyWindowInsets", WindowInsets.class, new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                param.args[0] = ((WindowInsets) param.args[0]).consumeDisplayCutout();
//            }
//        });

        XposedHelpers.findAndHookConstructor(WindowInsets.class,
                Insets[].class /* typeInsetsMap */, Insets[].class /* typeMaxInsetsMap */,
                boolean[].class /* typeVisibilityMap */, boolean.class /* isRound */,
                boolean.class /* alwaysConsumeSystemBars */, DisplayCutout.class /* displayCutout */,
                "android.view.RoundedCorners" /* roundedCorners */, "android.view.PrivacyIndicatorBounds" /* privacyIndicatorBounds */,
                int.class /* compatInsetsTypes */, boolean.class /* compatIgnoreVisibility */, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.args[5] = null;
                param.args[8] = ((int) param.args[8]) & ~WindowInsets.Type.displayCutout();
                Log.d(LOG_TAG, "construct windowinsets");
            }
        });
    }
}
