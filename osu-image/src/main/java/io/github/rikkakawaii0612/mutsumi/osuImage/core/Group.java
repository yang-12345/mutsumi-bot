package io.github.rikkakawaii0612.mutsumi.osuImage.core;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Group extends AbstractElement {
    private final List<Element> elements;

    public Group(Element... elements) {
        this.elements = new ArrayList<>();
        this.addChildren(elements);
    }

    @Override
    public int getWidth() {
        int result = 0;
        for (Element e : this.elements) {
            result = Math.max(result, e.getX() + e.getWidth());
        }
        return result;
    }

    @Override
    public int getHeight() {
        int result = 0;
        for (Element e : this.elements) {
            result = Math.max(result, e.getY() + e.getHeight());
        }
        return result;
    }

    @Override
    public void render(Graphics2D g) {
        this.elements.forEach(element -> {
            int x = element.getX(), y = element.getY();
            g.translate(x, y);

            double alpha = element.getAlpha() * this.getAlpha();
            Composite composite = g.getComposite();
            if (alpha < 1.0D) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha));
            }

            element.render(g);

            g.translate(-x, -y);
            if (alpha < 1.0D) {
                g.setComposite(composite);
            }
        });
    }

    public void addChildren(Element... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }
}
