package com.digitalheroes.golfcharity;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiContractFileTest {

    @Test
    void openApiContractFileShouldContainCriticalPaths() throws IOException {
        Path specPath = Path.of("..", "docs", "openapi.yaml").normalize();
        assertThat(Files.exists(specPath)).isTrue();

        String yamlContent = Files.readString(specPath);
        Map<String, Object> document = new Yaml().load(yamlContent);

        assertThat(document).containsKey("paths");
        @SuppressWarnings("unchecked")
        Map<String, Object> paths = (Map<String, Object>) document.get("paths");

        assertThat(paths)
                .containsKeys(
                        "/auth/register",
                        "/auth/login",
                        "/subscriptions/checkout-session",
                        "/subscriptions/activate",
                        "/scores"
                );

        assertThat(document).containsKey("components");
    }
}
