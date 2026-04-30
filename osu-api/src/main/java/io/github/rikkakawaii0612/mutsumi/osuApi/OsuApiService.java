package io.github.rikkakawaii0612.mutsumi.osuApi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.rikkakawaii0612.mutsumi.api.Service;
import io.github.rikkakawaii0612.mutsumi.api.ServiceLookup;
import io.github.rikkakawaii0612.mutsumi.osuApi.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OsuApiService implements Service {
    private static final Logger LOGGER = LoggerFactory.getLogger("OsuApi");
    private static final String API_BASE_URL = "https://osu.ppy.sh/api/v2/";

    private String clientId;
    private String clientSecret;
    // 长期限流, 最多存储 60 个令牌, 每秒补充 1 个
    private final Bucket longTerm = Bucket.builder()
            .addLimit(Bandwidth.builder()
                    .capacity(60L)
                    .refillGreedy(1L, Duration.ofSeconds(1L))
                    .build())
            .build();
    // 突发限流, 使 post 请求必须间隔至少 0.08s
    private final Bucket interval = Bucket.builder()
            .addLimit(Bandwidth.builder()
                    .capacity(1L)
                    .refillGreedy(1L, Duration.ofMillis(80L))
                    .build())
            .build();

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String accessToken;
    private long tokenExpiredAt = Long.MIN_VALUE;

    // 不需要 API_BASE_URL 前缀
    private Optional<JsonNode> post(String url) {
        try {
            if (this.longTerm.asBlocking().tryConsume(1, Duration.ofSeconds(60L))
                    && this.interval.asBlocking().tryConsume(1, Duration.ofSeconds(60L))) {
                return doPost(url);
            } else {
                LOGGER.warn("Too many post request!");
                return Optional.empty();
            }
        } catch (InterruptedException e) {
            return Optional.empty();
        }
    }

    private Optional<JsonNode> doPost(String url) {
        this.checkAccessToken();
        if (this.accessToken == null) {
            return Optional.empty();
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + url))
                .header("Authorization", "Bearer " + this.accessToken)
                .header("x-api-version", "20250410")
                .GET()
                .build();

        HttpResponse<String> response;
        try {
            response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception _) {
            return Optional.empty();
        }

        if (response.statusCode() != 200) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(this.objectMapper.readTree(response.body()));
        } catch (Exception e) {
            LOGGER.warn("Http response is not a json object: ", e);
            return Optional.empty();
        }
    }

    public Optional<User> getUser(String username) {
        this.checkAccessToken();
        if (this.accessToken == null) {
            return Optional.empty();
        }

        String str = URLEncoder.encode(username, StandardCharsets.UTF_8);
        Optional<JsonNode> optional = this.post("users/" + str);
        if (optional.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(this.objectMapper.readValue(optional.get().toString(), User.class));
        } catch (Exception e) {
            LOGGER.warn("Failed to resolve Score: ", e);
            return Optional.empty();
        }
    }

    public Optional<List<Score>> getBestScores(long id, PlayMode mode) {
        this.checkAccessToken();
        if (this.accessToken == null) {
            return Optional.empty();
        }

        String str = mode.getName();
        List<JsonNode> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Optional<JsonNode> optional = this.post(
                    String.format("users/%d/scores/best?mode=%s&limit=100&offset=%d",
                            id, str, i * 100));

            if (optional.isEmpty()) {
                return Optional.empty();
            }

            JsonNode node = optional.get();
            if (!node.isArray()) {
                LOGGER.warn("Found non-array response body while obtaining osu!user {}'s best scores in {} mode.",
                        id, str);
                return Optional.empty();
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
            return Optional.empty();
        }

        return Optional.of(scores);
    }

    public Optional<Beatmap> getBeatmap(long id) {
        this.checkAccessToken();
        if (this.accessToken == null) {
            return Optional.empty();
        }

        Optional<JsonNode> optional = this.post("beatmaps/" + id);
        if (optional.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(this.objectMapper.readValue(optional.get().toString(), Beatmap.class));
        } catch (Exception e) {
            LOGGER.warn("Failed to resolve Beatmap: ", e);
            return Optional.empty();
        }
    }

    public Optional<List<Beatmap>> getBeatmaps(long[] ids) {
        return this.getBeatmaps(Arrays.stream(ids).boxed().toList());
    }

    public Optional<List<Beatmap>> getBeatmaps(Collection<Long> ids) {
        this.checkAccessToken();
        if (this.accessToken == null) {
            return Optional.empty();
        }

        if (ids.isEmpty()) {
            return Optional.empty();
        }

        StringBuilder builder = new StringBuilder();
        for (long id : ids) {
            builder.append("&ids%5B%5D=").append(id);
        }

        String url = "beatmaps?" + builder.substring(1);
        Optional<JsonNode> optional = this.post(url);
        if (optional.isEmpty()) {
            return Optional.empty();
        }

        try {
            List<Beatmap> list = this.objectMapper.readValue(optional.get().get("beatmaps").toString(),
                    new TypeReference<>() {});
            return Optional.of(list);
        } catch (Exception e) {
            LOGGER.warn("Failed to resolve List<Beatmap>: ", e);
            return Optional.empty();
        }
    }

    public Optional<Score> getScore(long id) {
        this.checkAccessToken();
        if (this.accessToken == null) {
            return Optional.empty();
        }

        Optional<JsonNode> optional = this.post("scores/" + id);
        if (optional.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(this.objectMapper.readValue(optional.get().toString(), Score.class));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<BeatmapUserScore> getUserBeatmapScore(long user, long beatmap) {
        this.checkAccessToken();
        if (this.accessToken == null) {
            return Optional.empty();
        }

        Optional<JsonNode> optional = this.post(String.format("beatmaps/%d/scores/users/%d", beatmap, user));
        if (optional.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(this.objectMapper.readValue(optional.get().toString(), BeatmapUserScore.class));
        } catch (Exception e) {
            LOGGER.warn("Failed to resolve BeatmapUserScore: ", e);
            return Optional.empty();
        }
    }

    public Optional<List<Score>> getUserBeatmapScores(long user, long beatmap) {
        this.checkAccessToken();
        if (this.accessToken == null) {
            return Optional.empty();
        }

        Optional<JsonNode> optional = this.post(String.format("beatmaps/%d/scores/users/%d/all", beatmap, user));
        if (optional.isEmpty()) {
            return Optional.empty();
        }

        try {
            List<Score> list = this.objectMapper.readValue(optional.get().get("scores").toString(),
                    new TypeReference<>() {});
            return Optional.of(list);
        } catch (Exception e) {
            LOGGER.warn("Failed to resolve List<BeatmapUserScore>: ", e);
            return Optional.empty();
        }
    }

    public Optional<List<BeatmapPlayCount>> getAllPlayedBeatmaps(long id) {
        this.checkAccessToken();
        if (this.accessToken == null) {
            return Optional.empty();
        }

        List<JsonNode> nodes = new ArrayList<>();

        // 异步 post 获取游玩次数最多的谱面 (实际上包含了所有谱面)
        // 每次循环尝试同时获取 4 页 (400 个) 谱面
        try (ExecutorService executor = Executors.newFixedThreadPool(4)) {
            for (int i = 0; ; i++) {
                List<CompletableFuture<Optional<JsonNode>>> futures = new ArrayList<>();

                for (int j = 0; j < 4; j++) {
                    int offset = j + 4 * i;
                    futures.add(CompletableFuture.supplyAsync(() -> this.post(
                            String.format("users/%d/beatmapsets/most_played?limit=100&offset=%d",
                                    id, offset * 100)), executor));
                }
                // 其实应该给 Optional.empty() 加个处理的
                List<JsonNode> list = futures.stream()
                        .map(CompletableFuture::join)
                        .flatMap(Optional::stream)
                        .toList();
                // JSON 数组拆解存入列表
                for (JsonNode node : list) {
                    node.forEach(nodes::add);
                }

                if (nodes.isEmpty() || list.getLast().size() < 100) {
                    break;
                }
            }
        }

        List<BeatmapPlayCount> beatmaps = new ArrayList<>();
        try {
            for (JsonNode jsonNode : nodes) {
                beatmaps.add(this.objectMapper.readValue(jsonNode.toString(), BeatmapPlayCount.class));
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to resolve BeatmapPlayCount: ", e);
            return Optional.empty();
        }

        return Optional.of(beatmaps);
    }

    private void checkAccessToken() {
        // 设置 Access Token 有效期 23h55min, 留 5min 期限空间
        if (System.nanoTime() > this.tokenExpiredAt + 86100000000000L) {
            String str = this.getAccessToken();
            if (str != null) {
                this.tokenExpiredAt = System.nanoTime();
                this.accessToken = str;
            }
        }
    }

    private String getAccessToken() {
        if (this.clientId == null || this.clientSecret == null) {
            return null;
        }
        String requestBody = String.format(
                "{\"client_id\":\"%s\",\"client_secret\":\"%s\",\"grant_type\":\"client_credentials\",\"scope\":\"public\"}",
                this.clientId, this.clientSecret);

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
                LOGGER.warn("Failed to get access token. Status Code: {}", response.statusCode());
                return null;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.body());
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            LOGGER.warn("Cannot get access token: ", e);
            return null;
        }
    }

    @Override
    public void load(String id, ServiceLookup lookup) {
        JsonNode node = lookup.getConfig().getOrCreate(id);
        if (!node.has("clientId") || !node.has("clientSecret")) {
            LOGGER.warn("Cannot read client id and client secret from config");
            return;
        }

        this.clientId = node.get("clientId").asText();
        this.clientSecret = node.get("clientSecret").asText();
        String str = this.getAccessToken();
        if (str != null) {
            this.tokenExpiredAt = System.nanoTime();
            this.accessToken = str;
        }
    }

    @Override
    public void unload() {
        this.httpClient.shutdown();
    }
}
