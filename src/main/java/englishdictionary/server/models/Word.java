package englishdictionary.server.models;

import lombok.Data;

@Data
public class Word {
    private Integer id;
    private String word;
    private String definition;
}
