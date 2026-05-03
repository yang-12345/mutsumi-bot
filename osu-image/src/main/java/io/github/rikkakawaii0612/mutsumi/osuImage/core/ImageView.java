package io.github.rikkakawaii0612.mutsumi.osuImage.core;

import io.github.rikkakawaii0612.mutsumi.osuImage.util.ImageUtil;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ImageView extends AbstractElement {
    private static final Logger LOGGER = LoggerFactory.getLogger("OsuImageService");

    private BufferedImage image;
    private int corner;

    public ImageView(String imagePath) {
        this(ImageView.class.getClassLoader().getResourceAsStream(imagePath));
    }

    public ImageView(URL url) {
        try (InputStream is = url.openStream()) {
            BufferedImage image = ImageUtil.readImage(is);
            if (image != null) {
                this.image = image;
            } else {
                throw new IOException("image is null");
            }
        } catch (IOException e) {
            LOGGER.warn("Failed to read image from url '{}': ", url, e);
            this.image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }
    }

    public ImageView(InputStream inputStream) {
        if (inputStream == null) {
            LOGGER.warn("Trying to create image from null input stream");
            this.image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            return;
        }

        // 不要使用 ImageIO.read(), 这会在 JVM 线程上占用图片资源, 导致无法卸载服务
        BufferedImage image = ImageUtil.readImage(inputStream);
        if (image != null) {
            this.image = image;
        } else {
            LOGGER.warn("Failed to read image from input stream {}", inputStream.getClass().getName());
            this.image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }
    }

    public ImageView(BufferedImage image) {
        this.image = image;
    }

    public ImageView() {
        this.image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public int getWidth() {
        return this.image.getWidth();
    }

    @Override
    public int getHeight() {
        return this.image.getHeight();
    }

    @Override
    public void render(Graphics2D g) {
        if (this.image == null) {
            return;
        }

        if (this.corner <= 0) {
            g.drawImage(this.image, 0, 0, null);
            return;
        }

        int w = this.getWidth();
        int h = this.getHeight();

        BufferedImage bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);


        g2d.fill(new RoundRectangle2D.Double(0.0D, 0.0D, w, h, this.corner, this.corner));

        g2d.setComposite(AlphaComposite.SrcIn);
        g2d.drawImage(this.image, 0, 0, null);
        g2d.dispose();

        g.drawImage(bufferedImage, 0, 0, null);
    }

    public int getCorner() {
        return this.corner;
    }

    public void setCorner(int corner) {
        this.corner = corner;
    }

    public void cut(int x, int y, int width, int height) {
        this.image = this.image.getSubimage(
                Math.max(0, x),
                Math.max(0, y),
                Math.min(this.image.getWidth(), width),
                Math.min(this.image.getHeight(), height));
    }

    public void resize(int width, int height) {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufferedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        Image image = this.image.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        this.image = bufferedImage;
    }

    public ImageView copySource() {
        return new ImageView(this.image);
    }
}
