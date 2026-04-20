package io.github.rikkakawaii0612.mutsumi.osuApi;

import io.github.rikkakawaii0612.mutsumi.api.service.Service;
import io.github.rikkakawaii0612.mutsumi.osuApi.data.*;
import io.github.rikkakawaii0612.mutsumi.api.service.ServiceRequest;
import io.github.rikkakawaii0612.mutsumi.api.service.data.ObjectData;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Extension
public class OsuDataService extends Service {
    private static final Logger LOGGER = LoggerFactory.getLogger("OsuApi");

    @Override
    public ObjectData call(ServiceRequest request) {
        String key = request.getHeader("value");
        ObjectData objectData = request.getData("value");
        if (key.isBlank()) {
            LOGGER.warn("Trying to get blank key from data type '{}'!", objectData.getType());
            return ObjectData.EMPTY;
        }
        if (objectData.isEmpty()) {
            LOGGER.warn("Trying to get key '{}' from empty data!", key);
            return ObjectData.EMPTY;
        }

        return switch (objectData) {
            case User user -> resolveUser(user, key);
            case Beatmap beatmap -> resolveBeatmap(beatmap, key);
            case Beatmapset beatmapset -> resolveBeatmapset(beatmapset, key);
            case Score score -> resolveScore(score, key);
            case BeatmapPlayCount beatmapPlayCount -> resolveBeatmapPlayCount(beatmapPlayCount, key);
            case BeatmapUserScore beatmapUserScore -> resolveBeatmapUserScore(beatmapUserScore, key);
            default -> {
                LOGGER.warn("Trying to get key from unsupported type of data: {}", objectData.getType());
                yield ObjectData.EMPTY;
            }
        };
    }

    private static ObjectData resolveUser(User user, String key) {
        return switch (key) {
            case "id" -> ObjectData.of(user.id);
            case "name" -> ObjectData.of(user.name);
            case "playMode" -> ObjectData.of(user.playMode.getName());
            case "avatarUrl" -> ObjectData.of(user.avatarUrl);
            case "online" -> ObjectData.of(user.online);
            default -> {
                LOGGER.warn("Trying to get unsupported key from User: {}", key);
                yield ObjectData.EMPTY;
            }
        };
    }

    private static ObjectData resolveBeatmap(Beatmap beatmap, String key) {
        return switch (key) {
            case "id" -> ObjectData.of(beatmap.id);
            case "rating" -> ObjectData.of(beatmap.rating);
            case "playMode" -> ObjectData.of(beatmap.playMode.getName());
            case "version" -> ObjectData.of(beatmap.version);
            case "beatmapsetId" -> ObjectData.of(beatmap.beatmapsetId);
            case "beatmapset" -> ObjectData.of(beatmap.beatmapset);
            case "rankStatus" -> ObjectData.of(beatmap.rankStatus.getName());
            case "cs" -> ObjectData.of(beatmap.cs);
            default -> {
                LOGGER.warn("Trying to get unsupported key from Beatmap: {}", key);
                yield ObjectData.EMPTY;
            }
        };
    }

    private static ObjectData resolveBeatmapset(Beatmapset beatmapset, String key) {
        return switch (key) {
            case "id" -> ObjectData.of(beatmapset.id);
            case "artist" -> ObjectData.of(beatmapset.artist);
            case "artistUnicode" -> ObjectData.of(beatmapset.artistUnicode);
            case "title" -> ObjectData.of(beatmapset.title);
            case "titleUnicode" -> ObjectData.of(beatmapset.titleUnicode);
            case "creator" -> ObjectData.of(beatmapset.creator);
            case "beatmaps" -> ObjectData.of(beatmapset.beatmaps);
            default -> {
                LOGGER.warn("Trying to get unsupported key from Beatmapset: {}", key);
                yield ObjectData.EMPTY;
            }
        };
    }

    private static ObjectData resolveScore(Score score, String key) {
        return switch (key) {
            case "id" -> ObjectData.of(score.id);
            case "beatmapId" -> ObjectData.of(score.beatmapId);
            case "accuracy" -> ObjectData.of(score.accuracy);
            case "pp" -> ObjectData.of(score.pp);
            case "beatmap" -> ObjectData.of(score.beatmap);
            case "beatmapset" -> ObjectData.of(score.beatmapset);
            default -> {
                LOGGER.warn("Trying to get unsupported key from Score: {}", key);
                yield ObjectData.EMPTY;
            }
        };
    }

    private static ObjectData resolveBeatmapPlayCount(BeatmapPlayCount beatmapPlayCount, String key) {
        return switch (key) {
            case "beatmapId" -> ObjectData.of(beatmapPlayCount.beatmapId);
            case "beatmap" -> ObjectData.of(beatmapPlayCount.beatmap);
            case "beatmapset" -> ObjectData.of(beatmapPlayCount.beatmapset);
            case "count" -> ObjectData.of(beatmapPlayCount.count);
            default -> {
                LOGGER.warn("Trying to get unsupported key from BeatmapPlayCount: {}", key);
                yield ObjectData.EMPTY;
            }
        };
    }

    private static ObjectData resolveBeatmapUserScore(BeatmapUserScore beatmapUserScore, String key) {
        return switch (key) {
            case "position" -> ObjectData.of(beatmapUserScore.position);
            case "score" -> ObjectData.of(beatmapUserScore.score);
            default -> {
                LOGGER.warn("Trying to get unsupported key from BeatmapUserScore: {}", key);
                yield ObjectData.EMPTY;
            }
        };
    }

    @Override
    public void load() {
    }

    @Override
    public void unload() {
    }

    @Override
    public String getId() {
        return "osu-api-util";
    }
}
