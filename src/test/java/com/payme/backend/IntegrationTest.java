package com.payme.backend;

import com.payme.backend.config.AsyncSyncConfiguration;
import com.payme.backend.config.EmbeddedElasticsearch;
import com.payme.backend.config.EmbeddedRedis;
import com.payme.backend.config.EmbeddedSQL;
import com.payme.backend.config.JacksonConfiguration;
import com.payme.backend.config.TestSecurityConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = { PaymebackendApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class, TestSecurityConfiguration.class }
)
@EmbeddedRedis
@EmbeddedElasticsearch
@EmbeddedSQL
public @interface IntegrationTest {
}
