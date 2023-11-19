package englishdictionary.server.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

@Service
public class ControllerUtilities {
    public String getCurrentResourcePath(HttpServletRequest request) {
        return "[" + request.getServletPath() + "] - [" + request.getMethod() + "]";
    }

}
