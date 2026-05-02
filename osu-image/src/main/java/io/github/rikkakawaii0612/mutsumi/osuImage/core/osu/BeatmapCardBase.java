package io.github.rikkakawaii0612.mutsumi.osuImage.core.osu;

import io.github.rikkakawaii0612.mutsumi.osuApi.data.Beatmap;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.Group;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.ImageView;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.Rectangle;
import io.github.rikkakawaii0612.mutsumi.osuImage.util.ARGB;

public abstract class BeatmapCardBase extends Group {
    private static final int BEATMAP_BACKGROUND = ARGB.toArgb(50, 48, 44);

    public BeatmapCardBase(Beatmap beatmap) {
        Rectangle background = new Rectangle(900, 250);
        background.setColor(BEATMAP_BACKGROUND);
        background.setCorner(40);

        ImageView bgCover = new ImageView(beatmap.beatmapset.covers.cover);
        bgCover.setCorner(40);
        bgCover.setAlpha(0.15D);

        ImageView icon = bgCover.copySource();
        icon.setPosition(10, 5);
        icon.setCorner(30);

        this.addChildren(background, bgCover, icon);
    }

    @Override
    public int getWidth() {
        return 900;
    }

    @Override
    public int getHeight() {
        return 120;
    }
}
