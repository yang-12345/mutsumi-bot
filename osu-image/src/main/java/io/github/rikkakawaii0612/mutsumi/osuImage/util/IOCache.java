package io.github.rikkakawaii0612.mutsumi.osuImage.util;

import io.github.rikkakawaii0612.mutsumi.osuApi.data.Beatmapset;
import io.github.rikkakawaii0612.mutsumi.osuApi.data.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class IOCache {
    public static final Logger LOGGER = LoggerFactory.getLogger("OsuImageService");
    private static final LimitedMap<Long, BufferedImage> AVATARS = new LimitedMap<>(100);
    private static final LimitedMap<Long, BufferedImage> BEATMAP_BACKGROUNDS = new LimitedMap<>(100);

    public static BufferedImage getAvatar(User user) {
        Optional<BufferedImage> optional = AVATARS.cache(user.id, () -> {
            String str = String.valueOf(user.id);
            BufferedImage bufferedImage = readFromDisk("users", str);
            if (bufferedImage != null) {
                return Optional.of(bufferedImage);
            }

            try {
                String url = user.avatarUrl;
                try (InputStream is = URI.create(url).toURL().openStream()) {
                    BufferedImage image = ImageUtil.readImage(is);
                    if (image == null) {
                        throw new IOException("image is null");
                    }
                    LOGGER.info("Downloaded osu!user {}'s avatar (url={})", user.id, url);
                    writeToDisk("users", str, image);
                    return Optional.of(image);
                }

            } catch (IOException e) {
                LOGGER.error("Cannot get avatar of osu! user (id: {}): ", user.id, e);
                return Optional.empty();
            }
        });

        return optional.orElseGet(() -> createDefaultImage(256, 256));
    }

    public static BufferedImage getBeatmapsetCover(Beatmapset beatmapset) {
        Optional<BufferedImage> optional =  BEATMAP_BACKGROUNDS.cache(beatmapset.id, () -> {
            String str = String.valueOf(beatmapset.id);
            BufferedImage bufferedImage = readFromDisk("beatmaps", str);
            if (bufferedImage != null) {
                return Optional.of(bufferedImage);
            }

            try {
                String url = beatmapset.covers.cover;
                try (InputStream is = URI.create(url).toURL().openStream()) {
                    BufferedImage image = ImageUtil.readImage(is);
                    if (image == null) {
                        throw new IOException("image is null");
                    }
                    LOGGER.info("Downloaded cover of beatmapset (id={}) (url={})", beatmapset.id, url);
                    writeToDisk("beatmaps", str, image);
                    return Optional.of(image);
                }

            } catch (IOException e) {
                LOGGER.error("Cannot get cover of beatmapset (id={}): ", beatmapset.id, e);
                return Optional.empty();
            }
        });

        return optional.orElseGet(() -> createDefaultImage(900, 250));
    }

    private static BufferedImage createDefaultImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        return image;
    }

    private static BufferedImage readFromDisk(String path, String fileName) {
        String root = getRootDirectory();

        Path path1 = Paths.get(root, "caches", path);
        if (!Files.exists(path1)) {
            return null;
        }

        File file = Paths.get(root, "caches", path, fileName + ".png").toFile();
        if (!file.exists()) {
            return null;
        }

        try (InputStream is = new FileInputStream(file)) {
            return ImageUtil.readImage(is);
        } catch (IOException e) {
            return null;
        }
    }

    private static void writeToDisk(String path, String fileName, BufferedImage image) {
        String root = getRootDirectory();

        Path path1 = Paths.get(root, "caches", path);
        if (!Files.exists(path1)) {
            try {
                Files.createDirectories(path1);
            } catch (IOException e) {
                LOGGER.error("Failed to write image to disk: ", e);
                return;
            }
        }

        try {
            ImageIO.write(image, "png", Paths.get(root, "caches", path, fileName + ".png").toFile());
        } catch (IOException e) {
            LOGGER.error("Failed to write image to disk: ", e);
        }
    }

    public static String getRootDirectory() {
        try {
            URL url = IOCache.class.getProtectionDomain().getCodeSource().getLocation();
            Path path = Paths.get(url.toURI());
            File file = path.toFile();
            if (file.isFile()) {
                return file.getParent();
            } else {
                return file.getAbsolutePath();
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException("Cannot resolve JAR path", e);
        }
    }
}
