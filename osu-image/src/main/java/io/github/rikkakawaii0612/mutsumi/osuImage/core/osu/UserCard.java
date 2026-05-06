package io.github.rikkakawaii0612.mutsumi.osuImage.core.osu;

import io.github.rikkakawaii0612.mutsumi.osuApi.data.User;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.Group;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.ImageView;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.Rectangle;
import io.github.rikkakawaii0612.mutsumi.osuImage.core.Text;
import io.github.rikkakawaii0612.mutsumi.osuImage.util.ARGB;
import io.github.rikkakawaii0612.mutsumi.osuImage.util.IOCache;

public class UserCard extends Group {
    private static final int BACKGROUND = ARGB.toArgb(68, 60, 54);

    private final int width;

    public UserCard(User user, double pp) {
        Text username = new Text(user.username);
        username.setMaxFontSize(36);
        username.setPosition(138, 25);

        Text ppField = new Text(String.format("%.1fpp", pp));
        ppField.setMaxFontSize(36);
        ppField.setPosition(138, 75);

        this.width = Math.max(180, 180 + Math.max(username.getWidth(), ppField.getWidth()));

        Rectangle background = new Rectangle(this.width, 140);
        background.setColor(BACKGROUND);
        background.setCorner(40);

        ImageView avatar = new ImageView(IOCache.getAvatar(user));
        avatar.setPosition(15, 15);
        avatar.resize(110, 110);
        avatar.setCorner(30);

        ImageView bgCover = avatar.copySource();
        bgCover.resize(this.width, this.width);
        bgCover.setCorner(40);
        bgCover.setAlpha(0.1D);
        bgCover.cut(0, (this.width - 140) / 2, this.width, 140);

        this.addChildren(background, bgCover, avatar, username, ppField);
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return 140;
    }
}
