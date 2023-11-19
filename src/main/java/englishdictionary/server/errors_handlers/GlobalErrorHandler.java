package englishdictionary.server.errors_handlers;

import com.google.api.gax.rpc.NotFoundException;
import com.google.firebase.auth.FirebaseAuthException;
import englishdictionary.server.errors.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.concurrent.ExecutionException;

@RestControllerAdvice
public class GlobalErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            WordlistNotFoundException.class,
            UserNotFoundException.class,
            WordNotFoundException.class,
            DuplicateWordlistException.class
    })
    public ResponseEntity<String> handleNotFoundException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler({
            UserDisableException.class,
            AuthorizationException.class,
            FirebaseAuthException.class
    })
    public ResponseEntity<String> handleUnauthorizedException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
    @ExceptionHandler({
            IllegalArgumentException.class
    })
    public ResponseEntity<String> handleIllegalArgumentException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    @ExceptionHandler({ExecutionException.class, InterruptedException.class})
    public ResponseEntity<String> handleSystemException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("Internal Server Error");
    }

}
