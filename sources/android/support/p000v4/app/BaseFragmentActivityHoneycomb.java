package android.support.p000v4.app;

import android.content.Context;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.View;

/* renamed from: android.support.v4.app.BaseFragmentActivityHoneycomb */
abstract class BaseFragmentActivityHoneycomb extends BaseFragmentActivityEclair {
    BaseFragmentActivityHoneycomb() {
    }

    public View onCreateView(View view, String str, Context context, AttributeSet attributeSet) {
        View dispatchFragmentsOnCreateView = dispatchFragmentsOnCreateView(view, str, context, attributeSet);
        return (dispatchFragmentsOnCreateView != null || VERSION.SDK_INT < 11) ? dispatchFragmentsOnCreateView : super.onCreateView(view, str, context, attributeSet);
    }
}
