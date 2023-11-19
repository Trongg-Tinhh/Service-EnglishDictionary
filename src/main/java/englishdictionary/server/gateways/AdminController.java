package englishdictionary.server.gateways;

import com.google.api.gax.rpc.NotFoundException;
import com.google.firebase.auth.FirebaseAuthException;
import englishdictionary.server.dtos.UserDto;
import englishdictionary.server.errors.UserNotFoundException;
import englishdictionary.server.models.*;
import englishdictionary.server.services.AdminService;
import englishdictionary.server.services.UserService;
import englishdictionary.server.utils.ControllerUtilities;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/admin/")
public class AdminController {
    @Autowired
    private ControllerUtilities utilFuncs;
    private final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private String getFunctionCall(String functionName, String resource) {
        return "[AdminController] call [" + functionName + "] to resource " + resource;
    }

    @Autowired
    private AdminService adminService;
    @Autowired
    private UserService userService;

    @PostMapping("edit/gender")
    public ResponseEntity<String> editGender(@RequestBody Gender gender, HttpServletRequest request){
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("editGender", resource);
        try{
            logger.info(prompt);
            adminService.editGender(gender.getName(), gender.getDocId());
            return ResponseEntity.ok().build();
        }catch (ExecutionException e) {
            logger.error("An error occurred when getting resource " + resource + " - Execution Error - \n Error message:\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (InterruptedException e) {
            logger.error("An error occurred when getting resource " + resource + " - Interrupted Error - \n Error message:\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("edit/level")
    public ResponseEntity<String> editLevel(@RequestBody Level level, HttpServletRequest request){
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("editLevel", resource);
        try{
            logger.info(prompt);
            adminService.editLevel(level.getName(), level.getDocId());
            return ResponseEntity.ok().build();
        }catch (ExecutionException e) {
            logger.error("An error occurred when getting resource " + resource + " - Execution Error - \n Error message:\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (InterruptedException e) {
            logger.error("An error occurred when getting resource " + resource + " - Interrupted Error - \n Error message:\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("edit/occupation")
    public ResponseEntity<String> editOccupation(@RequestBody Occupation occupation, HttpServletRequest request){
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("editOccupation", resource);
        try{
            logger.info(prompt);
            adminService.editOccupation(occupation.getName(), occupation.getDocId());
            return ResponseEntity.ok().build();
        }catch (ExecutionException e) {
            logger.error("An error occurred when getting resource " + resource + " - Execution Error - \n Error message:\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (InterruptedException e) {
            logger.error("An error occurred when getting resource " + resource + " - Interrupted Error - \n Error message:\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("delete/gender/{docId}")
    public ResponseEntity<String> deleteGender(@PathVariable("docId") String docId, HttpServletRequest request){
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("deleteGender", resource);
        try{
            logger.info(prompt);
            adminService.deleteGender(docId);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            logger.error("An error occurred when getting resource " + resource + " - Error - \n Error message:\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("delete/level/{docId}")
    public ResponseEntity<String> deleteLevel(@PathVariable("docId") String docId, HttpServletRequest request){
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("deleteLevel", resource);
        try{
            logger.info(prompt);
            adminService.deleteLevel(docId);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            logger.error("An error occurred when getting resource " + resource + " - Error - \n Error message:\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("delete/occupation/{docId}")
    public ResponseEntity<String> deleteOccupation(@PathVariable("docId") String docId, HttpServletRequest request){
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("deleteOccupation", resource);
        try{
            logger.info(prompt);
            adminService.deleteOccupation(docId);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            logger.error("An error occurred when getting resource " + resource + " - Error - \n Error message:\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("disable/user/{id}")
    public ResponseEntity<String> setUserActiveState(@PathVariable("id") String id, HttpServletRequest request){
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("deleteUser", resource);
        try{
            logger.info(prompt);
            adminService.deleteUser(id);
            return ResponseEntity.ok().build();

        } catch (ExecutionException  e) {
            logger.error("An error occurred when executing " + resource + " - Execution interrupted - \n Error message: \n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No document to execute");
        } catch (InterruptedException e) {
            logger.error("InterruptedException");
            throw new RuntimeException(e);

        }
    }

    @GetMapping("/get/user/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable String id){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(adminService.getUser(id));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/get/all/user")
    public ResponseEntity<List<UserDto>> getAllUser(){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(adminService.getAllUser());
        } catch (FirebaseAuthException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
