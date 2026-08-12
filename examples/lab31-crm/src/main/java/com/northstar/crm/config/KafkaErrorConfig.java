package com.northstar.crm.config;

import com.northstar.crm.event.InvalidCustomerEventException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorConfig {

  @Bean
  public CommonErrorHandler kafkaErrorHandler(KafkaTemplate<Object, Object> template) {
    // Bounded retry (2 attempts, 500ms apart); on exhaustion publish to <topic>.DLT.
    DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template);
    DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, new FixedBackOff(500L, 2L));
    // Contract errors never succeed on retry -> straight to DLT, no backoff loop.
    handler.addNotRetryableExceptions(InvalidCustomerEventException.class);
    return handler;
  }
}
