package bureau.release.system.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/firmware")
@RequiredArgsConstructor
public class FirmwareController {

    @GetMapping
    public String getFirmware(@RequestParam(required = false, defaultValue = "0") int limit,
                              @RequestParam(required = false, defaultValue = "0") int offset) {
        return "";
    }

    @GetMapping("/{firmwareId}/versions")
    public String getFirmwareVersions(@PathVariable int firmwareId) {
        // TODO Что есть версия прошивки? У нас под каждую прошивку свой репозиторий внутри проекта, где ее версии?
        return "";
    }

    @PostMapping
    public String createFirmware(@RequestBody byte[] firmwareData) {
        return "";
    }
}
