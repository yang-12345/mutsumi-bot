package io.github.rikkakawaii0612.mutsumi.guessService.image;

import io.github.rikkakawaii0612.mutsumi.guessService.GameInfo;
import io.github.rikkakawaii0612.mutsumi.osuApi.data.User;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.Canvas;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.osu.UserCard;
import io.github.rikkakawaii0612.mutsumi.osuImage.util.ARGB;

public class Images {
    private static final int BACKGROUND = ARGB.toArgb(68, 60, 54);

    public static byte[] renderGameInfo(GameInfo gameInfo) {
        int count = gameInfo.getSongCount();
        int height = 300 + (count + 1) / 2 * 150;
        Canvas canvas = new Canvas(1920, height);

        canvas.addImageView(0, 0, "assets/head.png");
        canvas.addRectangle(0, 220, 1920, height).let(rectangle -> {
            rectangle.setColor(BACKGROUND);
            rectangle.setCorner(40);
        });
        User user = gameInfo.getUser();
        canvas.addElement(40, 40, new UserCard(user, user.statistics.pp));

        for (int i = 0; i < count; i++) {
            canvas.addElement(32 + (i % 2) * 950, 281 + (i / 2) * 150,
                    new GameBeatmapCard(gameInfo, gameInfo.getBeatmap(i), i, !gameInfo.isDecrypted(i)));
        }

        canvas.addText(38, 235, "Mode: " + gameInfo.getMode().getName()
                + "  Opened Characters: " + gameInfo.getOpenedCharacters()).let(text -> {
            text.setMaxFontSize(28);
        });

        return canvas.render();
    }
}
