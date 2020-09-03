package android.support.design.widget;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

class ValueAnimatorCompatImplEclairMr1 extends Impl {
    private static final int DEFAULT_DURATION = 200;
    private static final int HANDLER_DELAY = 10;
    private static final Handler sHandler = new Handler(Looper.getMainLooper());
    private float mAnimatedFraction;
    private int mDuration = 200;
    private final float[] mFloatValues = new float[2];
    private final int[] mIntValues = new int[2];
    private Interpolator mInterpolator;
    private boolean mIsRunning;
    private AnimatorListenerProxy mListener;
    private final Runnable mRunnable = new Runnable() {
        public void run() {
            ValueAnimatorCompatImplEclairMr1.this.update();
        }
    };
    private long mStartTime;
    private AnimatorUpdateListenerProxy mUpdateListener;

    ValueAnimatorCompatImplEclairMr1() {
    }

    public void start() {
        if (!this.mIsRunning) {
            if (this.mInterpolator == null) {
                this.mInterpolator = new AccelerateDecelerateInterpolator();
            }
            this.mStartTime = SystemClock.uptimeMillis();
            this.mIsRunning = true;
            AnimatorListenerProxy animatorListenerProxy = this.mListener;
            if (animatorListenerProxy != null) {
                animatorListenerProxy.onAnimationStart();
            }
            sHandler.postDelayed(this.mRunnable, 10);
        }
    }

    public boolean isRunning() {
        return this.mIsRunning;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    public void setListener(AnimatorListenerProxy animatorListenerProxy) {
        this.mListener = animatorListenerProxy;
    }

    public void setUpdateListener(AnimatorUpdateListenerProxy animatorUpdateListenerProxy) {
        this.mUpdateListener = animatorUpdateListenerProxy;
    }

    public void setIntValues(int i, int i2) {
        int[] iArr = this.mIntValues;
        iArr[0] = i;
        iArr[1] = i2;
    }

    public int getAnimatedIntValue() {
        int[] iArr = this.mIntValues;
        return AnimationUtils.lerp(iArr[0], iArr[1], getAnimatedFraction());
    }

    public void setFloatValues(float f, float f2) {
        float[] fArr = this.mFloatValues;
        fArr[0] = f;
        fArr[1] = f2;
    }

    public float getAnimatedFloatValue() {
        float[] fArr = this.mFloatValues;
        return AnimationUtils.lerp(fArr[0], fArr[1], getAnimatedFraction());
    }

    public void setDuration(int i) {
        this.mDuration = i;
    }

    public void cancel() {
        this.mIsRunning = false;
        sHandler.removeCallbacks(this.mRunnable);
        AnimatorListenerProxy animatorListenerProxy = this.mListener;
        if (animatorListenerProxy != null) {
            animatorListenerProxy.onAnimationCancel();
        }
    }

    public float getAnimatedFraction() {
        return this.mAnimatedFraction;
    }

    public void end() {
        if (this.mIsRunning) {
            this.mIsRunning = false;
            sHandler.removeCallbacks(this.mRunnable);
            this.mAnimatedFraction = 1.0f;
            AnimatorUpdateListenerProxy animatorUpdateListenerProxy = this.mUpdateListener;
            if (animatorUpdateListenerProxy != null) {
                animatorUpdateListenerProxy.onAnimationUpdate();
            }
            AnimatorListenerProxy animatorListenerProxy = this.mListener;
            if (animatorListenerProxy != null) {
                animatorListenerProxy.onAnimationEnd();
            }
        }
    }

    public long getDuration() {
        return (long) this.mDuration;
    }

    /* access modifiers changed from: private */
    public void update() {
        if (this.mIsRunning) {
            float uptimeMillis = ((float) (SystemClock.uptimeMillis() - this.mStartTime)) / ((float) this.mDuration);
            Interpolator interpolator = this.mInterpolator;
            if (interpolator != null) {
                uptimeMillis = interpolator.getInterpolation(uptimeMillis);
            }
            this.mAnimatedFraction = uptimeMillis;
            AnimatorUpdateListenerProxy animatorUpdateListenerProxy = this.mUpdateListener;
            if (animatorUpdateListenerProxy != null) {
                animatorUpdateListenerProxy.onAnimationUpdate();
            }
            if (SystemClock.uptimeMillis() >= this.mStartTime + ((long) this.mDuration)) {
                this.mIsRunning = false;
                AnimatorListenerProxy animatorListenerProxy = this.mListener;
                if (animatorListenerProxy != null) {
                    animatorListenerProxy.onAnimationEnd();
                }
            }
        }
        if (this.mIsRunning) {
            sHandler.postDelayed(this.mRunnable, 10);
        }
    }
}
