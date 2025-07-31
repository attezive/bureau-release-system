package bureau.release.system.controller;

import bureau.release.system.exception.ClientNotFoundException;
import bureau.release.system.exception.OrasException;
import bureau.release.system.service.dto.ErrorDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDto> handleEntityNotFoundException(EntityNotFoundException exception) {
        log.error("Database: EntityNotFoundException: {} from {}", exception.getMessage(), exception.getStackTrace()[0]);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto(exception.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDto> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.error("IllegalArgumentException: {} from {}", exception.getMessage(),  exception.getStackTrace()[0]);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDto(exception.getMessage()));
    }

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFoundException(ClientNotFoundException exception) {
        log.error("FeignClient: NotFoundException: {} from {}", exception.getMessage(), exception.getStackTrace()[0]);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto(exception.getMessage()));
    }

    @ExceptionHandler(OrasException.class)
    public ResponseEntity<ErrorDto> handleOrasException(OrasException exception) {
        log.error("Oras command exec: OrasException: {} from {}", exception.getMessage(), exception.getStackTrace()[0]);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ErrorDto(exception.getMessage()));
    }
}
