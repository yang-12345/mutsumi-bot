package io.github.rikkakawaii0612.mutsumi.osuImage.core.osu;

import io.github.rikkakawaii0612.mutsumi.osuApi.data.Beatmap;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.Group;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.ImageView;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.Rectangle;
import io.github.rikkakawaii0612.mutsumi.osuImage.util.ARGB;

import java.net.MalformedURLException;
import java.net.URI;

public abstract class BeatmapCardBase extends Group {
    private static final int BEATMAP_BACKGROUND = ARGB.toArgb(50, 48, 44);

    public BeatmapCardBase(String coverUrl) {
        Rectangle background = new Rectangle(900, 120);
        background.setColor(BEATMAP_BACKGROUND);
        background.setCorner(40);

        ImageView bgCover;
        try {
            bgCover = new ImageView(URI.create(coverUrl).toURL());
        } catch (MalformedURLException e) {
            bgCover = new ImageView();
        }

        ImageView icon = bgCover.copySource();

        bgCover.cut(0, (bgCover.getHeight() - 250) / 2, 900, 120);
        bgCover.setCorner(40);
        bgCover.setAlpha(0.15D);

        icon.resize(396, 110);
        icon.cut(108, 0, 170, 110);
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
