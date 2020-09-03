package android.support.design.widget;

import android.graphics.drawable.Drawable.ConstantState;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.DrawableContainer.DrawableContainerState;
import android.os.Build.VERSION;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

class DrawableUtils {
    private static final String LOG_TAG = "DrawableUtils";
    private static Field sDrawableContainerStateField;
    private static boolean sDrawableContainerStateFieldFetched;
    private static Method sSetConstantStateMethod;
    private static boolean sSetConstantStateMethodFetched;

    private DrawableUtils() {
    }

    static boolean setContainerConstantState(DrawableContainer drawableContainer, ConstantState constantState) {
        if (VERSION.SDK_INT >= 9) {
            return setContainerConstantStateV9(drawableContainer, constantState);
        }
        return setContainerConstantStateV7(drawableContainer, constantState);
    }

    private static boolean setContainerConstantStateV9(DrawableContainer drawableContainer, ConstantState constantState) {
        boolean z = sSetConstantStateMethodFetched;
        String str = LOG_TAG;
        if (!z) {
            try {
                sSetConstantStateMethod = DrawableContainer.class.getDeclaredMethod("setConstantState", new Class[]{DrawableContainerState.class});
                sSetConstantStateMethod.setAccessible(true);
            } catch (NoSuchMethodException unused) {
                Log.e(str, "Could not fetch setConstantState(). Oh well.");
            }
            sSetConstantStateMethodFetched = true;
        }
        Method method = sSetConstantStateMethod;
        if (method != null) {
            try {
                method.invoke(drawableContainer, new Object[]{constantState});
                return true;
            } catch (Exception unused2) {
                Log.e(str, "Could not invoke setConstantState(). Oh well.");
            }
        }
        return false;
    }

    private static boolean setContainerConstantStateV7(DrawableContainer drawableContainer, ConstantState constantState) {
        boolean z = sDrawableContainerStateFieldFetched;
        String str = LOG_TAG;
        if (!z) {
            try {
                sDrawableContainerStateField = DrawableContainer.class.getDeclaredField("mDrawableContainerStateField");
                sDrawableContainerStateField.setAccessible(true);
            } catch (NoSuchFieldException unused) {
                Log.e(str, "Could not fetch mDrawableContainerStateField. Oh well.");
            }
            sDrawableContainerStateFieldFetched = true;
        }
        Field field = sDrawableContainerStateField;
        if (field != null) {
            try {
                field.set(drawableContainer, constantState);
                return true;
            } catch (Exception unused2) {
                Log.e(str, "Could not set mDrawableContainerStateField. Oh well.");
            }
        }
        return false;
    }
}
