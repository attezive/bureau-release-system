package bureau.release.system.service.dto.error;

import java.util.List;

public record ValidationErrorResponse(List<Violation> violations) {
}
