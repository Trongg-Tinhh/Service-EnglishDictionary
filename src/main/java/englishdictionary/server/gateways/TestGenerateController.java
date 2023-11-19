package englishdictionary.server.gateways;

import englishdictionary.server.models.Question;
import englishdictionary.server.services.TestGenerateService;
import englishdictionary.server.utils.ControllerUtilities;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.http.client.HttpResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/tests")
public class TestGenerateController {

    @Autowired
    private TestGenerateService testGenerateService;
    @Autowired
    private ControllerUtilities utilFuncs;
    private final Logger logger = LoggerFactory.getLogger(TestGenerateController.class);

    @GetMapping("/questions")
    public ResponseEntity<List<Question>> getQuestions(@RequestParam(value = "number", required = false) Integer numberOfQuestions, HttpServletRequest request) {
        try {
            List<Question> questions = testGenerateService.getListQuestions(numberOfQuestions);
            return ResponseEntity.status(HttpStatus.OK).body(questions);
        } catch (ExecutionException | InterruptedException e) {
            logger.error("An error occurred when getting resource " + utilFuncs.getCurrentResourcePath(request) + " with message:\n " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
