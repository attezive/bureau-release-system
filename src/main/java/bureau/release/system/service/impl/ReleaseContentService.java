package bureau.release.system.service.impl;

import bureau.release.system.dal.FirmwareDao;
import bureau.release.system.dal.ReleaseContentDao;
import bureau.release.system.dal.ReleaseDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReleaseContentService {
    private final ReleaseDao releaseDao;
    private final ReleaseContentDao releaseContentDao;
    private final FirmwareDao firmwareDao;

}
