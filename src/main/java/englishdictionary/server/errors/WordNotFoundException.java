package englishdictionary.server.errors;

public class WordNotFoundException extends RuntimeException{
    public WordNotFoundException(Integer id) {
        super("Word with Id " + id + " could not be found");
    }
}
