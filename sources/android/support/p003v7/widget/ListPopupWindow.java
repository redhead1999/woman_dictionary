package android.support.p003v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.SystemClock;
import android.support.p000v4.text.TextUtilsCompat;
import android.support.p000v4.view.MotionEventCompat;
import android.support.p000v4.view.ViewCompat;
import android.support.p000v4.view.ViewPropertyAnimatorCompat;
import android.support.p000v4.widget.ListViewAutoScrollHelper;
import android.support.p000v4.widget.PopupWindowCompat;
import android.support.p003v7.appcompat.C0254R;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.KeyEvent.DispatcherState;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import java.lang.reflect.Method;

/* renamed from: android.support.v7.widget.ListPopupWindow */
public class ListPopupWindow {
    private static final boolean DEBUG = false;
    private static final int EXPAND_LIST_TIMEOUT = 250;
    public static final int INPUT_METHOD_FROM_FOCUSABLE = 0;
    public static final int INPUT_METHOD_NEEDED = 1;
    public static final int INPUT_METHOD_NOT_NEEDED = 2;
    public static final int MATCH_PARENT = -1;
    public static final int POSITION_PROMPT_ABOVE = 0;
    public static final int POSITION_PROMPT_BELOW = 1;
    private static final String TAG = "ListPopupWindow";
    public static final int WRAP_CONTENT = -2;
    private static Method sClipToWindowEnabledMethod;
    private static Method sGetMaxAvailableHeightMethod;
    private ListAdapter mAdapter;
    private Context mContext;
    private boolean mDropDownAlwaysVisible;
    private View mDropDownAnchorView;
    private int mDropDownGravity;
    private int mDropDownHeight;
    private int mDropDownHorizontalOffset;
    /* access modifiers changed from: private */
    public DropDownListView mDropDownList;
    private Drawable mDropDownListHighlight;
    private int mDropDownVerticalOffset;
    private boolean mDropDownVerticalOffsetSet;
    private int mDropDownWidth;
    private int mDropDownWindowLayoutType;
    private boolean mForceIgnoreOutsideTouch;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    private final ListSelectorHider mHideSelector;
    private OnItemClickListener mItemClickListener;
    private OnItemSelectedListener mItemSelectedListener;
    private int mLayoutDirection;
    int mListItemExpandMaximum;
    private boolean mModal;
    private DataSetObserver mObserver;
    /* access modifiers changed from: private */
    public PopupWindow mPopup;
    private int mPromptPosition;
    private View mPromptView;
    /* access modifiers changed from: private */
    public final ResizePopupRunnable mResizePopupRunnable;
    private final PopupScrollListener mScrollListener;
    private Runnable mShowDropDownRunnable;
    private Rect mTempRect;
    private final PopupTouchInterceptor mTouchInterceptor;

    /* renamed from: android.support.v7.widget.ListPopupWindow$DropDownListView */
    private static class DropDownListView extends ListViewCompat {
        private ViewPropertyAnimatorCompat mClickAnimation;
        private boolean mDrawsInPressedState;
        private boolean mHijackFocus;
        /* access modifiers changed from: private */
        public boolean mListSelectionHidden;
        private ListViewAutoScrollHelper mScrollHelper;

        public DropDownListView(Context context, boolean z) {
            super(context, null, C0254R.attr.dropDownListViewStyle);
            this.mHijackFocus = z;
            setCacheColorHint(0);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:5:0x000c, code lost:
            if (r0 != 3) goto L_0x000e;
         */
        /* JADX WARNING: Removed duplicated region for block: B:12:0x001e  */
        /* JADX WARNING: Removed duplicated region for block: B:22:0x004f  */
        /* JADX WARNING: Removed duplicated region for block: B:26:0x0065  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onForwardedEvent(android.view.MotionEvent r8, int r9) {
            /*
                r7 = this;
                int r0 = android.support.p000v4.view.MotionEventCompat.getActionMasked(r8)
                r1 = 0
                r2 = 1
                if (r0 == r2) goto L_0x0016
                r3 = 2
                if (r0 == r3) goto L_0x0014
                r9 = 3
                if (r0 == r9) goto L_0x0011
            L_0x000e:
                r9 = 0
                r3 = 1
                goto L_0x0046
            L_0x0011:
                r9 = 0
                r3 = 0
                goto L_0x0046
            L_0x0014:
                r3 = 1
                goto L_0x0017
            L_0x0016:
                r3 = 0
            L_0x0017:
                int r9 = r8.findPointerIndex(r9)
                if (r9 >= 0) goto L_0x001e
                goto L_0x0011
            L_0x001e:
                float r4 = r8.getX(r9)
                int r4 = (int) r4
                float r9 = r8.getY(r9)
                int r9 = (int) r9
                int r5 = r7.pointToPosition(r4, r9)
                r6 = -1
                if (r5 != r6) goto L_0x0031
                r9 = 1
                goto L_0x0046
            L_0x0031:
                int r3 = r7.getFirstVisiblePosition()
                int r3 = r5 - r3
                android.view.View r3 = r7.getChildAt(r3)
                float r4 = (float) r4
                float r9 = (float) r9
                r7.setPressedItem(r3, r5, r4, r9)
                if (r0 != r2) goto L_0x000e
                r7.clickPressedItem(r3, r5)
                goto L_0x000e
            L_0x0046:
                if (r3 == 0) goto L_0x004a
                if (r9 == 0) goto L_0x004d
            L_0x004a:
                r7.clearPressedItem()
            L_0x004d:
                if (r3 == 0) goto L_0x0065
                android.support.v4.widget.ListViewAutoScrollHelper r9 = r7.mScrollHelper
                if (r9 != 0) goto L_0x005a
                android.support.v4.widget.ListViewAutoScrollHelper r9 = new android.support.v4.widget.ListViewAutoScrollHelper
                r9.<init>(r7)
                r7.mScrollHelper = r9
            L_0x005a:
                android.support.v4.widget.ListViewAutoScrollHelper r9 = r7.mScrollHelper
                r9.setEnabled(r2)
                android.support.v4.widget.ListViewAutoScrollHelper r9 = r7.mScrollHelper
                r9.onTouch(r7, r8)
                goto L_0x006c
            L_0x0065:
                android.support.v4.widget.ListViewAutoScrollHelper r8 = r7.mScrollHelper
                if (r8 == 0) goto L_0x006c
                r8.setEnabled(r1)
            L_0x006c:
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.p003v7.widget.ListPopupWindow.DropDownListView.onForwardedEvent(android.view.MotionEvent, int):boolean");
        }

        private void clickPressedItem(View view, int i) {
            performItemClick(view, i, getItemIdAtPosition(i));
        }

        private void clearPressedItem() {
            this.mDrawsInPressedState = false;
            setPressed(false);
            drawableStateChanged();
            View childAt = getChildAt(this.mMotionPosition - getFirstVisiblePosition());
            if (childAt != null) {
                childAt.setPressed(false);
            }
            ViewPropertyAnimatorCompat viewPropertyAnimatorCompat = this.mClickAnimation;
            if (viewPropertyAnimatorCompat != null) {
                viewPropertyAnimatorCompat.cancel();
                this.mClickAnimation = null;
            }
        }

        private void setPressedItem(View view, int i, float f, float f2) {
            this.mDrawsInPressedState = true;
            if (VERSION.SDK_INT >= 21) {
                drawableHotspotChanged(f, f2);
            }
            if (!isPressed()) {
                setPressed(true);
            }
            layoutChildren();
            if (this.mMotionPosition != -1) {
                View childAt = getChildAt(this.mMotionPosition - getFirstVisiblePosition());
                if (!(childAt == null || childAt == view || !childAt.isPressed())) {
                    childAt.setPressed(false);
                }
            }
            this.mMotionPosition = i;
            float left = f - ((float) view.getLeft());
            float top = f2 - ((float) view.getTop());
            if (VERSION.SDK_INT >= 21) {
                view.drawableHotspotChanged(left, top);
            }
            if (!view.isPressed()) {
                view.setPressed(true);
            }
            positionSelectorLikeTouchCompat(i, view, f, f2);
            setSelectorEnabled(false);
            refreshDrawableState();
        }

        /* access modifiers changed from: protected */
        public boolean touchModeDrawsInPressedStateCompat() {
            return this.mDrawsInPressedState || super.touchModeDrawsInPressedStateCompat();
        }

        public boolean isInTouchMode() {
            return (this.mHijackFocus && this.mListSelectionHidden) || super.isInTouchMode();
        }

        public boolean hasWindowFocus() {
            return this.mHijackFocus || super.hasWindowFocus();
        }

        public boolean isFocused() {
            return this.mHijackFocus || super.isFocused();
        }

        public boolean hasFocus() {
            return this.mHijackFocus || super.hasFocus();
        }
    }

    /* renamed from: android.support.v7.widget.ListPopupWindow$ForwardingListener */
    public static abstract class ForwardingListener implements OnTouchListener {
        private int mActivePointerId;
        private Runnable mDisallowIntercept;
        private boolean mForwarding;
        private final int mLongPressTimeout;
        private final float mScaledTouchSlop;
        /* access modifiers changed from: private */
        public final View mSrc;
        private final int mTapTimeout;
        private final int[] mTmpLocation = new int[2];
        private Runnable mTriggerLongPress;
        private boolean mWasLongPress;

        /* renamed from: android.support.v7.widget.ListPopupWindow$ForwardingListener$DisallowIntercept */
        private class DisallowIntercept implements Runnable {
            private DisallowIntercept() {
            }

            public void run() {
                ForwardingListener.this.mSrc.getParent().requestDisallowInterceptTouchEvent(true);
            }
        }

        /* renamed from: android.support.v7.widget.ListPopupWindow$ForwardingListener$TriggerLongPress */
        private class TriggerLongPress implements Runnable {
            private TriggerLongPress() {
            }

            public void run() {
                ForwardingListener.this.onLongPress();
            }
        }

        public abstract ListPopupWindow getPopup();

        public ForwardingListener(View view) {
            this.mSrc = view;
            this.mScaledTouchSlop = (float) ViewConfiguration.get(view.getContext()).getScaledTouchSlop();
            this.mTapTimeout = ViewConfiguration.getTapTimeout();
            this.mLongPressTimeout = (this.mTapTimeout + ViewConfiguration.getLongPressTimeout()) / 2;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            boolean z;
            boolean z2 = this.mForwarding;
            if (z2) {
                z = this.mWasLongPress ? onTouchForwarded(motionEvent) : onTouchForwarded(motionEvent) || !onForwardingStopped();
            } else {
                z = onTouchObserved(motionEvent) && onForwardingStarted();
                if (z) {
                    long uptimeMillis = SystemClock.uptimeMillis();
                    MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0);
                    this.mSrc.onTouchEvent(obtain);
                    obtain.recycle();
                }
            }
            this.mForwarding = z;
            if (z || z2) {
                return true;
            }
            return false;
        }

        /* access modifiers changed from: protected */
        public boolean onForwardingStarted() {
            ListPopupWindow popup = getPopup();
            if (popup != null && !popup.isShowing()) {
                popup.show();
            }
            return true;
        }

        /* access modifiers changed from: protected */
        public boolean onForwardingStopped() {
            ListPopupWindow popup = getPopup();
            if (popup != null && popup.isShowing()) {
                popup.dismiss();
            }
            return true;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x0017, code lost:
            if (r1 != 3) goto L_0x0070;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private boolean onTouchObserved(android.view.MotionEvent r6) {
            /*
                r5 = this;
                android.view.View r0 = r5.mSrc
                boolean r1 = r0.isEnabled()
                r2 = 0
                if (r1 != 0) goto L_0x000a
                return r2
            L_0x000a:
                int r1 = android.support.p000v4.view.MotionEventCompat.getActionMasked(r6)
                if (r1 == 0) goto L_0x0041
                r3 = 1
                if (r1 == r3) goto L_0x003d
                r4 = 2
                if (r1 == r4) goto L_0x001a
                r6 = 3
                if (r1 == r6) goto L_0x003d
                goto L_0x0070
            L_0x001a:
                int r1 = r5.mActivePointerId
                int r1 = r6.findPointerIndex(r1)
                if (r1 < 0) goto L_0x0070
                float r4 = r6.getX(r1)
                float r6 = r6.getY(r1)
                float r1 = r5.mScaledTouchSlop
                boolean r6 = pointInView(r0, r4, r6, r1)
                if (r6 != 0) goto L_0x0070
                r5.clearCallbacks()
                android.view.ViewParent r6 = r0.getParent()
                r6.requestDisallowInterceptTouchEvent(r3)
                return r3
            L_0x003d:
                r5.clearCallbacks()
                goto L_0x0070
            L_0x0041:
                int r6 = r6.getPointerId(r2)
                r5.mActivePointerId = r6
                r5.mWasLongPress = r2
                java.lang.Runnable r6 = r5.mDisallowIntercept
                r1 = 0
                if (r6 != 0) goto L_0x0055
                android.support.v7.widget.ListPopupWindow$ForwardingListener$DisallowIntercept r6 = new android.support.v7.widget.ListPopupWindow$ForwardingListener$DisallowIntercept
                r6.<init>()
                r5.mDisallowIntercept = r6
            L_0x0055:
                java.lang.Runnable r6 = r5.mDisallowIntercept
                int r3 = r5.mTapTimeout
                long r3 = (long) r3
                r0.postDelayed(r6, r3)
                java.lang.Runnable r6 = r5.mTriggerLongPress
                if (r6 != 0) goto L_0x0068
                android.support.v7.widget.ListPopupWindow$ForwardingListener$TriggerLongPress r6 = new android.support.v7.widget.ListPopupWindow$ForwardingListener$TriggerLongPress
                r6.<init>()
                r5.mTriggerLongPress = r6
            L_0x0068:
                java.lang.Runnable r6 = r5.mTriggerLongPress
                int r1 = r5.mLongPressTimeout
                long r3 = (long) r1
                r0.postDelayed(r6, r3)
            L_0x0070:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.p003v7.widget.ListPopupWindow.ForwardingListener.onTouchObserved(android.view.MotionEvent):boolean");
        }

        private void clearCallbacks() {
            Runnable runnable = this.mTriggerLongPress;
            if (runnable != null) {
                this.mSrc.removeCallbacks(runnable);
            }
            Runnable runnable2 = this.mDisallowIntercept;
            if (runnable2 != null) {
                this.mSrc.removeCallbacks(runnable2);
            }
        }

        /* access modifiers changed from: private */
        public void onLongPress() {
            clearCallbacks();
            View view = this.mSrc;
            if (view.isEnabled() && !view.isLongClickable() && onForwardingStarted()) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                long uptimeMillis = SystemClock.uptimeMillis();
                MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0);
                view.onTouchEvent(obtain);
                obtain.recycle();
                this.mForwarding = true;
                this.mWasLongPress = true;
            }
        }

        private boolean onTouchForwarded(MotionEvent motionEvent) {
            View view = this.mSrc;
            ListPopupWindow popup = getPopup();
            if (popup != null && popup.isShowing()) {
                DropDownListView access$600 = popup.mDropDownList;
                if (access$600 != null && access$600.isShown()) {
                    MotionEvent obtainNoHistory = MotionEvent.obtainNoHistory(motionEvent);
                    toGlobalMotionEvent(view, obtainNoHistory);
                    toLocalMotionEvent(access$600, obtainNoHistory);
                    boolean onForwardedEvent = access$600.onForwardedEvent(obtainNoHistory, this.mActivePointerId);
                    obtainNoHistory.recycle();
                    int actionMasked = MotionEventCompat.getActionMasked(motionEvent);
                    boolean z = true;
                    boolean z2 = (actionMasked == 1 || actionMasked == 3) ? false : true;
                    if (!onForwardedEvent || !z2) {
                        z = false;
                    }
                    return z;
                }
            }
            return false;
        }

        private static boolean pointInView(View view, float f, float f2, float f3) {
            float f4 = -f3;
            return f >= f4 && f2 >= f4 && f < ((float) (view.getRight() - view.getLeft())) + f3 && f2 < ((float) (view.getBottom() - view.getTop())) + f3;
        }

        private boolean toLocalMotionEvent(View view, MotionEvent motionEvent) {
            int[] iArr = this.mTmpLocation;
            view.getLocationOnScreen(iArr);
            motionEvent.offsetLocation((float) (-iArr[0]), (float) (-iArr[1]));
            return true;
        }

        private boolean toGlobalMotionEvent(View view, MotionEvent motionEvent) {
            int[] iArr = this.mTmpLocation;
            view.getLocationOnScreen(iArr);
            motionEvent.offsetLocation((float) iArr[0], (float) iArr[1]);
            return true;
        }
    }

    /* renamed from: android.support.v7.widget.ListPopupWindow$ListSelectorHider */
    private class ListSelectorHider implements Runnable {
        private ListSelectorHider() {
        }

        public void run() {
            ListPopupWindow.this.clearListSelection();
        }
    }

    /* renamed from: android.support.v7.widget.ListPopupWindow$PopupDataSetObserver */
    private class PopupDataSetObserver extends DataSetObserver {
        private PopupDataSetObserver() {
        }

        public void onChanged() {
            if (ListPopupWindow.this.isShowing()) {
                ListPopupWindow.this.show();
            }
        }

        public void onInvalidated() {
            ListPopupWindow.this.dismiss();
        }
    }

    /* renamed from: android.support.v7.widget.ListPopupWindow$PopupScrollListener */
    private class PopupScrollListener implements OnScrollListener {
        public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        }

        private PopupScrollListener() {
        }

        public void onScrollStateChanged(AbsListView absListView, int i) {
            if (i == 1 && !ListPopupWindow.this.isInputMethodNotNeeded() && ListPopupWindow.this.mPopup.getContentView() != null) {
                ListPopupWindow.this.mHandler.removeCallbacks(ListPopupWindow.this.mResizePopupRunnable);
                ListPopupWindow.this.mResizePopupRunnable.run();
            }
        }
    }

    /* renamed from: android.support.v7.widget.ListPopupWindow$PopupTouchInterceptor */
    private class PopupTouchInterceptor implements OnTouchListener {
        private PopupTouchInterceptor() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            if (action == 0 && ListPopupWindow.this.mPopup != null && ListPopupWindow.this.mPopup.isShowing() && x >= 0 && x < ListPopupWindow.this.mPopup.getWidth() && y >= 0 && y < ListPopupWindow.this.mPopup.getHeight()) {
                ListPopupWindow.this.mHandler.postDelayed(ListPopupWindow.this.mResizePopupRunnable, 250);
            } else if (action == 1) {
                ListPopupWindow.this.mHandler.removeCallbacks(ListPopupWindow.this.mResizePopupRunnable);
            }
            return false;
        }
    }

    /* renamed from: android.support.v7.widget.ListPopupWindow$ResizePopupRunnable */
    private class ResizePopupRunnable implements Runnable {
        private ResizePopupRunnable() {
        }

        public void run() {
            if (ListPopupWindow.this.mDropDownList != null && ViewCompat.isAttachedToWindow(ListPopupWindow.this.mDropDownList) && ListPopupWindow.this.mDropDownList.getCount() > ListPopupWindow.this.mDropDownList.getChildCount() && ListPopupWindow.this.mDropDownList.getChildCount() <= ListPopupWindow.this.mListItemExpandMaximum) {
                ListPopupWindow.this.mPopup.setInputMethodMode(2);
                ListPopupWindow.this.show();
            }
        }
    }

    private static boolean isConfirmKey(int i) {
        return i == 66 || i == 23;
    }

    static {
        String str = TAG;
        try {
            sClipToWindowEnabledMethod = PopupWindow.class.getDeclaredMethod("setClipToScreenEnabled", new Class[]{Boolean.TYPE});
        } catch (NoSuchMethodException unused) {
            Log.i(str, "Could not find method setClipToScreenEnabled() on PopupWindow. Oh well.");
        }
        try {
            sGetMaxAvailableHeightMethod = PopupWindow.class.getDeclaredMethod("getMaxAvailableHeight", new Class[]{View.class, Integer.TYPE, Boolean.TYPE});
        } catch (NoSuchMethodException unused2) {
            Log.i(str, "Could not find method getMaxAvailableHeight(View, int, boolean) on PopupWindow. Oh well.");
        }
    }

    public ListPopupWindow(Context context) {
        this(context, null, C0254R.attr.listPopupWindowStyle);
    }

    public ListPopupWindow(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, C0254R.attr.listPopupWindowStyle);
    }

    public ListPopupWindow(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public ListPopupWindow(Context context, AttributeSet attributeSet, int i, int i2) {
        this.mDropDownHeight = -2;
        this.mDropDownWidth = -2;
        this.mDropDownWindowLayoutType = 1002;
        this.mDropDownGravity = 0;
        this.mDropDownAlwaysVisible = false;
        this.mForceIgnoreOutsideTouch = false;
        this.mListItemExpandMaximum = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        this.mPromptPosition = 0;
        this.mResizePopupRunnable = new ResizePopupRunnable();
        this.mTouchInterceptor = new PopupTouchInterceptor();
        this.mScrollListener = new PopupScrollListener();
        this.mHideSelector = new ListSelectorHider();
        this.mTempRect = new Rect();
        this.mContext = context;
        this.mHandler = new Handler(context.getMainLooper());
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C0254R.styleable.ListPopupWindow, i, i2);
        this.mDropDownHorizontalOffset = obtainStyledAttributes.getDimensionPixelOffset(C0254R.styleable.ListPopupWindow_android_dropDownHorizontalOffset, 0);
        this.mDropDownVerticalOffset = obtainStyledAttributes.getDimensionPixelOffset(C0254R.styleable.ListPopupWindow_android_dropDownVerticalOffset, 0);
        if (this.mDropDownVerticalOffset != 0) {
            this.mDropDownVerticalOffsetSet = true;
        }
        obtainStyledAttributes.recycle();
        this.mPopup = new AppCompatPopupWindow(context, attributeSet, i);
        this.mPopup.setInputMethodMode(1);
        this.mLayoutDirection = TextUtilsCompat.getLayoutDirectionFromLocale(this.mContext.getResources().getConfiguration().locale);
    }

    public void setAdapter(ListAdapter listAdapter) {
        DataSetObserver dataSetObserver = this.mObserver;
        if (dataSetObserver == null) {
            this.mObserver = new PopupDataSetObserver();
        } else {
            ListAdapter listAdapter2 = this.mAdapter;
            if (listAdapter2 != null) {
                listAdapter2.unregisterDataSetObserver(dataSetObserver);
            }
        }
        this.mAdapter = listAdapter;
        if (this.mAdapter != null) {
            listAdapter.registerDataSetObserver(this.mObserver);
        }
        DropDownListView dropDownListView = this.mDropDownList;
        if (dropDownListView != null) {
            dropDownListView.setAdapter(this.mAdapter);
        }
    }

    public void setPromptPosition(int i) {
        this.mPromptPosition = i;
    }

    public int getPromptPosition() {
        return this.mPromptPosition;
    }

    public void setModal(boolean z) {
        this.mModal = z;
        this.mPopup.setFocusable(z);
    }

    public boolean isModal() {
        return this.mModal;
    }

    public void setForceIgnoreOutsideTouch(boolean z) {
        this.mForceIgnoreOutsideTouch = z;
    }

    public void setDropDownAlwaysVisible(boolean z) {
        this.mDropDownAlwaysVisible = z;
    }

    public boolean isDropDownAlwaysVisible() {
        return this.mDropDownAlwaysVisible;
    }

    public void setSoftInputMode(int i) {
        this.mPopup.setSoftInputMode(i);
    }

    public int getSoftInputMode() {
        return this.mPopup.getSoftInputMode();
    }

    public void setListSelector(Drawable drawable) {
        this.mDropDownListHighlight = drawable;
    }

    public Drawable getBackground() {
        return this.mPopup.getBackground();
    }

    public void setBackgroundDrawable(Drawable drawable) {
        this.mPopup.setBackgroundDrawable(drawable);
    }

    public void setAnimationStyle(int i) {
        this.mPopup.setAnimationStyle(i);
    }

    public int getAnimationStyle() {
        return this.mPopup.getAnimationStyle();
    }

    public View getAnchorView() {
        return this.mDropDownAnchorView;
    }

    public void setAnchorView(View view) {
        this.mDropDownAnchorView = view;
    }

    public int getHorizontalOffset() {
        return this.mDropDownHorizontalOffset;
    }

    public void setHorizontalOffset(int i) {
        this.mDropDownHorizontalOffset = i;
    }

    public int getVerticalOffset() {
        if (!this.mDropDownVerticalOffsetSet) {
            return 0;
        }
        return this.mDropDownVerticalOffset;
    }

    public void setVerticalOffset(int i) {
        this.mDropDownVerticalOffset = i;
        this.mDropDownVerticalOffsetSet = true;
    }

    public void setDropDownGravity(int i) {
        this.mDropDownGravity = i;
    }

    public int getWidth() {
        return this.mDropDownWidth;
    }

    public void setWidth(int i) {
        this.mDropDownWidth = i;
    }

    public void setContentWidth(int i) {
        Drawable background = this.mPopup.getBackground();
        if (background != null) {
            background.getPadding(this.mTempRect);
            this.mDropDownWidth = this.mTempRect.left + this.mTempRect.right + i;
            return;
        }
        setWidth(i);
    }

    public int getHeight() {
        return this.mDropDownHeight;
    }

    public void setHeight(int i) {
        this.mDropDownHeight = i;
    }

    public void setWindowLayoutType(int i) {
        this.mDropDownWindowLayoutType = i;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mItemClickListener = onItemClickListener;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.mItemSelectedListener = onItemSelectedListener;
    }

    public void setPromptView(View view) {
        boolean isShowing = isShowing();
        if (isShowing) {
            removePromptView();
        }
        this.mPromptView = view;
        if (isShowing) {
            show();
        }
    }

    public void postShow() {
        this.mHandler.post(this.mShowDropDownRunnable);
    }

    public void show() {
        int buildDropDown = buildDropDown();
        boolean isInputMethodNotNeeded = isInputMethodNotNeeded();
        PopupWindowCompat.setWindowLayoutType(this.mPopup, this.mDropDownWindowLayoutType);
        boolean z = true;
        if (this.mPopup.isShowing()) {
            int i = this.mDropDownWidth;
            if (i == -1) {
                i = -1;
            } else if (i == -2) {
                i = getAnchorView().getWidth();
            }
            int i2 = this.mDropDownHeight;
            if (i2 == -1) {
                if (!isInputMethodNotNeeded) {
                    buildDropDown = -1;
                }
                if (isInputMethodNotNeeded) {
                    this.mPopup.setWidth(this.mDropDownWidth == -1 ? -1 : 0);
                    this.mPopup.setHeight(0);
                } else {
                    this.mPopup.setWidth(this.mDropDownWidth == -1 ? -1 : 0);
                    this.mPopup.setHeight(-1);
                }
            } else if (i2 != -2) {
                buildDropDown = i2;
            }
            PopupWindow popupWindow = this.mPopup;
            if (this.mForceIgnoreOutsideTouch || this.mDropDownAlwaysVisible) {
                z = false;
            }
            popupWindow.setOutsideTouchable(z);
            this.mPopup.update(getAnchorView(), this.mDropDownHorizontalOffset, this.mDropDownVerticalOffset, i < 0 ? -1 : i, buildDropDown < 0 ? -1 : buildDropDown);
            return;
        }
        int i3 = this.mDropDownWidth;
        if (i3 == -1) {
            i3 = -1;
        } else if (i3 == -2) {
            i3 = getAnchorView().getWidth();
        }
        int i4 = this.mDropDownHeight;
        if (i4 == -1) {
            buildDropDown = -1;
        } else if (i4 != -2) {
            buildDropDown = i4;
        }
        this.mPopup.setWidth(i3);
        this.mPopup.setHeight(buildDropDown);
        setPopupClipToScreenEnabled(true);
        PopupWindow popupWindow2 = this.mPopup;
        if (this.mForceIgnoreOutsideTouch || this.mDropDownAlwaysVisible) {
            z = false;
        }
        popupWindow2.setOutsideTouchable(z);
        this.mPopup.setTouchInterceptor(this.mTouchInterceptor);
        PopupWindowCompat.showAsDropDown(this.mPopup, getAnchorView(), this.mDropDownHorizontalOffset, this.mDropDownVerticalOffset, this.mDropDownGravity);
        this.mDropDownList.setSelection(-1);
        if (!this.mModal || this.mDropDownList.isInTouchMode()) {
            clearListSelection();
        }
        if (!this.mModal) {
            this.mHandler.post(this.mHideSelector);
        }
    }

    public void dismiss() {
        this.mPopup.dismiss();
        removePromptView();
        this.mPopup.setContentView(null);
        this.mDropDownList = null;
        this.mHandler.removeCallbacks(this.mResizePopupRunnable);
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.mPopup.setOnDismissListener(onDismissListener);
    }

    private void removePromptView() {
        View view = this.mPromptView;
        if (view != null) {
            ViewParent parent = view.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(this.mPromptView);
            }
        }
    }

    public void setInputMethodMode(int i) {
        this.mPopup.setInputMethodMode(i);
    }

    public int getInputMethodMode() {
        return this.mPopup.getInputMethodMode();
    }

    public void setSelection(int i) {
        DropDownListView dropDownListView = this.mDropDownList;
        if (isShowing() && dropDownListView != null) {
            dropDownListView.mListSelectionHidden = false;
            dropDownListView.setSelection(i);
            if (VERSION.SDK_INT >= 11 && dropDownListView.getChoiceMode() != 0) {
                dropDownListView.setItemChecked(i, true);
            }
        }
    }

    public void clearListSelection() {
        DropDownListView dropDownListView = this.mDropDownList;
        if (dropDownListView != null) {
            dropDownListView.mListSelectionHidden = true;
            dropDownListView.requestLayout();
        }
    }

    public boolean isShowing() {
        return this.mPopup.isShowing();
    }

    public boolean isInputMethodNotNeeded() {
        return this.mPopup.getInputMethodMode() == 2;
    }

    public boolean performItemClick(int i) {
        if (!isShowing()) {
            return false;
        }
        if (this.mItemClickListener != null) {
            DropDownListView dropDownListView = this.mDropDownList;
            int i2 = i;
            this.mItemClickListener.onItemClick(dropDownListView, dropDownListView.getChildAt(i - dropDownListView.getFirstVisiblePosition()), i2, dropDownListView.getAdapter().getItemId(i));
        }
        return true;
    }

    public Object getSelectedItem() {
        if (!isShowing()) {
            return null;
        }
        return this.mDropDownList.getSelectedItem();
    }

    public int getSelectedItemPosition() {
        if (!isShowing()) {
            return -1;
        }
        return this.mDropDownList.getSelectedItemPosition();
    }

    public long getSelectedItemId() {
        if (!isShowing()) {
            return Long.MIN_VALUE;
        }
        return this.mDropDownList.getSelectedItemId();
    }

    public View getSelectedView() {
        if (!isShowing()) {
            return null;
        }
        return this.mDropDownList.getSelectedView();
    }

    public ListView getListView() {
        return this.mDropDownList;
    }

    /* access modifiers changed from: 0000 */
    public void setListItemExpandMax(int i) {
        this.mListItemExpandMaximum = i;
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        int i2;
        int i3;
        if (isShowing() && i != 62 && (this.mDropDownList.getSelectedItemPosition() >= 0 || !isConfirmKey(i))) {
            int selectedItemPosition = this.mDropDownList.getSelectedItemPosition();
            boolean z = !this.mPopup.isAboveAnchor();
            ListAdapter listAdapter = this.mAdapter;
            int i4 = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
            if (listAdapter != null) {
                boolean areAllItemsEnabled = listAdapter.areAllItemsEnabled();
                if (areAllItemsEnabled) {
                    i3 = 0;
                } else {
                    i3 = this.mDropDownList.lookForSelectablePosition(0, true);
                }
                i2 = areAllItemsEnabled ? listAdapter.getCount() - 1 : this.mDropDownList.lookForSelectablePosition(listAdapter.getCount() - 1, false);
                i4 = i3;
            } else {
                i2 = Integer.MIN_VALUE;
            }
            if ((!z || i != 19 || selectedItemPosition > i4) && (z || i != 20 || selectedItemPosition < i2)) {
                this.mDropDownList.mListSelectionHidden = false;
                if (this.mDropDownList.onKeyDown(i, keyEvent)) {
                    this.mPopup.setInputMethodMode(2);
                    this.mDropDownList.requestFocusFromTouch();
                    show();
                    if (i == 19 || i == 20 || i == 23 || i == 66) {
                        return true;
                    }
                } else if (!z || i != 20) {
                    if (!z && i == 19 && selectedItemPosition == i4) {
                        return true;
                    }
                    return false;
                } else if (selectedItemPosition == i2) {
                    return true;
                }
            } else {
                clearListSelection();
                this.mPopup.setInputMethodMode(1);
                show();
                return true;
            }
        }
        return false;
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (!isShowing() || this.mDropDownList.getSelectedItemPosition() < 0) {
            return false;
        }
        boolean onKeyUp = this.mDropDownList.onKeyUp(i, keyEvent);
        if (onKeyUp && isConfirmKey(i)) {
            dismiss();
        }
        return onKeyUp;
    }

    public boolean onKeyPreIme(int i, KeyEvent keyEvent) {
        if (i == 4 && isShowing()) {
            View view = this.mDropDownAnchorView;
            if (keyEvent.getAction() == 0 && keyEvent.getRepeatCount() == 0) {
                DispatcherState keyDispatcherState = view.getKeyDispatcherState();
                if (keyDispatcherState != null) {
                    keyDispatcherState.startTracking(keyEvent, this);
                }
                return true;
            } else if (keyEvent.getAction() == 1) {
                DispatcherState keyDispatcherState2 = view.getKeyDispatcherState();
                if (keyDispatcherState2 != null) {
                    keyDispatcherState2.handleUpEvent(keyEvent);
                }
                if (keyEvent.isTracking() && !keyEvent.isCanceled()) {
                    dismiss();
                    return true;
                }
            }
        }
        return false;
    }

    public OnTouchListener createDragToOpenListener(View view) {
        return new ForwardingListener(view) {
            public ListPopupWindow getPopup() {
                return ListPopupWindow.this;
            }
        };
    }

    private int buildDropDown() {
        int i;
        int i2;
        int makeMeasureSpec;
        View view;
        int i3;
        boolean z = true;
        if (this.mDropDownList == null) {
            Context context = this.mContext;
            this.mShowDropDownRunnable = new Runnable() {
                public void run() {
                    View anchorView = ListPopupWindow.this.getAnchorView();
                    if (anchorView != null && anchorView.getWindowToken() != null) {
                        ListPopupWindow.this.show();
                    }
                }
            };
            this.mDropDownList = new DropDownListView(context, !this.mModal);
            Drawable drawable = this.mDropDownListHighlight;
            if (drawable != null) {
                this.mDropDownList.setSelector(drawable);
            }
            this.mDropDownList.setAdapter(this.mAdapter);
            this.mDropDownList.setOnItemClickListener(this.mItemClickListener);
            this.mDropDownList.setFocusable(true);
            this.mDropDownList.setFocusableInTouchMode(true);
            this.mDropDownList.setOnItemSelectedListener(new OnItemSelectedListener() {
                public void onNothingSelected(AdapterView<?> adapterView) {
                }

                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                    if (i != -1) {
                        DropDownListView access$600 = ListPopupWindow.this.mDropDownList;
                        if (access$600 != null) {
                            access$600.mListSelectionHidden = false;
                        }
                    }
                }
            });
            this.mDropDownList.setOnScrollListener(this.mScrollListener);
            OnItemSelectedListener onItemSelectedListener = this.mItemSelectedListener;
            if (onItemSelectedListener != null) {
                this.mDropDownList.setOnItemSelectedListener(onItemSelectedListener);
            }
            View view2 = this.mDropDownList;
            View view3 = this.mPromptView;
            if (view3 != null) {
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(1);
                LayoutParams layoutParams = new LayoutParams(-1, 0, 1.0f);
                int i4 = this.mPromptPosition;
                if (i4 == 0) {
                    linearLayout.addView(view3);
                    linearLayout.addView(view2, layoutParams);
                } else if (i4 != 1) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Invalid hint position ");
                    sb.append(this.mPromptPosition);
                    Log.e(TAG, sb.toString());
                } else {
                    linearLayout.addView(view2, layoutParams);
                    linearLayout.addView(view3);
                }
                int i5 = this.mDropDownWidth;
                if (i5 >= 0) {
                    i3 = Integer.MIN_VALUE;
                } else {
                    i5 = 0;
                    i3 = 0;
                }
                view3.measure(MeasureSpec.makeMeasureSpec(i5, i3), 0);
                LayoutParams layoutParams2 = (LayoutParams) view3.getLayoutParams();
                i = view3.getMeasuredHeight() + layoutParams2.topMargin + layoutParams2.bottomMargin;
                view = linearLayout;
            } else {
                i = 0;
                view = view2;
            }
            this.mPopup.setContentView(view);
        } else {
            ViewGroup viewGroup = (ViewGroup) this.mPopup.getContentView();
            View view4 = this.mPromptView;
            if (view4 != null) {
                LayoutParams layoutParams3 = (LayoutParams) view4.getLayoutParams();
                i = view4.getMeasuredHeight() + layoutParams3.topMargin + layoutParams3.bottomMargin;
            } else {
                i = 0;
            }
        }
        Drawable background = this.mPopup.getBackground();
        if (background != null) {
            background.getPadding(this.mTempRect);
            i2 = this.mTempRect.top + this.mTempRect.bottom;
            if (!this.mDropDownVerticalOffsetSet) {
                this.mDropDownVerticalOffset = -this.mTempRect.top;
            }
        } else {
            this.mTempRect.setEmpty();
            i2 = 0;
        }
        if (this.mPopup.getInputMethodMode() != 2) {
            z = false;
        }
        int maxAvailableHeight = getMaxAvailableHeight(getAnchorView(), this.mDropDownVerticalOffset, z);
        if (this.mDropDownAlwaysVisible || this.mDropDownHeight == -1) {
            return maxAvailableHeight + i2;
        }
        int i6 = this.mDropDownWidth;
        if (i6 == -2) {
            makeMeasureSpec = MeasureSpec.makeMeasureSpec(this.mContext.getResources().getDisplayMetrics().widthPixels - (this.mTempRect.left + this.mTempRect.right), Integer.MIN_VALUE);
        } else if (i6 != -1) {
            makeMeasureSpec = MeasureSpec.makeMeasureSpec(i6, 1073741824);
        } else {
            makeMeasureSpec = MeasureSpec.makeMeasureSpec(this.mContext.getResources().getDisplayMetrics().widthPixels - (this.mTempRect.left + this.mTempRect.right), 1073741824);
        }
        int measureHeightOfChildrenCompat = this.mDropDownList.measureHeightOfChildrenCompat(makeMeasureSpec, 0, -1, maxAvailableHeight - i, -1);
        if (measureHeightOfChildrenCompat > 0) {
            i += i2;
        }
        return measureHeightOfChildrenCompat + i;
    }

    private void setPopupClipToScreenEnabled(boolean z) {
        Method method = sClipToWindowEnabledMethod;
        if (method != null) {
            try {
                method.invoke(this.mPopup, new Object[]{Boolean.valueOf(z)});
            } catch (Exception unused) {
                Log.i(TAG, "Could not call setClipToScreenEnabled() on PopupWindow. Oh well.");
            }
        }
    }

    private int getMaxAvailableHeight(View view, int i, boolean z) {
        Method method = sGetMaxAvailableHeightMethod;
        if (method != null) {
            try {
                return ((Integer) method.invoke(this.mPopup, new Object[]{view, Integer.valueOf(i), Boolean.valueOf(z)})).intValue();
            } catch (Exception unused) {
                Log.i(TAG, "Could not call getMaxAvailableHeightMethod(View, int, boolean) on PopupWindow. Using the public version.");
            }
        }
        return this.mPopup.getMaxAvailableHeight(view, i);
    }
}
