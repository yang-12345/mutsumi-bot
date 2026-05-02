package io.github.rikkakawaii0612.mutsumi.osuImage.core;

import java.awt.*;

public interface Element {
    int getWidth();

    int getHeight();

    int getX();

    int getY();

    void setX(int x);

    void setY(int y);

    default void setPosition(int x, int y) {
        this.setX(x);
        this.setY(y);
    }

    double getAlpha();

    void setAlpha(double alpha);

    void render(Graphics2D g);
}
