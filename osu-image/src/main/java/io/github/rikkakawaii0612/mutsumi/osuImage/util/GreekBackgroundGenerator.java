package io.github.rikkakawaii0612.mutsumi.osuImage.util;

import io.github.rikkakawaii0612.mutsumi.osuImage.core.Canvas;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.ImageView;

import java.awt.*;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GreekBackgroundGenerator {
    private static final double PHI = 0.6180339887498948D; // 黄金分割率
    private static final Map<String, byte[]> CACHE = new ConcurrentHashMap<>();

    /**
     * 给背景图添加神秘希腊字母.
     *
     * @param bg 背景图, 以字节数组形式存储
     * @param greek 希腊字母透明图, 以字节数组形式存储
     * @return 结果, 以字节数组形式存储
     */
    public static byte[] addGreek(byte[] bg, byte[] greek) {
        ImageView bgImage = new ImageView(bg), greekImage = new ImageView(greek);
        double bw = bgImage.getWidth(), bh = bgImage.getHeight(),
                gw = greekImage.getWidth(), gh = greekImage.getHeight();
        double scale = PHI / (1.0D - Math.min(gw / bw, gh / bh));
        double w = scale * gw, h = scale * gh;
        greekImage.resize((int) w, (int) h);
        Canvas canvas = new Canvas((int) bw, (int) bh);
        canvas.addElement(0, 0, bgImage);
        canvas.addElement((int) ((bw - w) / 2.0D), (int) ((bh - h) / 2.0D), greekImage);
        return canvas.render();
    }

    public static byte[] getGreek(String type) {
        return CACHE.computeIfAbsent(type, _ -> {
            String path;
            switch (type.toLowerCase(Locale.ROOT)) {
                case "10", "10th" -> path = "assets/10th.png";
                case "a", "alpha" -> path = "assets/alpha.png";
                case "b", "beta" -> path = "assets/beta.png";
                case "g", "gamma" -> path = "assets/gamma.png";
                case "d", "delta" -> path = "assets/delta.png";
                case "e", "ep", "epsilon" -> path = "assets/epsilon.png";
                case "z", "zeta" -> path = "assets/zeta.png";
                case "n", "eta" -> path = "assets/eta.png";
                default -> {
                    return null;
                }
            }
            return new ImageView(path).toByteArray();
        });
    }
}
