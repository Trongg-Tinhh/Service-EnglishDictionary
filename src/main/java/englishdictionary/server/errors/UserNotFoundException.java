package englishdictionary.server.errors;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String id) {
        super("User with Id " + id + " could not be found");
    }
}
