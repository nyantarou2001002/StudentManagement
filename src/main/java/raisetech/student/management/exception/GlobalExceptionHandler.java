package raisetech.student.management.exception;

import java.util.HashMap;
import java.util.Map;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * リクエストパラメータに適切ではない入力形式で入力した時にエラーメッセージを返すメソッドです。
   *
   * @param ex　例外クラス
   * @return　無効な入力が発生したフィールドとエラーメッセージ
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {

    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
        "無効な入力形式です", errors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }


  @ExceptionHandler(OpenApiResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ErrorResponse> handleResourceNotFoundException(OpenApiResourceNotFoundException ex){
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "リソースが見つかりません", Map.of("例外の詳細", ex.getMessage()));
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }


}
