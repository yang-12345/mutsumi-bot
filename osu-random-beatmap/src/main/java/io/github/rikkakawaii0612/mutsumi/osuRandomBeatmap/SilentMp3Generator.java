package io.github.rikkakawaii0612.mutsumi.osuRandomBeatmap;

import de.sciss.jump3r.lowlevel.LameEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFormat;
import java.io.ByteArrayOutputStream;

/**
 * 使用 Jump3r 生成指定时长的静音 MP3，并返回字节数组
 * 又是 Deepseek 神力嘿嘿 (这种为了单一功能而写一大堆代码的东西就交给 AI 比较好)
 */
public class SilentMp3Generator {
    private static final Logger LOGGER = LoggerFactory.getLogger("OsuRandomBeatmap");

    private static final AudioFormat PCM_FORMAT = new AudioFormat(44100, 16, 2, true, false);

    public static byte[] generateSilentMp3(int durationSeconds) {
        // 1. 计算总 PCM 数据量
        int totalPcmBytes = (int) (PCM_FORMAT.getFrameRate() * durationSeconds * PCM_FORMAT.getFrameSize());

        // 2. 创建 Jump3r 编码器
        LameEncoder encoder = new LameEncoder(PCM_FORMAT,
                128,
                LameEncoder.CHANNEL_MODE_STEREO,
                LameEncoder.QUALITY_HIGHEST,
                false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (baos) {
            byte[] mp3Buffer = new byte[(int) encoder.getMP3BufferSize()];
            byte[] pcmBuffer = new byte[4096]; // 固定大小的PCM缓冲区，避免大数组分配
            int remainingBytes = totalPcmBytes;
            while (remainingBytes > 0) {
                int bytesToWrite = Math.min(remainingBytes, pcmBuffer.length);
                // 3. 关键：正确的参数顺序
                int bytesEncoded = encoder.encodeBuffer(pcmBuffer, 0, bytesToWrite, mp3Buffer);
                if (bytesEncoded > 0) {
                    baos.write(mp3Buffer, 0, bytesEncoded);
                }
                remainingBytes -= bytesToWrite;
            }

            // 4. 刷新编码器内部缓冲区，确保所有数据都被写入
            encoder.close();

        } catch (Exception e) {
            LOGGER.error("Exception in generating .mp3 file: ", e);
        }

        return baos.toByteArray();
    }
}