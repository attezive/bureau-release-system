package bureau.release.system.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/releases")
public class ReleasesController {

    @GetMapping
    public String getReleases(@RequestParam(required = false, defaultValue = "0") int limit,
                              @RequestParam(required = false, defaultValue = "0") int offset,
                              @RequestParam(required = false) String mission) {
        return "1";
    }

    @GetMapping("/{releaseId}")
    public String getRelease(@PathVariable int releaseId) {
        return "";
    }

    @PostMapping
    public String createRelease(@RequestBody byte[] releaseData) {
        return "";
    }
}
