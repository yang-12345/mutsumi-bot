package io.github.rikkakawaii0612.mutsumi.osuImage.core;

import io.github.rikkakawaii0612.mutsumi.osuImage.util.ARGB;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Text extends AbstractElement implements Colored {
    private String text;
    private int color = 0xFFFFFFFF;
    private int maxWidth = Integer.MAX_VALUE;
    private int maxHeight = Integer.MAX_VALUE;
    private String font = "等线";
    private int maxFontSize = 6;
    private int minFontSize = 12;

    private int width;
    private int height;
    // 用于检查是否需要重新计算宽度和高度
    private boolean dirty = true;

    public Text(String text) {
        this.text = text;
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
        if (this.dirty) {
            this.calculateSize();
        }
        return this.width;
    }

    @Override
    public int getHeight() {
        if (this.dirty) {
            this.calculateSize();
        }
        return this.height;
    }

    // 渲染代码有点长
    // 大概就是自动换行之类的东西
    @Override
    public void render(Graphics2D g) {
        if (maxFontSize < minFontSize || minFontSize <= 6) {
            return;
        }

        Color color = g.getColor();
        g.setColor(ARGB.toColor(this.color));

        FontRenderContext context = g.getFontRenderContext();

        char[] arr = text.toCharArray();
        int fontSize = maxFontSize;
        double h = 0;
        Font en = null, cn = null;
        boolean outOfRange = true;
        for (; fontSize >= minFontSize; fontSize--) {
            en = new Font(this.font, Font.BOLD, fontSize);
            cn = new Font(this.font, Font.BOLD, fontSize - 6);
            double w = 0;
            double lines = 1;
            for (char c : arr) {
                double d = en.canDisplay(c) ? en.getStringBounds(String.valueOf(c), context).getWidth()
                        : cn.getStringBounds(String.valueOf(c), context).getWidth();
                if (w + d > maxWidth) {
                    w = d;
                    lines++;
                } else {
                    w += d;
                }
            }

            h = -5 + Math.max(en.getMaxCharBounds(context).getHeight(), cn.getMaxCharBounds(context).getHeight());
            if (maxHeight != -1 && lines * h <= maxHeight) {
                outOfRange = false;
                break;
            }
        }

        Font underline = en.deriveFont(en.getSize2D() - 4.0F);
        double w = 0, lines = 0;
        double dotWidth = en.getStringBounds(".", context).getWidth();
        double d = maxWidth - 3.0D * dotWidth;
        int limitLines = maxHeight / (int) h - 1;

        double resultWidth = 0;

        for (char c : arr) {
            boolean bl = en.canDisplay(c);
            Rectangle2D rec = bl ? en.getStringBounds(String.valueOf(c), context)
                    : cn.getStringBounds(String.valueOf(c), context);
            double e = rec.getWidth();
            if (outOfRange && lines == limitLines && w + e > d) {
                Rectangle2D rectangle2D = en.getStringBounds("...", context);
                g.setFont(en);
                g.drawString("...", (int) w, (int) (lines * h - rectangle2D.getY()));
                break;
            }

            if (c != '_') {
                g.setFont(bl ? en : cn);
                if (w + e > maxWidth) {
                    lines++;
                    g.drawString(String.valueOf(c), 0, (int) (lines * h - rec.getY()));
                    w = e;
                } else {
                    g.drawString(String.valueOf(c), (int) w, (int) (lines * h - rec.getY()));
                    w += e;
                }
            } else {
                g.setFont(underline);
                double f = underline.getStringBounds("_", context).getWidth();
                int i = (int) ((rec.getWidth() - f) / 2.0D);
                if (w + e > maxWidth) {
                    lines++;
                    g.drawString("_", i, -7 + (int) (lines * h - rec.getY()));
                    w = e;
                } else {
                    g.drawString("_", (int) w + i, -7 + (int) (lines * h - rec.getY()));
                    w += e;
                    resultWidth = Math.max(w, resultWidth);
                }
            }
        }

        g.setColor(color);

        this.width = (int) resultWidth;
        this.height = (int) (h * (1 + lines));
    }

    public void setText(String text) {
        if (!this.text.equals(text)) {
            this.markDirty();
        }
        this.text = text;
    }

    public void setMaxWidth(int maxWidth) {
        if (this.maxWidth != maxWidth) {
            this.markDirty();
        }
        this.maxWidth = maxWidth;
    }

    public void setMaxHeight(int maxHeight) {
        if (this.maxHeight != maxHeight) {
            this.markDirty();
        }
        this.maxHeight = maxHeight;
    }

    public int getMaxWidth() {
        return this.maxWidth;
    }

    public int getMaxHeight() {
        return this.maxHeight;
    }

    public String getFont() {
        return this.font;
    }

    public void setFont(String font) {
        if (!this.font.equals(font)) {
            this.markDirty();
        }
        this.font = font;
    }

    public int getMaxFontSize() {
        return this.maxFontSize;
    }

    public int getMinFontSize() {
        return this.minFontSize;
    }

    public void setMaxFontSize(int maxFontSize) {
        if (this.maxFontSize != maxFontSize) {
            this.markDirty();
        }
        this.maxFontSize = maxFontSize;
    }

    public void setMinFontSize(int minFontSize) {
        if (this.minFontSize != minFontSize) {
            this.markDirty();
        }
        this.minFontSize = minFontSize;
    }

    private void markDirty() {
        this.dirty = true;
    }

    private void calculateSize() {
        if (maxFontSize < minFontSize || minFontSize <= 6) {
            this.width = 0;
            this.height = 0;
            return;
        }

        Graphics2D g = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();
        this.render(g);
        this.dirty = false;
    }
}
