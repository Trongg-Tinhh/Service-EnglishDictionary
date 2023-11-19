package englishdictionary.server.models;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class User {
    private String email;
    private String fullName;
    private Integer gender;
    private Integer level;
    private Integer occupation;
    private String password;
}
