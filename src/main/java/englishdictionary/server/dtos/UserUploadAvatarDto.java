package englishdictionary.server.dtos;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUploadAvatarDto {
    private MultipartFile file;
    private String userId;
}
