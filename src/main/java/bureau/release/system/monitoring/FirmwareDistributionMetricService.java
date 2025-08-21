package bureau.release.system.monitoring;

import bureau.release.system.model.Firmware;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FirmwareDistributionMetricService {
    private final MeterRegistry meterRegistry;

    public void recordFirmware(Firmware firmware) {
        Counter.builder("firmware_distribution_metric")
                .description("Firmware distribution in releases metric")
                .tags(
                        "project", firmware.getOciName().split("/")[0],
                        "repository", firmware.getOciName(),
                        "type", firmware.getFirmwareType().getName())
                .register(meterRegistry)
                .increment();
    }
}
