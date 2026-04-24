package io.github.rikkakawaii0612.mutsumi.osuApi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.rikkakawaii0612.mutsumi.api.service.data.IntegerObjectData;
import io.github.rikkakawaii0612.mutsumi.api.service.data.ListObjectData;
import io.github.rikkakawaii0612.mutsumi.api.service.data.LongObjectData;
import io.github.rikkakawaii0612.mutsumi.osuApi.data.*;
import io.github.rikkakawaii0612.mutsumi.api.service.Service;
import io.github.rikkakawaii0612.mutsumi.api.service.data.ObjectData;
import io.github.rikkakawaii0612.mutsumi.api.service.ServiceRequest;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Extension
public class OsuApiService extends Service {
    private static final Logger LOGGER = LoggerFactory.getLogger("OsuApi");
    private static final String API_BASE_URL = "https://osu.ppy.sh/api/v2/";

    private static RateLimiter rateLimiter;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String accessToken;

    public OsuApiService() {
    }

    // you don't need to add prefix API_BASE_URL
    public Optional<JsonNode> post(String url) {
        return RateLimiter.decorateSupplier(rateLimiter, () -> doPost(url)).get();
    }

    private Optional<JsonNode> doPost(String url) {
        if (this.accessToken == null) {
            return Optional.empty();
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + url))
                    .header("Authorization", "Bearer " + this.accessToken)
                    .header("x-api-version", "20250410")
                    .GET()
                    .build();

            HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return Optional.empty();
            }

            return Optional.ofNullable(this.objectMapper.readTree(response.body()));

        } catch (Exception e) {
            LOGGER.warn("Http post failed: ", e);
            return Optional.empty();
        }
    }

    @Override
    public ObjectData call(ServiceRequest request) {
        String service = request.getHeader("service");
        switch (service) {
            case "getUser" -> {
                String id = request.getHeader("id");
                if (!id.isBlank()) {
                    return this.getUser(id);
                }
                String username = request.getHeader("username");
                if (!username.isBlank()) {
                    return this.getUser(username);
                }
                LOGGER.warn("No valid data 'id' or 'username' in request of 'getUser'");
                return ObjectData.EMPTY;
            }

            case "getBestScores" -> {
                String id = request.getHeader("id");
                if (id.isBlank()) {
                    LOGGER.warn("No data 'id' in request of 'getBestScores'");
                    return ObjectData.EMPTY;
                }
                PlayMode mode = PlayMode.of(request.getHeader("mode"));
                if (mode == null) {
                    LOGGER.warn("No data 'mode' in request of 'getBestScores'");
                    return ObjectData.EMPTY;
                }
                long l;
                try {
                    l = Long.parseLong(id);
                } catch (NumberFormatException _) {
                    LOGGER.warn("Data 'id' is not a number in request of 'getBestScores'");
                    return ObjectData.EMPTY;
                }
                return this.getBestScores(l, mode);
            }

            case "getBeatmap" -> {
                String id = request.getHeader("id");
                if (id.isBlank()) {
                    LOGGER.warn("No data 'id' in request of 'getBeatmap'");
                    return ObjectData.EMPTY;
                }
                long l;
                try {
                    l = Long.parseLong(id);
                } catch (NumberFormatException _) {
                    LOGGER.warn("Data 'id' is not a number in request of 'getBeatmap'");
                    return ObjectData.EMPTY;
                }
                return this.getBeatmap(l);
            }

            case "getBeatmaps" -> {
                ObjectData data = request.getData("ids");
                if (data.isEmpty()) {
                    LOGGER.warn("No data 'ids' in request of 'getBeatmaps'");
                    return ObjectData.EMPTY;
                }
                if (!data.is("base.List")) {
                    LOGGER.warn("Data 'ids' is expected to be List, but found {} in request of 'getBeatmaps'",
                            data.getType());
                    return ObjectData.EMPTY;
                }

                List<? extends ObjectData> listObjectData = ListObjectData.read(data);
                if (listObjectData.isEmpty()) {
                    LOGGER.warn("List data 'ids' is empty in request of 'getBeatmaps'");
                    return ObjectData.EMPTY;
                }

                long[] ids = new long[listObjectData.size()];
                for (int i = 0; i < listObjectData.size(); i++) {
                    ObjectData o = listObjectData.get(i);
                    if (!o.is("base.Long")) {
                        LOGGER.warn("Data 'ids' is expected to contain Long element," +
                                        " but found {} in request of 'getBeatmaps'",
                                data.getType());
                        return ObjectData.EMPTY;
                    }
                    ids[i] = o.asLong();
                }

                return this.getBeatmaps(ids);
            }

            case "getScore" -> {
                String id = request.getHeader("id");
                if (id.isBlank()) {
                    LOGGER.warn("No data 'id' in request of 'getScore'");
                    return ObjectData.EMPTY;
                }
                long l;
                try {
                    l = Long.parseLong(id);
                } catch (NumberFormatException _) {
                    LOGGER.warn("Data 'id' is not a number in request of 'getScore'");
                    return ObjectData.EMPTY;
                }
                return this.getScore(l);
            }

            case "getUserBeatmapScore" -> {
                String user = request.getHeader("user");
                if (user.isBlank()) {
                    LOGGER.warn("No data 'user' in request of 'getUserBeatmapScore'");
                    return ObjectData.EMPTY;
                }
                String beatmap = request.getHeader("beatmap");
                if (beatmap.isBlank()) {
                    LOGGER.warn("No data 'beatmap' in request of 'getUserBeatmapScore'");
                    return ObjectData.EMPTY;
                }

                long userId, beatmapId;
                try {
                    userId = Long.parseLong(user);
                } catch (NumberFormatException _) {
                    LOGGER.warn("Data 'user' is not a number in request of 'getUserBeatmapScore'");
                    return ObjectData.EMPTY;
                }
                try {
                    beatmapId = Long.parseLong(beatmap);
                } catch (NumberFormatException _) {
                    LOGGER.warn("Data 'beatmap' is not a number in request of 'getUserBeatmapScore'");
                    return ObjectData.EMPTY;
                }
                return this.getUserBeatmapScore(userId, beatmapId);
            }

            case "getUserBeatmapScores" -> {
                String user = request.getHeader("user");
                if (user.isBlank()) {
                    LOGGER.warn("No data 'user' in request of 'getUserBeatmapScores'");
                    return ObjectData.EMPTY;
                }
                String beatmap = request.getHeader("beatmap");
                if (beatmap.isBlank()) {
                    LOGGER.warn("No data 'beatmap' in request of 'getUserBeatmapScores'");
                    return ObjectData.EMPTY;
                }

                long userId, beatmapId;
                try {
                    userId = Long.parseLong(user);
                } catch (NumberFormatException _) {
                    LOGGER.warn("Data 'user' is not a number in request of 'getUserBeatmapScores'");
                    return ObjectData.EMPTY;
                }
                try {
                    beatmapId = Long.parseLong(beatmap);
                } catch (NumberFormatException _) {
                    LOGGER.warn("Data 'beatmap' is not a number in request of 'getUserBeatmapScores'");
                    return ObjectData.EMPTY;
                }
                return this.getUserBeatmapScores(userId, beatmapId);
            }

            case "getAllPlayedBeatmaps" -> {
                String id = request.getHeader("id");
                if (id.isBlank()) {
                    LOGGER.warn("No data 'id' in request of 'getAllPlayedBeatmaps'");
                    return ObjectData.EMPTY;
                }
                long l;
                try {
                    l = Long.parseLong(id);
                } catch (NumberFormatException _) {
                    LOGGER.warn("Data 'id' is not a number in request of 'getAllPlayedBeatmaps'");
                    return ObjectData.EMPTY;
                }
                return this.getAllPlayedBeatmaps(l);
            }

            default -> {
                LOGGER.warn("Trying to access unsupported service: {}", service);
                return ObjectData.EMPTY;
            }
        }
    }

    private ObjectData getUser(String username) {
        if (this.accessToken == null) {
            return ObjectData.EMPTY;
        }

        String str = URLEncoder.encode(username, StandardCharsets.UTF_8);
        Optional<JsonNode> optional = this.post("users/" + str);
        if (optional.isEmpty()) {
            return ObjectData.EMPTY;
        }

        try {
            return this.objectMapper.readValue(optional.get().toString(), User.class);
        } catch (Exception e) {
            LOGGER.warn("Failed to resolve Score: ", e);
            return ObjectData.EMPTY;
        }
    }

    private ObjectData getBestScores(long id, PlayMode mode) {
        if (this.accessToken == null) {
            return ObjectData.EMPTY;
        }

        String str = mode.getName();
        List<JsonNode> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Optional<JsonNode> optional = this.post(
                    String.format("users/%d/scores/best?mode=%s&limit=100&offset=%d",
                            id, str, i * 100));

            if (optional.isEmpty()) {
                return ObjectData.EMPTY;
            }

            JsonNode node = optional.get();
            if (!node.isArray()) {
                LOGGER.warn("Found non-array response body while obtaining osu!user {}'s best scores in {} mode.",
                        id, str);
                return ObjectData.EMPTY;
            }

            node.forEach(list::add);
        }


        List<Score> scores = new ArrayList<>();
        try {
            for (JsonNode jsonNode : list) {
                scores.add(this.objectMapper.readValue(jsonNode.toString(), Score.class));
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to resolve Score: ", e);
            return ObjectData.EMPTY;
        }

        return ObjectData.of(scores);
    }

    private ObjectData getBeatmap(long id) {
        if (this.accessToken == null) {
            return ObjectData.EMPTY;
        }

        Optional<JsonNode> optional = this.post("beatmaps/" + id);
        if (optional.isEmpty()) {
            return ObjectData.EMPTY;
        }

        try {
            return this.objectMapper.readValue(optional.get().toString(), Beatmap.class);
        } catch (Exception e) {
            LOGGER.warn("Failed to resolve Beatmap: ", e);
            return ObjectData.EMPTY;
        }
    }

    private ObjectData getBeatmaps(long[] ids) {
        if (this.accessToken == null) {
            return ObjectData.EMPTY;
        }

        if (ids.length == 0) {
            return ObjectData.EMPTY;
        }

        StringBuilder builder = new StringBuilder();
        for (long id : ids) {
            builder.append("&ids%5B%5D=").append(id);
        }

        String url = "beatmaps?" + builder.substring(1);
        Optional<JsonNode> optional = this.post(url);
        if (optional.isEmpty()) {
            return ObjectData.EMPTY;
        }

        try {
            List<Beatmap> list = this.objectMapper.readValue(optional.get().get("beatmaps").toString(),
                    new TypeReference<>() {});
            return ObjectData.of(list);
        } catch (Exception e) {
            LOGGER.warn("Failed to resolve List<Beatmap>: ", e);
            return ObjectData.EMPTY;
        }
    }

    private ObjectData getScore(long id) {
        if (this.accessToken == null) {
            return ObjectData.EMPTY;
        }

        Optional<JsonNode> optional = this.post("scores/" + id);
        if (optional.isEmpty()) {
            return ObjectData.EMPTY;
        }

        try {
            return this.objectMapper.readValue(optional.get().toString(), Score.class);
        } catch (Exception e) {
            return ObjectData.EMPTY;
        }
    }

    private ObjectData getUserBeatmapScore(long user, long beatmap) {
        if (this.accessToken == null) {
            return ObjectData.EMPTY;
        }

        Optional<JsonNode> optional = this.post(String.format("beatmaps/%d/scores/users/%d", beatmap, user));
        if (optional.isEmpty()) {
            return ObjectData.EMPTY;
        }

        try {
            return this.objectMapper.readValue(optional.get().toString(), BeatmapUserScore.class);
        } catch (Exception e) {
            LOGGER.warn("Failed to resolve BeatmapUserScore: ", e);
            return ObjectData.EMPTY;
        }
    }

    private ObjectData getUserBeatmapScores(long user, long beatmap) {
        if (this.accessToken == null) {
            return ListObjectData.EMPTY;
        }

        Optional<JsonNode> optional = this.post(String.format("beatmaps/%d/scores/users/%d/all", beatmap, user));
        if (optional.isEmpty()) {
            return ListObjectData.EMPTY;
        }

        try {
            List<Score> list = this.objectMapper.readValue(optional.get().get("scores").toString(),
                    new TypeReference<>() {});
            return ObjectData.of(list);
        } catch (Exception e) {
            LOGGER.warn("Failed to resolve List<BeatmapUserScore>: ", e);
            return ListObjectData.EMPTY;
        }
    }

    private ObjectData getAllPlayedBeatmaps(long id) {
        if (this.accessToken == null) {
            return ObjectData.EMPTY;
        }

        List<JsonNode> list = new ArrayList<>();
        for (int i = 0; ; i++) {
            Optional<JsonNode> optional = this.post(
                    String.format("users/%d/beatmapsets/most_played?limit=100&offset=%d",
                            id, i * 100));

            if (optional.isEmpty()) {
                return ObjectData.EMPTY;
            }

            JsonNode node = optional.get();
            if (!node.isArray()) {
                LOGGER.warn("Found non-array response body while obtaining osu!user {}'s all played beatmaps.",
                        id);
                return ObjectData.EMPTY;
            }

            node.forEach(list::add);
            if (node.size() < 100) {
                break;
            }
        }

        List<BeatmapPlayCount> beatmaps = new ArrayList<>();
        try {
            for (JsonNode jsonNode : list) {
                beatmaps.add(this.objectMapper.readValue(jsonNode.toString(), BeatmapPlayCount.class));
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to resolve BeatmapPlayCount: ", e);
            return ObjectData.EMPTY;
        }

        return ObjectData.of(beatmaps);
    }

    @Override
    public void load() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(60)
                .limitRefreshPeriod(Duration.ofSeconds(30))
                .timeoutDuration(Duration.ofSeconds(60))
                .build();
        rateLimiter = RateLimiter.of("osu-api-limiter", config);

        JsonNode node = this.getModule().getConfig();
        if (!node.has("clientId") || !node.has("clientSecret")) {
            LOGGER.warn("Cannot read client id and client secret from config!");
            return;
        }

        String clientId = node.get("clientId").asText();
        String clientSecret = node.get("clientSecret").asText();

        String requestBody = String.format(
                "{\"client_id\":\"%s\",\"client_secret\":\"%s\",\"grant_type\":\"client_credentials\",\"scope\":\"public\"}",
                clientId, clientSecret
        );

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://osu.ppy.sh/oauth/token"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response;
            try (HttpClient httpClient = HttpClient.newHttpClient()) {
                response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            }

            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to get access token. Status code: " + response.statusCode());
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.body());
            this.accessToken = jsonNode.get("access_token").asText();
        } catch (Exception e) {
            LOGGER.warn("Cannot get access token: ", e);
        }
    }

    @Override
    public void unload() {
        this.httpClient.close();
    }

    @Override
    public String getId() {
        return "osu-api";
    }
}
