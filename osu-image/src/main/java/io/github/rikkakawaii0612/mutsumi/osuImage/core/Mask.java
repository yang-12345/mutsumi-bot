package io.github.rikkakawaii0612.mutsumi.osuImage.core;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Mask extends Group {
    private final Group mask = new Group();

    public Mask(Element... element) {
        super(element);
    }

    public Group getMask() {
        return this.mask;
    }

    @Override
    public void render(Graphics2D g) {
        int w = this.getWidth();
        int h = this.getHeight();

        BufferedImage mask = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gMask = mask.createGraphics();
        gMask.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        gMask.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        gMask.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        this.mask.render(gMask);
        gMask.dispose();

        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        super.render(g2d);

        int[] maskPixels = mask.getRGB(0, 0, w, h, null, 0, w);
        int[] resultPixels = image.getRGB(0, 0, w, h, null, 0, w);
        for (int i = 0; i < resultPixels.length; i++) {
            int maskAlpha = maskPixels[i] >>> 24;
            if (maskAlpha == 0) continue;

            int pixel = resultPixels[i];
            int alpha = (255 - maskAlpha) * (pixel >>> 24) / 255;
            resultPixels[i] = (pixel & 0x00FFFFFF) | (alpha << 24);
        }
        image.setRGB(0, 0, w, h, resultPixels, 0, w);

        g.drawImage(image, 0, 0, null);
    }
}
