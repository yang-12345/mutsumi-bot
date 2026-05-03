package io.github.rikkakawaii0612.mutsumi.osuImage.util;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class ImageUtil {
    public static BufferedImage readImage(InputStream is) {
        try (ImageInputStream iis = ImageIO.createImageInputStream(is)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                try {
                    reader.setInput(iis);
                    return reader.read(0);
                } catch (IOException _) {
                } finally {
                    reader.dispose();
                }
            }
        } catch (IOException _) {
        }

        return null;
    }
}
