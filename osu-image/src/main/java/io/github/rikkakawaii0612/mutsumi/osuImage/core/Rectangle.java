package io.github.rikkakawaii0612.mutsumi.osuImage.core;

import io.github.rikkakawaii0612.mutsumi.osuImage.util.ARGB;

import java.awt.*;

public class Rectangle extends AbstractElement implements Colored {
    private int color = 0xFFFFFFFF;
    private int width = 0;
    private int height = 0;
    private int corner = 0;

    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public int getColor() {
        return this.color;
    }

    @Override
    public void setColor(int argb) {
        this.color = argb;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getCorner() {
        return this.corner;
    }

    public void setCorner(int corner) {
        this.corner = corner;
    }

    @Override
    public void render(Graphics2D g) {
        Color color = g.getColor();
        g.setColor(ARGB.toColor(this.color));
        if (this.corner <= 0) {
            g.fillRect(0, 0, this.width, this.height);
        } else {
            g.fillRoundRect(0, 0, this.width, this.height, this.corner, this.corner);
        }
        g.setColor(color);
    }
}
