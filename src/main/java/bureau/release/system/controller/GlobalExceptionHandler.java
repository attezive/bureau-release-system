package bureau.release.system.controller;

import bureau.release.system.exception.*;
import bureau.release.system.service.dto.error.ErrorDto;
import bureau.release.system.service.dto.error.ValidationErrorResponse;
import bureau.release.system.service.dto.error.Violation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import land.oras.exception.OrasException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.PropertyValueException;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.List;

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
                .body(new ErrorDto("Illegal argument: " + exception.getMessage()));
    }

    @ExceptionHandler(OrasException.class)
    public ResponseEntity<ErrorDto> handleOrasException(OrasException exception) {
        log.error("OrasException: {} from {}", exception.getMessage(), exception.getStackTrace()[0]);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDto(exception.getMessage()));
    }

    @ExceptionHandler(ClientException.class)
    public ResponseEntity<ErrorDto> handleClientException(ClientException exception) {
        log.error("ClientException: {} from {}", exception.getMessage(), exception.getStackTrace()[0]);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDto(exception.getMessage()));
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

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolationException(ConstraintViolationException exception) {
        log.error("ConstraintViolationException: {} from {}", exception.getMessage(), exception.getStackTrace()[0]);
        List<Violation> violations = exception.getConstraintViolations().stream()
                .map(
                        violation -> new Violation(
                                violation.getPropertyPath().toString().split("\\.", 2)[1],
                                violation.getMessage()
                        )
                ).toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ValidationErrorResponse(violations));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorDto> handleIOException(IOException exception) {
        log.error("IOException: {} from {}", exception.getMessage(), exception.getStackTrace()[0]);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDto(exception.getMessage()));
    }

    @ExceptionHandler(PSQLException.class)
    public ResponseEntity<ErrorDto> handlePSQLException(PSQLException exception) {
        log.error("PSQLException: {} from {}", exception.getMessage(), exception.getStackTrace()[0]);
        if (exception.getMessage().contains("unique constraint")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDto(exception.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDto(exception.getMessage()));
    }
}
