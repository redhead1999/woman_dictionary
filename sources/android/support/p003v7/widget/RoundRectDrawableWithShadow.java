package android.support.p003v7.widget;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.support.p003v7.cardview.C0257R;

/* renamed from: android.support.v7.widget.RoundRectDrawableWithShadow */
class RoundRectDrawableWithShadow extends Drawable {
    static final double COS_45 = Math.cos(Math.toRadians(45.0d));
    static final float SHADOW_MULTIPLIER = 1.5f;
    static RoundRectHelper sRoundRectHelper;
    private boolean mAddPaddingForCorners = true;
    final RectF mCardBounds;
    float mCornerRadius;
    Paint mCornerShadowPaint;
    Path mCornerShadowPath;
    private boolean mDirty = true;
    Paint mEdgeShadowPaint;
    final int mInsetShadow;
    float mMaxShadowSize;
    Paint mPaint;
    private boolean mPrintedShadowClipWarning = false;
    float mRawMaxShadowSize;
    float mRawShadowSize;
    private final int mShadowEndColor;
    float mShadowSize;
    private final int mShadowStartColor;

    /* renamed from: android.support.v7.widget.RoundRectDrawableWithShadow$RoundRectHelper */
    interface RoundRectHelper {
        void drawRoundRect(Canvas canvas, RectF rectF, float f, Paint paint);
    }

    public int getOpacity() {
        return -3;
    }

    RoundRectDrawableWithShadow(Resources resources, int i, float f, float f2, float f3) {
        this.mShadowStartColor = resources.getColor(C0257R.color.cardview_shadow_start_color);
        this.mShadowEndColor = resources.getColor(C0257R.color.cardview_shadow_end_color);
        this.mInsetShadow = resources.getDimensionPixelSize(C0257R.dimen.cardview_compat_inset_shadow);
        this.mPaint = new Paint(5);
        this.mPaint.setColor(i);
        this.mCornerShadowPaint = new Paint(5);
        this.mCornerShadowPaint.setStyle(Style.FILL);
        this.mCornerRadius = (float) ((int) (f + 0.5f));
        this.mCardBounds = new RectF();
        this.mEdgeShadowPaint = new Paint(this.mCornerShadowPaint);
        this.mEdgeShadowPaint.setAntiAlias(false);
        setShadowSize(f2, f3);
    }

    private int toEven(float f) {
        int i = (int) (f + 0.5f);
        return i % 2 == 1 ? i - 1 : i;
    }

    public void setAddPaddingForCorners(boolean z) {
        this.mAddPaddingForCorners = z;
        invalidateSelf();
    }

    public void setAlpha(int i) {
        this.mPaint.setAlpha(i);
        this.mCornerShadowPaint.setAlpha(i);
        this.mEdgeShadowPaint.setAlpha(i);
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        this.mDirty = true;
    }

    /* access modifiers changed from: 0000 */
    public void setShadowSize(float f, float f2) {
        String str = ". Must be >= 0";
        if (f < 0.0f) {
            StringBuilder sb = new StringBuilder();
            sb.append("Invalid shadow size ");
            sb.append(f);
            sb.append(str);
            throw new IllegalArgumentException(sb.toString());
        } else if (f2 >= 0.0f) {
            float even = (float) toEven(f);
            float even2 = (float) toEven(f2);
            if (even > even2) {
                if (!this.mPrintedShadowClipWarning) {
                    this.mPrintedShadowClipWarning = true;
                }
                even = even2;
            }
            if (this.mRawShadowSize != even || this.mRawMaxShadowSize != even2) {
                this.mRawShadowSize = even;
                this.mRawMaxShadowSize = even2;
                float f3 = even * SHADOW_MULTIPLIER;
                int i = this.mInsetShadow;
                this.mShadowSize = (float) ((int) (f3 + ((float) i) + 0.5f));
                this.mMaxShadowSize = even2 + ((float) i);
                this.mDirty = true;
                invalidateSelf();
            }
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Invalid max shadow size ");
            sb2.append(f2);
            sb2.append(str);
            throw new IllegalArgumentException(sb2.toString());
        }
    }

    public boolean getPadding(Rect rect) {
        int ceil = (int) Math.ceil((double) calculateVerticalPadding(this.mRawMaxShadowSize, this.mCornerRadius, this.mAddPaddingForCorners));
        int ceil2 = (int) Math.ceil((double) calculateHorizontalPadding(this.mRawMaxShadowSize, this.mCornerRadius, this.mAddPaddingForCorners));
        rect.set(ceil2, ceil, ceil2, ceil);
        return true;
    }

    static float calculateVerticalPadding(float f, float f2, boolean z) {
        if (!z) {
            return f * SHADOW_MULTIPLIER;
        }
        double d = (double) (f * SHADOW_MULTIPLIER);
        double d2 = 1.0d - COS_45;
        double d3 = (double) f2;
        Double.isNaN(d3);
        double d4 = d2 * d3;
        Double.isNaN(d);
        return (float) (d + d4);
    }

    static float calculateHorizontalPadding(float f, float f2, boolean z) {
        if (!z) {
            return f;
        }
        double d = (double) f;
        double d2 = 1.0d - COS_45;
        double d3 = (double) f2;
        Double.isNaN(d3);
        double d4 = d2 * d3;
        Double.isNaN(d);
        return (float) (d + d4);
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }

    /* access modifiers changed from: 0000 */
    public void setCornerRadius(float f) {
        if (f >= 0.0f) {
            float f2 = (float) ((int) (f + 0.5f));
            if (this.mCornerRadius != f2) {
                this.mCornerRadius = f2;
                this.mDirty = true;
                invalidateSelf();
                return;
            }
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Invalid radius ");
        sb.append(f);
        sb.append(". Must be >= 0");
        throw new IllegalArgumentException(sb.toString());
    }

    public void draw(Canvas canvas) {
        if (this.mDirty) {
            buildComponents(getBounds());
            this.mDirty = false;
        }
        canvas.translate(0.0f, this.mRawShadowSize / 2.0f);
        drawShadow(canvas);
        canvas.translate(0.0f, (-this.mRawShadowSize) / 2.0f);
        sRoundRectHelper.drawRoundRect(canvas, this.mCardBounds, this.mCornerRadius, this.mPaint);
    }

    private void drawShadow(Canvas canvas) {
        float f = this.mCornerRadius;
        float f2 = (-f) - this.mShadowSize;
        float f3 = f + ((float) this.mInsetShadow) + (this.mRawShadowSize / 2.0f);
        float f4 = f3 * 2.0f;
        boolean z = this.mCardBounds.width() - f4 > 0.0f;
        boolean z2 = this.mCardBounds.height() - f4 > 0.0f;
        int save = canvas.save();
        canvas.translate(this.mCardBounds.left + f3, this.mCardBounds.top + f3);
        canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
        if (z) {
            canvas.drawRect(0.0f, f2, this.mCardBounds.width() - f4, -this.mCornerRadius, this.mEdgeShadowPaint);
        }
        canvas.restoreToCount(save);
        int save2 = canvas.save();
        canvas.translate(this.mCardBounds.right - f3, this.mCardBounds.bottom - f3);
        canvas.rotate(180.0f);
        canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
        if (z) {
            canvas.drawRect(0.0f, f2, this.mCardBounds.width() - f4, (-this.mCornerRadius) + this.mShadowSize, this.mEdgeShadowPaint);
        }
        canvas.restoreToCount(save2);
        int save3 = canvas.save();
        canvas.translate(this.mCardBounds.left + f3, this.mCardBounds.bottom - f3);
        canvas.rotate(270.0f);
        canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
        if (z2) {
            canvas.drawRect(0.0f, f2, this.mCardBounds.height() - f4, -this.mCornerRadius, this.mEdgeShadowPaint);
        }
        canvas.restoreToCount(save3);
        int save4 = canvas.save();
        canvas.translate(this.mCardBounds.right - f3, this.mCardBounds.top + f3);
        canvas.rotate(90.0f);
        canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
        if (z2) {
            canvas.drawRect(0.0f, f2, this.mCardBounds.height() - f4, -this.mCornerRadius, this.mEdgeShadowPaint);
        }
        canvas.restoreToCount(save4);
    }

    private void buildShadowCorners() {
        float f = this.mCornerRadius;
        RectF rectF = new RectF(-f, -f, f, f);
        RectF rectF2 = new RectF(rectF);
        float f2 = this.mShadowSize;
        rectF2.inset(-f2, -f2);
        Path path = this.mCornerShadowPath;
        if (path == null) {
            this.mCornerShadowPath = new Path();
        } else {
            path.reset();
        }
        this.mCornerShadowPath.setFillType(FillType.EVEN_ODD);
        this.mCornerShadowPath.moveTo(-this.mCornerRadius, 0.0f);
        this.mCornerShadowPath.rLineTo(-this.mShadowSize, 0.0f);
        this.mCornerShadowPath.arcTo(rectF2, 180.0f, 90.0f, false);
        this.mCornerShadowPath.arcTo(rectF, 270.0f, -90.0f, false);
        this.mCornerShadowPath.close();
        float f3 = this.mCornerRadius;
        float f4 = this.mShadowSize;
        float f5 = f3 / (f3 + f4);
        Paint paint = this.mCornerShadowPaint;
        float f6 = f3 + f4;
        int i = this.mShadowStartColor;
        RadialGradient radialGradient = new RadialGradient(0.0f, 0.0f, f6, new int[]{i, i, this.mShadowEndColor}, new float[]{0.0f, f5, 1.0f}, TileMode.CLAMP);
        paint.setShader(radialGradient);
        Paint paint2 = this.mEdgeShadowPaint;
        float f7 = this.mCornerRadius;
        float f8 = -f7;
        float f9 = this.mShadowSize;
        float f10 = f8 + f9;
        float f11 = (-f7) - f9;
        int i2 = this.mShadowStartColor;
        LinearGradient linearGradient = new LinearGradient(0.0f, f10, 0.0f, f11, new int[]{i2, i2, this.mShadowEndColor}, new float[]{0.0f, 0.5f, 1.0f}, TileMode.CLAMP);
        paint2.setShader(linearGradient);
        this.mEdgeShadowPaint.setAntiAlias(false);
    }

    private void buildComponents(Rect rect) {
        float f = this.mRawMaxShadowSize * SHADOW_MULTIPLIER;
        this.mCardBounds.set(((float) rect.left) + this.mRawMaxShadowSize, ((float) rect.top) + f, ((float) rect.right) - this.mRawMaxShadowSize, ((float) rect.bottom) - f);
        buildShadowCorners();
    }

    /* access modifiers changed from: 0000 */
    public float getCornerRadius() {
        return this.mCornerRadius;
    }

    /* access modifiers changed from: 0000 */
    public void getMaxShadowAndCornerPadding(Rect rect) {
        getPadding(rect);
    }

    /* access modifiers changed from: 0000 */
    public void setShadowSize(float f) {
        setShadowSize(f, this.mRawMaxShadowSize);
    }

    /* access modifiers changed from: 0000 */
    public void setMaxShadowSize(float f) {
        setShadowSize(this.mRawShadowSize, f);
    }

    /* access modifiers changed from: 0000 */
    public float getShadowSize() {
        return this.mRawShadowSize;
    }

    /* access modifiers changed from: 0000 */
    public float getMaxShadowSize() {
        return this.mRawMaxShadowSize;
    }

    /* access modifiers changed from: 0000 */
    public float getMinWidth() {
        float f = this.mRawMaxShadowSize;
        return (Math.max(f, this.mCornerRadius + ((float) this.mInsetShadow) + (f / 2.0f)) * 2.0f) + ((this.mRawMaxShadowSize + ((float) this.mInsetShadow)) * 2.0f);
    }

    /* access modifiers changed from: 0000 */
    public float getMinHeight() {
        float f = this.mRawMaxShadowSize;
        return (Math.max(f, this.mCornerRadius + ((float) this.mInsetShadow) + ((f * SHADOW_MULTIPLIER) / 2.0f)) * 2.0f) + (((this.mRawMaxShadowSize * SHADOW_MULTIPLIER) + ((float) this.mInsetShadow)) * 2.0f);
    }

    public void setColor(int i) {
        this.mPaint.setColor(i);
        invalidateSelf();
    }
}
