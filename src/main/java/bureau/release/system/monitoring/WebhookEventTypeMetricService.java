package bureau.release.system.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebhookEventTypeMetricService {
    private final MeterRegistry meterRegistry;

    public void recordEvent(String type) {
        Counter.builder("webhook_event_type_metric")
                .description("Webhook event type metric")
                .tags("type", type)
                .register(meterRegistry)
                .increment();
    }
}
