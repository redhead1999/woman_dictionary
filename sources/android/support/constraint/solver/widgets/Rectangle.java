package android.support.constraint.solver.widgets;

public class Rectangle {
    public int height;
    public int width;

    /* renamed from: x */
    public int f7x;

    /* renamed from: y */
    public int f8y;

    public void setBounds(int i, int i2, int i3, int i4) {
        this.f7x = i;
        this.f8y = i2;
        this.width = i3;
        this.height = i4;
    }

    /* access modifiers changed from: 0000 */
    public void grow(int i, int i2) {
        this.f7x -= i;
        this.f8y -= i2;
        this.width += i * 2;
        this.height += i2 * 2;
    }

    /* access modifiers changed from: 0000 */
    public boolean intersects(Rectangle rectangle) {
        int i = this.f7x;
        int i2 = rectangle.f7x;
        if (i >= i2 && i < i2 + rectangle.width) {
            int i3 = this.f8y;
            int i4 = rectangle.f8y;
            if (i3 >= i4 && i3 < i4 + rectangle.height) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(int i, int i2) {
        int i3 = this.f7x;
        if (i >= i3 && i < i3 + this.width) {
            int i4 = this.f8y;
            if (i2 >= i4 && i2 < i4 + this.height) {
                return true;
            }
        }
        return false;
    }

    public int getCenterX() {
        return (this.f7x + this.width) / 2;
    }

    public int getCenterY() {
        return (this.f8y + this.height) / 2;
    }
}
