package englishdictionary.server.errors;

public class DuplicateWordlistException extends RuntimeException {
    public DuplicateWordlistException(String name) {
        super("Wordlist with name " + name + " already exists");
    }
}
