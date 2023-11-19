package englishdictionary.server.models;

import lombok.Data;

@Data
public class Answer {
    private String answer;
    private Boolean isCorrect;
}
