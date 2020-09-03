package android.support.p000v4.app;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.p000v4.app.BackStackRecord.TransitionState;
import android.support.p000v4.app.Fragment.SavedState;
import android.support.p000v4.app.FragmentManager.BackStackEntry;
import android.support.p000v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.p000v4.util.DebugUtils;
import android.support.p000v4.util.LogWriter;
import android.support.p000v4.view.LayoutInflaterFactory;
import android.support.p000v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* renamed from: android.support.v4.app.FragmentManagerImpl */
/* compiled from: FragmentManager */
final class FragmentManagerImpl extends FragmentManager implements LayoutInflaterFactory {
    static final Interpolator ACCELERATE_CUBIC = new AccelerateInterpolator(1.5f);
    static final Interpolator ACCELERATE_QUINT = new AccelerateInterpolator(2.5f);
    static final int ANIM_DUR = 220;
    public static final int ANIM_STYLE_CLOSE_ENTER = 3;
    public static final int ANIM_STYLE_CLOSE_EXIT = 4;
    public static final int ANIM_STYLE_FADE_ENTER = 5;
    public static final int ANIM_STYLE_FADE_EXIT = 6;
    public static final int ANIM_STYLE_OPEN_ENTER = 1;
    public static final int ANIM_STYLE_OPEN_EXIT = 2;
    static boolean DEBUG = false;
    static final Interpolator DECELERATE_CUBIC = new DecelerateInterpolator(1.5f);
    static final Interpolator DECELERATE_QUINT = new DecelerateInterpolator(2.5f);
    static final boolean HONEYCOMB = (VERSION.SDK_INT >= 11);
    static final String TAG = "FragmentManager";
    static final String TARGET_REQUEST_CODE_STATE_TAG = "android:target_req_state";
    static final String TARGET_STATE_TAG = "android:target_state";
    static final String USER_VISIBLE_HINT_TAG = "android:user_visible_hint";
    static final String VIEW_STATE_TAG = "android:view_state";
    static Field sAnimationListenerField = null;
    ArrayList<Fragment> mActive;
    ArrayList<Fragment> mAdded;
    ArrayList<Integer> mAvailBackStackIndices;
    ArrayList<Integer> mAvailIndices;
    ArrayList<BackStackRecord> mBackStack;
    ArrayList<OnBackStackChangedListener> mBackStackChangeListeners;
    ArrayList<BackStackRecord> mBackStackIndices;
    FragmentContainer mContainer;
    FragmentController mController;
    ArrayList<Fragment> mCreatedMenus;
    int mCurState = 0;
    boolean mDestroyed;
    Runnable mExecCommit = new Runnable() {
        public void run() {
            FragmentManagerImpl.this.execPendingActions();
        }
    };
    boolean mExecutingActions;
    boolean mHavePendingDeferredStart;
    FragmentHostCallback mHost;
    boolean mNeedMenuInvalidate;
    String mNoTransactionsBecause;
    Fragment mParent;
    ArrayList<Runnable> mPendingActions;
    SparseArray<Parcelable> mStateArray = null;
    Bundle mStateBundle = null;
    boolean mStateSaved;
    Runnable[] mTmpActions;

    /* renamed from: android.support.v4.app.FragmentManagerImpl$AnimateOnHWLayerIfNeededListener */
    /* compiled from: FragmentManager */
    static class AnimateOnHWLayerIfNeededListener implements AnimationListener {
        private AnimationListener mOrignalListener = null;
        private boolean mShouldRunOnHWLayer = false;
        /* access modifiers changed from: private */
        public View mView = null;

        public AnimateOnHWLayerIfNeededListener(View view, Animation animation) {
            if (view != null && animation != null) {
                this.mView = view;
            }
        }

        public AnimateOnHWLayerIfNeededListener(View view, Animation animation, AnimationListener animationListener) {
            if (view != null && animation != null) {
                this.mOrignalListener = animationListener;
                this.mView = view;
            }
        }

        @CallSuper
        public void onAnimationStart(Animation animation) {
            View view = this.mView;
            if (view != null) {
                this.mShouldRunOnHWLayer = FragmentManagerImpl.shouldRunOnHWLayer(view, animation);
                if (this.mShouldRunOnHWLayer) {
                    this.mView.post(new Runnable() {
                        public void run() {
                            ViewCompat.setLayerType(AnimateOnHWLayerIfNeededListener.this.mView, 2, null);
                        }
                    });
                }
            }
            AnimationListener animationListener = this.mOrignalListener;
            if (animationListener != null) {
                animationListener.onAnimationStart(animation);
            }
        }

        @CallSuper
        public void onAnimationEnd(Animation animation) {
            View view = this.mView;
            if (view != null && this.mShouldRunOnHWLayer) {
                view.post(new Runnable() {
                    public void run() {
                        ViewCompat.setLayerType(AnimateOnHWLayerIfNeededListener.this.mView, 0, null);
                    }
                });
            }
            AnimationListener animationListener = this.mOrignalListener;
            if (animationListener != null) {
                animationListener.onAnimationEnd(animation);
            }
        }

        public void onAnimationRepeat(Animation animation) {
            AnimationListener animationListener = this.mOrignalListener;
            if (animationListener != null) {
                animationListener.onAnimationRepeat(animation);
            }
        }
    }

    /* renamed from: android.support.v4.app.FragmentManagerImpl$FragmentTag */
    /* compiled from: FragmentManager */
    static class FragmentTag {
        public static final int[] Fragment = {16842755, 16842960, 16842961};
        public static final int Fragment_id = 1;
        public static final int Fragment_name = 0;
        public static final int Fragment_tag = 2;

        FragmentTag() {
        }
    }

    public static int reverseTransit(int i) {
        if (i == 4097) {
            return 8194;
        }
        if (i == 4099) {
            return FragmentTransaction.TRANSIT_FRAGMENT_FADE;
        }
        if (i != 8194) {
            return 0;
        }
        return FragmentTransaction.TRANSIT_FRAGMENT_OPEN;
    }

    public static int transitToStyleIndex(int i, boolean z) {
        if (i == 4097) {
            return z ? 1 : 2;
        }
        if (i == 4099) {
            return z ? 5 : 6;
        }
        if (i != 8194) {
            return -1;
        }
        return z ? 3 : 4;
    }

    /* access modifiers changed from: 0000 */
    public LayoutInflaterFactory getLayoutInflaterFactory() {
        return this;
    }

    FragmentManagerImpl() {
    }

    static boolean modifiesAlpha(Animation animation) {
        if (animation instanceof AlphaAnimation) {
            return true;
        }
        if (animation instanceof AnimationSet) {
            List animations = ((AnimationSet) animation).getAnimations();
            for (int i = 0; i < animations.size(); i++) {
                if (animations.get(i) instanceof AlphaAnimation) {
                    return true;
                }
            }
        }
        return false;
    }

    static boolean shouldRunOnHWLayer(View view, Animation animation) {
        return VERSION.SDK_INT >= 19 && ViewCompat.getLayerType(view) == 0 && ViewCompat.hasOverlappingRendering(view) && modifiesAlpha(animation);
    }

    private void throwException(RuntimeException runtimeException) {
        String message = runtimeException.getMessage();
        String str = TAG;
        Log.e(str, message);
        Log.e(str, "Activity state:");
        PrintWriter printWriter = new PrintWriter(new LogWriter(str));
        FragmentHostCallback fragmentHostCallback = this.mHost;
        String str2 = "Failed dumping state";
        String str3 = "  ";
        if (fragmentHostCallback != null) {
            try {
                fragmentHostCallback.onDump(str3, null, printWriter, new String[0]);
            } catch (Exception e) {
                Log.e(str, str2, e);
            }
        } else {
            try {
                dump(str3, null, printWriter, new String[0]);
            } catch (Exception e2) {
                Log.e(str, str2, e2);
            }
        }
        throw runtimeException;
    }

    public FragmentTransaction beginTransaction() {
        return new BackStackRecord(this);
    }

    public boolean executePendingTransactions() {
        return execPendingActions();
    }

    public void popBackStack() {
        enqueueAction(new Runnable() {
            public void run() {
                FragmentManagerImpl fragmentManagerImpl = FragmentManagerImpl.this;
                fragmentManagerImpl.popBackStackState(fragmentManagerImpl.mHost.getHandler(), null, -1, 0);
            }
        }, false);
    }

    public boolean popBackStackImmediate() {
        checkStateLoss();
        executePendingTransactions();
        return popBackStackState(this.mHost.getHandler(), null, -1, 0);
    }

    public void popBackStack(final String str, final int i) {
        enqueueAction(new Runnable() {
            public void run() {
                FragmentManagerImpl fragmentManagerImpl = FragmentManagerImpl.this;
                fragmentManagerImpl.popBackStackState(fragmentManagerImpl.mHost.getHandler(), str, -1, i);
            }
        }, false);
    }

    public boolean popBackStackImmediate(String str, int i) {
        checkStateLoss();
        executePendingTransactions();
        return popBackStackState(this.mHost.getHandler(), str, -1, i);
    }

    public void popBackStack(final int i, final int i2) {
        if (i >= 0) {
            enqueueAction(new Runnable() {
                public void run() {
                    FragmentManagerImpl fragmentManagerImpl = FragmentManagerImpl.this;
                    fragmentManagerImpl.popBackStackState(fragmentManagerImpl.mHost.getHandler(), null, i, i2);
                }
            }, false);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Bad id: ");
        sb.append(i);
        throw new IllegalArgumentException(sb.toString());
    }

    public boolean popBackStackImmediate(int i, int i2) {
        checkStateLoss();
        executePendingTransactions();
        if (i >= 0) {
            return popBackStackState(this.mHost.getHandler(), null, i, i2);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Bad id: ");
        sb.append(i);
        throw new IllegalArgumentException(sb.toString());
    }

    public int getBackStackEntryCount() {
        ArrayList<BackStackRecord> arrayList = this.mBackStack;
        if (arrayList != null) {
            return arrayList.size();
        }
        return 0;
    }

    public BackStackEntry getBackStackEntryAt(int i) {
        return (BackStackEntry) this.mBackStack.get(i);
    }

    public void addOnBackStackChangedListener(OnBackStackChangedListener onBackStackChangedListener) {
        if (this.mBackStackChangeListeners == null) {
            this.mBackStackChangeListeners = new ArrayList<>();
        }
        this.mBackStackChangeListeners.add(onBackStackChangedListener);
    }

    public void removeOnBackStackChangedListener(OnBackStackChangedListener onBackStackChangedListener) {
        ArrayList<OnBackStackChangedListener> arrayList = this.mBackStackChangeListeners;
        if (arrayList != null) {
            arrayList.remove(onBackStackChangedListener);
        }
    }

    public void putFragment(Bundle bundle, String str, Fragment fragment) {
        if (fragment.mIndex < 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Fragment ");
            sb.append(fragment);
            sb.append(" is not currently in the FragmentManager");
            throwException(new IllegalStateException(sb.toString()));
        }
        bundle.putInt(str, fragment.mIndex);
    }

    public Fragment getFragment(Bundle bundle, String str) {
        int i = bundle.getInt(str, -1);
        if (i == -1) {
            return null;
        }
        String str2 = ": index ";
        String str3 = "Fragment no longer exists for key ";
        if (i >= this.mActive.size()) {
            StringBuilder sb = new StringBuilder();
            sb.append(str3);
            sb.append(str);
            sb.append(str2);
            sb.append(i);
            throwException(new IllegalStateException(sb.toString()));
        }
        Fragment fragment = (Fragment) this.mActive.get(i);
        if (fragment == null) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str3);
            sb2.append(str);
            sb2.append(str2);
            sb2.append(i);
            throwException(new IllegalStateException(sb2.toString()));
        }
        return fragment;
    }

    public List<Fragment> getFragments() {
        return this.mActive;
    }

    public SavedState saveFragmentInstanceState(Fragment fragment) {
        if (fragment.mIndex < 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Fragment ");
            sb.append(fragment);
            sb.append(" is not currently in the FragmentManager");
            throwException(new IllegalStateException(sb.toString()));
        }
        if (fragment.mState <= 0) {
            return null;
        }
        Bundle saveFragmentBasicState = saveFragmentBasicState(fragment);
        if (saveFragmentBasicState != null) {
            return new SavedState(saveFragmentBasicState);
        }
        return null;
    }

    public boolean isDestroyed() {
        return this.mDestroyed;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("FragmentManager{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(" in ");
        Fragment fragment = this.mParent;
        if (fragment != null) {
            DebugUtils.buildShortClassTag(fragment, sb);
        } else {
            DebugUtils.buildShortClassTag(this.mHost, sb);
        }
        sb.append("}}");
        return sb.toString();
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("    ");
        String sb2 = sb.toString();
        ArrayList<Fragment> arrayList = this.mActive;
        if (arrayList != null) {
            int size = arrayList.size();
            if (size > 0) {
                printWriter.print(str);
                printWriter.print("Active Fragments in ");
                printWriter.print(Integer.toHexString(System.identityHashCode(this)));
                printWriter.println(":");
                for (int i = 0; i < size; i++) {
                    Fragment fragment = (Fragment) this.mActive.get(i);
                    printWriter.print(str);
                    printWriter.print("  #");
                    printWriter.print(i);
                    printWriter.print(": ");
                    printWriter.println(fragment);
                    if (fragment != null) {
                        fragment.dump(sb2, fileDescriptor, printWriter, strArr);
                    }
                }
            }
        }
        ArrayList<Fragment> arrayList2 = this.mAdded;
        if (arrayList2 != null) {
            int size2 = arrayList2.size();
            if (size2 > 0) {
                printWriter.print(str);
                printWriter.println("Added Fragments:");
                for (int i2 = 0; i2 < size2; i2++) {
                    Fragment fragment2 = (Fragment) this.mAdded.get(i2);
                    printWriter.print(str);
                    printWriter.print("  #");
                    printWriter.print(i2);
                    printWriter.print(": ");
                    printWriter.println(fragment2.toString());
                }
            }
        }
        ArrayList<Fragment> arrayList3 = this.mCreatedMenus;
        if (arrayList3 != null) {
            int size3 = arrayList3.size();
            if (size3 > 0) {
                printWriter.print(str);
                printWriter.println("Fragments Created Menus:");
                for (int i3 = 0; i3 < size3; i3++) {
                    Fragment fragment3 = (Fragment) this.mCreatedMenus.get(i3);
                    printWriter.print(str);
                    printWriter.print("  #");
                    printWriter.print(i3);
                    printWriter.print(": ");
                    printWriter.println(fragment3.toString());
                }
            }
        }
        ArrayList<BackStackRecord> arrayList4 = this.mBackStack;
        if (arrayList4 != null) {
            int size4 = arrayList4.size();
            if (size4 > 0) {
                printWriter.print(str);
                printWriter.println("Back Stack:");
                for (int i4 = 0; i4 < size4; i4++) {
                    BackStackRecord backStackRecord = (BackStackRecord) this.mBackStack.get(i4);
                    printWriter.print(str);
                    printWriter.print("  #");
                    printWriter.print(i4);
                    printWriter.print(": ");
                    printWriter.println(backStackRecord.toString());
                    backStackRecord.dump(sb2, fileDescriptor, printWriter, strArr);
                }
            }
        }
        synchronized (this) {
            if (this.mBackStackIndices != null) {
                int size5 = this.mBackStackIndices.size();
                if (size5 > 0) {
                    printWriter.print(str);
                    printWriter.println("Back Stack Indices:");
                    for (int i5 = 0; i5 < size5; i5++) {
                        BackStackRecord backStackRecord2 = (BackStackRecord) this.mBackStackIndices.get(i5);
                        printWriter.print(str);
                        printWriter.print("  #");
                        printWriter.print(i5);
                        printWriter.print(": ");
                        printWriter.println(backStackRecord2);
                    }
                }
            }
            if (this.mAvailBackStackIndices != null && this.mAvailBackStackIndices.size() > 0) {
                printWriter.print(str);
                printWriter.print("mAvailBackStackIndices: ");
                printWriter.println(Arrays.toString(this.mAvailBackStackIndices.toArray()));
            }
        }
        ArrayList<Runnable> arrayList5 = this.mPendingActions;
        if (arrayList5 != null) {
            int size6 = arrayList5.size();
            if (size6 > 0) {
                printWriter.print(str);
                printWriter.println("Pending Actions:");
                for (int i6 = 0; i6 < size6; i6++) {
                    Runnable runnable = (Runnable) this.mPendingActions.get(i6);
                    printWriter.print(str);
                    printWriter.print("  #");
                    printWriter.print(i6);
                    printWriter.print(": ");
                    printWriter.println(runnable);
                }
            }
        }
        printWriter.print(str);
        printWriter.println("FragmentManager misc state:");
        printWriter.print(str);
        printWriter.print("  mHost=");
        printWriter.println(this.mHost);
        printWriter.print(str);
        printWriter.print("  mContainer=");
        printWriter.println(this.mContainer);
        if (this.mParent != null) {
            printWriter.print(str);
            printWriter.print("  mParent=");
            printWriter.println(this.mParent);
        }
        printWriter.print(str);
        printWriter.print("  mCurState=");
        printWriter.print(this.mCurState);
        printWriter.print(" mStateSaved=");
        printWriter.print(this.mStateSaved);
        printWriter.print(" mDestroyed=");
        printWriter.println(this.mDestroyed);
        if (this.mNeedMenuInvalidate) {
            printWriter.print(str);
            printWriter.print("  mNeedMenuInvalidate=");
            printWriter.println(this.mNeedMenuInvalidate);
        }
        if (this.mNoTransactionsBecause != null) {
            printWriter.print(str);
            printWriter.print("  mNoTransactionsBecause=");
            printWriter.println(this.mNoTransactionsBecause);
        }
        ArrayList<Integer> arrayList6 = this.mAvailIndices;
        if (arrayList6 != null && arrayList6.size() > 0) {
            printWriter.print(str);
            printWriter.print("  mAvailIndices: ");
            printWriter.println(Arrays.toString(this.mAvailIndices.toArray()));
        }
    }

    static Animation makeOpenCloseAnimation(Context context, float f, float f2, float f3, float f4) {
        AnimationSet animationSet = new AnimationSet(false);
        ScaleAnimation scaleAnimation = new ScaleAnimation(f, f2, f, f2, 1, 0.5f, 1, 0.5f);
        scaleAnimation.setInterpolator(DECELERATE_QUINT);
        scaleAnimation.setDuration(220);
        animationSet.addAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation(f3, f4);
        alphaAnimation.setInterpolator(DECELERATE_CUBIC);
        alphaAnimation.setDuration(220);
        animationSet.addAnimation(alphaAnimation);
        return animationSet;
    }

    static Animation makeFadeAnimation(Context context, float f, float f2) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(f, f2);
        alphaAnimation.setInterpolator(DECELERATE_CUBIC);
        alphaAnimation.setDuration(220);
        return alphaAnimation;
    }

    /* access modifiers changed from: 0000 */
    public Animation loadAnimation(Fragment fragment, int i, boolean z, int i2) {
        Animation onCreateAnimation = fragment.onCreateAnimation(i, z, fragment.mNextAnim);
        if (onCreateAnimation != null) {
            return onCreateAnimation;
        }
        if (fragment.mNextAnim != 0) {
            Animation loadAnimation = AnimationUtils.loadAnimation(this.mHost.getContext(), fragment.mNextAnim);
            if (loadAnimation != null) {
                return loadAnimation;
            }
        }
        if (i == 0) {
            return null;
        }
        int transitToStyleIndex = transitToStyleIndex(i, z);
        if (transitToStyleIndex < 0) {
            return null;
        }
        switch (transitToStyleIndex) {
            case 1:
                return makeOpenCloseAnimation(this.mHost.getContext(), 1.125f, 1.0f, 0.0f, 1.0f);
            case 2:
                return makeOpenCloseAnimation(this.mHost.getContext(), 1.0f, 0.975f, 1.0f, 0.0f);
            case 3:
                return makeOpenCloseAnimation(this.mHost.getContext(), 0.975f, 1.0f, 0.0f, 1.0f);
            case 4:
                return makeOpenCloseAnimation(this.mHost.getContext(), 1.0f, 1.075f, 1.0f, 0.0f);
            case 5:
                return makeFadeAnimation(this.mHost.getContext(), 0.0f, 1.0f);
            case 6:
                return makeFadeAnimation(this.mHost.getContext(), 1.0f, 0.0f);
            default:
                if (i2 == 0 && this.mHost.onHasWindowAnimations()) {
                    i2 = this.mHost.onGetWindowAnimations();
                }
                if (i2 == 0) {
                }
                return null;
        }
    }

    public void performPendingDeferredStart(Fragment fragment) {
        if (fragment.mDeferStart) {
            if (this.mExecutingActions) {
                this.mHavePendingDeferredStart = true;
                return;
            }
            fragment.mDeferStart = false;
            moveToState(fragment, this.mCurState, 0, 0, false);
        }
    }

    private void setHWLayerAnimListenerIfAlpha(View view, Animation animation) {
        String str = TAG;
        if (view != null && animation != null && shouldRunOnHWLayer(view, animation)) {
            AnimationListener animationListener = null;
            try {
                if (sAnimationListenerField == null) {
                    sAnimationListenerField = Animation.class.getDeclaredField("mListener");
                    sAnimationListenerField.setAccessible(true);
                }
                animationListener = (AnimationListener) sAnimationListenerField.get(animation);
            } catch (NoSuchFieldException e) {
                Log.e(str, "No field with the name mListener is found in Animation class", e);
            } catch (IllegalAccessException e2) {
                Log.e(str, "Cannot access Animation's mListener field", e2);
            }
            animation.setAnimationListener(new AnimateOnHWLayerIfNeededListener(view, animation, animationListener));
        }
    }

    /* JADX WARNING: type inference failed for: r14v1, types: [android.util.SparseArray<android.os.Parcelable>, android.os.Bundle] */
    /* JADX WARNING: type inference failed for: r14v2 */
    /* JADX WARNING: type inference failed for: r14v3, types: [android.os.Bundle, android.view.View] */
    /* JADX WARNING: type inference failed for: r0v28, types: [android.view.ViewGroup] */
    /* JADX WARNING: type inference failed for: r0v32 */
    /* JADX WARNING: type inference failed for: r0v35, types: [android.view.ViewGroup] */
    /* JADX WARNING: type inference failed for: r14v4 */
    /* JADX WARNING: type inference failed for: r14v5 */
    /* JADX WARNING: type inference failed for: r14v6 */
    /* JADX WARNING: type inference failed for: r14v7 */
    /* JADX WARNING: type inference failed for: r14v8 */
    /* JADX WARNING: type inference failed for: r14v9 */
    /* JADX WARNING: type inference failed for: r14v10 */
    /* JADX WARNING: type inference failed for: r14v11 */
    /* JADX WARNING: type inference failed for: r14v12 */
    /* JADX WARNING: type inference failed for: r14v13 */
    /* JADX WARNING: type inference failed for: r14v14 */
    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r14v1, types: [android.util.SparseArray<android.os.Parcelable>, android.os.Bundle]
      assigns: []
      uses: [android.os.Bundle, android.util.SparseArray<android.os.Parcelable>]
      mth insns count: 417
    	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
    	at jadx.core.ProcessClass.process(ProcessClass.java:30)
    	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:49)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:49)
    	at jadx.core.ProcessClass.process(ProcessClass.java:35)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
     */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x0242  */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x0260  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x03ea  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0170  */
    /* JADX WARNING: Unknown variable types count: 8 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void moveToState(android.support.p000v4.app.Fragment r21, int r22, int r23, int r24, boolean r25) {
        /*
            r20 = this;
            r6 = r20
            r7 = r21
            r8 = r23
            r9 = r24
            boolean r0 = r7.mAdded
            r10 = 1
            if (r0 == 0) goto L_0x0015
            boolean r0 = r7.mDetached
            if (r0 == 0) goto L_0x0012
            goto L_0x0015
        L_0x0012:
            r0 = r22
            goto L_0x001a
        L_0x0015:
            r0 = r22
            if (r0 <= r10) goto L_0x001a
            r0 = 1
        L_0x001a:
            boolean r1 = r7.mRemoving
            if (r1 == 0) goto L_0x0024
            int r1 = r7.mState
            if (r0 <= r1) goto L_0x0024
            int r0 = r7.mState
        L_0x0024:
            boolean r1 = r7.mDeferStart
            r11 = 4
            r12 = 3
            if (r1 == 0) goto L_0x0032
            int r1 = r7.mState
            if (r1 >= r11) goto L_0x0032
            if (r0 <= r12) goto L_0x0032
            r13 = 3
            goto L_0x0033
        L_0x0032:
            r13 = r0
        L_0x0033:
            int r0 = r7.mState
            java.lang.String r14 = "Fragment "
            r15 = 2
            r5 = 0
            java.lang.String r4 = "FragmentManager"
            r3 = 0
            if (r0 >= r13) goto L_0x029b
            boolean r0 = r7.mFromLayout
            if (r0 == 0) goto L_0x0047
            boolean r0 = r7.mInLayout
            if (r0 != 0) goto L_0x0047
            return
        L_0x0047:
            android.view.View r0 = r7.mAnimatingAway
            if (r0 == 0) goto L_0x0065
            r7.mAnimatingAway = r3
            int r2 = r7.mStateAfterAnimating
            r16 = 0
            r17 = 0
            r18 = 1
            r0 = r20
            r1 = r21
            r3 = r16
            r19 = r4
            r4 = r17
            r5 = r18
            r0.moveToState(r1, r2, r3, r4, r5)
            goto L_0x0067
        L_0x0065:
            r19 = r4
        L_0x0067:
            int r0 = r7.mState
            r1 = 8
            r2 = 11
            if (r0 == 0) goto L_0x008e
            if (r0 == r10) goto L_0x0088
            if (r0 == r15) goto L_0x0082
            if (r0 == r12) goto L_0x0082
            if (r0 == r11) goto L_0x007c
            r10 = r13
            r3 = r19
            goto L_0x03e6
        L_0x007c:
            r3 = r19
            r0 = 4
            r14 = 0
            goto L_0x025e
        L_0x0082:
            r3 = r19
            r0 = 3
            r14 = 0
            goto L_0x0240
        L_0x0088:
            r3 = r19
            r5 = 0
        L_0x008b:
            r14 = 0
            goto L_0x016e
        L_0x008e:
            boolean r0 = DEBUG
            if (r0 == 0) goto L_0x00a9
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "moveto CREATED: "
            r0.append(r3)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            r3 = r19
            android.util.Log.v(r3, r0)
            goto L_0x00ab
        L_0x00a9:
            r3 = r19
        L_0x00ab:
            android.os.Bundle r0 = r7.mSavedFragmentState
            if (r0 == 0) goto L_0x00f7
            android.os.Bundle r0 = r7.mSavedFragmentState
            android.support.v4.app.FragmentHostCallback r4 = r6.mHost
            android.content.Context r4 = r4.getContext()
            java.lang.ClassLoader r4 = r4.getClassLoader()
            r0.setClassLoader(r4)
            android.os.Bundle r0 = r7.mSavedFragmentState
            java.lang.String r4 = "android:view_state"
            android.util.SparseArray r0 = r0.getSparseParcelableArray(r4)
            r7.mSavedViewState = r0
            android.os.Bundle r0 = r7.mSavedFragmentState
            java.lang.String r4 = "android:target_state"
            android.support.v4.app.Fragment r0 = r6.getFragment(r0, r4)
            r7.mTarget = r0
            android.support.v4.app.Fragment r0 = r7.mTarget
            if (r0 == 0) goto L_0x00e2
            android.os.Bundle r0 = r7.mSavedFragmentState
            java.lang.String r4 = "android:target_req_state"
            r5 = 0
            int r0 = r0.getInt(r4, r5)
            r7.mTargetRequestCode = r0
            goto L_0x00e3
        L_0x00e2:
            r5 = 0
        L_0x00e3:
            android.os.Bundle r0 = r7.mSavedFragmentState
            java.lang.String r4 = "android:user_visible_hint"
            boolean r0 = r0.getBoolean(r4, r10)
            r7.mUserVisibleHint = r0
            boolean r0 = r7.mUserVisibleHint
            if (r0 != 0) goto L_0x00f8
            r7.mDeferStart = r10
            if (r13 <= r12) goto L_0x00f8
            r13 = 3
            goto L_0x00f8
        L_0x00f7:
            r5 = 0
        L_0x00f8:
            android.support.v4.app.FragmentHostCallback r0 = r6.mHost
            r7.mHost = r0
            android.support.v4.app.Fragment r4 = r6.mParent
            r7.mParentFragment = r4
            if (r4 == 0) goto L_0x0105
            android.support.v4.app.FragmentManagerImpl r0 = r4.mChildFragmentManager
            goto L_0x0109
        L_0x0105:
            android.support.v4.app.FragmentManagerImpl r0 = r0.getFragmentManagerImpl()
        L_0x0109:
            r7.mFragmentManager = r0
            r7.mCalled = r5
            android.support.v4.app.FragmentHostCallback r0 = r6.mHost
            android.content.Context r0 = r0.getContext()
            r7.onAttach(r0)
            boolean r0 = r7.mCalled
            if (r0 == 0) goto L_0x0281
            android.support.v4.app.Fragment r0 = r7.mParentFragment
            if (r0 != 0) goto L_0x0123
            android.support.v4.app.FragmentHostCallback r0 = r6.mHost
            r0.onAttachFragment(r7)
        L_0x0123:
            boolean r0 = r7.mRetaining
            if (r0 != 0) goto L_0x012c
            android.os.Bundle r0 = r7.mSavedFragmentState
            r7.performCreate(r0)
        L_0x012c:
            r7.mRetaining = r5
            boolean r0 = r7.mFromLayout
            if (r0 == 0) goto L_0x008b
            android.os.Bundle r0 = r7.mSavedFragmentState
            android.view.LayoutInflater r0 = r7.getLayoutInflater(r0)
            android.os.Bundle r4 = r7.mSavedFragmentState
            r14 = 0
            android.view.View r0 = r7.performCreateView(r0, r14, r4)
            r7.mView = r0
            android.view.View r0 = r7.mView
            if (r0 == 0) goto L_0x016c
            android.view.View r0 = r7.mView
            r7.mInnerView = r0
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r2) goto L_0x0153
            android.view.View r0 = r7.mView
            android.support.p000v4.view.ViewCompat.setSaveFromParentEnabled(r0, r5)
            goto L_0x015b
        L_0x0153:
            android.view.View r0 = r7.mView
            android.view.ViewGroup r0 = android.support.p000v4.app.NoSaveStateFrameLayout.wrap(r0)
            r7.mView = r0
        L_0x015b:
            boolean r0 = r7.mHidden
            if (r0 == 0) goto L_0x0164
            android.view.View r0 = r7.mView
            r0.setVisibility(r1)
        L_0x0164:
            android.view.View r0 = r7.mView
            android.os.Bundle r4 = r7.mSavedFragmentState
            r7.onViewCreated(r0, r4)
            goto L_0x016e
        L_0x016c:
            r7.mInnerView = r14
        L_0x016e:
            if (r13 <= r10) goto L_0x023f
            boolean r0 = DEBUG
            if (r0 == 0) goto L_0x0188
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "moveto ACTIVITY_CREATED: "
            r0.append(r4)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            android.util.Log.v(r3, r0)
        L_0x0188:
            boolean r0 = r7.mFromLayout
            if (r0 != 0) goto L_0x022f
            int r0 = r7.mContainerId
            if (r0 == 0) goto L_0x01da
            android.support.v4.app.FragmentContainer r0 = r6.mContainer
            int r4 = r7.mContainerId
            android.view.View r0 = r0.onFindViewById(r4)
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            if (r0 != 0) goto L_0x01db
            boolean r4 = r7.mRestored
            if (r4 != 0) goto L_0x01db
            java.lang.IllegalArgumentException r4 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            java.lang.String r11 = "No view found for id 0x"
            r15.append(r11)
            int r11 = r7.mContainerId
            java.lang.String r11 = java.lang.Integer.toHexString(r11)
            r15.append(r11)
            java.lang.String r11 = " ("
            r15.append(r11)
            android.content.res.Resources r11 = r21.getResources()
            int r12 = r7.mContainerId
            java.lang.String r11 = r11.getResourceName(r12)
            r15.append(r11)
            java.lang.String r11 = ") for fragment "
            r15.append(r11)
            r15.append(r7)
            java.lang.String r11 = r15.toString()
            r4.<init>(r11)
            r6.throwException(r4)
            goto L_0x01db
        L_0x01da:
            r0 = r14
        L_0x01db:
            r7.mContainer = r0
            android.os.Bundle r4 = r7.mSavedFragmentState
            android.view.LayoutInflater r4 = r7.getLayoutInflater(r4)
            android.os.Bundle r11 = r7.mSavedFragmentState
            android.view.View r4 = r7.performCreateView(r4, r0, r11)
            r7.mView = r4
            android.view.View r4 = r7.mView
            if (r4 == 0) goto L_0x022d
            android.view.View r4 = r7.mView
            r7.mInnerView = r4
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 < r2) goto L_0x01fd
            android.view.View r2 = r7.mView
            android.support.p000v4.view.ViewCompat.setSaveFromParentEnabled(r2, r5)
            goto L_0x0205
        L_0x01fd:
            android.view.View r2 = r7.mView
            android.view.ViewGroup r2 = android.support.p000v4.app.NoSaveStateFrameLayout.wrap(r2)
            r7.mView = r2
        L_0x0205:
            if (r0 == 0) goto L_0x021c
            android.view.animation.Animation r2 = r6.loadAnimation(r7, r8, r10, r9)
            if (r2 == 0) goto L_0x0217
            android.view.View r4 = r7.mView
            r6.setHWLayerAnimListenerIfAlpha(r4, r2)
            android.view.View r4 = r7.mView
            r4.startAnimation(r2)
        L_0x0217:
            android.view.View r2 = r7.mView
            r0.addView(r2)
        L_0x021c:
            boolean r0 = r7.mHidden
            if (r0 == 0) goto L_0x0225
            android.view.View r0 = r7.mView
            r0.setVisibility(r1)
        L_0x0225:
            android.view.View r0 = r7.mView
            android.os.Bundle r1 = r7.mSavedFragmentState
            r7.onViewCreated(r0, r1)
            goto L_0x022f
        L_0x022d:
            r7.mInnerView = r14
        L_0x022f:
            android.os.Bundle r0 = r7.mSavedFragmentState
            r7.performActivityCreated(r0)
            android.view.View r0 = r7.mView
            if (r0 == 0) goto L_0x023d
            android.os.Bundle r0 = r7.mSavedFragmentState
            r7.restoreViewState(r0)
        L_0x023d:
            r7.mSavedFragmentState = r14
        L_0x023f:
            r0 = 3
        L_0x0240:
            if (r13 <= r0) goto L_0x025d
            boolean r0 = DEBUG
            if (r0 == 0) goto L_0x025a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "moveto STARTED: "
            r0.append(r1)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            android.util.Log.v(r3, r0)
        L_0x025a:
            r21.performStart()
        L_0x025d:
            r0 = 4
        L_0x025e:
            if (r13 <= r0) goto L_0x03e5
            boolean r0 = DEBUG
            if (r0 == 0) goto L_0x0278
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "moveto RESUMED: "
            r0.append(r1)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            android.util.Log.v(r3, r0)
        L_0x0278:
            r21.performResume()
            r7.mSavedFragmentState = r14
            r7.mSavedViewState = r14
            goto L_0x03e5
        L_0x0281:
            android.support.v4.app.SuperNotCalledException r0 = new android.support.v4.app.SuperNotCalledException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r14)
            r1.append(r7)
            java.lang.String r2 = " did not call through to super.onAttach()"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x029b:
            r0 = r3
            r3 = r4
            int r1 = r7.mState
            if (r1 <= r13) goto L_0x03e5
            int r1 = r7.mState
            if (r1 == r10) goto L_0x0375
            if (r1 == r15) goto L_0x030b
            r2 = 3
            if (r1 == r2) goto L_0x02ed
            r2 = 4
            if (r1 == r2) goto L_0x02cf
            r2 = 5
            if (r1 == r2) goto L_0x02b2
            goto L_0x03e5
        L_0x02b2:
            if (r13 >= r2) goto L_0x02cf
            boolean r1 = DEBUG
            if (r1 == 0) goto L_0x02cc
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "movefrom RESUMED: "
            r1.append(r2)
            r1.append(r7)
            java.lang.String r1 = r1.toString()
            android.util.Log.v(r3, r1)
        L_0x02cc:
            r21.performPause()
        L_0x02cf:
            r1 = 4
            if (r13 >= r1) goto L_0x02ed
            boolean r1 = DEBUG
            if (r1 == 0) goto L_0x02ea
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "movefrom STARTED: "
            r1.append(r2)
            r1.append(r7)
            java.lang.String r1 = r1.toString()
            android.util.Log.v(r3, r1)
        L_0x02ea:
            r21.performStop()
        L_0x02ed:
            r1 = 3
            if (r13 >= r1) goto L_0x030b
            boolean r1 = DEBUG
            if (r1 == 0) goto L_0x0308
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "movefrom STOPPED: "
            r1.append(r2)
            r1.append(r7)
            java.lang.String r1 = r1.toString()
            android.util.Log.v(r3, r1)
        L_0x0308:
            r21.performReallyStop()
        L_0x030b:
            if (r13 >= r15) goto L_0x0375
            boolean r1 = DEBUG
            if (r1 == 0) goto L_0x0325
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "movefrom ACTIVITY_CREATED: "
            r1.append(r2)
            r1.append(r7)
            java.lang.String r1 = r1.toString()
            android.util.Log.v(r3, r1)
        L_0x0325:
            android.view.View r1 = r7.mView
            if (r1 == 0) goto L_0x0338
            android.support.v4.app.FragmentHostCallback r1 = r6.mHost
            boolean r1 = r1.onShouldSaveFragmentState(r7)
            if (r1 == 0) goto L_0x0338
            android.util.SparseArray<android.os.Parcelable> r1 = r7.mSavedViewState
            if (r1 != 0) goto L_0x0338
            r20.saveFragmentViewState(r21)
        L_0x0338:
            r21.performDestroyView()
            android.view.View r1 = r7.mView
            if (r1 == 0) goto L_0x036f
            android.view.ViewGroup r1 = r7.mContainer
            if (r1 == 0) goto L_0x036f
            int r1 = r6.mCurState
            if (r1 <= 0) goto L_0x0350
            boolean r1 = r6.mDestroyed
            if (r1 != 0) goto L_0x0350
            android.view.animation.Animation r1 = r6.loadAnimation(r7, r8, r5, r9)
            goto L_0x0351
        L_0x0350:
            r1 = r0
        L_0x0351:
            if (r1 == 0) goto L_0x0368
            android.view.View r2 = r7.mView
            r7.mAnimatingAway = r2
            r7.mStateAfterAnimating = r13
            android.view.View r2 = r7.mView
            android.support.v4.app.FragmentManagerImpl$5 r4 = new android.support.v4.app.FragmentManagerImpl$5
            r4.<init>(r2, r1, r7)
            r1.setAnimationListener(r4)
            android.view.View r2 = r7.mView
            r2.startAnimation(r1)
        L_0x0368:
            android.view.ViewGroup r1 = r7.mContainer
            android.view.View r2 = r7.mView
            r1.removeView(r2)
        L_0x036f:
            r7.mContainer = r0
            r7.mView = r0
            r7.mInnerView = r0
        L_0x0375:
            if (r13 >= r10) goto L_0x03e5
            boolean r1 = r6.mDestroyed
            if (r1 == 0) goto L_0x0386
            android.view.View r1 = r7.mAnimatingAway
            if (r1 == 0) goto L_0x0386
            android.view.View r1 = r7.mAnimatingAway
            r7.mAnimatingAway = r0
            r1.clearAnimation()
        L_0x0386:
            android.view.View r1 = r7.mAnimatingAway
            if (r1 == 0) goto L_0x038d
            r7.mStateAfterAnimating = r13
            goto L_0x03e6
        L_0x038d:
            boolean r1 = DEBUG
            if (r1 == 0) goto L_0x03a5
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "movefrom CREATED: "
            r1.append(r2)
            r1.append(r7)
            java.lang.String r1 = r1.toString()
            android.util.Log.v(r3, r1)
        L_0x03a5:
            boolean r1 = r7.mRetaining
            if (r1 != 0) goto L_0x03ad
            r21.performDestroy()
            goto L_0x03af
        L_0x03ad:
            r7.mState = r5
        L_0x03af:
            r7.mCalled = r5
            r21.onDetach()
            boolean r1 = r7.mCalled
            if (r1 == 0) goto L_0x03cb
            if (r25 != 0) goto L_0x03e5
            boolean r1 = r7.mRetaining
            if (r1 != 0) goto L_0x03c2
            r20.makeInactive(r21)
            goto L_0x03e5
        L_0x03c2:
            r7.mHost = r0
            r7.mParentFragment = r0
            r7.mFragmentManager = r0
            r7.mChildFragmentManager = r0
            goto L_0x03e5
        L_0x03cb:
            android.support.v4.app.SuperNotCalledException r0 = new android.support.v4.app.SuperNotCalledException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r14)
            r1.append(r7)
            java.lang.String r2 = " did not call through to super.onDetach()"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x03e5:
            r10 = r13
        L_0x03e6:
            int r0 = r7.mState
            if (r0 == r10) goto L_0x0417
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "moveToState: Fragment state for "
            r0.append(r1)
            r0.append(r7)
            java.lang.String r1 = " not updated inline; "
            r0.append(r1)
            java.lang.String r1 = "expected state "
            r0.append(r1)
            r0.append(r10)
            java.lang.String r1 = " found "
            r0.append(r1)
            int r1 = r7.mState
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            android.util.Log.w(r3, r0)
            r7.mState = r10
        L_0x0417:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.p000v4.app.FragmentManagerImpl.moveToState(android.support.v4.app.Fragment, int, int, int, boolean):void");
    }

    /* access modifiers changed from: 0000 */
    public void moveToState(Fragment fragment) {
        moveToState(fragment, this.mCurState, 0, 0, false);
    }

    /* access modifiers changed from: 0000 */
    public void moveToState(int i, boolean z) {
        moveToState(i, 0, 0, z);
    }

    /* access modifiers changed from: 0000 */
    public void moveToState(int i, int i2, int i3, boolean z) {
        if (this.mHost == null && i != 0) {
            throw new IllegalStateException("No host");
        } else if (z || this.mCurState != i) {
            this.mCurState = i;
            if (this.mActive != null) {
                boolean z2 = false;
                for (int i4 = 0; i4 < this.mActive.size(); i4++) {
                    Fragment fragment = (Fragment) this.mActive.get(i4);
                    if (fragment != null) {
                        moveToState(fragment, i, i2, i3, false);
                        if (fragment.mLoaderManager != null) {
                            z2 |= fragment.mLoaderManager.hasRunningLoaders();
                        }
                    }
                }
                if (!z2) {
                    startPendingDeferredFragments();
                }
                if (this.mNeedMenuInvalidate) {
                    FragmentHostCallback fragmentHostCallback = this.mHost;
                    if (fragmentHostCallback != null && this.mCurState == 5) {
                        fragmentHostCallback.onSupportInvalidateOptionsMenu();
                        this.mNeedMenuInvalidate = false;
                    }
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void startPendingDeferredFragments() {
        if (this.mActive != null) {
            for (int i = 0; i < this.mActive.size(); i++) {
                Fragment fragment = (Fragment) this.mActive.get(i);
                if (fragment != null) {
                    performPendingDeferredStart(fragment);
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void makeActive(Fragment fragment) {
        if (fragment.mIndex < 0) {
            ArrayList<Integer> arrayList = this.mAvailIndices;
            if (arrayList == null || arrayList.size() <= 0) {
                if (this.mActive == null) {
                    this.mActive = new ArrayList<>();
                }
                fragment.setIndex(this.mActive.size(), this.mParent);
                this.mActive.add(fragment);
            } else {
                ArrayList<Integer> arrayList2 = this.mAvailIndices;
                fragment.setIndex(((Integer) arrayList2.remove(arrayList2.size() - 1)).intValue(), this.mParent);
                this.mActive.set(fragment.mIndex, fragment);
            }
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("Allocated fragment index ");
                sb.append(fragment);
                Log.v(TAG, sb.toString());
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void makeInactive(Fragment fragment) {
        if (fragment.mIndex >= 0) {
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("Freeing fragment index ");
                sb.append(fragment);
                Log.v(TAG, sb.toString());
            }
            this.mActive.set(fragment.mIndex, null);
            if (this.mAvailIndices == null) {
                this.mAvailIndices = new ArrayList<>();
            }
            this.mAvailIndices.add(Integer.valueOf(fragment.mIndex));
            this.mHost.inactivateFragment(fragment.mWho);
            fragment.initState();
        }
    }

    public void addFragment(Fragment fragment, boolean z) {
        if (this.mAdded == null) {
            this.mAdded = new ArrayList<>();
        }
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("add: ");
            sb.append(fragment);
            Log.v(TAG, sb.toString());
        }
        makeActive(fragment);
        if (fragment.mDetached) {
            return;
        }
        if (!this.mAdded.contains(fragment)) {
            this.mAdded.add(fragment);
            fragment.mAdded = true;
            fragment.mRemoving = false;
            if (fragment.mHasMenu && fragment.mMenuVisible) {
                this.mNeedMenuInvalidate = true;
            }
            if (z) {
                moveToState(fragment);
                return;
            }
            return;
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Fragment already added: ");
        sb2.append(fragment);
        throw new IllegalStateException(sb2.toString());
    }

    public void removeFragment(Fragment fragment, int i, int i2) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("remove: ");
            sb.append(fragment);
            sb.append(" nesting=");
            sb.append(fragment.mBackStackNesting);
            Log.v(TAG, sb.toString());
        }
        boolean z = !fragment.isInBackStack();
        if (!fragment.mDetached || z) {
            ArrayList<Fragment> arrayList = this.mAdded;
            if (arrayList != null) {
                arrayList.remove(fragment);
            }
            if (fragment.mHasMenu && fragment.mMenuVisible) {
                this.mNeedMenuInvalidate = true;
            }
            fragment.mAdded = false;
            fragment.mRemoving = true;
            moveToState(fragment, z ? 0 : 1, i, i2, false);
        }
    }

    public void hideFragment(Fragment fragment, int i, int i2) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("hide: ");
            sb.append(fragment);
            Log.v(TAG, sb.toString());
        }
        if (!fragment.mHidden) {
            fragment.mHidden = true;
            if (fragment.mView != null) {
                Animation loadAnimation = loadAnimation(fragment, i, false, i2);
                if (loadAnimation != null) {
                    setHWLayerAnimListenerIfAlpha(fragment.mView, loadAnimation);
                    fragment.mView.startAnimation(loadAnimation);
                }
                fragment.mView.setVisibility(8);
            }
            if (fragment.mAdded && fragment.mHasMenu && fragment.mMenuVisible) {
                this.mNeedMenuInvalidate = true;
            }
            fragment.onHiddenChanged(true);
        }
    }

    public void showFragment(Fragment fragment, int i, int i2) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("show: ");
            sb.append(fragment);
            Log.v(TAG, sb.toString());
        }
        if (fragment.mHidden) {
            fragment.mHidden = false;
            if (fragment.mView != null) {
                Animation loadAnimation = loadAnimation(fragment, i, true, i2);
                if (loadAnimation != null) {
                    setHWLayerAnimListenerIfAlpha(fragment.mView, loadAnimation);
                    fragment.mView.startAnimation(loadAnimation);
                }
                fragment.mView.setVisibility(0);
            }
            if (fragment.mAdded && fragment.mHasMenu && fragment.mMenuVisible) {
                this.mNeedMenuInvalidate = true;
            }
            fragment.onHiddenChanged(false);
        }
    }

    public void detachFragment(Fragment fragment, int i, int i2) {
        boolean z = DEBUG;
        String str = TAG;
        if (z) {
            StringBuilder sb = new StringBuilder();
            sb.append("detach: ");
            sb.append(fragment);
            Log.v(str, sb.toString());
        }
        if (!fragment.mDetached) {
            fragment.mDetached = true;
            if (fragment.mAdded) {
                if (this.mAdded != null) {
                    if (DEBUG) {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("remove from detach: ");
                        sb2.append(fragment);
                        Log.v(str, sb2.toString());
                    }
                    this.mAdded.remove(fragment);
                }
                if (fragment.mHasMenu && fragment.mMenuVisible) {
                    this.mNeedMenuInvalidate = true;
                }
                fragment.mAdded = false;
                moveToState(fragment, 1, i, i2, false);
            }
        }
    }

    public void attachFragment(Fragment fragment, int i, int i2) {
        boolean z = DEBUG;
        String str = TAG;
        if (z) {
            StringBuilder sb = new StringBuilder();
            sb.append("attach: ");
            sb.append(fragment);
            Log.v(str, sb.toString());
        }
        if (fragment.mDetached) {
            fragment.mDetached = false;
            if (!fragment.mAdded) {
                if (this.mAdded == null) {
                    this.mAdded = new ArrayList<>();
                }
                if (!this.mAdded.contains(fragment)) {
                    if (DEBUG) {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("add from attach: ");
                        sb2.append(fragment);
                        Log.v(str, sb2.toString());
                    }
                    this.mAdded.add(fragment);
                    fragment.mAdded = true;
                    if (fragment.mHasMenu && fragment.mMenuVisible) {
                        this.mNeedMenuInvalidate = true;
                    }
                    moveToState(fragment, this.mCurState, i, i2, false);
                    return;
                }
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Fragment already added: ");
                sb3.append(fragment);
                throw new IllegalStateException(sb3.toString());
            }
        }
    }

    public Fragment findFragmentById(int i) {
        ArrayList<Fragment> arrayList = this.mAdded;
        if (arrayList != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                Fragment fragment = (Fragment) this.mAdded.get(size);
                if (fragment != null && fragment.mFragmentId == i) {
                    return fragment;
                }
            }
        }
        ArrayList<Fragment> arrayList2 = this.mActive;
        if (arrayList2 != null) {
            for (int size2 = arrayList2.size() - 1; size2 >= 0; size2--) {
                Fragment fragment2 = (Fragment) this.mActive.get(size2);
                if (fragment2 != null && fragment2.mFragmentId == i) {
                    return fragment2;
                }
            }
        }
        return null;
    }

    public Fragment findFragmentByTag(String str) {
        ArrayList<Fragment> arrayList = this.mAdded;
        if (!(arrayList == null || str == null)) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                Fragment fragment = (Fragment) this.mAdded.get(size);
                if (fragment != null && str.equals(fragment.mTag)) {
                    return fragment;
                }
            }
        }
        ArrayList<Fragment> arrayList2 = this.mActive;
        if (!(arrayList2 == null || str == null)) {
            for (int size2 = arrayList2.size() - 1; size2 >= 0; size2--) {
                Fragment fragment2 = (Fragment) this.mActive.get(size2);
                if (fragment2 != null && str.equals(fragment2.mTag)) {
                    return fragment2;
                }
            }
        }
        return null;
    }

    public Fragment findFragmentByWho(String str) {
        ArrayList<Fragment> arrayList = this.mActive;
        if (!(arrayList == null || str == null)) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                Fragment fragment = (Fragment) this.mActive.get(size);
                if (fragment != null) {
                    Fragment findFragmentByWho = fragment.findFragmentByWho(str);
                    if (findFragmentByWho != null) {
                        return findFragmentByWho;
                    }
                }
            }
        }
        return null;
    }

    private void checkStateLoss() {
        if (this.mStateSaved) {
            throw new IllegalStateException("Can not perform this action after onSaveInstanceState");
        } else if (this.mNoTransactionsBecause != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Can not perform this action inside of ");
            sb.append(this.mNoTransactionsBecause);
            throw new IllegalStateException(sb.toString());
        }
    }

    public void enqueueAction(Runnable runnable, boolean z) {
        if (!z) {
            checkStateLoss();
        }
        synchronized (this) {
            if (this.mDestroyed || this.mHost == null) {
                throw new IllegalStateException("Activity has been destroyed");
            }
            if (this.mPendingActions == null) {
                this.mPendingActions = new ArrayList<>();
            }
            this.mPendingActions.add(runnable);
            if (this.mPendingActions.size() == 1) {
                this.mHost.getHandler().removeCallbacks(this.mExecCommit);
                this.mHost.getHandler().post(this.mExecCommit);
            }
        }
    }

    public int allocBackStackIndex(BackStackRecord backStackRecord) {
        synchronized (this) {
            if (this.mAvailBackStackIndices != null) {
                if (this.mAvailBackStackIndices.size() > 0) {
                    int intValue = ((Integer) this.mAvailBackStackIndices.remove(this.mAvailBackStackIndices.size() - 1)).intValue();
                    if (DEBUG) {
                        String str = TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Adding back stack index ");
                        sb.append(intValue);
                        sb.append(" with ");
                        sb.append(backStackRecord);
                        Log.v(str, sb.toString());
                    }
                    this.mBackStackIndices.set(intValue, backStackRecord);
                    return intValue;
                }
            }
            if (this.mBackStackIndices == null) {
                this.mBackStackIndices = new ArrayList<>();
            }
            int size = this.mBackStackIndices.size();
            if (DEBUG) {
                String str2 = TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Setting back stack index ");
                sb2.append(size);
                sb2.append(" to ");
                sb2.append(backStackRecord);
                Log.v(str2, sb2.toString());
            }
            this.mBackStackIndices.add(backStackRecord);
            return size;
        }
    }

    public void setBackStackIndex(int i, BackStackRecord backStackRecord) {
        synchronized (this) {
            if (this.mBackStackIndices == null) {
                this.mBackStackIndices = new ArrayList<>();
            }
            int size = this.mBackStackIndices.size();
            if (i < size) {
                if (DEBUG) {
                    String str = TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Setting back stack index ");
                    sb.append(i);
                    sb.append(" to ");
                    sb.append(backStackRecord);
                    Log.v(str, sb.toString());
                }
                this.mBackStackIndices.set(i, backStackRecord);
            } else {
                while (size < i) {
                    this.mBackStackIndices.add(null);
                    if (this.mAvailBackStackIndices == null) {
                        this.mAvailBackStackIndices = new ArrayList<>();
                    }
                    if (DEBUG) {
                        String str2 = TAG;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("Adding available back stack index ");
                        sb2.append(size);
                        Log.v(str2, sb2.toString());
                    }
                    this.mAvailBackStackIndices.add(Integer.valueOf(size));
                    size++;
                }
                if (DEBUG) {
                    String str3 = TAG;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("Adding back stack index ");
                    sb3.append(i);
                    sb3.append(" with ");
                    sb3.append(backStackRecord);
                    Log.v(str3, sb3.toString());
                }
                this.mBackStackIndices.add(backStackRecord);
            }
        }
    }

    public void freeBackStackIndex(int i) {
        synchronized (this) {
            this.mBackStackIndices.set(i, null);
            if (this.mAvailBackStackIndices == null) {
                this.mAvailBackStackIndices = new ArrayList<>();
            }
            if (DEBUG) {
                String str = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Freeing back stack index ");
                sb.append(i);
                Log.v(str, sb.toString());
            }
            this.mAvailBackStackIndices.add(Integer.valueOf(i));
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0050, code lost:
        r6.mExecutingActions = true;
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0053, code lost:
        if (r3 >= r2) goto L_0x0064;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0055, code lost:
        r6.mTmpActions[r3].run();
        r6.mTmpActions[r3] = null;
        r3 = r3 + 1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean execPendingActions() {
        /*
            r6 = this;
            boolean r0 = r6.mExecutingActions
            if (r0 != 0) goto L_0x00a2
            android.os.Looper r0 = android.os.Looper.myLooper()
            android.support.v4.app.FragmentHostCallback r1 = r6.mHost
            android.os.Handler r1 = r1.getHandler()
            android.os.Looper r1 = r1.getLooper()
            if (r0 != r1) goto L_0x009a
            r0 = 1
            r1 = 0
            r2 = 0
        L_0x0017:
            monitor-enter(r6)
            java.util.ArrayList<java.lang.Runnable> r3 = r6.mPendingActions     // Catch:{ all -> 0x0097 }
            if (r3 == 0) goto L_0x0068
            java.util.ArrayList<java.lang.Runnable> r3 = r6.mPendingActions     // Catch:{ all -> 0x0097 }
            int r3 = r3.size()     // Catch:{ all -> 0x0097 }
            if (r3 != 0) goto L_0x0025
            goto L_0x0068
        L_0x0025:
            java.util.ArrayList<java.lang.Runnable> r2 = r6.mPendingActions     // Catch:{ all -> 0x0097 }
            int r2 = r2.size()     // Catch:{ all -> 0x0097 }
            java.lang.Runnable[] r3 = r6.mTmpActions     // Catch:{ all -> 0x0097 }
            if (r3 == 0) goto L_0x0034
            java.lang.Runnable[] r3 = r6.mTmpActions     // Catch:{ all -> 0x0097 }
            int r3 = r3.length     // Catch:{ all -> 0x0097 }
            if (r3 >= r2) goto L_0x0038
        L_0x0034:
            java.lang.Runnable[] r3 = new java.lang.Runnable[r2]     // Catch:{ all -> 0x0097 }
            r6.mTmpActions = r3     // Catch:{ all -> 0x0097 }
        L_0x0038:
            java.util.ArrayList<java.lang.Runnable> r3 = r6.mPendingActions     // Catch:{ all -> 0x0097 }
            java.lang.Runnable[] r4 = r6.mTmpActions     // Catch:{ all -> 0x0097 }
            r3.toArray(r4)     // Catch:{ all -> 0x0097 }
            java.util.ArrayList<java.lang.Runnable> r3 = r6.mPendingActions     // Catch:{ all -> 0x0097 }
            r3.clear()     // Catch:{ all -> 0x0097 }
            android.support.v4.app.FragmentHostCallback r3 = r6.mHost     // Catch:{ all -> 0x0097 }
            android.os.Handler r3 = r3.getHandler()     // Catch:{ all -> 0x0097 }
            java.lang.Runnable r4 = r6.mExecCommit     // Catch:{ all -> 0x0097 }
            r3.removeCallbacks(r4)     // Catch:{ all -> 0x0097 }
            monitor-exit(r6)     // Catch:{ all -> 0x0097 }
            r6.mExecutingActions = r0
            r3 = 0
        L_0x0053:
            if (r3 >= r2) goto L_0x0064
            java.lang.Runnable[] r4 = r6.mTmpActions
            r4 = r4[r3]
            r4.run()
            java.lang.Runnable[] r4 = r6.mTmpActions
            r5 = 0
            r4[r3] = r5
            int r3 = r3 + 1
            goto L_0x0053
        L_0x0064:
            r6.mExecutingActions = r1
            r2 = 1
            goto L_0x0017
        L_0x0068:
            monitor-exit(r6)     // Catch:{ all -> 0x0097 }
            boolean r0 = r6.mHavePendingDeferredStart
            if (r0 == 0) goto L_0x0096
            r0 = 0
            r3 = 0
        L_0x006f:
            java.util.ArrayList<android.support.v4.app.Fragment> r4 = r6.mActive
            int r4 = r4.size()
            if (r0 >= r4) goto L_0x008f
            java.util.ArrayList<android.support.v4.app.Fragment> r4 = r6.mActive
            java.lang.Object r4 = r4.get(r0)
            android.support.v4.app.Fragment r4 = (android.support.p000v4.app.Fragment) r4
            if (r4 == 0) goto L_0x008c
            android.support.v4.app.LoaderManagerImpl r5 = r4.mLoaderManager
            if (r5 == 0) goto L_0x008c
            android.support.v4.app.LoaderManagerImpl r4 = r4.mLoaderManager
            boolean r4 = r4.hasRunningLoaders()
            r3 = r3 | r4
        L_0x008c:
            int r0 = r0 + 1
            goto L_0x006f
        L_0x008f:
            if (r3 != 0) goto L_0x0096
            r6.mHavePendingDeferredStart = r1
            r6.startPendingDeferredFragments()
        L_0x0096:
            return r2
        L_0x0097:
            r0 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x0097 }
            throw r0
        L_0x009a:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r1 = "Must be called from main thread of process"
            r0.<init>(r1)
            throw r0
        L_0x00a2:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r1 = "Recursive entry to executePendingTransactions"
            r0.<init>(r1)
            goto L_0x00ab
        L_0x00aa:
            throw r0
        L_0x00ab:
            goto L_0x00aa
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.p000v4.app.FragmentManagerImpl.execPendingActions():boolean");
    }

    /* access modifiers changed from: 0000 */
    public void reportBackStackChanged() {
        if (this.mBackStackChangeListeners != null) {
            for (int i = 0; i < this.mBackStackChangeListeners.size(); i++) {
                ((OnBackStackChangedListener) this.mBackStackChangeListeners.get(i)).onBackStackChanged();
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void addBackStackState(BackStackRecord backStackRecord) {
        if (this.mBackStack == null) {
            this.mBackStack = new ArrayList<>();
        }
        this.mBackStack.add(backStackRecord);
        reportBackStackChanged();
    }

    /* access modifiers changed from: 0000 */
    public boolean popBackStackState(Handler handler, String str, int i, int i2) {
        int i3;
        ArrayList<BackStackRecord> arrayList = this.mBackStack;
        if (arrayList == null) {
            return false;
        }
        if (str == null && i < 0 && (i2 & 1) == 0) {
            int size = arrayList.size() - 1;
            if (size < 0) {
                return false;
            }
            BackStackRecord backStackRecord = (BackStackRecord) this.mBackStack.remove(size);
            SparseArray sparseArray = new SparseArray();
            SparseArray sparseArray2 = new SparseArray();
            backStackRecord.calculateBackFragments(sparseArray, sparseArray2);
            backStackRecord.popFromBackStack(true, null, sparseArray, sparseArray2);
            reportBackStackChanged();
        } else {
            if (str != null || i >= 0) {
                i3 = this.mBackStack.size() - 1;
                while (i3 >= 0) {
                    BackStackRecord backStackRecord2 = (BackStackRecord) this.mBackStack.get(i3);
                    if ((str != null && str.equals(backStackRecord2.getName())) || (i >= 0 && i == backStackRecord2.mIndex)) {
                        break;
                    }
                    i3--;
                }
                if (i3 < 0) {
                    return false;
                }
                if ((i2 & 1) != 0) {
                    while (true) {
                        i3--;
                        if (i3 < 0) {
                            break;
                        }
                        BackStackRecord backStackRecord3 = (BackStackRecord) this.mBackStack.get(i3);
                        if ((str == null || !str.equals(backStackRecord3.getName())) && (i < 0 || i != backStackRecord3.mIndex)) {
                            break;
                        }
                    }
                }
            } else {
                i3 = -1;
            }
            if (i3 == this.mBackStack.size() - 1) {
                return false;
            }
            ArrayList arrayList2 = new ArrayList();
            for (int size2 = this.mBackStack.size() - 1; size2 > i3; size2--) {
                arrayList2.add(this.mBackStack.remove(size2));
            }
            int size3 = arrayList2.size() - 1;
            SparseArray sparseArray3 = new SparseArray();
            SparseArray sparseArray4 = new SparseArray();
            for (int i4 = 0; i4 <= size3; i4++) {
                ((BackStackRecord) arrayList2.get(i4)).calculateBackFragments(sparseArray3, sparseArray4);
            }
            TransitionState transitionState = null;
            int i5 = 0;
            while (i5 <= size3) {
                if (DEBUG) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Popping back stack state: ");
                    sb.append(arrayList2.get(i5));
                    Log.v(TAG, sb.toString());
                }
                transitionState = ((BackStackRecord) arrayList2.get(i5)).popFromBackStack(i5 == size3, transitionState, sparseArray3, sparseArray4);
                i5++;
            }
            reportBackStackChanged();
        }
        return true;
    }

    /* access modifiers changed from: 0000 */
    public ArrayList<Fragment> retainNonConfig() {
        ArrayList<Fragment> arrayList = null;
        if (this.mActive != null) {
            for (int i = 0; i < this.mActive.size(); i++) {
                Fragment fragment = (Fragment) this.mActive.get(i);
                if (fragment != null && fragment.mRetainInstance) {
                    if (arrayList == null) {
                        arrayList = new ArrayList<>();
                    }
                    arrayList.add(fragment);
                    fragment.mRetaining = true;
                    fragment.mTargetIndex = fragment.mTarget != null ? fragment.mTarget.mIndex : -1;
                    if (DEBUG) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("retainNonConfig: keeping retained ");
                        sb.append(fragment);
                        Log.v(TAG, sb.toString());
                    }
                }
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: 0000 */
    public void saveFragmentViewState(Fragment fragment) {
        if (fragment.mInnerView != null) {
            SparseArray<Parcelable> sparseArray = this.mStateArray;
            if (sparseArray == null) {
                this.mStateArray = new SparseArray<>();
            } else {
                sparseArray.clear();
            }
            fragment.mInnerView.saveHierarchyState(this.mStateArray);
            if (this.mStateArray.size() > 0) {
                fragment.mSavedViewState = this.mStateArray;
                this.mStateArray = null;
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public Bundle saveFragmentBasicState(Fragment fragment) {
        Bundle bundle;
        if (this.mStateBundle == null) {
            this.mStateBundle = new Bundle();
        }
        fragment.performSaveInstanceState(this.mStateBundle);
        if (!this.mStateBundle.isEmpty()) {
            bundle = this.mStateBundle;
            this.mStateBundle = null;
        } else {
            bundle = null;
        }
        if (fragment.mView != null) {
            saveFragmentViewState(fragment);
        }
        if (fragment.mSavedViewState != null) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putSparseParcelableArray(VIEW_STATE_TAG, fragment.mSavedViewState);
        }
        if (!fragment.mUserVisibleHint) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putBoolean(USER_VISIBLE_HINT_TAG, fragment.mUserVisibleHint);
        }
        return bundle;
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x0162  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.os.Parcelable saveAllState() {
        /*
            r13 = this;
            r13.execPendingActions()
            boolean r0 = HONEYCOMB
            r1 = 1
            if (r0 == 0) goto L_0x000a
            r13.mStateSaved = r1
        L_0x000a:
            java.util.ArrayList<android.support.v4.app.Fragment> r0 = r13.mActive
            r2 = 0
            if (r0 == 0) goto L_0x01ae
            int r0 = r0.size()
            if (r0 > 0) goto L_0x0017
            goto L_0x01ae
        L_0x0017:
            java.util.ArrayList<android.support.v4.app.Fragment> r0 = r13.mActive
            int r0 = r0.size()
            android.support.v4.app.FragmentState[] r3 = new android.support.p000v4.app.FragmentState[r0]
            r4 = 0
            r5 = 0
            r6 = 0
        L_0x0022:
            java.lang.String r7 = " has cleared index: "
            java.lang.String r8 = "Failure saving state: active "
            java.lang.String r9 = ": "
            java.lang.String r10 = "FragmentManager"
            if (r5 >= r0) goto L_0x00e6
            java.util.ArrayList<android.support.v4.app.Fragment> r11 = r13.mActive
            java.lang.Object r11 = r11.get(r5)
            android.support.v4.app.Fragment r11 = (android.support.p000v4.app.Fragment) r11
            if (r11 == 0) goto L_0x00e2
            int r6 = r11.mIndex
            if (r6 >= 0) goto L_0x0059
            java.lang.IllegalStateException r6 = new java.lang.IllegalStateException
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r12.append(r8)
            r12.append(r11)
            r12.append(r7)
            int r7 = r11.mIndex
            r12.append(r7)
            java.lang.String r7 = r12.toString()
            r6.<init>(r7)
            r13.throwException(r6)
        L_0x0059:
            android.support.v4.app.FragmentState r6 = new android.support.v4.app.FragmentState
            r6.<init>(r11)
            r3[r5] = r6
            int r7 = r11.mState
            if (r7 <= 0) goto L_0x00bd
            android.os.Bundle r7 = r6.mSavedFragmentState
            if (r7 != 0) goto L_0x00bd
            android.os.Bundle r7 = r13.saveFragmentBasicState(r11)
            r6.mSavedFragmentState = r7
            android.support.v4.app.Fragment r7 = r11.mTarget
            if (r7 == 0) goto L_0x00c1
            android.support.v4.app.Fragment r7 = r11.mTarget
            int r7 = r7.mIndex
            if (r7 >= 0) goto L_0x009b
            java.lang.IllegalStateException r7 = new java.lang.IllegalStateException
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r12 = "Failure saving state: "
            r8.append(r12)
            r8.append(r11)
            java.lang.String r12 = " has target not in fragment manager: "
            r8.append(r12)
            android.support.v4.app.Fragment r12 = r11.mTarget
            r8.append(r12)
            java.lang.String r8 = r8.toString()
            r7.<init>(r8)
            r13.throwException(r7)
        L_0x009b:
            android.os.Bundle r7 = r6.mSavedFragmentState
            if (r7 != 0) goto L_0x00a6
            android.os.Bundle r7 = new android.os.Bundle
            r7.<init>()
            r6.mSavedFragmentState = r7
        L_0x00a6:
            android.os.Bundle r7 = r6.mSavedFragmentState
            android.support.v4.app.Fragment r8 = r11.mTarget
            java.lang.String r12 = "android:target_state"
            r13.putFragment(r7, r12, r8)
            int r7 = r11.mTargetRequestCode
            if (r7 == 0) goto L_0x00c1
            android.os.Bundle r7 = r6.mSavedFragmentState
            int r8 = r11.mTargetRequestCode
            java.lang.String r12 = "android:target_req_state"
            r7.putInt(r12, r8)
            goto L_0x00c1
        L_0x00bd:
            android.os.Bundle r7 = r11.mSavedFragmentState
            r6.mSavedFragmentState = r7
        L_0x00c1:
            boolean r7 = DEBUG
            if (r7 == 0) goto L_0x00e1
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "Saved state of "
            r7.append(r8)
            r7.append(r11)
            r7.append(r9)
            android.os.Bundle r6 = r6.mSavedFragmentState
            r7.append(r6)
            java.lang.String r6 = r7.toString()
            android.util.Log.v(r10, r6)
        L_0x00e1:
            r6 = 1
        L_0x00e2:
            int r5 = r5 + 1
            goto L_0x0022
        L_0x00e6:
            if (r6 != 0) goto L_0x00f2
            boolean r0 = DEBUG
            if (r0 == 0) goto L_0x00f1
            java.lang.String r0 = "saveAllState: no fragments!"
            android.util.Log.v(r10, r0)
        L_0x00f1:
            return r2
        L_0x00f2:
            java.util.ArrayList<android.support.v4.app.Fragment> r0 = r13.mAdded
            if (r0 == 0) goto L_0x015d
            int r0 = r0.size()
            if (r0 <= 0) goto L_0x015d
            int[] r1 = new int[r0]
            r5 = 0
        L_0x00ff:
            if (r5 >= r0) goto L_0x015e
            java.util.ArrayList<android.support.v4.app.Fragment> r6 = r13.mAdded
            java.lang.Object r6 = r6.get(r5)
            android.support.v4.app.Fragment r6 = (android.support.p000v4.app.Fragment) r6
            int r6 = r6.mIndex
            r1[r5] = r6
            r6 = r1[r5]
            if (r6 >= 0) goto L_0x0136
            java.lang.IllegalStateException r6 = new java.lang.IllegalStateException
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r8)
            java.util.ArrayList<android.support.v4.app.Fragment> r12 = r13.mAdded
            java.lang.Object r12 = r12.get(r5)
            r11.append(r12)
            r11.append(r7)
            r12 = r1[r5]
            r11.append(r12)
            java.lang.String r11 = r11.toString()
            r6.<init>(r11)
            r13.throwException(r6)
        L_0x0136:
            boolean r6 = DEBUG
            if (r6 == 0) goto L_0x015a
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r11 = "saveAllState: adding fragment #"
            r6.append(r11)
            r6.append(r5)
            r6.append(r9)
            java.util.ArrayList<android.support.v4.app.Fragment> r11 = r13.mAdded
            java.lang.Object r11 = r11.get(r5)
            r6.append(r11)
            java.lang.String r6 = r6.toString()
            android.util.Log.v(r10, r6)
        L_0x015a:
            int r5 = r5 + 1
            goto L_0x00ff
        L_0x015d:
            r1 = r2
        L_0x015e:
            java.util.ArrayList<android.support.v4.app.BackStackRecord> r0 = r13.mBackStack
            if (r0 == 0) goto L_0x01a2
            int r0 = r0.size()
            if (r0 <= 0) goto L_0x01a2
            android.support.v4.app.BackStackState[] r2 = new android.support.p000v4.app.BackStackState[r0]
        L_0x016a:
            if (r4 >= r0) goto L_0x01a2
            android.support.v4.app.BackStackState r5 = new android.support.v4.app.BackStackState
            java.util.ArrayList<android.support.v4.app.BackStackRecord> r6 = r13.mBackStack
            java.lang.Object r6 = r6.get(r4)
            android.support.v4.app.BackStackRecord r6 = (android.support.p000v4.app.BackStackRecord) r6
            r5.<init>(r6)
            r2[r4] = r5
            boolean r5 = DEBUG
            if (r5 == 0) goto L_0x019f
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "saveAllState: adding back stack #"
            r5.append(r6)
            r5.append(r4)
            r5.append(r9)
            java.util.ArrayList<android.support.v4.app.BackStackRecord> r6 = r13.mBackStack
            java.lang.Object r6 = r6.get(r4)
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            android.util.Log.v(r10, r5)
        L_0x019f:
            int r4 = r4 + 1
            goto L_0x016a
        L_0x01a2:
            android.support.v4.app.FragmentManagerState r0 = new android.support.v4.app.FragmentManagerState
            r0.<init>()
            r0.mActive = r3
            r0.mAdded = r1
            r0.mBackStack = r2
            return r0
        L_0x01ae:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.p000v4.app.FragmentManagerImpl.saveAllState():android.os.Parcelable");
    }

    /* access modifiers changed from: 0000 */
    public void restoreAllState(Parcelable parcelable, List<Fragment> list) {
        String str;
        if (parcelable != null) {
            FragmentManagerState fragmentManagerState = (FragmentManagerState) parcelable;
            if (fragmentManagerState.mActive != null) {
                String str2 = TAG;
                if (list != null) {
                    for (int i = 0; i < list.size(); i++) {
                        Fragment fragment = (Fragment) list.get(i);
                        if (DEBUG) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("restoreAllState: re-attaching retained ");
                            sb.append(fragment);
                            Log.v(str2, sb.toString());
                        }
                        FragmentState fragmentState = fragmentManagerState.mActive[fragment.mIndex];
                        fragmentState.mInstance = fragment;
                        fragment.mSavedViewState = null;
                        fragment.mBackStackNesting = 0;
                        fragment.mInLayout = false;
                        fragment.mAdded = false;
                        fragment.mTarget = null;
                        if (fragmentState.mSavedFragmentState != null) {
                            fragmentState.mSavedFragmentState.setClassLoader(this.mHost.getContext().getClassLoader());
                            fragment.mSavedViewState = fragmentState.mSavedFragmentState.getSparseParcelableArray(VIEW_STATE_TAG);
                            fragment.mSavedFragmentState = fragmentState.mSavedFragmentState;
                        }
                    }
                }
                this.mActive = new ArrayList<>(fragmentManagerState.mActive.length);
                ArrayList<Integer> arrayList = this.mAvailIndices;
                if (arrayList != null) {
                    arrayList.clear();
                }
                int i2 = 0;
                while (true) {
                    str = ": ";
                    if (i2 >= fragmentManagerState.mActive.length) {
                        break;
                    }
                    FragmentState fragmentState2 = fragmentManagerState.mActive[i2];
                    if (fragmentState2 != null) {
                        Fragment instantiate = fragmentState2.instantiate(this.mHost, this.mParent);
                        if (DEBUG) {
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("restoreAllState: active #");
                            sb2.append(i2);
                            sb2.append(str);
                            sb2.append(instantiate);
                            Log.v(str2, sb2.toString());
                        }
                        this.mActive.add(instantiate);
                        fragmentState2.mInstance = null;
                    } else {
                        this.mActive.add(null);
                        if (this.mAvailIndices == null) {
                            this.mAvailIndices = new ArrayList<>();
                        }
                        if (DEBUG) {
                            StringBuilder sb3 = new StringBuilder();
                            sb3.append("restoreAllState: avail #");
                            sb3.append(i2);
                            Log.v(str2, sb3.toString());
                        }
                        this.mAvailIndices.add(Integer.valueOf(i2));
                    }
                    i2++;
                }
                if (list != null) {
                    for (int i3 = 0; i3 < list.size(); i3++) {
                        Fragment fragment2 = (Fragment) list.get(i3);
                        if (fragment2.mTargetIndex >= 0) {
                            if (fragment2.mTargetIndex < this.mActive.size()) {
                                fragment2.mTarget = (Fragment) this.mActive.get(fragment2.mTargetIndex);
                            } else {
                                StringBuilder sb4 = new StringBuilder();
                                sb4.append("Re-attaching retained fragment ");
                                sb4.append(fragment2);
                                sb4.append(" target no longer exists: ");
                                sb4.append(fragment2.mTargetIndex);
                                Log.w(str2, sb4.toString());
                                fragment2.mTarget = null;
                            }
                        }
                    }
                }
                if (fragmentManagerState.mAdded != null) {
                    this.mAdded = new ArrayList<>(fragmentManagerState.mAdded.length);
                    int i4 = 0;
                    while (i4 < fragmentManagerState.mAdded.length) {
                        Fragment fragment3 = (Fragment) this.mActive.get(fragmentManagerState.mAdded[i4]);
                        if (fragment3 == null) {
                            StringBuilder sb5 = new StringBuilder();
                            sb5.append("No instantiated fragment for index #");
                            sb5.append(fragmentManagerState.mAdded[i4]);
                            throwException(new IllegalStateException(sb5.toString()));
                        }
                        fragment3.mAdded = true;
                        if (DEBUG) {
                            StringBuilder sb6 = new StringBuilder();
                            sb6.append("restoreAllState: added #");
                            sb6.append(i4);
                            sb6.append(str);
                            sb6.append(fragment3);
                            Log.v(str2, sb6.toString());
                        }
                        if (!this.mAdded.contains(fragment3)) {
                            this.mAdded.add(fragment3);
                            i4++;
                        } else {
                            throw new IllegalStateException("Already added!");
                        }
                    }
                } else {
                    this.mAdded = null;
                }
                if (fragmentManagerState.mBackStack != null) {
                    this.mBackStack = new ArrayList<>(fragmentManagerState.mBackStack.length);
                    for (int i5 = 0; i5 < fragmentManagerState.mBackStack.length; i5++) {
                        BackStackRecord instantiate2 = fragmentManagerState.mBackStack[i5].instantiate(this);
                        if (DEBUG) {
                            StringBuilder sb7 = new StringBuilder();
                            sb7.append("restoreAllState: back stack #");
                            sb7.append(i5);
                            sb7.append(" (index ");
                            sb7.append(instantiate2.mIndex);
                            sb7.append("): ");
                            sb7.append(instantiate2);
                            Log.v(str2, sb7.toString());
                            instantiate2.dump("  ", new PrintWriter(new LogWriter(str2)), false);
                        }
                        this.mBackStack.add(instantiate2);
                        if (instantiate2.mIndex >= 0) {
                            setBackStackIndex(instantiate2.mIndex, instantiate2);
                        }
                    }
                } else {
                    this.mBackStack = null;
                }
            }
        }
    }

    public void attachController(FragmentHostCallback fragmentHostCallback, FragmentContainer fragmentContainer, Fragment fragment) {
        if (this.mHost == null) {
            this.mHost = fragmentHostCallback;
            this.mContainer = fragmentContainer;
            this.mParent = fragment;
            return;
        }
        throw new IllegalStateException("Already attached");
    }

    public void noteStateNotSaved() {
        this.mStateSaved = false;
    }

    public void dispatchCreate() {
        this.mStateSaved = false;
        moveToState(1, false);
    }

    public void dispatchActivityCreated() {
        this.mStateSaved = false;
        moveToState(2, false);
    }

    public void dispatchStart() {
        this.mStateSaved = false;
        moveToState(4, false);
    }

    public void dispatchResume() {
        this.mStateSaved = false;
        moveToState(5, false);
    }

    public void dispatchPause() {
        moveToState(4, false);
    }

    public void dispatchStop() {
        this.mStateSaved = true;
        moveToState(3, false);
    }

    public void dispatchReallyStop() {
        moveToState(2, false);
    }

    public void dispatchDestroyView() {
        moveToState(1, false);
    }

    public void dispatchDestroy() {
        this.mDestroyed = true;
        execPendingActions();
        moveToState(0, false);
        this.mHost = null;
        this.mContainer = null;
        this.mParent = null;
    }

    public void dispatchConfigurationChanged(Configuration configuration) {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); i++) {
                Fragment fragment = (Fragment) this.mAdded.get(i);
                if (fragment != null) {
                    fragment.performConfigurationChanged(configuration);
                }
            }
        }
    }

    public void dispatchLowMemory() {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); i++) {
                Fragment fragment = (Fragment) this.mAdded.get(i);
                if (fragment != null) {
                    fragment.performLowMemory();
                }
            }
        }
    }

    public boolean dispatchCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        boolean z;
        ArrayList<Fragment> arrayList = null;
        if (this.mAdded != null) {
            z = false;
            for (int i = 0; i < this.mAdded.size(); i++) {
                Fragment fragment = (Fragment) this.mAdded.get(i);
                if (fragment != null && fragment.performCreateOptionsMenu(menu, menuInflater)) {
                    if (arrayList == null) {
                        arrayList = new ArrayList<>();
                    }
                    arrayList.add(fragment);
                    z = true;
                }
            }
        } else {
            z = false;
        }
        if (this.mCreatedMenus != null) {
            for (int i2 = 0; i2 < this.mCreatedMenus.size(); i2++) {
                Fragment fragment2 = (Fragment) this.mCreatedMenus.get(i2);
                if (arrayList == null || !arrayList.contains(fragment2)) {
                    fragment2.onDestroyOptionsMenu();
                }
            }
        }
        this.mCreatedMenus = arrayList;
        return z;
    }

    public boolean dispatchPrepareOptionsMenu(Menu menu) {
        if (this.mAdded == null) {
            return false;
        }
        boolean z = false;
        for (int i = 0; i < this.mAdded.size(); i++) {
            Fragment fragment = (Fragment) this.mAdded.get(i);
            if (fragment != null && fragment.performPrepareOptionsMenu(menu)) {
                z = true;
            }
        }
        return z;
    }

    public boolean dispatchOptionsItemSelected(MenuItem menuItem) {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); i++) {
                Fragment fragment = (Fragment) this.mAdded.get(i);
                if (fragment != null && fragment.performOptionsItemSelected(menuItem)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean dispatchContextItemSelected(MenuItem menuItem) {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); i++) {
                Fragment fragment = (Fragment) this.mAdded.get(i);
                if (fragment != null && fragment.performContextItemSelected(menuItem)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void dispatchOptionsMenuClosed(Menu menu) {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); i++) {
                Fragment fragment = (Fragment) this.mAdded.get(i);
                if (fragment != null) {
                    fragment.performOptionsMenuClosed(menu);
                }
            }
        }
    }

    public View onCreateView(View view, String str, Context context, AttributeSet attributeSet) {
        Fragment fragment;
        Context context2 = context;
        AttributeSet attributeSet2 = attributeSet;
        String str2 = str;
        Fragment fragment2 = null;
        if (!"fragment".equals(str)) {
            return null;
        }
        String attributeValue = attributeSet2.getAttributeValue(null, "class");
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet2, FragmentTag.Fragment);
        int i = 0;
        if (attributeValue == null) {
            attributeValue = obtainStyledAttributes.getString(0);
        }
        String str3 = attributeValue;
        int resourceId = obtainStyledAttributes.getResourceId(1, -1);
        String string = obtainStyledAttributes.getString(2);
        obtainStyledAttributes.recycle();
        if (!Fragment.isSupportFragmentClass(this.mHost.getContext(), str3)) {
            return null;
        }
        if (view != null) {
            i = view.getId();
        }
        if (i == -1 && resourceId == -1 && string == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(attributeSet.getPositionDescription());
            sb.append(": Must specify unique android:id, android:tag, or have a parent with an id for ");
            sb.append(str3);
            throw new IllegalArgumentException(sb.toString());
        }
        if (resourceId != -1) {
            fragment2 = findFragmentById(resourceId);
        }
        if (fragment2 == null && string != null) {
            fragment2 = findFragmentByTag(string);
        }
        if (fragment2 == null && i != -1) {
            fragment2 = findFragmentById(i);
        }
        if (DEBUG) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("onCreateView: id=0x");
            sb2.append(Integer.toHexString(resourceId));
            sb2.append(" fname=");
            sb2.append(str3);
            sb2.append(" existing=");
            sb2.append(fragment2);
            Log.v(TAG, sb2.toString());
        }
        if (fragment2 == null) {
            Fragment instantiate = Fragment.instantiate(context, str3);
            instantiate.mFromLayout = true;
            instantiate.mFragmentId = resourceId != 0 ? resourceId : i;
            instantiate.mContainerId = i;
            instantiate.mTag = string;
            instantiate.mInLayout = true;
            instantiate.mFragmentManager = this;
            FragmentHostCallback fragmentHostCallback = this.mHost;
            instantiate.mHost = fragmentHostCallback;
            instantiate.onInflate(fragmentHostCallback.getContext(), attributeSet2, instantiate.mSavedFragmentState);
            addFragment(instantiate, true);
            fragment = instantiate;
        } else if (!fragment2.mInLayout) {
            fragment2.mInLayout = true;
            fragment2.mHost = this.mHost;
            if (!fragment2.mRetaining) {
                fragment2.onInflate(this.mHost.getContext(), attributeSet2, fragment2.mSavedFragmentState);
            }
            fragment = fragment2;
        } else {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(attributeSet.getPositionDescription());
            sb3.append(": Duplicate id 0x");
            sb3.append(Integer.toHexString(resourceId));
            sb3.append(", tag ");
            sb3.append(string);
            sb3.append(", or parent id 0x");
            sb3.append(Integer.toHexString(i));
            sb3.append(" with another fragment for ");
            sb3.append(str3);
            throw new IllegalArgumentException(sb3.toString());
        }
        if (this.mCurState >= 1 || !fragment.mFromLayout) {
            moveToState(fragment);
        } else {
            moveToState(fragment, 1, 0, 0, false);
        }
        if (fragment.mView != null) {
            if (resourceId != 0) {
                fragment.mView.setId(resourceId);
            }
            if (fragment.mView.getTag() == null) {
                fragment.mView.setTag(string);
            }
            return fragment.mView;
        }
        StringBuilder sb4 = new StringBuilder();
        sb4.append("Fragment ");
        sb4.append(str3);
        sb4.append(" did not create a view.");
        throw new IllegalStateException(sb4.toString());
    }
}
