package android.support.p000v4.media;

import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* renamed from: android.support.v4.media.RatingCompat */
public final class RatingCompat implements Parcelable {
    public static final Creator<RatingCompat> CREATOR = new Creator<RatingCompat>() {
        public RatingCompat createFromParcel(Parcel parcel) {
            return new RatingCompat(parcel.readInt(), parcel.readFloat());
        }

        public RatingCompat[] newArray(int i) {
            return new RatingCompat[i];
        }
    };
    public static final int RATING_3_STARS = 3;
    public static final int RATING_4_STARS = 4;
    public static final int RATING_5_STARS = 5;
    public static final int RATING_HEART = 1;
    public static final int RATING_NONE = 0;
    private static final float RATING_NOT_RATED = -1.0f;
    public static final int RATING_PERCENTAGE = 6;
    public static final int RATING_THUMB_UP_DOWN = 2;
    private static final String TAG = "Rating";
    private Object mRatingObj;
    private final int mRatingStyle;
    private final float mRatingValue;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.support.v4.media.RatingCompat$StarStyle */
    public @interface StarStyle {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.support.v4.media.RatingCompat$Style */
    public @interface Style {
    }

    private RatingCompat(int i, float f) {
        this.mRatingStyle = i;
        this.mRatingValue = f;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Rating:style=");
        sb.append(this.mRatingStyle);
        sb.append(" rating=");
        float f = this.mRatingValue;
        sb.append(f < 0.0f ? "unrated" : String.valueOf(f));
        return sb.toString();
    }

    public int describeContents() {
        return this.mRatingStyle;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mRatingStyle);
        parcel.writeFloat(this.mRatingValue);
    }

    public static RatingCompat newUnratedRating(int i) {
        switch (i) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                return new RatingCompat(i, RATING_NOT_RATED);
            default:
                return null;
        }
    }

    public static RatingCompat newHeartRating(boolean z) {
        return new RatingCompat(1, z ? 1.0f : 0.0f);
    }

    public static RatingCompat newThumbRating(boolean z) {
        return new RatingCompat(2, z ? 1.0f : 0.0f);
    }

    public static RatingCompat newStarRating(int i, float f) {
        float f2;
        String str = TAG;
        if (i == 3) {
            f2 = 3.0f;
        } else if (i == 4) {
            f2 = 4.0f;
        } else if (i != 5) {
            StringBuilder sb = new StringBuilder();
            sb.append("Invalid rating style (");
            sb.append(i);
            sb.append(") for a star rating");
            Log.e(str, sb.toString());
            return null;
        } else {
            f2 = 5.0f;
        }
        if (f >= 0.0f && f <= f2) {
            return new RatingCompat(i, f);
        }
        Log.e(str, "Trying to set out of range star-based rating");
        return null;
    }

    public static RatingCompat newPercentageRating(float f) {
        if (f >= 0.0f && f <= 100.0f) {
            return new RatingCompat(6, f);
        }
        Log.e(TAG, "Invalid percentage-based rating value");
        return null;
    }

    public boolean isRated() {
        return this.mRatingValue >= 0.0f;
    }

    public int getRatingStyle() {
        return this.mRatingStyle;
    }

    public boolean hasHeart() {
        boolean z = false;
        if (this.mRatingStyle != 1) {
            return false;
        }
        if (this.mRatingValue == 1.0f) {
            z = true;
        }
        return z;
    }

    public boolean isThumbUp() {
        boolean z = false;
        if (this.mRatingStyle != 2) {
            return false;
        }
        if (this.mRatingValue == 1.0f) {
            z = true;
        }
        return z;
    }

    public float getStarRating() {
        int i = this.mRatingStyle;
        return ((i == 3 || i == 4 || i == 5) && isRated()) ? this.mRatingValue : RATING_NOT_RATED;
    }

    public float getPercentRating() {
        return (this.mRatingStyle != 6 || !isRated()) ? RATING_NOT_RATED : this.mRatingValue;
    }

    public static RatingCompat fromRating(Object obj) {
        RatingCompat ratingCompat = null;
        if (obj != null && VERSION.SDK_INT >= 21) {
            int ratingStyle = RatingCompatApi21.getRatingStyle(obj);
            if (RatingCompatApi21.isRated(obj)) {
                switch (ratingStyle) {
                    case 1:
                        ratingCompat = newHeartRating(RatingCompatApi21.hasHeart(obj));
                        break;
                    case 2:
                        ratingCompat = newThumbRating(RatingCompatApi21.isThumbUp(obj));
                        break;
                    case 3:
                    case 4:
                    case 5:
                        ratingCompat = newStarRating(ratingStyle, RatingCompatApi21.getStarRating(obj));
                        break;
                    case 6:
                        ratingCompat = newPercentageRating(RatingCompatApi21.getPercentRating(obj));
                        break;
                    default:
                        return null;
                }
            } else {
                ratingCompat = newUnratedRating(ratingStyle);
            }
            ratingCompat.mRatingObj = obj;
        }
        return ratingCompat;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0044, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Object getRating() {
        /*
            r2 = this;
            java.lang.Object r0 = r2.mRatingObj
            if (r0 != 0) goto L_0x0050
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 21
            if (r0 >= r1) goto L_0x000b
            goto L_0x0050
        L_0x000b:
            boolean r0 = r2.isRated()
            if (r0 == 0) goto L_0x0045
            int r0 = r2.mRatingStyle
            switch(r0) {
                case 1: goto L_0x0038;
                case 2: goto L_0x002d;
                case 3: goto L_0x0022;
                case 4: goto L_0x0022;
                case 5: goto L_0x0022;
                case 6: goto L_0x0017;
                default: goto L_0x0016;
            }
        L_0x0016:
            goto L_0x0043
        L_0x0017:
            float r0 = r2.getPercentRating()
            java.lang.Object r0 = android.support.p000v4.media.RatingCompatApi21.newPercentageRating(r0)
            r2.mRatingObj = r0
            goto L_0x0043
        L_0x0022:
            float r1 = r2.getStarRating()
            java.lang.Object r0 = android.support.p000v4.media.RatingCompatApi21.newStarRating(r0, r1)
            r2.mRatingObj = r0
            goto L_0x004d
        L_0x002d:
            boolean r0 = r2.isThumbUp()
            java.lang.Object r0 = android.support.p000v4.media.RatingCompatApi21.newThumbRating(r0)
            r2.mRatingObj = r0
            goto L_0x004d
        L_0x0038:
            boolean r0 = r2.hasHeart()
            java.lang.Object r0 = android.support.p000v4.media.RatingCompatApi21.newHeartRating(r0)
            r2.mRatingObj = r0
            goto L_0x004d
        L_0x0043:
            r0 = 0
            return r0
        L_0x0045:
            int r0 = r2.mRatingStyle
            java.lang.Object r0 = android.support.p000v4.media.RatingCompatApi21.newUnratedRating(r0)
            r2.mRatingObj = r0
        L_0x004d:
            java.lang.Object r0 = r2.mRatingObj
            return r0
        L_0x0050:
            java.lang.Object r0 = r2.mRatingObj
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.p000v4.media.RatingCompat.getRating():java.lang.Object");
    }
}
