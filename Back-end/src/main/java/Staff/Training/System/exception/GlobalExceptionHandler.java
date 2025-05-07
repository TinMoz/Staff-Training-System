package staff.training.system.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

//此為全局異常處理
@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 返回400狀態碼
    @ExceptionHandler(MethodArgumentNotValidException.class) // 捕獲MethodArgumentNotValidException異常
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>(); // 用於存儲錯誤信息
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField(); // 獲取錯誤字段名稱
            String errorMessage = error.getDefaultMessage(); // 獲取錯誤信息
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(RuntimeException.class) // 捕獲RuntimeException異常
    @ResponseStatus(HttpStatus.BAD_REQUEST)// 返回400狀態碼
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}