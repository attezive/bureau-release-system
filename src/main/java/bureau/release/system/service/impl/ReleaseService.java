package bureau.release.system.service.impl;

import bureau.release.system.dal.ReleaseDao;
import bureau.release.system.dal.ReleaseStatusDao;
import bureau.release.system.model.Release;
import bureau.release.system.model.ReleaseStatus;
import bureau.release.system.service.dto.ReleaseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReleaseService {
    private final ReleaseDao releaseDao;
    private final ReleaseStatusDao releaseStatusDao;
    private final ReleaseContentService releaseContentService;

    @Transactional
    public ReleaseDto createRelease(ReleaseDto releaseDto) {
        Release release = Release
                .builder()
                .name(releaseDto.getName())
                //TODO реализовать статусы
                .status(releaseStatusDao.findById(1)
                        .orElseThrow(() -> new EntityNotFoundException("Firmware not found")))
                .ociName(releaseDto.getOciName())
                .reference(releaseDto.getReference())
                .releaseDate(releaseDto.getReleaseDate())
                .releaseContents(new ArrayList<>())
                .build();
        return new ReleaseDto(releaseDao.save(release), releaseDto.getContentIds());
    }



    @Transactional
    public List<ReleaseStatus> getReleaseStatuses(){
        return releaseStatusDao.findAll();
    }

}
