package io.github.rikkakawaii0612.mutsumi.osuImage.util;

import io.github.rikkakawaii0612.mutsumi.osuImage.core.Canvas;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.ImageView;

import java.awt.*;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GreekBackgroundGenerator {
    private static final Map<String, String> TYPES_TO_IMAGES = new ConcurrentHashMap<>();
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
        double scale = Math.min(1.6D * bw / gw, bh / gh);
        double w = scale * gw, h = scale * gh;
        greekImage.resize((int) w, (int) h);
        Canvas canvas = new Canvas((int) bw, (int) bh);
        canvas.addElement(0, 0, bgImage);
        canvas.addElement((int) ((bw - w) / 2.0D), (int) ((bh - h) / 2.0D), greekImage);
        canvas.setGlitch(20.0D);
        int i = (int) Math.min(bw / 100.0D, bh / 100.0D);
        canvas.setChromaticAberration(i, i, 0, 0, -i, -i);
        return canvas.render();
    }

    public static byte[] getGreek(String type) {
        return CACHE.computeIfAbsent(type, s -> {
            String str = s.toLowerCase(Locale.ROOT);
            if (!TYPES_TO_IMAGES.containsKey(str)) {
                return null;
            }
            return new ImageView(TYPES_TO_IMAGES.get(str)).toByteArray();
        });
    }

    public static void registerGreekType(String imagePath, String... types) {
        for (String str : types) {
            TYPES_TO_IMAGES.put(str.toLowerCase(Locale.ROOT), "assets/greeks/" + imagePath);
        }
    }

    static {
        // 素材来源: https://github.com/YakumoZn/nonebot-plugin-osugreek
        registerGreekType("5.png", "5", "5th", "5dan", "大天空", "far in the blue sky", "far in the blue sky...");
        registerGreekType("10.png", "10", "10th", "10dan");
        registerGreekType("alpha.png", "a", "alpha");
        registerGreekType("beta.png", "b", "beta");
        registerGreekType("gamma.png", "g", "gamma");
        registerGreekType("delta.png", "d", "delta");
        registerGreekType("epsilon.png", "e", "ep", "epsilon");
        registerGreekType("zeta.png", "z", "zeta");
        registerGreekType("eta.png", "n", "eta");
        registerGreekType("theta.png", "t", "theta");
        registerGreekType("iota.png", "i", "iota");
        registerGreekType("kappa.png", "k", "kappa");
        registerGreekType("ln1.png", "ln1");
        registerGreekType("ln2.png", "ln2");
        registerGreekType("ln3.png", "ln3");
        registerGreekType("ln4.png", "ln4");
        registerGreekType("ln5.png", "ln5");
        registerGreekType("ln6.png", "ln6");
        registerGreekType("ln7.png", "ln7");
        registerGreekType("ln8.png", "ln8");
        registerGreekType("ln9.png", "ln9");
        registerGreekType("ln10.png", "ln10");
        registerGreekType("ln11.png", "ln11");
        registerGreekType("ln12.png", "ln12");
        registerGreekType("ln13.png", "ln13");
        registerGreekType("ln14.png", "ln14");
        registerGreekType("ln15.png", "ln15");
        registerGreekType("ln16.png", "ln16");
        registerGreekType("ex1v2.png", "ex1v2", "extra1v2", "extra-1v2");
        registerGreekType("ex2v2.png", "ex2v2", "extra2v2", "extra-2v2");
        registerGreekType("ex3v2.png", "ex3v2", "extra3v2", "extra-3v2");
        registerGreekType("ex4v2.png", "ex4v2", "extra4v2", "extra-4v2");
        registerGreekType("ex5v2.png", "ex5v2", "extra5v2", "extra-5v2");
        registerGreekType("ex6v2.png", "ex6v2", "extra6v2", "extra-6v2");
        registerGreekType("ex7v2.png", "ex7v2", "extra7v2", "extra-7v2");
        registerGreekType("ex8v2.png", "ex8v2", "extra8v2", "extra-8v2");
        registerGreekType("ex9v2.png", "ex9v2", "extra9v2", "extra-9v2");
        registerGreekType("exfv2.png", "exfv2", "extra-finalv2");
        registerGreekType("ex1v3.png", "ex1", "extra1", "extra-1", "ex1v3", "extra1v3", "extra-1v3");
        registerGreekType("ex2v3.png", "ex2", "extra2", "extra-2", "ex2v3", "extra2v3", "extra-2v3");
        registerGreekType("ex3v3.png", "ex3", "extra3", "extra-3", "ex3v3", "extra3v3", "extra-3v3");
        registerGreekType("ex4v3.png", "ex4", "extra4", "extra-4", "ex4v3", "extra4v3", "extra-4v3");
        registerGreekType("ex5v3.png", "ex5", "extra5", "extra-5", "ex5v3", "extra5v3", "extra-5v3");
        registerGreekType("ex6v3.png", "ex6", "extra6", "extra-6", "ex6v3", "extra6v3", "extra-6v3");
        registerGreekType("ex7v3.png", "ex7", "extra7", "extra-7", "ex7v3", "extra7v3", "extra-7v3");
        registerGreekType("ex8v3.png", "ex8", "extra8", "extra-8", "ex8v3", "extra8v3", "extra-8v3");
        registerGreekType("ex9v3.png", "ex9", "extra9", "extra-9", "ex9v3", "extra9v3", "extra-9v3");
        registerGreekType("exfv3.png", "exf", "exfinal", "extra-final", "exfv3", "exfinalv3", "extra-finalv3");
        registerGreekType("7kG.png", "7kg", "7kgamma", "7k gamma");
        registerGreekType("7kA.png", "7ka", "7kazimuth", "7k azimuth", "azimuth");
        registerGreekType("7kZ.png", "7kz", "7kzenith", "7k zenith", "zenith");
        registerGreekType("7kS.png", "s", "7ks", "7kstellium", "7k stellium", "stellium");
        registerGreekType("chi.png", "chi", "x");
        registerGreekType("gemini.png", "gemini");
        registerGreekType("omega.png", "o", "omega");
        registerGreekType("phi.png", "phi");
        registerGreekType("psi.png", "psi");
        registerGreekType("kether.png", "kether");
        registerGreekType("haku.png", "h", "haku", "白", "白段");
        registerGreekType("v1.png", "v1", "vibro1", "vibro-1");
        registerGreekType("v2.png", "v2", "vibro2", "vibro-2");
        registerGreekType("v3.png", "v3", "vibro3", "vibro-3");
        registerGreekType("v4.png", "v4", "vibro4", "vibro-4");
        registerGreekType("v5.png", "v5", "vibro5", "vibro-5");
        registerGreekType("v6.png", "v6", "vibro6", "vibro-6");
        registerGreekType("v7.png", "v7", "vibro7", "vibro-7");
        registerGreekType("v8.png", "v8", "vibro8", "vibro-8");
        registerGreekType("v9.png", "v9", "vibro9", "vibro-9");
        registerGreekType("v10.png", "v10", "vibro10", "vibro-10");
        registerGreekType("v11.png", "v11", "vibro11", "vibro-11");
        registerGreekType("v12.png", "v12", "vibro12", "vibro-12");
        registerGreekType("v13.png", "v13", "vibro13", "vibro-13");
        registerGreekType("v14.png", "v14", "vibro14", "vibro-14");
        registerGreekType("aywdm.png", "aywdm", "哎呀我的妈", "哎呦我的妈");
        registerGreekType("hyw.png", "何意味", "还以为", "何以为", "");
        registerGreekType("question.png", "?", "？", "question", "问号");
    }
}
