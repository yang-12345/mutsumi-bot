package io.github.rikkakawaii0612.mutsumi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.rikkakawaii0612.mutsumi.api.ModuleContext;
import io.github.rikkakawaii0612.mutsumi.api.ServiceModule;
import io.github.rikkakawaii0612.mutsumi.api.contact.MutsumiBot;
import io.github.rikkakawaii0612.mutsumi.api.service.Service;
import io.github.rikkakawaii0612.mutsumi.api.service.ServiceLocator;
import io.github.rikkakawaii0612.mutsumi.impl.LocalMutsumiBotImpl;
import io.github.rikkakawaii0612.mutsumi.impl.ModuleContextImpl;
import io.github.rikkakawaii0612.mutsumi.impl.MutsumiBotImpl;
import io.github.rikkakawaii0612.mutsumi.impl.ServiceLocatorImpl;
import org.pf4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;

public class ModuleManager extends DefaultPluginManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("ModuleManager");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final ServiceLocatorImpl serviceLocator = new ServiceLocatorImpl(this);
    private final MutsumiBot bot;

    public ModuleManager() {
        super(Paths.get("modules"));
        boolean localBot = false;
        try (InputStream is = new FileInputStream(Paths.get("config", "app.json").toFile())) {
            JsonNode jsonNode = OBJECT_MAPPER.readTree(is);
            if (jsonNode.has("localBot") && jsonNode.get("localBot").asBoolean()) {
                localBot = true;
            }
        } catch (FileNotFoundException _) {
            LOGGER.warn("Main app config 'app.json' does not exist");
        } catch (Exception e) {
            LOGGER.warn("Cannot read main app config 'app.json': ", e);
        }
        this.bot = localBot ? new LocalMutsumiBotImpl(this) : new MutsumiBotImpl(this);
    }

    @Override
    protected PluginFactory createPluginFactory() {
        return new ModuleFactory();
    }

    @Override
    protected ExtensionFactory createExtensionFactory() {
        return new SingletonExtensionFactory(this);
    }

    public void loadModules() {
        this.addPluginStateListener(_ -> this.serviceLocator.update());
        this.loadPlugins();
        this.startPlugins();

        int servicesCount = 0;
        for (PluginWrapper pluginWrapper : this.getPlugins()) {
            String id = pluginWrapper.getPluginId();
            List<Service> services = this.getExtensions(Service.class, id);
            if (!services.isEmpty()) {
                try {
                    for (Service service : services) {
                        service.initialize((ServiceModule) pluginWrapper.getPlugin());
                        servicesCount++;
                    }
                } catch (Exception e) {
                    LOGGER.error("Failed to load services. All services will not be loaded: ", e);
                    this.stopPlugins();
                    this.unloadPlugins();
                    return;
                }
            }
        }

        LOGGER.info("Successfully loaded {} service(s).", servicesCount);
    }

    public MutsumiBot getBot() {
        return bot;
    }

    public class ModuleFactory implements PluginFactory {
        public ModuleFactory() {
        }

        @Override
        public Plugin create(PluginWrapper wrapper) {
            String pluginClassName = wrapper.getDescriptor().getPluginClass();

            Class<?> clazz;
            try {
                clazz = wrapper.getPluginClassLoader().loadClass(pluginClassName);
            } catch (ClassNotFoundException e) {
                LOGGER.error(e.getMessage(), e);
                return null;
            }

            try {
                return (Plugin) clazz.getDeclaredConstructor(ModuleContext.class)
                        .newInstance(new ModuleContextImpl(wrapper.getPluginId(), serviceLocator));
            } catch (Exception e) {
                LOGGER.error("Failed to create module {}", clazz.getSimpleName(), e);
                return null;
            }
        }
    }
}
