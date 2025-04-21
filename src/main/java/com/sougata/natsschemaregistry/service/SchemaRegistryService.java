package com.sougata.natsschemaregistry.service;

import com.sougata.natsschemaregistry.model.SchemaBinding;
import com.sougata.natsschemaregistry.store.InMemorySchemaStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SchemaRegistryService {

    private final InMemorySchemaStore store;

    public void registerSchema(SchemaBinding binding) {
        Optional<SchemaBinding> existing = store.findByTopic(binding.getTopic());
        existing.ifPresent(existingBinding -> {
            if (!existingBinding.getDescriptorSha256().equals(binding.getDescriptorSha256())) {
                throw new IllegalStateException("Schema conflict for topic: " + binding.getTopic());
            }
        });
        store.saveBinding(binding);
    }

    public Optional<SchemaBinding> getSchemaForTopic(String topic) {
        return store.findByTopic(topic);
    }

    public Collection<SchemaBinding> getAllBindings() {
        return store.getAll();
    }
}
