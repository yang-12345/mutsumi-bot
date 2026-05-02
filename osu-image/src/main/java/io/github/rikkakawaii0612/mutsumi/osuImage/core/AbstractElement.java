package io.github.rikkakawaii0612.mutsumi.osuImage.core;

public abstract class AbstractElement implements Element {
    private int x = 0;
    private int y = 0;
    private double alpha = 1.0D;

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public double getAlpha() {
        return this.alpha;
    }

    @Override
    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }
}
