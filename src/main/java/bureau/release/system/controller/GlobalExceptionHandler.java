package bureau.release.system.controller;

import bureau.release.system.exception.*;
import bureau.release.system.service.dto.ErrorDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.PropertyValueException;
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
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDto("Illegal argument " + exception.getMessage()));
    }

    @ExceptionHandler(OrasException.class)
    public ResponseEntity<ErrorDto> handleOrasException(OrasException exception) {
        log.error("Oras command exec: OrasException: {} from {}", exception.getMessage(), exception.getStackTrace()[0]);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ErrorDto(exception.getMessage()));
    }

    @ExceptionHandler(ClientException.class)
    public ResponseEntity<ErrorDto> handleClientException(ClientException exception) {
        log.error("ClientException: {} from {}", exception.getMessage(), exception.getStackTrace()[0]);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ErrorDto(exception.getMessage()));
    }

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFoundException(ClientNotFoundException exception) {
        log.error("ClientException: NotFoundException: {} from {}", exception.getMessage(), exception.getStackTrace()[0]);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto(exception.getMessage()));
    }

    @ExceptionHandler(ReleaseStreamException.class)
    public ResponseEntity<ErrorDto> handleReleaseStreamException(ReleaseStreamException exception) {
        log.error("ReleaseStreamException: {} from {}", exception.getMessage(), exception.getStackTrace()[0]);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDto(exception.getMessage()));
    }

    @ExceptionHandler(PropertyValueException.class)
    public ResponseEntity<ErrorDto> handlePropertyValueException(PropertyValueException exception) {
        log.error("PropertyValueException: {} from {}", exception.getMessage(), exception.getStackTrace()[0]);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDto(exception.getMessage()));
    }
}
