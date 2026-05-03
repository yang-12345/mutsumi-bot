package io.github.rikkakawaii0612.mutsumi.osuImage.core;

import io.github.rikkakawaii0612.mutsumi.osuImage.util.Reference;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Canvas {
    private final int width;
    private final int height;
    private final List<Element> elements = new ArrayList<>();

    public Canvas(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Reference<Text> addText(int x, int y, String str) {
        Text text = new Text(str);
        text.setPosition(x, y);
        this.elements.add(text);
        return new Reference<>(text);
    }

    public Reference<Rectangle> addRectangle(int x, int y, int width, int height) {
        Rectangle rectangle = new Rectangle(width, height);
        rectangle.setPosition(x, y);
        this.elements.add(rectangle);
        return new Reference<>(rectangle);
    }

    public Reference<ImageView> addImageView(int x, int y, String imagePath) {
        ImageView imageView = new ImageView(imagePath);
        imageView.setPosition(x, y);
        this.elements.add(imageView);
        return new Reference<>(imageView);
    }

    public <E extends Element> Reference<E> addElement(int x, int y, E element) {
        element.setPosition(x, y);
        this.elements.add(element);
        return new Reference<>(element);
    }

    public byte[] render() {
        BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        this.elements.forEach(element -> {
            int x = element.getX(), y = element.getY();
            g.translate(x, y);

            double alpha = element.getAlpha();
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

        g.dispose();
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }
}
