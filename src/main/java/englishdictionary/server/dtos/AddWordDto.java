package englishdictionary.server.dtos;

import englishdictionary.server.models.Word;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddWordDto {
    private Integer id;
    private String wordlistId;
    private Word word;
}
