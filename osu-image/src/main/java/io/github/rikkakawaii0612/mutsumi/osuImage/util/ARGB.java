package io.github.rikkakawaii0612.mutsumi.osuImage.util;

import java.awt.*;

public class ARGB {
    public static Color toColor(int argb) {
        return new Color(argb & 0xFF, (argb >>> 8) & 0xFF, (argb >>> 16) & 0xFF, argb >>> 24);
    }

    public static int toArgb(int r, int g, int b) {
        return r | (g << 8) | (b << 16) | 0xFF000000;
    }

    public static int toArgb(int r, int g, int b, int a) {
        return r | (g << 8) | (b << 16) | (a << 24);
    }
}
