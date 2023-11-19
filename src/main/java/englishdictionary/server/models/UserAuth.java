package englishdictionary.server.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAuth {
    private String email;
    private String password;
}
