package io.github.rikkakawaii0612.mutsumi.osuRandomBeatmap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Generators {
    private static final Logger LOGGER = LoggerFactory.getLogger("OsuRandomBeatmap");

    public static BeatmapContent generate(int endTime, double bpm, double od, double hp, Generator generator) {
        BeatmapContent beatmapContent = new BeatmapContent();
        beatmapContent.setBpm(bpm);
        beatmapContent.setOd(od);
        beatmapContent.setHp(hp);
        while (generator.getCurrentTime() <= endTime) {
            for (HitObject hitObject : generator.next()) {
                beatmapContent.addObject(hitObject);
            }
        }

        return beatmapContent;
    }

    public static byte[] toOsz(int endTime, BeatmapContent content) {
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
}
