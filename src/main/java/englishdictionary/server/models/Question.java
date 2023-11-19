package englishdictionary.server.models;

import lombok.Data;

import java.util.List;

@Data
public class Question {
    private String id;
    private String name;
    private List<Answer> answers;
}
