package englishdictionary.server.errors;

public class WordlistNotFoundException extends RuntimeException{
    public WordlistNotFoundException(String id) {
        super("Wordlist with Id " + id + "could not be found");
    }
}
