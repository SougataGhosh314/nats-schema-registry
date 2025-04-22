package com.sougata.natsschemaregistry.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sougata.natsschemaregistry.model.SchemaBinding;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class FileBasedSchemaStore {

    private final Map<String, SchemaBinding> bindings = new ConcurrentHashMap<>();

    @Value("${registry.store.file:schemas.json}")
    private String storeFile;

    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    /**
     * Load schema bindings from a local file (JSON) at startup.
     */
    @PostConstruct
    public void loadFromFile() {
        try {
            File file = new File(storeFile);
            if (!file.exists()) {
                log.info("Schema store file does not exist yet: {}", storeFile);
                return;
            }

            List<SchemaBinding> list = mapper.readValue(file, new TypeReference<>() {});
            list.forEach(binding -> bindings.put(binding.getTopic(), binding));
            log.info("Loaded {} schema bindings from file {}", bindings.size(), storeFile);
        } catch (Exception e) {
            log.warn("Failed to load schema bindings from file {}: {}", storeFile, e.getMessage());
        }
    }

    /**
     * Save the schema binding and persist the entire store to file.
     */
    public synchronized void saveBinding(SchemaBinding binding) {
        bindings.put(binding.getTopic(), binding);
        persistToFile();
    }

    /**
     * Find a schema binding by topic.
     */
    public Optional<SchemaBinding> findByTopic(String topic) {
        return Optional.ofNullable(bindings.get(topic));
    }

    /**
     * Get all stored schema bindings.
     */
    public Collection<SchemaBinding> getAll() {
        return bindings.values();
    }

    /**
     * Write the current in-memory store to a local JSON file.
     */
    private void persistToFile() {
        try {
            mapper.writeValue(new File(storeFile), bindings.values());
            log.debug("Schema store successfully persisted to file {}", storeFile);
        } catch (Exception e) {
            log.warn("Failed to persist schema store to file {}: {}", storeFile, e.getMessage());
        }
    }
}
