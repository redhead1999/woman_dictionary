package android.support.design.widget;

import android.util.StateSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

final class StateListAnimator {
    private AnimationListener mAnimationListener = new AnimationListener() {
        public void onAnimationRepeat(Animation animation) {
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            if (StateListAnimator.this.mRunningAnimation == animation) {
                StateListAnimator.this.mRunningAnimation = null;
            }
        }
    };
    private Tuple mLastMatch = null;
    /* access modifiers changed from: private */
    public Animation mRunningAnimation = null;
    private final ArrayList<Tuple> mTuples = new ArrayList<>();
    private WeakReference<View> mViewRef;

    static class Tuple {
        final Animation mAnimation;
        final int[] mSpecs;

        private Tuple(int[] iArr, Animation animation) {
            this.mSpecs = iArr;
            this.mAnimation = animation;
        }

        /* access modifiers changed from: 0000 */
        public int[] getSpecs() {
            return this.mSpecs;
        }

        /* access modifiers changed from: 0000 */
        public Animation getAnimation() {
            return this.mAnimation;
        }
    }

    StateListAnimator() {
    }

    public void addState(int[] iArr, Animation animation) {
        Tuple tuple = new Tuple(iArr, animation);
        animation.setAnimationListener(this.mAnimationListener);
        this.mTuples.add(tuple);
    }

    /* access modifiers changed from: 0000 */
    public Animation getRunningAnimation() {
        return this.mRunningAnimation;
    }

    /* access modifiers changed from: 0000 */
    public View getTarget() {
        WeakReference<View> weakReference = this.mViewRef;
        if (weakReference == null) {
            return null;
        }
        return (View) weakReference.get();
    }

    /* access modifiers changed from: 0000 */
    public void setTarget(View view) {
        View target = getTarget();
        if (target != view) {
            if (target != null) {
                clearTarget();
            }
            if (view != null) {
                this.mViewRef = new WeakReference<>(view);
            }
        }
    }

    private void clearTarget() {
        View target = getTarget();
        int size = this.mTuples.size();
        for (int i = 0; i < size; i++) {
            if (target.getAnimation() == ((Tuple) this.mTuples.get(i)).mAnimation) {
                target.clearAnimation();
            }
        }
        this.mViewRef = null;
        this.mLastMatch = null;
        this.mRunningAnimation = null;
    }

    /* access modifiers changed from: 0000 */
    public void setState(int[] iArr) {
        Tuple tuple;
        int size = this.mTuples.size();
        int i = 0;
        while (true) {
            if (i >= size) {
                tuple = null;
                break;
            }
            tuple = (Tuple) this.mTuples.get(i);
            if (StateSet.stateSetMatches(tuple.mSpecs, iArr)) {
                break;
            }
            i++;
        }
        Tuple tuple2 = this.mLastMatch;
        if (tuple != tuple2) {
            if (tuple2 != null) {
                cancel();
            }
            this.mLastMatch = tuple;
            View view = (View) this.mViewRef.get();
            if (!(tuple == null || view == null || view.getVisibility() != 0)) {
                start(tuple);
            }
        }
    }

    private void start(Tuple tuple) {
        this.mRunningAnimation = tuple.mAnimation;
        View target = getTarget();
        if (target != null) {
            target.startAnimation(this.mRunningAnimation);
        }
    }

    private void cancel() {
        if (this.mRunningAnimation != null) {
            View target = getTarget();
            if (target != null && target.getAnimation() == this.mRunningAnimation) {
                target.clearAnimation();
            }
            this.mRunningAnimation = null;
        }
    }

    /* access modifiers changed from: 0000 */
    public ArrayList<Tuple> getTuples() {
        return this.mTuples;
    }

    public void jumpToCurrentState() {
        if (this.mRunningAnimation != null) {
            View target = getTarget();
            if (target != null && target.getAnimation() == this.mRunningAnimation) {
                target.clearAnimation();
            }
        }
    }
}
