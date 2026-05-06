package io.github.rikkakawaii0612.mutsumi.api.contact.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * {@link Image} 的一个实现, 会从给定的 URL 读取图片并缓存.
 * 如果读取成功, 之后获取数据时都会返回已获取到的数据.
 */
public class CachedURLImage implements Image {
    private static final Logger LOGGER = LoggerFactory.getLogger("Mutsumi");
    private final URL url;
    private byte[] cache;

    public CachedURLImage(String url) throws MalformedURLException {
        this.url = URI.create(url).toURL();
    }

    public CachedURLImage(URL url) {
        this.url = url;
    }

    public URL getUrl() {
        return this.url;
    }

    @Override
    public byte[] getData() {
        if (this.cache != null) {
            return this.cache;
        }
        // 分批次读取数据
        try (InputStream is = this.url.openStream()) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[8192];
            int nRead;
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            this.cache = buffer.toByteArray();
        } catch (IOException e) {
            LOGGER.error("Exception in reading image data: ", e);
        }
        return this.cache;
    }
}
