package android.support.p000v4.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.LayoutInflater.Factory2;
import android.view.View;
import java.lang.reflect.Field;

/* renamed from: android.support.v4.view.LayoutInflaterCompatHC */
class LayoutInflaterCompatHC {
    private static final String TAG = "LayoutInflaterCompatHC";
    private static boolean sCheckedField;
    private static Field sLayoutInflaterFactory2Field;

    /* renamed from: android.support.v4.view.LayoutInflaterCompatHC$FactoryWrapperHC */
    static class FactoryWrapperHC extends FactoryWrapper implements Factory2 {
        FactoryWrapperHC(LayoutInflaterFactory layoutInflaterFactory) {
            super(layoutInflaterFactory);
        }

        public View onCreateView(View view, String str, Context context, AttributeSet attributeSet) {
            return this.mDelegateFactory.onCreateView(view, str, context, attributeSet);
        }
    }

    LayoutInflaterCompatHC() {
    }

    static void setFactory(LayoutInflater layoutInflater, LayoutInflaterFactory layoutInflaterFactory) {
        FactoryWrapperHC factoryWrapperHC = layoutInflaterFactory != null ? new FactoryWrapperHC(layoutInflaterFactory) : null;
        layoutInflater.setFactory2(factoryWrapperHC);
        Factory factory = layoutInflater.getFactory();
        if (factory instanceof Factory2) {
            forceSetFactory2(layoutInflater, (Factory2) factory);
        } else {
            forceSetFactory2(layoutInflater, factoryWrapperHC);
        }
    }

    static void forceSetFactory2(LayoutInflater layoutInflater, Factory2 factory2) {
        boolean z = sCheckedField;
        String str = "; inflation may have unexpected results.";
        String str2 = TAG;
        if (!z) {
            try {
                sLayoutInflaterFactory2Field = LayoutInflater.class.getDeclaredField("mFactory2");
                sLayoutInflaterFactory2Field.setAccessible(true);
            } catch (NoSuchFieldException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("forceSetFactory2 Could not find field 'mFactory2' on class ");
                sb.append(LayoutInflater.class.getName());
                sb.append(str);
                Log.e(str2, sb.toString(), e);
            }
            sCheckedField = true;
        }
        Field field = sLayoutInflaterFactory2Field;
        if (field != null) {
            try {
                field.set(layoutInflater, factory2);
            } catch (IllegalAccessException e2) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("forceSetFactory2 could not set the Factory2 on LayoutInflater ");
                sb2.append(layoutInflater);
                sb2.append(str);
                Log.e(str2, sb2.toString(), e2);
            }
        }
    }
}
