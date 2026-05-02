package io.github.rikkakawaii0612.mutsumi.scoreService;

import io.github.rikkakawaii0612.mutsumi.osuApi.data.Score;
import io.github.rikkakawaii0612.mutsumi.osuApi.data.User;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.Canvas;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.osu.ScoreCard;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.osu.UserCard;
import io.github.rikkakawaii0612.mutsumi.osuImage.util.ARGB;

import java.util.List;

public class Images {
    private static final int BACKGROUND = ARGB.toArgb(68, 60, 54);

    public static byte[] renderScores(User user, double pp, List<Score> scores) {
        int count = scores.size();
        int height = 300 + (count + 1) / 2 * 150;

        Canvas canvas = new Canvas(1920, height);
        canvas.addImageView(0, 0, "assets/head.png");
        canvas.addRectangle(0, 220, 1920, height).let(rectangle -> {
            rectangle.setColor(BACKGROUND);
            rectangle.setCorner(40);
        });
        canvas.addElement(40, 40, new UserCard(user, pp));

        for (int i = 0; i < count; i++) {
            canvas.addElement(32 + (i % 2) * 950, 281 + (i / 2) * 150, new ScoreCard(scores.get(i)));
        }

        return canvas.render();
    }
}
