package io.github.rikkakawaii0612.mutsumi.osuImage.core;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class ImageView extends AbstractElement {
    private BufferedImage image;
    private Rectangle2D cut;
    private int corner;

    public ImageView(String imagePath) {
        this(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource(imagePath)));
    }

    public ImageView(URL url) {
        try {
            this.image = ImageIO.read(url);
        } catch (IOException _) {
        }
    }

    public ImageView(BufferedImage image) {
        this.image = image;
    }

    @Override
    public int getWidth() {
        return this.cut == null ? this.image.getWidth() : (int) this.cut.getWidth();
    }

    @Override
    public int getHeight() {
        return this.cut == null ? this.image.getHeight() : (int) this.cut.getHeight();
    }

    @Override
    public void render(Graphics2D g) {
        if (this.image == null) {
            return;
        }

        int w = this.getWidth();
        int h = this.getHeight();
        if (this.cut == null && this.corner <= 0) {
            g.drawImage(this.image, 0, 0, null);
            return;
        }

        BufferedImage bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        if (this.cut != null) {
            g2d.fill(new RoundRectangle2D.Double(this.cut.getX(), this.cut.getY(),
                    this.cut.getWidth(), this.cut.getHeight(), this.corner, this.corner));
        } else {
            g2d.fill(new RoundRectangle2D.Double(0.0D, 0.0D, w, h, this.corner, this.corner));
        }

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0F));
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

    public void setCut(int x, int y, int width, int height) {
        this.cut = new Rectangle2D.Double(x, y, width, height);
    }

    public void resize(int width, int height) {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufferedImage.createGraphics();
        Image image = this.image.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        this.image = bufferedImage;
    }

    public ImageView copySource() {
        return new ImageView(this.image);
    }
}
