package englishdictionary.server.gateways;

import englishdictionary.server.dtos.AddWordDto;
import englishdictionary.server.dtos.CreateWordListDto;
import englishdictionary.server.errors.DuplicateWordlistException;
import englishdictionary.server.errors.WordNotFoundException;
import englishdictionary.server.errors.WordlistNotFoundException;
import englishdictionary.server.models.Word;
import englishdictionary.server.models.Wordlist;
import englishdictionary.server.services.WordlistService;
import englishdictionary.server.utils.ControllerUtilities;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/wordlists")
public class WordListController {
    @Autowired
    private WordlistService wordlistService;

    @Autowired
    private ControllerUtilities utilFuncs;

    private Logger logger = LoggerFactory.getLogger(WordListController.class);

    private String getFunctionCall(String functionName, String resource) {
        return "[WordlistController] call [" + functionName + "] to resource " + resource;
    }

    @GetMapping("/{user-id}")
    public ResponseEntity<List<Wordlist>> getAllUserWordLists(@PathVariable("user-id") String userId, HttpServletRequest request) {
        try {
            String prompt = getFunctionCall("getAllUserWordLists", utilFuncs.getCurrentResourcePath(request));
            logger.info(prompt);
            List<Wordlist> wordLists = wordlistService.getAllUserWordLists(userId);
            logger.info(prompt + " - Completed");
            return new ResponseEntity<>(wordLists, HttpStatus.OK);
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error when getting resource " + utilFuncs.getCurrentResourcePath(request) + " - Execution failed with message [" + e.getMessage() + "]");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{user-id}/{wordlist-id}")
    public ResponseEntity<Wordlist> getUserWordlist(@PathVariable("user-id") String userId, @PathVariable("wordlist-id") String wordlistId, HttpServletRequest request) {
        try {
            String prompt = getFunctionCall("getUserWordlist", utilFuncs.getCurrentResourcePath(request));
            logger.info(prompt);
            Wordlist wordlist = wordlistService.getWordlistById(wordlistId);
            logger.info(prompt + " - Completed");
            return new ResponseEntity<>(wordlist, HttpStatus.OK);
        }
        catch (WordlistNotFoundException wordlistNotFoundException) {
            logger.error("Error occurred when getting resource " + utilFuncs.getCurrentResourcePath(request) + " - Not exists");
            throw wordlistNotFoundException;
        }
        catch (ExecutionException | InterruptedException e) {
            logger.error("Error when getting resource " + utilFuncs.getCurrentResourcePath(request) + " - Execution failed with message [" + e.getMessage() + "]");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public List<Wordlist> searchForWordList(@RequestParam(name = "name") String name, @RequestParam(name = "word") String word, HttpServletRequest request) {
        try {
            String prompt = getFunctionCall("searchForWordlist", utilFuncs.getCurrentResourcePath(request));
            logger.info(prompt);
            List<Wordlist> result = wordlistService.searchForWordlist(name, word);
            logger.info(prompt + " - Completed");
            return result;
        }
        catch (WordNotFoundException | WordlistNotFoundException notFoundException) {
            logger.error("Error occurred when getting resource " + utilFuncs.getCurrentResourcePath(request) + " - Not exists");
            throw notFoundException;
        }
        catch (ExecutionException | InterruptedException e) {
            logger.error("Error when getting resource " + utilFuncs.getCurrentResourcePath(request) + " - Execution failed with message [" + e.getMessage() + "]");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/{wordlist-id}/word/{word-id}")
    public Word getWordInWordlist(
            @PathVariable("wordlist-id") String wordlistId,
            @PathVariable("word-id") Integer wordId,
            HttpServletRequest request
    ) {
        try {
            String prompt = getFunctionCall("getWordInWordlist", utilFuncs.getCurrentResourcePath(request));
            logger.info(prompt);
            Word word = wordlistService.getWordlistWord(wordlistId, wordId);
            logger.info(prompt + " - Completed");
            return word;
        }
        catch (WordlistNotFoundException | WordNotFoundException notFoundException) {
            logger.error("Error when getting resource " + utilFuncs.getCurrentResourcePath(request) + " - Not exists");
            throw notFoundException;
        }
        catch (ExecutionException | InterruptedException e) {
            logger.error("Error when getting resource " + utilFuncs.getCurrentResourcePath(request) + " - Execution failed with message [" + e.getMessage() + "]");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "")
    public ResponseEntity<Wordlist> createWordlist(@RequestBody CreateWordListDto wordListDto, HttpServletRequest request) {
        try {
            String prompt = getFunctionCall("createWordlist", utilFuncs.getCurrentResourcePath(request));
            logger.info(prompt);
            Wordlist wordlist = wordlistService.createWordlist(wordListDto.getName(),wordListDto.getUserId());
            logger.info(prompt + " - Completed");
            return new ResponseEntity<>(wordlist, HttpStatus.CREATED);
        }
        catch (DuplicateWordlistException duplicateWordlistException) {
            logger.error("Error when creating resource " + utilFuncs.getCurrentResourcePath(request) + " - Already exists");
            throw duplicateWordlistException;
        }
        catch (InvalidParameterException invalidParameterException) {
            logger.error("Error when creating resource " + utilFuncs.getCurrentResourcePath(request) + " - Invalid param value");
            throw invalidParameterException;
        }
        catch (ExecutionException | InterruptedException e) {
            logger.error("Error when creating resource " + utilFuncs.getCurrentResourcePath(request) + " - Execution failed with message [" + e.getMessage() + "]");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public HttpStatus deleteWordlist(@PathVariable("id") String id, HttpServletRequest request) {
        try {
            String prompt = getFunctionCall("deleteWordlist", utilFuncs.getCurrentResourcePath(request));
            logger.info(prompt);
            if (wordlistService.deleteWordlist(id)) {
                logger.info(prompt + " - Completed");
                return HttpStatus.ACCEPTED;
            }
        }
        catch (ExecutionException | InterruptedException e) {
            logger.error("Error when deleting resource " + utilFuncs.getCurrentResourcePath(request) + " - Execution failed with message [" + e.getMessage() + "]");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.error("Error when deleting resource " + utilFuncs.getCurrentResourcePath(request) + " - Execution interrupted");
        return HttpStatus.BAD_REQUEST;
    }

    @DeleteMapping("/{wordlist-id}/word/{word-id}")
    public HttpStatus deleteWordlistWord(
            @PathVariable("wordlist-id") String wordlistId,
            @PathVariable("word-id") Integer wordId,
            HttpServletRequest request
    ) {
        try {
            String prompt = getFunctionCall("deleteWordlist", utilFuncs.getCurrentResourcePath(request));
            logger.info(prompt);
            if (wordlistService.removeWordlistWord(wordlistId, wordId)) {
                logger.info(prompt + " - Completed");
                return HttpStatus.OK;
            }
            logger.error("Error when deleting resource " + utilFuncs.getCurrentResourcePath(request) + " - Execution interrupted");
            return HttpStatus.BAD_REQUEST;
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error when deleting resource " + utilFuncs.getCurrentResourcePath(request) + " - Execution failed with message [" + e.getMessage() + "]");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/name/{wordlistId}/{name}")
    public HttpStatus renameWordlist(
            @PathVariable("wordlistId") String wordlistId,
            @PathVariable("name") String name,
            HttpServletRequest request) {
        try {
            String prompt = getFunctionCall("renameWordlist", utilFuncs.getCurrentResourcePath(request));
            logger.info(prompt);
            if (wordlistService.renameWordList(wordlistId, name)) {
                logger.info(prompt + " - Completed");
                return HttpStatus.OK;
            }
            logger.error("Error when changing resource " + utilFuncs.getCurrentResourcePath(request) + " - Execution interrupted");
            return HttpStatus.BAD_REQUEST;
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error when changing resource " + utilFuncs.getCurrentResourcePath(request) + " - Execution failed with message [" + e.getMessage() + "]");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/word")
    public HttpStatus addWordToWordlist(@RequestBody AddWordDto dto, HttpServletRequest request) {
        try {
            String prompt = getFunctionCall("addWordToWordlist", utilFuncs.getCurrentResourcePath(request));
            logger.info(prompt);
            if (wordlistService.addWordToWordlist(dto.getWordlistId(), dto.getWord())) {
                logger.info(prompt + " - Completed");
                return HttpStatus.CREATED;
            }
            logger.error("Error when adding resource " + utilFuncs.getCurrentResourcePath(request) + " - Execution interrupted");
            return HttpStatus.BAD_REQUEST;
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error when adding resource " + utilFuncs.getCurrentResourcePath(request) + " - Execution failed with message [" + e.getMessage() + "]");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
