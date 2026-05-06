package io.github.rikkakawaii0612.mutsumi.osuRandomBeatmap.beatmap;

import io.github.rikkakawaii0612.mutsumi.osuRandomBeatmap.SilentMp3Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public abstract class Generator {
    private static final Logger LOGGER = LoggerFactory.getLogger("OsuRandomBeatmap");

    protected int startTime = 0;
    protected int endTime = 10000;
    protected double bpm = 120.0D;
    protected double od = 8.0D;
    protected double hp = 8.0D;

    public Generator() {
    }

    public final byte[] generate() {
        BeatmapContent beatmapContent = new BeatmapContent(this.bpm, this.od, this.hp);
        while (this.getCurrentTime() <= this.endTime) {
            for (HitObject hitObject : this.next()) {
                beatmapContent.addObject(hitObject);
            }
        }
        return toOsz(this.endTime, beatmapContent);
    }

    public List<String> getInfo() {
        List<String> list = new ArrayList<>();
        list.add("BPM (bpm)：" + this.bpm);
        list.add("时间区间/ms (time)：" + this.startTime + "~" + this.endTime);
        list.add("OD (od)：" + this.od);
        list.add("HP (hp)：" + this.hp);
        return list;
    }

    public void setParameter(String key, String value) {
        switch (key.toLowerCase(Locale.ROOT)) {
            case "bpm" -> {
                try {
                    double bpm = Double.parseDouble(value);
                    if (!Double.isFinite(bpm)) {
                        throw new IllegalArgumentException("……BPM 起码得是个有限值吧？");
                    }
                    if (bpm <= 0.0D) {
                        throw new IllegalArgumentException("……BPM 不应该是正的吗。");
                    }
                    if (bpm > 1000.0D) {
                        throw new IllegalArgumentException("唔，BPM太高了，%s 会受不了的……");
                    }
                    this.bpm = bpm;
                } catch (NumberFormatException _) {
                    throw new IllegalArgumentException("BPM 必须是个数字。");
                }
            }

            case "time" -> {
                if (!value.contains("~")) {
                    throw new IllegalArgumentException("时间区间格式不正确。应为 a~b 的格式。");
                }
                String[] arr = value.split("~");
                if (arr.length != 2) {
                    throw new IllegalArgumentException("时间区间格式不正确。应为 a~b 的格式。");
                }
                try {
                    int startTime = Integer.parseInt(arr[0].trim()), endTime = Integer.parseInt(arr[1].trim());
                    if (startTime > endTime) {
                        throw new IllegalArgumentException("什么叫谱面还没开始就结束了？");
                    }
                    if (startTime < 0) {
                        throw new IllegalArgumentException("起始时间怎么是负的。");
                    }
                    if (endTime > 600000) {
                        throw new IllegalArgumentException("唔，时长太长了，%s 会受不了的……");
                    }
                    this.startTime = startTime;
                    this.endTime = endTime;
                } catch (NumberFormatException _) {
                    throw new IllegalArgumentException("你输入的时间不是整数。");
                }
            }

            case "od" -> {
                try {
                    double od = Double.parseDouble(value);
                    if (od < 0.0D || od > 10.0D || !Double.isFinite(od)) {
                        throw new IllegalArgumentException("OD 必须在 [0,10] 内。");
                    }
                    this.od = od;
                } catch (NumberFormatException _) {
                    throw new IllegalArgumentException("OD 必须是个数字。");
                }
            }

            case "hp" -> {
                try {
                    double hp = Double.parseDouble(value);
                    if (hp < 0.0D || hp > 10.0D || !Double.isFinite(hp)) {
                        throw new IllegalArgumentException("HP 必须在 [0,10] 内。");
                    }
                    this.hp = hp;
                } catch (NumberFormatException _) {
                    throw new IllegalArgumentException("HP 必须是个数字。");
                }
            }

            default -> throw new IllegalArgumentException("没有该参数。");
        }
    }

    private static byte[] toOsz(int endTime, BeatmapContent content) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            ZipEntry beatmap = new ZipEntry("Beatmap@" + Integer.toHexString(content.hashCode()) + ".osu");
            zos.putNextEntry(beatmap);
            zos.write(content.toFileContent().getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();

            ZipEntry audio = new ZipEntry("audio.mp3");
            zos.putNextEntry(audio);
            zos.write(SilentMp3Generator.generateSilentMp3(3 + endTime / 1000));
            zos.closeEntry();

            zos.finish();
            return baos.toByteArray();

        } catch (IOException e) {
            LOGGER.error("Failed to convert .osz file: ", e);
            return null;
        }
    }

    protected abstract int getCurrentTime();

    protected abstract HitObject[] next();

    public abstract String getName();
}
