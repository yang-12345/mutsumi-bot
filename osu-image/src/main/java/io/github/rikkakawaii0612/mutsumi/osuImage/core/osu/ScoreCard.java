package io.github.rikkakawaii0612.mutsumi.osuImage.core.osu;

import io.github.rikkakawaii0612.mutsumi.osuApi.data.Score;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.Mask;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.Rectangle;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.Text;
import io.github.rikkakawaii0612.mutsumi.osuImage.util.ARGB;

public class ScoreCard extends BeatmapCardBase {
    private static final int BORDER = ARGB.toArgb(96, 96, 216);

    public ScoreCard(Score score) {
        super(score.beatmapset.covers.cover);

        Text title = new Text(score.beatmapset.titleUnicode);
        title.setPosition(195, 8);
        title.setMinFontSize(36);
        title.setMaxFontSize(36);
        title.setMaxWidth(550);
        title.setMaxHeight(43);

        Text artist = new Text(score.beatmapset.artistUnicode);
        artist.setPosition(195, 53);
        artist.setMinFontSize(25);
        artist.setMaxFontSize(25);
        artist.setMaxWidth(550);
        artist.setMaxHeight(30);

        Text accuracy = new Text(String.format("  | %.2f%%", 100.0D * score.accuracy));
        accuracy.setColor(0xFF7F7F7F);
        accuracy.setMinFontSize(22);
        accuracy.setMaxFontSize(22);
        accuracy.setMaxWidth(550);
        accuracy.setMaxHeight(30);

        Text version = new Text(score.beatmap.version);
        version.setPosition(195, 85);
        version.setColor(0xFF7F7F7F);
        version.setMinFontSize(22);
        version.setMaxFontSize(22);
        version.setMaxWidth(550 - accuracy.getWidth());
        version.setMaxHeight(30);

        accuracy.setPosition(195 + version.getWidth(), 85);

        Rectangle border = new Rectangle(900, 120);
        border.setColor(BORDER);
        border.setCorner(40);

        Mask borderMask = new Mask(border);

        Rectangle borderMaskRec = new Rectangle(760, 120);
        borderMaskRec.setCorner(30);
        borderMask.getMask().addChildren(borderMaskRec);

        Text pp = new Text(String.format("%.0fpp", score.pp));
        pp.setMaxFontSize(38);
        pp.setPosition(830 - pp.getWidth() / 2, 60 - pp.getHeight() / 2);


        this.addChildren(title, artist, accuracy, version, borderMask, pp);
    }
}
