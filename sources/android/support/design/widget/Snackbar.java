package android.support.design.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.C0007R;
import android.support.design.widget.SwipeDismissBehavior.OnDismissListener;
import android.support.p000v4.view.ViewCompat;
import android.support.p000v4.view.ViewPropertyAnimatorListenerAdapter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Snackbar {
    static final int ANIMATION_DURATION = 250;
    static final int ANIMATION_FADE_DURATION = 180;
    public static final int LENGTH_INDEFINITE = -2;
    public static final int LENGTH_LONG = 0;
    public static final int LENGTH_SHORT = -1;
    private static final int MSG_DISMISS = 1;
    private static final int MSG_SHOW = 0;
    /* access modifiers changed from: private */
    public static final Handler sHandler = new Handler(Looper.getMainLooper(), new android.os.Handler.Callback() {
        public boolean handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                ((Snackbar) message.obj).showView();
                return true;
            } else if (i != 1) {
                return false;
            } else {
                ((Snackbar) message.obj).hideView(message.arg1);
                return true;
            }
        }
    });
    private final AccessibilityManager mAccessibilityManager;
    private Callback mCallback;
    private final Context mContext;
    private int mDuration;
    /* access modifiers changed from: private */
    public final Callback mManagerCallback = new Callback() {
        public void show() {
            Snackbar.sHandler.sendMessage(Snackbar.sHandler.obtainMessage(0, Snackbar.this));
        }

        public void dismiss(int i) {
            Snackbar.sHandler.sendMessage(Snackbar.sHandler.obtainMessage(1, i, 0, Snackbar.this));
        }
    };
    private final ViewGroup mTargetParent;
    /* access modifiers changed from: private */
    public final SnackbarLayout mView;

    final class Behavior extends SwipeDismissBehavior<SnackbarLayout> {
        Behavior() {
        }

        public boolean canSwipeDismissView(View view) {
            return view instanceof SnackbarLayout;
        }

        public boolean onInterceptTouchEvent(CoordinatorLayout coordinatorLayout, SnackbarLayout snackbarLayout, MotionEvent motionEvent) {
            if (coordinatorLayout.isPointInChildBounds(snackbarLayout, (int) motionEvent.getX(), (int) motionEvent.getY())) {
                int actionMasked = motionEvent.getActionMasked();
                if (actionMasked == 0) {
                    SnackbarManager.getInstance().cancelTimeout(Snackbar.this.mManagerCallback);
                } else if (actionMasked == 1 || actionMasked == 3) {
                    SnackbarManager.getInstance().restoreTimeout(Snackbar.this.mManagerCallback);
                }
            }
            return super.onInterceptTouchEvent(coordinatorLayout, snackbarLayout, motionEvent);
        }
    }

    public static abstract class Callback {
        public static final int DISMISS_EVENT_ACTION = 1;
        public static final int DISMISS_EVENT_CONSECUTIVE = 4;
        public static final int DISMISS_EVENT_MANUAL = 3;
        public static final int DISMISS_EVENT_SWIPE = 0;
        public static final int DISMISS_EVENT_TIMEOUT = 2;

        @Retention(RetentionPolicy.SOURCE)
        public @interface DismissEvent {
        }

        public void onDismissed(Snackbar snackbar, int i) {
        }

        public void onShown(Snackbar snackbar) {
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {
    }

    public static class SnackbarLayout extends LinearLayout {
        private Button mActionView;
        private int mMaxInlineActionWidth;
        private int mMaxWidth;
        private TextView mMessageView;
        private OnAttachStateChangeListener mOnAttachStateChangeListener;
        private OnLayoutChangeListener mOnLayoutChangeListener;

        interface OnAttachStateChangeListener {
            void onViewAttachedToWindow(View view);

            void onViewDetachedFromWindow(View view);
        }

        interface OnLayoutChangeListener {
            void onLayoutChange(View view, int i, int i2, int i3, int i4);
        }

        public SnackbarLayout(Context context) {
            this(context, null);
        }

        public SnackbarLayout(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C0007R.styleable.SnackbarLayout);
            this.mMaxWidth = obtainStyledAttributes.getDimensionPixelSize(C0007R.styleable.SnackbarLayout_android_maxWidth, -1);
            this.mMaxInlineActionWidth = obtainStyledAttributes.getDimensionPixelSize(C0007R.styleable.SnackbarLayout_maxActionInlineWidth, -1);
            if (obtainStyledAttributes.hasValue(C0007R.styleable.SnackbarLayout_elevation)) {
                ViewCompat.setElevation(this, (float) obtainStyledAttributes.getDimensionPixelSize(C0007R.styleable.SnackbarLayout_elevation, 0));
            }
            obtainStyledAttributes.recycle();
            setClickable(true);
            LayoutInflater.from(context).inflate(C0007R.layout.design_layout_snackbar_include, this);
            ViewCompat.setAccessibilityLiveRegion(this, 1);
            ViewCompat.setImportantForAccessibility(this, 1);
        }

        /* access modifiers changed from: protected */
        public void onFinishInflate() {
            super.onFinishInflate();
            this.mMessageView = (TextView) findViewById(C0007R.C0009id.snackbar_text);
            this.mActionView = (Button) findViewById(C0007R.C0009id.snackbar_action);
        }

        /* access modifiers changed from: 0000 */
        public TextView getMessageView() {
            return this.mMessageView;
        }

        /* access modifiers changed from: 0000 */
        public Button getActionView() {
            return this.mActionView;
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0053, code lost:
            if (updateViewsWithinLayout(1, r0, r0 - r1) != false) goto L_0x0062;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x005e, code lost:
            if (updateViewsWithinLayout(0, r0, r0) != false) goto L_0x0062;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onMeasure(int r8, int r9) {
            /*
                r7 = this;
                super.onMeasure(r8, r9)
                int r0 = r7.mMaxWidth
                if (r0 <= 0) goto L_0x0018
                int r0 = r7.getMeasuredWidth()
                int r1 = r7.mMaxWidth
                if (r0 <= r1) goto L_0x0018
                r8 = 1073741824(0x40000000, float:2.0)
                int r8 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r8)
                super.onMeasure(r8, r9)
            L_0x0018:
                android.content.res.Resources r0 = r7.getResources()
                int r1 = android.support.design.C0007R.dimen.design_snackbar_padding_vertical_2lines
                int r0 = r0.getDimensionPixelSize(r1)
                android.content.res.Resources r1 = r7.getResources()
                int r2 = android.support.design.C0007R.dimen.design_snackbar_padding_vertical
                int r1 = r1.getDimensionPixelSize(r2)
                android.widget.TextView r2 = r7.mMessageView
                android.text.Layout r2 = r2.getLayout()
                int r2 = r2.getLineCount()
                r3 = 0
                r4 = 1
                if (r2 <= r4) goto L_0x003c
                r2 = 1
                goto L_0x003d
            L_0x003c:
                r2 = 0
            L_0x003d:
                if (r2 == 0) goto L_0x0056
                int r5 = r7.mMaxInlineActionWidth
                if (r5 <= 0) goto L_0x0056
                android.widget.Button r5 = r7.mActionView
                int r5 = r5.getMeasuredWidth()
                int r6 = r7.mMaxInlineActionWidth
                if (r5 <= r6) goto L_0x0056
                int r1 = r0 - r1
                boolean r0 = r7.updateViewsWithinLayout(r4, r0, r1)
                if (r0 == 0) goto L_0x0061
                goto L_0x0062
            L_0x0056:
                if (r2 == 0) goto L_0x0059
                goto L_0x005a
            L_0x0059:
                r0 = r1
            L_0x005a:
                boolean r0 = r7.updateViewsWithinLayout(r3, r0, r0)
                if (r0 == 0) goto L_0x0061
                goto L_0x0062
            L_0x0061:
                r4 = 0
            L_0x0062:
                if (r4 == 0) goto L_0x0067
                super.onMeasure(r8, r9)
            L_0x0067:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.design.widget.Snackbar.SnackbarLayout.onMeasure(int, int):void");
        }

        /* access modifiers changed from: 0000 */
        public void animateChildrenIn(int i, int i2) {
            ViewCompat.setAlpha(this.mMessageView, 0.0f);
            long j = (long) i2;
            long j2 = (long) i;
            ViewCompat.animate(this.mMessageView).alpha(1.0f).setDuration(j).setStartDelay(j2).start();
            if (this.mActionView.getVisibility() == 0) {
                ViewCompat.setAlpha(this.mActionView, 0.0f);
                ViewCompat.animate(this.mActionView).alpha(1.0f).setDuration(j).setStartDelay(j2).start();
            }
        }

        /* access modifiers changed from: 0000 */
        public void animateChildrenOut(int i, int i2) {
            ViewCompat.setAlpha(this.mMessageView, 1.0f);
            long j = (long) i2;
            long j2 = (long) i;
            ViewCompat.animate(this.mMessageView).alpha(0.0f).setDuration(j).setStartDelay(j2).start();
            if (this.mActionView.getVisibility() == 0) {
                ViewCompat.setAlpha(this.mActionView, 1.0f);
                ViewCompat.animate(this.mActionView).alpha(0.0f).setDuration(j).setStartDelay(j2).start();
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            OnLayoutChangeListener onLayoutChangeListener = this.mOnLayoutChangeListener;
            if (onLayoutChangeListener != null) {
                onLayoutChangeListener.onLayoutChange(this, i, i2, i3, i4);
            }
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            OnAttachStateChangeListener onAttachStateChangeListener = this.mOnAttachStateChangeListener;
            if (onAttachStateChangeListener != null) {
                onAttachStateChangeListener.onViewAttachedToWindow(this);
            }
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            OnAttachStateChangeListener onAttachStateChangeListener = this.mOnAttachStateChangeListener;
            if (onAttachStateChangeListener != null) {
                onAttachStateChangeListener.onViewDetachedFromWindow(this);
            }
        }

        /* access modifiers changed from: 0000 */
        public void setOnLayoutChangeListener(OnLayoutChangeListener onLayoutChangeListener) {
            this.mOnLayoutChangeListener = onLayoutChangeListener;
        }

        /* access modifiers changed from: 0000 */
        public void setOnAttachStateChangeListener(OnAttachStateChangeListener onAttachStateChangeListener) {
            this.mOnAttachStateChangeListener = onAttachStateChangeListener;
        }

        private boolean updateViewsWithinLayout(int i, int i2, int i3) {
            boolean z;
            if (i != getOrientation()) {
                setOrientation(i);
                z = true;
            } else {
                z = false;
            }
            if (this.mMessageView.getPaddingTop() == i2 && this.mMessageView.getPaddingBottom() == i3) {
                return z;
            }
            updateTopBottomPadding(this.mMessageView, i2, i3);
            return true;
        }

        private static void updateTopBottomPadding(View view, int i, int i2) {
            if (ViewCompat.isPaddingRelative(view)) {
                ViewCompat.setPaddingRelative(view, ViewCompat.getPaddingStart(view), i, ViewCompat.getPaddingEnd(view), i2);
            } else {
                view.setPadding(view.getPaddingLeft(), i, view.getPaddingRight(), i2);
            }
        }
    }

    private Snackbar(ViewGroup viewGroup) {
        this.mTargetParent = viewGroup;
        this.mContext = viewGroup.getContext();
        ThemeUtils.checkAppCompatTheme(this.mContext);
        this.mView = (SnackbarLayout) LayoutInflater.from(this.mContext).inflate(C0007R.layout.design_layout_snackbar, this.mTargetParent, false);
        this.mAccessibilityManager = (AccessibilityManager) this.mContext.getSystemService("accessibility");
    }

    @NonNull
    public static Snackbar make(@NonNull View view, @NonNull CharSequence charSequence, int i) {
        Snackbar snackbar = new Snackbar(findSuitableParent(view));
        snackbar.setText(charSequence);
        snackbar.setDuration(i);
        return snackbar;
    }

    @NonNull
    public static Snackbar make(@NonNull View view, @StringRes int i, int i2) {
        return make(view, view.getResources().getText(i), i2);
    }

    private static ViewGroup findSuitableParent(View view) {
        ViewGroup viewGroup = null;
        while (!(view instanceof CoordinatorLayout)) {
            if (view instanceof FrameLayout) {
                if (view.getId() == 16908290) {
                    return (ViewGroup) view;
                }
                viewGroup = (ViewGroup) view;
            }
            if (view != null) {
                ViewParent parent = view.getParent();
                if (parent instanceof View) {
                    view = (View) parent;
                    continue;
                } else {
                    view = null;
                    continue;
                }
            }
            if (view == null) {
                return viewGroup;
            }
        }
        return (ViewGroup) view;
    }

    @NonNull
    public Snackbar setAction(@StringRes int i, OnClickListener onClickListener) {
        return setAction(this.mContext.getText(i), onClickListener);
    }

    @NonNull
    public Snackbar setAction(CharSequence charSequence, final OnClickListener onClickListener) {
        Button actionView = this.mView.getActionView();
        if (TextUtils.isEmpty(charSequence) || onClickListener == null) {
            actionView.setVisibility(8);
            actionView.setOnClickListener(null);
        } else {
            actionView.setVisibility(0);
            actionView.setText(charSequence);
            actionView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    onClickListener.onClick(view);
                    Snackbar.this.dispatchDismiss(1);
                }
            });
        }
        return this;
    }

    @NonNull
    public Snackbar setActionTextColor(ColorStateList colorStateList) {
        this.mView.getActionView().setTextColor(colorStateList);
        return this;
    }

    @NonNull
    public Snackbar setActionTextColor(@ColorInt int i) {
        this.mView.getActionView().setTextColor(i);
        return this;
    }

    @NonNull
    public Snackbar setText(@NonNull CharSequence charSequence) {
        this.mView.getMessageView().setText(charSequence);
        return this;
    }

    @NonNull
    public Snackbar setText(@StringRes int i) {
        return setText(this.mContext.getText(i));
    }

    @NonNull
    public Snackbar setDuration(int i) {
        this.mDuration = i;
        return this;
    }

    public int getDuration() {
        return this.mDuration;
    }

    @NonNull
    public View getView() {
        return this.mView;
    }

    public void show() {
        SnackbarManager.getInstance().show(this.mDuration, this.mManagerCallback);
    }

    public void dismiss() {
        dispatchDismiss(3);
    }

    /* access modifiers changed from: private */
    public void dispatchDismiss(int i) {
        SnackbarManager.getInstance().dismiss(this.mManagerCallback, i);
    }

    @NonNull
    public Snackbar setCallback(Callback callback) {
        this.mCallback = callback;
        return this;
    }

    public boolean isShown() {
        return SnackbarManager.getInstance().isCurrent(this.mManagerCallback);
    }

    public boolean isShownOrQueued() {
        return SnackbarManager.getInstance().isCurrentOrNext(this.mManagerCallback);
    }

    /* access modifiers changed from: 0000 */
    public final void showView() {
        if (this.mView.getParent() == null) {
            LayoutParams layoutParams = this.mView.getLayoutParams();
            if (layoutParams instanceof CoordinatorLayout.LayoutParams) {
                Behavior behavior = new Behavior();
                behavior.setStartAlphaSwipeDistance(0.1f);
                behavior.setEndAlphaSwipeDistance(0.6f);
                behavior.setSwipeDirection(0);
                behavior.setListener(new OnDismissListener() {
                    public void onDismiss(View view) {
                        view.setVisibility(8);
                        Snackbar.this.dispatchDismiss(0);
                    }

                    public void onDragStateChanged(int i) {
                        if (i == 0) {
                            SnackbarManager.getInstance().restoreTimeout(Snackbar.this.mManagerCallback);
                        } else if (i == 1 || i == 2) {
                            SnackbarManager.getInstance().cancelTimeout(Snackbar.this.mManagerCallback);
                        }
                    }
                });
                ((CoordinatorLayout.LayoutParams) layoutParams).setBehavior(behavior);
            }
            this.mTargetParent.addView(this.mView);
        }
        this.mView.setOnAttachStateChangeListener(new OnAttachStateChangeListener() {
            public void onViewAttachedToWindow(View view) {
            }

            public void onViewDetachedFromWindow(View view) {
                if (Snackbar.this.isShownOrQueued()) {
                    Snackbar.sHandler.post(new Runnable() {
                        public void run() {
                            Snackbar.this.onViewHidden(3);
                        }
                    });
                }
            }
        });
        if (!ViewCompat.isLaidOut(this.mView)) {
            this.mView.setOnLayoutChangeListener(new OnLayoutChangeListener() {
                public void onLayoutChange(View view, int i, int i2, int i3, int i4) {
                    Snackbar.this.mView.setOnLayoutChangeListener(null);
                    if (Snackbar.this.shouldAnimate()) {
                        Snackbar.this.animateViewIn();
                    } else {
                        Snackbar.this.onViewShown();
                    }
                }
            });
        } else if (shouldAnimate()) {
            animateViewIn();
        } else {
            onViewShown();
        }
    }

    /* access modifiers changed from: private */
    public void animateViewIn() {
        if (VERSION.SDK_INT >= 14) {
            SnackbarLayout snackbarLayout = this.mView;
            ViewCompat.setTranslationY(snackbarLayout, (float) snackbarLayout.getHeight());
            ViewCompat.animate(this.mView).translationY(0.0f).setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR).setDuration(250).setListener(new ViewPropertyAnimatorListenerAdapter() {
                public void onAnimationStart(View view) {
                    Snackbar.this.mView.animateChildrenIn(70, Snackbar.ANIMATION_FADE_DURATION);
                }

                public void onAnimationEnd(View view) {
                    Snackbar.this.onViewShown();
                }
            }).start();
            return;
        }
        Animation loadAnimation = AnimationUtils.loadAnimation(this.mView.getContext(), C0007R.anim.design_snackbar_in);
        loadAnimation.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
        loadAnimation.setDuration(250);
        loadAnimation.setAnimationListener(new AnimationListener() {
            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                Snackbar.this.onViewShown();
            }
        });
        this.mView.startAnimation(loadAnimation);
    }

    private void animateViewOut(final int i) {
        if (VERSION.SDK_INT >= 14) {
            ViewCompat.animate(this.mView).translationY((float) this.mView.getHeight()).setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR).setDuration(250).setListener(new ViewPropertyAnimatorListenerAdapter() {
                public void onAnimationStart(View view) {
                    Snackbar.this.mView.animateChildrenOut(0, Snackbar.ANIMATION_FADE_DURATION);
                }

                public void onAnimationEnd(View view) {
                    Snackbar.this.onViewHidden(i);
                }
            }).start();
            return;
        }
        Animation loadAnimation = AnimationUtils.loadAnimation(this.mView.getContext(), C0007R.anim.design_snackbar_out);
        loadAnimation.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
        loadAnimation.setDuration(250);
        loadAnimation.setAnimationListener(new AnimationListener() {
            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                Snackbar.this.onViewHidden(i);
            }
        });
        this.mView.startAnimation(loadAnimation);
    }

    /* access modifiers changed from: 0000 */
    public final void hideView(int i) {
        if (!shouldAnimate() || this.mView.getVisibility() != 0) {
            onViewHidden(i);
        } else {
            animateViewOut(i);
        }
    }

    /* access modifiers changed from: private */
    public void onViewShown() {
        SnackbarManager.getInstance().onShown(this.mManagerCallback);
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onShown(this);
        }
    }

    /* access modifiers changed from: private */
    public void onViewHidden(int i) {
        SnackbarManager.getInstance().onDismissed(this.mManagerCallback);
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onDismissed(this, i);
        }
        ViewParent parent = this.mView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(this.mView);
        }
    }

    /* access modifiers changed from: private */
    public boolean shouldAnimate() {
        return !this.mAccessibilityManager.isEnabled();
    }
}
