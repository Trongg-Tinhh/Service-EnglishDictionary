package englishdictionary.server.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateWordListDto {
    private String name;
    private String userId;
}
