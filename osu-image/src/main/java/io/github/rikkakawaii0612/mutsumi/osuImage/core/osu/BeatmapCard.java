package io.github.rikkakawaii0612.mutsumi.osuImage.core.osu;

import io.github.rikkakawaii0612.mutsumi.osuApi.data.Beatmap;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.Text;

public class BeatmapCard extends BeatmapCardBase {
    public BeatmapCard(Beatmap beatmap) {
        super(beatmap);

        Text title = new Text(beatmap.beatmapset.title);
        title.setPosition(195, 5);
        title.setMinFontSize(32);
        title.setMaxFontSize(48);
        title.setMaxWidth(690);
        title.setMaxHeight(80);

        Text artist = new Text(beatmap.beatmapset.artist);
        int y = (80 + title.getHeight()) / 2;
        artist.setPosition(198, y);
        artist.setMinFontSize(24);
        artist.setMaxFontSize(32);
        artist.setMaxWidth(688);
        artist.setMaxHeight(120 - y);

        this.addChildren(title, artist);
    }
}
