package com.sougata.natsschemaregistry.controller;

import com.sougata.natsschemaregistry.model.SchemaBinding;
import com.sougata.natsschemaregistry.service.SchemaRegistryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SchemaRegistryController {

    private final SchemaRegistryService service;

    @PostMapping("/schemas")
    public ResponseEntity<String> registerSchema(@RequestBody SchemaBinding binding) {
        service.registerSchema(binding);
        return ResponseEntity.ok("Schema registered");
    }

    @GetMapping("/{topic}")
    public ResponseEntity<SchemaBinding> getSchema(@PathVariable String topic) {
        return service.getSchemaForTopic(topic)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Collection<SchemaBinding> listAll() {
        return service.getAllBindings();
    }
}
