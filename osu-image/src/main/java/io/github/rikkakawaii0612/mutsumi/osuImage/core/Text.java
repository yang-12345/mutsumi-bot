package io.github.rikkakawaii0612.mutsumi.osuImage.core;

import io.github.rikkakawaii0612.mutsumi.osuImage.util.ARGB;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Text extends AbstractElement implements Colored {
    private String text;
    private int color = 0xFFFFFFFF;
    private int maxWidth = Integer.MAX_VALUE;
    private int maxHeight = Integer.MAX_VALUE;
    private String[] fonts = {"Torus", "Inter", "Helvetica Neue", "Tahoma", "Arial","Hiragino Sans GB",
            "Microsoft YaHei", "Apple SD Gothic Neo", "system-ui", "sans-serif"};
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
        if (maxFontSize < minFontSize || minFontSize <= 6 || this.fonts.length == 0) {
            this.width = 0;
            this.height = 0;
            return;
        }

        Color color = g.getColor();
        g.setColor(ARGB.toColor(this.color));

        FontRenderContext context = g.getFontRenderContext();

        char[] arr = text.toCharArray();
        double h = 0;
        Font[] fonts = new Font[this.fonts.length];
        boolean outOfRange = true;
        for (int fontSize = maxFontSize; fontSize >= minFontSize; fontSize--) {
            for (int i = 0; i < this.fonts.length; i++) {
                fonts[i] = new Font(this.fonts[i], Font.BOLD, fontSize);
            }
            double w = 0;
            double lines = 1;
            for (char c : arr) {
                String str = String.valueOf(c);
                double d = fonts[fonts.length - 1].getStringBounds(str, context).getWidth();
                for (Font font : fonts) {
                    if (font.canDisplay(c)) {
                        d = font.getStringBounds(str, context).getWidth();
                    }
                }
                if (w + d > maxWidth) {
                    w = d;
                    lines++;
                } else {
                    w += d;
                }
            }

            h = -5 + Arrays.stream(fonts)
                    .mapToDouble(font -> font.getMaxCharBounds(context).getHeight())
                    .max().orElse(0.0D);
            if (lines * h <= maxHeight) {
                outOfRange = false;
                break;
            }
        }

        //Font underline = en.deriveFont(en.getSize2D() - 4.0F);
        //Font underline = en;
        double w = 0, lines = 0;
        Font dotFont = getPrimary('.', fonts);
        double dotWidth = dotFont.getStringBounds(".", context).getWidth();
        double d = maxWidth - 3.0D * dotWidth;
        int limitLines = maxHeight / (int) h - 1;
        if (limitLines < 0) {
            this.width = 0;
            this.height = 0;
            return;
        }

        double resultWidth = 0;

        for (char c : arr) {
            Font font = getPrimary(c, fonts);
            Rectangle2D rec = font.getStringBounds(String.valueOf(c), context);
            double e = rec.getWidth();
            if (outOfRange && lines == limitLines && w + e > d) {
                Rectangle2D rectangle2D = dotFont.getStringBounds("...", context);
                g.setFont(dotFont);
                g.drawString("...", (int) w, (int) (lines * h - rectangle2D.getY()));
                break;
            }

            // todo: 这肯定得改.
            if (c != '_') {
            //if (true) {
                g.setFont(font);
                if (w + e > maxWidth) {
                    lines++;
                    g.drawString(String.valueOf(c), 0, (int) (lines * h - rec.getY()));
                    w = e;
                } else {
                    g.drawString(String.valueOf(c), (int) w, (int) (lines * h - rec.getY()));
                    w += e;
                }
            } else {
                // '_'
                g.setFont(font);
                double f = font.getStringBounds("_", context).getWidth();
                int i = (int) ((rec.getWidth() - f) / 2.0D);
                if (w + e > maxWidth) {
                    lines++;
                    g.drawString("_", i, -7 + (int) (lines * h - rec.getY()));
                    w = e;
                } else {
                    g.drawString("_", (int) w + i, -7 + (int) (lines * h - rec.getY()));
                    w += e;
                }
            }
            resultWidth = Math.max(w, resultWidth);
        }

        g.setColor(color);

        this.width = (int) resultWidth;
        this.height = (int) (h * (1 + lines));
    }

    private static Font getPrimary(char c, Font[] fonts) {
        for (Font font : fonts) {
            if (font.canDisplay(c)) {
                return font;
            }
        }
        return fonts[fonts.length - 1];
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

    public String[] getFont() {
        return this.fonts;
    }

    public void setFont(String[] fonts) {
        if (Arrays.equals(this.fonts, fonts)) {
            this.markDirty();
        }
        this.fonts = fonts;
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
