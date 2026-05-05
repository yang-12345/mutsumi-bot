package io.github.rikkakawaii0612.mutsumi.osuImage.util;

import io.github.rikkakawaii0612.mutsumi.api.util.Pair;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.Canvas;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.ImageView;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GreekBackgroundGenerator {
    private static final Map<String, Pair<String, Param>> TYPES_TO_IMAGES = new ConcurrentHashMap<>();
    private static final Map<String, byte[]> CACHE = new ConcurrentHashMap<>();

    /**
     * 给背景图添加神秘希腊字母.
     *
     * @param bg 背景图, 以字节数组形式存储
     * @param greek 希腊字母透明图, 以字节数组形式存储
     * @return 结果, 以字节数组形式存储
     */
    public static byte[] addGreek(byte[] bg, byte[] greek, double chromaticAberration, double glitch) {
        ImageView bgImage = new ImageView(bg), greekImage = new ImageView(greek);
        double bw = bgImage.getWidth(), bh = bgImage.getHeight(),
                gw = greekImage.getWidth(), gh = greekImage.getHeight();
        double scale = Math.min(1.6D * bw / gw, bh / gh);
        double w = scale * gw, h = scale * gh;
        greekImage.resize((int) w, (int) h);
        Canvas canvas = new Canvas((int) bw, (int) bh);
        canvas.addElement(0, 0, bgImage);
        canvas.addElement((int) ((bw - w) / 2.0D), (int) ((bh - h) / 2.0D), greekImage);
        canvas.setGlitch(glitch);
        int i = (int) Math.min(chromaticAberration * bw / 100.0D, chromaticAberration * bh / 100.0D);
        canvas.setChromaticAberration(i, i, 0, 0, -i, -i);
        return canvas.render();
    }

    public static Pair<byte[], Param> getGreekInfo(String type) {
        byte[] arr = CACHE.computeIfAbsent(type, s -> {
            String str = s.toLowerCase(Locale.ROOT);
            if (!TYPES_TO_IMAGES.containsKey(str)) {
                return null;
            }
            return new ImageView(TYPES_TO_IMAGES.get(str).left()).toByteArray();
        });
        if (arr == null) {
            return null;
        }
        Pair<String, Param> pair = TYPES_TO_IMAGES.get(type);
        return new Pair<>(arr, pair.right());
    }

    public static void registerGreekType(String imagePath,
                                         double glitch,
                                         double chromaticAberration,
                                         String... types) {
        for (String str : types) {
            TYPES_TO_IMAGES.put(str.toLowerCase(Locale.ROOT),
                    new Pair<>("assets/greeks/" + imagePath, new Param(glitch, chromaticAberration)));
        }
    }

    static {
        // 素材来源: https://github.com/YakumoZn/nonebot-plugin-osugreek
        registerGreekType("5.png", 0.0D, 0.0D, "5", "5th", "5dan", "大天空");
        registerGreekType("10.png", 0.0D, 0.0D, "10", "10th", "10dan");
        registerGreekType("alpha.png", 0.0D, 0.0D, "a", "alpha");
        registerGreekType("beta.png", 0.0D, 0.0D, "b", "beta");
        registerGreekType("gamma.png", 5.0D, 0.5D, "g", "gamma");
        registerGreekType("delta.png", 10.0D, 0.8D, "d", "delta");
        registerGreekType("epsilon.png", 20.0D, 1.0D, "e", "ep", "epsilon");
        registerGreekType("zeta.png", 20.0D, 1.0D, "z", "zeta");
        registerGreekType("eta.png", 20.0D, 1.0D, "n", "eta");
        registerGreekType("theta.png", 25.0D, 1.1D, "t", "theta");
        registerGreekType("iota.png", 25.0D, 1.1D, "i", "iota");
        registerGreekType("kappa.png", 25.0D, 1.1D, "k", "kappa");
        registerGreekType("ln1.png", 0.0D, 0.0D, "ln1");
        registerGreekType("ln2.png", 0.0D, 0.0D, "ln2");
        registerGreekType("ln3.png", 0.0D, 0.0D, "ln3");
        registerGreekType("ln4.png", 0.0D, 0.0D, "ln4");
        registerGreekType("ln5.png", 0.0D, 0.0D, "ln5");
        registerGreekType("ln6.png", 0.0D, 0.0D, "ln6");
        registerGreekType("ln7.png", 0.0D, 0.0D, "ln7");
        registerGreekType("ln8.png", 0.0D, 0.0D, "ln8");
        registerGreekType("ln9.png", 0.0D, 0.0D, "ln9");
        registerGreekType("ln10.png", 0.0D, 0.0D, "ln10");
        registerGreekType("ln11.png", 0.0D, 0.0D, "ln11");
        registerGreekType("ln12.png", 0.0D, 0.0D, "ln12");
        registerGreekType("ln13.png", 0.0D, 0.0D, "ln13");
        registerGreekType("ln14.png", 10.0D, 0.8D, "ln14");
        registerGreekType("ln15.png", 20.0D, 1.0D, "ln15");
        registerGreekType("ln16.png", 20.0D, 1.0D, "ln16");
        registerGreekType("ex1v2.png", 5.0D, 0.3D, "ex1v2", "extra1v2", "extra-1v2");
        registerGreekType("ex2v2.png", 6.0D, 0.3D, "ex2v2", "extra2v2", "extra-2v2");
        registerGreekType("ex3v2.png", 7.0D, 0.5D, "ex3v2", "extra3v2", "extra-3v2");
        registerGreekType("ex4v2.png", 8.0D, 0.5D, "ex4v2", "extra4v2", "extra-4v2");
        registerGreekType("ex5v2.png", 10.0D, 0.7D, "ex5v2", "extra5v2", "extra-5v2");
        registerGreekType("ex6v2.png", 12.0D, 0.7D, "ex6v2", "extra6v2", "extra-6v2");
        registerGreekType("ex7v2.png", 14.0D, 0.8D, "ex7v2", "extra7v2", "extra-7v2");
        registerGreekType("ex8v2.png", 16.0D, 0.8D, "ex8v2", "extra8v2", "extra-8v2");
        registerGreekType("ex9v2.png", 25.0D, 1.1D, "ex9v2", "extra9v2", "extra-9v2");
        registerGreekType("exfv2.png", 20.0D, 1.0D, "exfv2", "extra-finalv2");
        registerGreekType("ex1v3.png", 5.0D, 0.3D, "ex1", "extra1", "extra-1", "ex1v3", "extra1v3", "extra-1v3");
        registerGreekType("ex2v3.png", 6.0D, 0.3D, "ex2", "extra2", "extra-2", "ex2v3", "extra2v3", "extra-2v3");
        registerGreekType("ex3v3.png", 7.0D, 0.5D, "ex3", "extra3", "extra-3", "ex3v3", "extra3v3", "extra-3v3");
        registerGreekType("ex4v3.png", 8.0D, 0.5D, "ex4", "extra4", "extra-4", "ex4v3", "extra4v3", "extra-4v3");
        registerGreekType("ex5v3.png", 10.0D, 0.7D, "ex5", "extra5", "extra-5", "ex5v3", "extra5v3", "extra-5v3");
        registerGreekType("ex6v3.png", 12.0D, 0.7D, "ex6", "extra6", "extra-6", "ex6v3", "extra6v3", "extra-6v3");
        registerGreekType("ex7v3.png", 14.0D, 0.8D, "ex7", "extra7", "extra-7", "ex7v3", "extra7v3", "extra-7v3");
        registerGreekType("ex8v3.png", 16.0D, 0.8D, "ex8", "extra8", "extra-8", "ex8v3", "extra8v3", "extra-8v3");
        registerGreekType("ex9v3.png", 20.0D, 1.0D, "ex9", "extra9", "extra-9", "ex9v3", "extra9v3", "extra-9v3");
        registerGreekType("exfv3.png", 20.0D, 1.0D, "exf", "exfinal", "extra-final", "exfv3", "exfinalv3", "extra-finalv3");
        registerGreekType("7kG.png", 0.0D, 0.0D, "7kg", "7kgamma", "7k gamma");
        registerGreekType("7kA.png", 5.0D, 0.3D, "7ka", "7kazimuth", "7k azimuth", "azimuth");
        registerGreekType("7kZ.png", 10.0D, 0.5D, "7kz", "7kzenith", "7k zenith", "zenith");
        registerGreekType("7kS.png", 20.0D, 1.0D, "s", "7ks", "7kstellium", "7k stellium", "stellium");
        registerGreekType("chi.png", 0.0D, 0.0D, "chi", "x");
        registerGreekType("gemini.png", 0.0D, 0.0D, "gemini");
        registerGreekType("omega.png", 0.0D, 0.0D, "o", "omega");
        registerGreekType("phi.png", 0.0D, 0.0D, "phi");
        registerGreekType("psi.png", 0.0D, 0.0D, "psi");
        registerGreekType("kether.png", 0.0D, 0.0D, "kether");
        registerGreekType("haku.png", 0.0D, 0.0D, "h", "haku", "白", "白段");
        registerGreekType("v1.png", 0.0D, 0.0D, "v1", "vibro1", "vibro-1");
        registerGreekType("v2.png", 0.0D, 0.0D, "v2", "vibro2", "vibro-2");
        registerGreekType("v3.png", 0.0D, 0.0D, "v3", "vibro3", "vibro-3");
        registerGreekType("v4.png", 0.0D, 0.0D, "v4", "vibro4", "vibro-4");
        registerGreekType("v5.png", 0.0D, 0.0D, "v5", "vibro5", "vibro-5");
        registerGreekType("v6.png", 0.0D, 0.0D, "v6", "vibro6", "vibro-6");
        registerGreekType("v7.png", 0.0D, 0.0D, "v7", "vibro7", "vibro-7");
        registerGreekType("v8.png", 5.0D, 0.3D, "v8", "vibro8", "vibro-8");
        registerGreekType("v9.png", 5.0D, 0.3D, "v9", "vibro9", "vibro-9");
        registerGreekType("v10.png", 10.0D, 0.5D, "v10", "vibro10", "vibro-10");
        registerGreekType("v11.png", 10.0D, 0.5D, "v11", "vibro11", "vibro-11");
        registerGreekType("v12.png", 15.0D, 0.7D, "v12", "vibro12", "vibro-12");
        registerGreekType("v13.png", 20.0D, 1.0D, "v13", "vibro13", "vibro-13");
        registerGreekType("v14.png", 20.0D, 1.0D, "v14", "vibro14", "vibro-14");
        registerGreekType("aywdm.png", 20.0D, 1.0D, "aywdm", "哎呀我的妈", "哎呦我的妈");
        registerGreekType("hyw.png", 20.0D, 1.0D, "何意味", "还以为", "何以为", "");
        registerGreekType("question.png", 20.0D, 1.0D, "?", "？", "question", "问号");
    }

    public record Param(double glitch, double chromaticAberration) {
    }
}
