package io.github.rikkakawaii0612.mutsumi.osuImage.core;

import io.github.rikkakawaii0612.mutsumi.osuImage.util.ARGB;
import io.github.rikkakawaii0612.mutsumi.osuImage.util.Reference;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Canvas {
    private final int width;
    private final int height;
    private final List<Element> elements = new ArrayList<>();
    private final int[] chromaticAberration = {0, 0, 0, 0, 0, 0};
    private double glitch = 0;

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

    public void setGlitch(double glitch) {
        this.glitch = glitch;
    }

    public void setChromaticAberration(int offsetRX, int offsetRY,
                                       int offsetGX, int offsetGY,
                                       int offsetBX, int offsetBY) {
        this.chromaticAberration[0] = offsetRX;
        this.chromaticAberration[1] = offsetRY;
        this.chromaticAberration[2] = offsetGX;
        this.chromaticAberration[3] = offsetGY;
        this.chromaticAberration[4] = offsetBX;
        this.chromaticAberration[5] = offsetBY;
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

        BufferedImage appliedGlitch = applyGlitchEffect(image, this.glitch, new Random());
        BufferedImage appliedCA = applyChromaticAberration(appliedGlitch,
                this.chromaticAberration[0], this.chromaticAberration[1],
                this.chromaticAberration[2], this.chromaticAberration[3],
                this.chromaticAberration[4], this.chromaticAberration[5]);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(appliedCA, "png", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }

    /**
     * 应用色差效果（RGB通道独立偏移，Alpha保持不变）
     *
     * @param src      原始图像（支持 ARGB 或 RGB）
     * @param offsetRX 红色通道水平偏移（正：向右，负：向左）
     * @param offsetRY 红色通道垂直偏移（正：向下，负：向上）
     * @param offsetGX 绿色通道水平偏移
     * @param offsetGY 绿色通道垂直偏移
     * @param offsetBX 蓝色通道水平偏移
     * @param offsetBY 蓝色通道垂直偏移
     * @return 处理后的图像（类型与原图相同）
     */
    private static BufferedImage applyChromaticAberration(BufferedImage src,
                                                          int offsetRX, int offsetRY,
                                                          int offsetGX, int offsetGY,
                                                          int offsetBX, int offsetBY) {
        int width = src.getWidth();
        int height = src.getHeight();

        // 创建与原图相同类型的空图像（Alpha 暂时填充为0，RGB填充0）
        BufferedImage dst = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // 获取源图像像素数组
        int[] srcPixels = src.getRGB(0, 0, width, height, null, 0, width);
        // 目标像素数组，先初始化为全透明黑色（ARGB=0）
        int[] dstPixels = new int[srcPixels.length];

        // 步骤1：初始化目标像素的 Alpha 值为原图相同位置的 Alpha（RGB 先设为0）
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int idx = y * width + x;
                int alpha = (srcPixels[idx] >> 24) & 0xFF;
                dstPixels[idx] = (alpha << 24);   // RGB = 0
            }
        }

        // 步骤2：将源图像的 RGB 通道分别偏移并写入目标像素
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int srcIdx = y * width + x;
                int argb = srcPixels[srcIdx];
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;

                // 红色通道偏移
                int rx = x + offsetRX;
                int ry = y + offsetRY;
                if (rx >= 0 && rx < width && ry >= 0 && ry < height) {
                    int dstIdx = ry * width + rx;
                    int dstArgb = dstPixels[dstIdx];
                    int alpha = (dstArgb >> 24) & 0xFF;
                    // 只替换红色分量，保留绿、蓝及 Alpha
                    int newRgb = (alpha << 24) | (r << 16) | (dstArgb & 0x00FFFF);
                    dstPixels[dstIdx] = newRgb;
                }

                // 绿色通道偏移
                int gx = x + offsetGX;
                int gy = y + offsetGY;
                if (gx >= 0 && gx < width && gy >= 0 && gy < height) {
                    int dstIdx = gy * width + gx;
                    int dstArgb = dstPixels[dstIdx];
                    int alpha = (dstArgb >> 24) & 0xFF;
                    int newRgb = (alpha << 24) | ((dstArgb & 0x00FF0000) | (g << 8) | (dstArgb & 0x000000FF));
                    dstPixels[dstIdx] = newRgb;
                }

                // 蓝色通道偏移
                int bx = x + offsetBX;
                int by = y + offsetBY;
                if (bx >= 0 && bx < width && by >= 0 && by < height) {
                    int dstIdx = by * width + bx;
                    int dstArgb = dstPixels[dstIdx];
                    int alpha = (dstArgb >> 24) & 0xFF;
                    int newRgb = (alpha << 24) | ((dstArgb & 0x00FFFF00) | b);
                    dstPixels[dstIdx] = newRgb;
                }
            }
        }

        // 将像素数组写回目标图像
        dst.setRGB(0, 0, width, height, dstPixels, 0, width);
        return dst;
    }

    public static BufferedImage applyGlitchEffect(BufferedImage src, double glitch, Random random) {
        int width = src.getWidth();
        int height = src.getHeight();
        BufferedImage dst = new BufferedImage(width, height, src.getType());
        dst.getGraphics().drawImage(src, 0, 0, null);

        // 产生“碎片”块
        int w = Math.max(width, 300);
        for (int i = 0; i < glitch; i++) {
            int sliceHeight = (int) (0.0006666666666667D * height * (random.nextInt(40) + 5));
            int yStart = random.nextInt(height - sliceHeight);
            int xShift = (int) (0.02D * w * (random.nextDouble(1.0D) - 0.5D));

            // 将目标区域复制到新的位置，模拟“撕裂”和“错位”
            for (int y = 0; y < sliceHeight; y++) {
                for (int x = 0; x < width; x++) {
                    if (yStart + y < height && yStart + y >= 0) {
                        int newX = x + xShift;
                        if (newX >= 0 && newX < width) {
                            int rgb = src.getRGB(x, yStart + y);
                            dst.setRGB(newX, yStart + y, rgb);
                        }
                    }
                }
            }
        }

        // 添加“噪点”和“条纹”
        for (int i = 0; i < width * height * glitch / 600.0D; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            // 生成随机颜色，造成“像素损坏”的感觉
            int rgb = dst.getRGB(x, y);
            int r = 7 * ARGB.red(rgb), g = 7 * ARGB.green(rgb), b = 7 * ARGB.blue(rgb);
            int rx = random.nextInt(256), gx = random.nextInt(256), bx = random.nextInt(256);
            dst.setRGB(x, y, ARGB.toArgb((r + rx) / 8, (g + gx) / 8, (b + bx) / 8, ARGB.alpha(rgb)));
        }
        return dst;
    }
}
