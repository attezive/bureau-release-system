package bureau.release.system.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/firmware")
@RequiredArgsConstructor
public class FirmwareController {

    @GetMapping
    public String getAllFirmware() {
        return "getAllFirmware";
    }

    @GetMapping("/{firmwareId}")
    public String getFirmware(@PathVariable String firmwareId) {
        return "getFirmware = " + firmwareId;
    }
}
