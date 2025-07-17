package bureau.release.system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/firmware")
public class FirmwareController {

    @GetMapping
    public String getAllFirmware() {
        return "getAllFirmware";
    }

    @GetMapping("/{firmwareId}")
    public String getFirmware(@PathVariable int firmwareId) {
        return "getFirmware = " + firmwareId;
    }
}
