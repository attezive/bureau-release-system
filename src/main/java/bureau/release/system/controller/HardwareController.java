package bureau.release.system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hardware")
public class HardwareController {

    @GetMapping
    public String getAllHardware() {
        return "getAllHardware";
    }

    @GetMapping("/{hardwareId}")
    public String getHardware(@PathVariable int hardwareId) {
        return "getHardware = " + hardwareId;
    }
}
