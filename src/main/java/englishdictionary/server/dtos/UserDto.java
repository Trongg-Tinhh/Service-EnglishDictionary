package englishdictionary.server.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String email;
    private String fullName;
    private Integer gender;
    private Integer level;
    private Integer occupation;
    private String id;
    private Boolean active;
}