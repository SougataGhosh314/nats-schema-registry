package com.sougata.natsschemaregistry.store;

import com.sougata.natsschemaregistry.model.SchemaBinding;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemorySchemaStore {
    private final Map<String, SchemaBinding> bindings = new ConcurrentHashMap<>();

    public Optional<SchemaBinding> findByTopic(String topic) {
        return Optional.ofNullable(bindings.get(topic));
    }

    public void saveBinding(SchemaBinding binding) {
        bindings.put(binding.getTopic(), binding);
    }

    public Collection<SchemaBinding> getAll() {
        return bindings.values();
    }
}
