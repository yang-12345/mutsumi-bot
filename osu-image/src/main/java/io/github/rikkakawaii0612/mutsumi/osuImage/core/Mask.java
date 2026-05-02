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
        BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        super.render(g2d);

        g2d.setComposite(AlphaComposite.SrcOut);
        this.mask.render(g2d);
        g2d.dispose();

        g.drawImage(image, 0, 0, null);
    }
}
