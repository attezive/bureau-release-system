package bureau.release.system.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class FirmwareHookMetricService {

    private final Counter fakeFirmwareCounter;
    private final Counter totalFirmwareCounter;

    public FirmwareHookMetricService(MeterRegistry meterRegistry) {
        fakeFirmwareCounter = Counter.builder("fake_hooked_firmware_metric")
                .description("Webhook payload is not Firmware Metric")
                .tags("endpoint", "firmware/harbor-webhook")
                .register(meterRegistry);

        totalFirmwareCounter = Counter.builder("total_hooked_firmware_metric")
                .description("Any Webhook payload for Metric")
                .tags("endpoint", "firmware/harbor-webhook")
                .register(meterRegistry);
    }

    public void incrementFakeFirmwareMetric() {
        fakeFirmwareCounter.increment();
    }

    public void incrementTotalFirmwareMetric() {
        totalFirmwareCounter.increment();
    }
}
