package io.languify.infra.logging;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleException(
      Exception ex, HttpServletRequest request) {
    HttpStatus status = resolveStatus(ex);

    String method = request != null ? request.getMethod() : "UNKNOWN";
    String path = request != null ? request.getRequestURI() : "unknown";

    log.error(
        "Unhandled HTTP exception | method={} path={} status={}",
        method,
        path,
        status.value(),
        ex);

    return ResponseEntity.status(status).body(Map.of("message", "Something went wrong"));
  }

  private HttpStatus resolveStatus(Exception ex) {
    if (ex instanceof ResponseStatusException responseStatusException) return HttpStatus.valueOf(responseStatusException.getStatusCode().value());

    ResponseStatus responseStatus =
        AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);

    if (responseStatus != null) return responseStatus.value();
    return HttpStatus.INTERNAL_SERVER_ERROR;
  }
}
