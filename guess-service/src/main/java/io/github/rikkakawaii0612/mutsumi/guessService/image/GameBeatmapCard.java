package io.github.rikkakawaii0612.mutsumi.guessService.image;

import io.github.rikkakawaii0612.mutsumi.guessService.GameInfo;
import io.github.rikkakawaii0612.mutsumi.osuApi.data.Beatmap;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.Rectangle;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.Text;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.osu.BeatmapCardBase;
import io.github.rikkakawaii0612.mutsumi.osuImage.util.ARGB;
import io.github.rikkakawaii0612.mutsumi.osuImage.util.IOCache;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GameBeatmapCard extends BeatmapCardBase {
    private static final BufferedImage ENCRYPTED = new BufferedImage(900, 250, BufferedImage.TYPE_INT_ARGB);
    private static final int INDEX_TAG = ARGB.toArgb(96, 96, 216);

    static {
        Graphics2D g = ENCRYPTED.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 900, 250);
        g.dispose();
    }

    public GameBeatmapCard(GameInfo gameInfo, Beatmap beatmap, int index, boolean encrypted) {
        super(encrypted ? ENCRYPTED : IOCache.getBeatmapsetCover(beatmap.beatmapset));
        Text titleText = new Text(encrypted ?
                gameInfo.encrypt(beatmap.beatmapset.titleUnicode) : beatmap.beatmapset.titleUnicode);
        titleText.setMinFontSize(32);
        titleText.setMaxFontSize(44);
        titleText.setMaxWidth(690);
        titleText.setMaxHeight(85);
        titleText.setPosition(195, 43 - titleText.getHeight() / 2);

        Text artistText = new Text(encrypted ?
                gameInfo.encrypt(beatmap.beatmapset.artistUnicode) : beatmap.beatmapset.artistUnicode);
        int i = 48 + titleText.getHeight() / 2;
        artistText.setPosition(198, i);
        artistText.setColor(0xFFBFBFBF);
        artistText.setMinFontSize(24);
        artistText.setMaxFontSize(32);
        artistText.setMaxWidth(687);
        artistText.setMaxHeight(117 - i);

        Rectangle indexTag = new Rectangle(index < 9 ? 43 : 53, 20);
        indexTag.setColor(INDEX_TAG);
        indexTag.setPosition(17, 11);
        indexTag.setCorner(20);

        Text indexText = new Text("#" + (1 + index));
        indexText.setPosition(27, 12);
        indexText.setMaxFontSize(18);

        this.addChildren(titleText, artistText, indexTag, indexText);
    }
}
