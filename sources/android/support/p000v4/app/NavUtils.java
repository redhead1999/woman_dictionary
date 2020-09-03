package android.support.p000v4.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.p000v4.content.IntentCompat;
import android.util.Log;

/* renamed from: android.support.v4.app.NavUtils */
public final class NavUtils {
    private static final NavUtilsImpl IMPL;
    public static final String PARENT_ACTIVITY = "android.support.PARENT_ACTIVITY";
    private static final String TAG = "NavUtils";

    /* renamed from: android.support.v4.app.NavUtils$NavUtilsImpl */
    interface NavUtilsImpl {
        Intent getParentActivityIntent(Activity activity);

        String getParentActivityName(Context context, ActivityInfo activityInfo);

        void navigateUpTo(Activity activity, Intent intent);

        boolean shouldUpRecreateTask(Activity activity, Intent intent);
    }

    /* renamed from: android.support.v4.app.NavUtils$NavUtilsImplBase */
    static class NavUtilsImplBase implements NavUtilsImpl {
        NavUtilsImplBase() {
        }

        public Intent getParentActivityIntent(Activity activity) {
            String parentActivityName = NavUtils.getParentActivityName(activity);
            if (parentActivityName == null) {
                return null;
            }
            ComponentName componentName = new ComponentName(activity, parentActivityName);
            try {
                return NavUtils.getParentActivityName(activity, componentName) == null ? IntentCompat.makeMainActivity(componentName) : new Intent().setComponent(componentName);
            } catch (NameNotFoundException unused) {
                StringBuilder sb = new StringBuilder();
                sb.append("getParentActivityIntent: bad parentActivityName '");
                sb.append(parentActivityName);
                sb.append("' in manifest");
                Log.e(NavUtils.TAG, sb.toString());
                return null;
            }
        }

        public boolean shouldUpRecreateTask(Activity activity, Intent intent) {
            String action = activity.getIntent().getAction();
            return action != null && !action.equals("android.intent.action.MAIN");
        }

        public void navigateUpTo(Activity activity, Intent intent) {
            intent.addFlags(67108864);
            activity.startActivity(intent);
            activity.finish();
        }

        public String getParentActivityName(Context context, ActivityInfo activityInfo) {
            if (activityInfo.metaData == null) {
                return null;
            }
            String string = activityInfo.metaData.getString(NavUtils.PARENT_ACTIVITY);
            if (string == null) {
                return null;
            }
            if (string.charAt(0) == '.') {
                StringBuilder sb = new StringBuilder();
                sb.append(context.getPackageName());
                sb.append(string);
                string = sb.toString();
            }
            return string;
        }
    }

    /* renamed from: android.support.v4.app.NavUtils$NavUtilsImplJB */
    static class NavUtilsImplJB extends NavUtilsImplBase {
        NavUtilsImplJB() {
        }

        public Intent getParentActivityIntent(Activity activity) {
            Intent parentActivityIntent = NavUtilsJB.getParentActivityIntent(activity);
            return parentActivityIntent == null ? superGetParentActivityIntent(activity) : parentActivityIntent;
        }

        /* access modifiers changed from: 0000 */
        public Intent superGetParentActivityIntent(Activity activity) {
            return super.getParentActivityIntent(activity);
        }

        public boolean shouldUpRecreateTask(Activity activity, Intent intent) {
            return NavUtilsJB.shouldUpRecreateTask(activity, intent);
        }

        public void navigateUpTo(Activity activity, Intent intent) {
            NavUtilsJB.navigateUpTo(activity, intent);
        }

        public String getParentActivityName(Context context, ActivityInfo activityInfo) {
            String parentActivityName = NavUtilsJB.getParentActivityName(activityInfo);
            return parentActivityName == null ? super.getParentActivityName(context, activityInfo) : parentActivityName;
        }
    }

    static {
        if (VERSION.SDK_INT >= 16) {
            IMPL = new NavUtilsImplJB();
        } else {
            IMPL = new NavUtilsImplBase();
        }
    }

    public static boolean shouldUpRecreateTask(Activity activity, Intent intent) {
        return IMPL.shouldUpRecreateTask(activity, intent);
    }

    public static void navigateUpFromSameTask(Activity activity) {
        Intent parentActivityIntent = getParentActivityIntent(activity);
        if (parentActivityIntent != null) {
            navigateUpTo(activity, parentActivityIntent);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Activity ");
        sb.append(activity.getClass().getSimpleName());
        sb.append(" does not have a parent activity name specified.");
        sb.append(" (Did you forget to add the android.support.PARENT_ACTIVITY <meta-data> ");
        sb.append(" element in your manifest?)");
        throw new IllegalArgumentException(sb.toString());
    }

    public static void navigateUpTo(Activity activity, Intent intent) {
        IMPL.navigateUpTo(activity, intent);
    }

    public static Intent getParentActivityIntent(Activity activity) {
        return IMPL.getParentActivityIntent(activity);
    }

    public static Intent getParentActivityIntent(Context context, Class<?> cls) throws NameNotFoundException {
        String parentActivityName = getParentActivityName(context, new ComponentName(context, cls));
        if (parentActivityName == null) {
            return null;
        }
        ComponentName componentName = new ComponentName(context, parentActivityName);
        return getParentActivityName(context, componentName) == null ? IntentCompat.makeMainActivity(componentName) : new Intent().setComponent(componentName);
    }

    public static Intent getParentActivityIntent(Context context, ComponentName componentName) throws NameNotFoundException {
        String parentActivityName = getParentActivityName(context, componentName);
        if (parentActivityName == null) {
            return null;
        }
        ComponentName componentName2 = new ComponentName(componentName.getPackageName(), parentActivityName);
        return getParentActivityName(context, componentName2) == null ? IntentCompat.makeMainActivity(componentName2) : new Intent().setComponent(componentName2);
    }

    @Nullable
    public static String getParentActivityName(Activity activity) {
        try {
            return getParentActivityName(activity, activity.getComponentName());
        } catch (NameNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Nullable
    public static String getParentActivityName(Context context, ComponentName componentName) throws NameNotFoundException {
        return IMPL.getParentActivityName(context, context.getPackageManager().getActivityInfo(componentName, 128));
    }

    private NavUtils() {
    }
}
