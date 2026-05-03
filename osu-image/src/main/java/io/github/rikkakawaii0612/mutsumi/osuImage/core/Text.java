package io.github.rikkakawaii0612.mutsumi.osuImage.core;

import io.github.rikkakawaii0612.mutsumi.osuImage.util.ARGB;
import io.github.rikkakawaii0612.mutsumi.osuImage.util.AttributedStringBuilder;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.AttributedCharacterIterator;
import java.util.Arrays;

public class Text extends AbstractElement implements Colored {
    private String text;
    private int color = 0xFFFFFFFF;
    private int maxWidth = Integer.MAX_VALUE;
    private int maxHeight = Integer.MAX_VALUE;
    private String[] fonts = {"Torus", "等线", "微软雅黑", "Inter", "Microsoft YaHei", "Helvetica Neue", "Tahoma", "Arial",
            "Hiragino Sans GB", "Apple SD Gothic Neo", "system-ui", "sans-serif"};
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

    // 0 人能看懂的文本渲染, 你能看懂吗
    @Override
    public void render(Graphics2D g) {
        if (maxFontSize < minFontSize || this.fonts.length == 0) {
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
            double lines = 1;
            AttributedStringBuilder builder = new AttributedStringBuilder();
            for (char c : arr) {
                builder.setFont(getPrimary(c, fonts)).append(c);
                TextLayout layout = new TextLayout(builder.build().getIterator(), context);
                if (layout.getAdvance() > maxWidth) {
                    lines++;
                    builder.clear();
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

        double lines = 0;
        Font dotFont = getPrimary('.', fonts);
        double ellipsisWidth = dotFont.getStringBounds("...", context).getWidth();
        double d = maxWidth - ellipsisWidth;
        int limitLines = maxHeight / (int) h - 1;
        if (limitLines < 0) {
            this.width = 0;
            this.height = 0;
            return;
        }

        double resultWidth = 0;
        double yOffset = 4.0D + Arrays.stream(fonts)
                .mapToDouble(font -> font.getMaxCharBounds(context).getY())
                .min().orElse(0.0D);

        AttributedStringBuilder builder = new AttributedStringBuilder();
        for (char c : arr) {
            Font font = getPrimary(c, fonts);
            builder.setFont(font).append(c);

            TextLayout layout = new TextLayout(builder.build().getIterator(), context);
            Rectangle2D rec = layout.getBounds();
            if (outOfRange && lines == limitLines && rec.getWidth() > d) {
                builder.removeLast().setFont(dotFont).append("...");
                AttributedCharacterIterator iterator = builder.build().getIterator();
                g.drawString(iterator, 0, (int) (lines * h - yOffset));
                TextLayout layout2 = new TextLayout(iterator, context);
                resultWidth = Math.max(layout2.getBounds().getWidth(), resultWidth);
                break;
            }

            if (rec.getWidth() > maxWidth) {
                builder.removeLast();
                g.drawString(builder.build().getIterator(), 0, (int) (lines * h - yOffset));
                builder.clear().append(c);
                lines++;
            }
            resultWidth = Math.max(rec.getWidth(), resultWidth);
        }

        AttributedCharacterIterator iterator = builder.build().getIterator();
        g.drawString(iterator, 0, (int) (lines * h - yOffset));

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
