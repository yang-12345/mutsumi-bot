package io.github.rikkakawaii0612.mutsumi.osuImage.core.osu;

import io.github.rikkakawaii0612.mutsumi.osuApi.data.Score;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.Mask;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.Rectangle;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.Text;
import io.github.rikkakawaii0612.mutsumi.osuImage.util.ARGB;

public class ScoreCard extends BeatmapCardBase {
    public ScoreCard(Score score) {
        super(score.beatmap);

        Text title = new Text(score.beatmap.beatmapset.title);
        title.setPosition(195, 5);
        title.setMinFontSize(32);
        title.setMaxFontSize(32);
        title.setMaxWidth(490);
        title.setMaxHeight(40);

        Text artist = new Text(score.beatmap.beatmapset.artist);
        artist.setPosition(198, 45);
        artist.setMinFontSize(24);
        artist.setMaxFontSize(24);
        artist.setMaxWidth(488);
        artist.setMaxHeight(30);

        Text version = new Text(score.beatmap.version);
        version.setPosition(198, 90);
        version.setColor(0xFF7F7F7F);
        version.setMinFontSize(20);
        version.setMaxFontSize(20);
        version.setMaxWidth(488);
        version.setMaxHeight(30);

        Rectangle ppBackground = new Rectangle(200, 120);
        ppBackground.setPosition(700, 0);
        ppBackground.setColor(ARGB.toArgb(96, 96, 216));
        ppBackground.setCorner(40);

        Mask mask = new Mask(ppBackground);
        mask.getMask().addChildren(new Rectangle(40, 120));

        Text pp = new Text(String.format("%.0fpp", score.pp));
        pp.setMaxFontSize(32);
        pp.setPosition(800 - pp.getWidth() / 2, 60 - pp.getHeight() / 2);

        this.addChildren(title, artist, version, mask, pp);
    }
}
