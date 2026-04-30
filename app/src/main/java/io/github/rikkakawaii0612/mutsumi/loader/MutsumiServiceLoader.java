package io.github.rikkakawaii0612.mutsumi.loader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.rikkakawaii0612.mutsumi.impl.MutsumiImpl;
import io.github.rikkakawaii0612.mutsumi.api.Config;
import io.github.rikkakawaii0612.mutsumi.api.Service;
import io.github.rikkakawaii0612.mutsumi.api.ServiceLookup;
import io.github.rikkakawaii0612.mutsumi.api.util.math.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class MutsumiServiceLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger("ServiceLoader");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final MutsumiImpl mutsumi;
    private URLClassLoader classLoader;
    private final Map<String, ServiceLookup.Wrapper> idsToServices = new HashMap<>();
    private final Map<String, JsonNode> configs = new HashMap<>();
    private final Config configImpl = id -> this.configs.computeIfAbsent(id, this::createConfig);

    public MutsumiServiceLoader(MutsumiImpl mutsumi) {
        this.mutsumi = mutsumi;
    }

    public void load() {
        if (this.classLoader != null) {
            LOGGER.warn("Trying to load service loader that has been loaded");
            this.unload();
        }

        this.loadConfigs();

        // 读取目录的服务 JAR 文件, 并创建 ClassLoader
        try {
            File modsDir = new File("services");
            if (!modsDir.isDirectory()) {
                Files.createDirectory(modsDir.toPath());
                return;
            }
            File[] files = modsDir.listFiles((_, name) -> name.toLowerCase().endsWith(".jar"));
            if (files == null || files.length == 0) {
                LOGGER.info("No services were found");
                return;
            }

            URL[] jarUrls = new URL[files.length];
            for (int i = 0; i < files.length; i++) {
                jarUrls[i] = files[i].toURI().toURL();
            }

            ClassLoader parent = MutsumiServiceLoader.class.getClassLoader();
            this.classLoader = new URLClassLoader(jarUrls, parent);
        } catch (Exception e) {
            LOGGER.error("Failed to create class loader: ", e);
            return;
        }

        // 读取服务元数据
        try {
            for (URL url : this.classLoader.getURLs()) {
                try (JarFile jarFile = new JarFile(url.getFile())) {
                    Manifest manifest = jarFile.getManifest();
                    Attributes attrs = manifest.getMainAttributes();
                    String id = attrs.getValue("Service-Id");
                    String version = attrs.getValue("Service-Version");
                    String author = attrs.getValue("Service-Author");
                    String dependenciesField = attrs.getValue("Service-Dependencies");
                    String mainClass = attrs.getValue("Service-Class");
                    if (id == null) {
                        LOGGER.warn("Found service that doesn't have an id. Ignored");
                        continue;
                    }
                    if (version == null) {
                        LOGGER.warn("Service {} doesn't have field 'Service-Version'. Ignored", id);
                        continue;
                    }
                    if (author == null) {
                        LOGGER.warn("Service {} doesn't have field 'Service-Author'. Ignored", id);
                        continue;
                    }
                    if (mainClass == null) {
                        LOGGER.warn("Service {} doesn't have field 'Service-Class'. Ignored", id);
                        continue;
                    }

                    List<String> dependencies = new ArrayList<>();
                    if (dependenciesField != null) {
                        // 依赖用逗号隔开, 两个依赖间的空格会被忽略
                        String[] arr = dependenciesField.trim().split(",\b*");
                        for (String str : arr) {
                            dependencies.add(str.trim());
                        }
                    }

                    Class<?> clazz = this.classLoader.loadClass(mainClass);
                    Service service = (Service) clazz.getConstructor().newInstance();
                    this.idsToServices.put(id, new ServiceLookup.Wrapper(
                            service, id, version, author, dependencies
                    ));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load metadata of services: ", e);
            this.unload();
            return;
        }

        // 加载服务
        try {
            Map<ServiceLookup.Wrapper, List<ServiceLookup.Wrapper>> dependencies = new HashMap<>();
            for (ServiceLookup.Wrapper wrapper : this.idsToServices.values()) {
                List<ServiceLookup.Wrapper> list = new ArrayList<>();
                for (String id : wrapper.dependencies()) {
                    if (!this.idsToServices.containsKey(id)) {
                        throw new ClassNotFoundException(
                                "Service '" + wrapper.id() + "' requires '" + id
                                        + "' as a dependency, which is missing!");
                    }
                    list.add(this.idsToServices.get(id));
                }
                dependencies.put(wrapper, list);
            }

            ServiceLookup lookup = new ServiceLookup(this.mutsumi, this.idsToServices, this.configImpl);

            // 按依赖关系进行拓扑排序
            List<ServiceLookup.Wrapper> sorted = MathUtils.topologicalSort(this.idsToServices.values(), dependencies);

            for (ServiceLookup.Wrapper wrapper : sorted) {
                wrapper.service().load(wrapper.id(), lookup);
            }

        } catch (Exception e) {
            LOGGER.error("Failed to load services: ", e);
            this.unload();
            return;
        }

        LOGGER.info("Loaded {} service(s)", this.idsToServices.size());
    }

    public void unload() {
        if (this.classLoader == null) {
            return;
        }

        boolean successful = true;
        for (ServiceLookup.Wrapper wrapper : this.idsToServices.values()) {
            try {
                wrapper.service().unload();
            } catch (Exception e) {
                LOGGER.error("Failed to unload service {}, which may occur memory leak: ", wrapper.id(), e);
                successful = false;
            }
        }

        if (successful) {
            LOGGER.info("Unloaded all services");
        } else {
            LOGGER.warn("Some services are not unloaded successfully. Be aware!");
        }


        ReferenceQueue<ClassLoader> queue = new ReferenceQueue<>();
        WeakReference<ClassLoader> weakReference = new WeakReference<>(this.classLoader, queue);

        try {
            this.classLoader.close();
        } catch (IOException e) {
            LOGGER.error("Failed to close service class loader, which may occur memory leak: ", e);
        } finally {
            this.classLoader = null;
        }

        // 检查类加载器是否在一秒内被回收, 报告可能的内存泄漏
        System.gc();
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            LOGGER.error("Thread was interrupted while checking GC of service class loader", e);
        } finally {
            if (Objects.equals(queue.poll(), weakReference)) {
                LOGGER.warn("Service class loader seems to be not garbage-collected, which may occur memory leak!");
            }
        }
    }

    public void loadConfigs() {
        this.configs.clear();
        try {
            File configsDir = new File("configs");
            if (!configsDir.isDirectory()) {
                Files.createDirectory(configsDir.toPath());
                return;
            }
            File[] files = configsDir.listFiles((_, name) -> name.toLowerCase().endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    String name = file.getName();
                    try (InputStream is = new FileInputStream(file)) {
                        this.configs.put(name.substring(0, name.length() - 5), OBJECT_MAPPER.readTree(is));
                    } catch (Exception e) {
                        LOGGER.warn("Cannot read config '{}': ", name, e);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Cannot read configs: ", e);
        }
    }

    private JsonNode createConfig(String id) {
        JsonNode node = OBJECT_MAPPER.createObjectNode();
        try {
            OBJECT_MAPPER.writeValue(Paths.get("configs", id + ".json").toFile(), node);
        } catch (IOException e) {
            LOGGER.error("Failed to create config for service '{}': ", id, e);
        }
        return node;
    }
}
