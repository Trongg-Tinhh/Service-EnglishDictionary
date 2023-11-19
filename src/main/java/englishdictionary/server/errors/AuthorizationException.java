package englishdictionary.server.errors;

public class AuthorizationException extends RuntimeException{

    public AuthorizationException() {
        super("Could not authorize user");
    }

}
