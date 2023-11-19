package englishdictionary.server.errors;

public class UserDisableException extends RuntimeException{
    public UserDisableException() {
        super("Could not login, user has been banned");
    }

}
