package englishdictionary.server.gateways;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import englishdictionary.server.component.verificationCodeTask;
import englishdictionary.server.errors.AuthorizationException;
import englishdictionary.server.errors.UserDisableException;
import englishdictionary.server.errors.UserNotFoundException;
import englishdictionary.server.utils.ControllerUtilities;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.google.firebase.auth.FirebaseAuthException;

import englishdictionary.server.models.User;
import englishdictionary.server.models.UserAuth;
import englishdictionary.server.services.UserService;
import org.springframework.stereotype.Component;

@Component
@RestController
@CrossOrigin(origins = "http://localhost:8088")
@RequestMapping("api/user/")
public class UserController {
    private final verificationCodeTask verificationCodeTask;
    private final UserService userServices;
    private final ControllerUtilities utilFuncs;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    private String getFunctionCall(String functionName, String resource) {
        return "[UserController] call [" + functionName + "] to resource " + resource;
    }

    public UserController(englishdictionary.server.component.verificationCodeTask verificationCodeTask, UserService userServices, ControllerUtilities controllerUtilities) {
        this.verificationCodeTask = verificationCodeTask;
        utilFuncs = controllerUtilities;
        this.userServices = userServices;
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<User> getUser(@PathVariable("id") String id, HttpServletRequest request) {
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("getUser", resource);
        try {
            logger.info(prompt);
            User user = userServices.getUser(id);
            logger.info(prompt + " - Completed");
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserNotFoundException userNotFoundException) {
            logger.error("An error occurred when getting resource " + resource + " - Not Found - \n Error message:\n" + userNotFoundException.getMessage());
            throw userNotFoundException;
        } catch (ExecutionException | InterruptedException e) {
            logger.error("An error occurred when getting resource " + resource + " - Execution interrupted - \n Error message: \n" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/{id}/profile/email")
    public ResponseEntity<String> getUserEmail(@PathVariable("id") String id, HttpServletRequest request) {
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("getUserEmail", resource);
        try {
            logger.info(prompt);
            String email = userServices.getUserEmail(id);
            logger.info(prompt + " - Completed");
            return new ResponseEntity<>(email, HttpStatus.OK);
        } catch (UserNotFoundException userNotFoundException) {
            logger.error("An error occurred when getting resource " + resource + " - Not Found - \n Error message:\n" + userNotFoundException.getMessage());
            throw userNotFoundException;
        } catch (ExecutionException | InterruptedException e) {
            logger.error("An error occurred when getting resource " + resource + " - Execution interrupted - \n Error message: \n" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/profile/fullname")
    public ResponseEntity<String> getUserFullname(@PathVariable("id") String id, HttpServletRequest request) {
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("getUserEmail", resource);
        try {
            logger.info(prompt);
            String fullName = userServices.getUserFullname(id);
            logger.info(prompt + " - Completed");
            return new ResponseEntity<>(fullName, HttpStatus.OK);
        } catch (UserNotFoundException userNotFoundException) {
            logger.error("An error occurred when getting resource " + resource + " - Not Found - \n Error message:\n" + userNotFoundException.getMessage());
            throw userNotFoundException;
        } catch (ExecutionException | InterruptedException e) {
            logger.error("An error occurred when getting resource " + resource + " - Execution interrupted - \n Error message: \n" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/profile/gender")
    public ResponseEntity<Integer> getUserGender(@PathVariable("id") String id, HttpServletRequest request) {
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("getUserEmail", resource);
        try {
            logger.info(prompt);
            Integer gender = userServices.getUserGender(id);
            logger.info(prompt + " - Completed");
            return new ResponseEntity<>(gender, HttpStatus.OK);
        } catch (UserNotFoundException userNotFoundException) {
            logger.error("An error occurred when getting resource " + resource + " - Not Found - \n Error message:\n" + userNotFoundException.getMessage());
            throw userNotFoundException;
        } catch (ExecutionException | InterruptedException e) {
            logger.error("An error occurred when getting resource " + resource + " - Execution interrupted - \n Error message: \n" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/profile/level")
    public ResponseEntity<Integer> getUserLevel(@PathVariable("id") String id, HttpServletRequest request) {
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("getUserEmail", resource);
        try {
            logger.info(prompt);
            Integer level = userServices.getUserLevel(id);
            logger.info(prompt + " - Completed");
            return new ResponseEntity<>(level, HttpStatus.OK);
        } catch (UserNotFoundException userNotFoundException) {
            logger.error("An error occurred when getting resource " + resource + " - Not Found - \n Error message:\n" + userNotFoundException.getMessage());
            throw userNotFoundException;
        } catch (ExecutionException | InterruptedException e) {
            logger.error("An error occurred when getting resource " + resource + " - Execution interrupted - \n Error message: \n" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/profile/occupation")
    public ResponseEntity<Integer> getUserOccupation(@PathVariable("id") String id, HttpServletRequest request) {
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("getUserEmail", resource);
        try {
            logger.info(prompt);
            Integer occupation = userServices.getUserOccupation(id);
            logger.info(prompt + " - Completed");
            return new ResponseEntity<>(occupation, HttpStatus.OK);
        } catch (UserNotFoundException userNotFoundException) {
            logger.error("An error occurred when getting resource " + resource + " - Not Found - \n Error message:\n" + userNotFoundException.getMessage());
            throw userNotFoundException;
        } catch (ExecutionException | InterruptedException e) {
            logger.error("An error occurred when getting resource " + resource + " - Execution interrupted - \n Error message: \n" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/")
    public ResponseEntity<String> login(@RequestBody UserAuth userAuth, HttpServletRequest request){
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("getUserEmail", resource);
        try {
            logger.info(prompt);
            String uid = userServices.getUserId(userAuth);
            logger.info(prompt + " - Completed");
            return ResponseEntity.status(HttpStatus.OK).body(uid);
        } catch (AuthorizationException authorizationException) {
            logger.error("An error occurred when getting resource " + resource + " - Unauthorized - \n Error message: \n" + authorizationException.getMessage());
            throw authorizationException;
        }
        catch (UserDisableException userDisableException){
            logger.error("An error occurred when getting resource " + resource + " - User Has Been Banned - \n Error message: \n" + userDisableException.getMessage());
            throw userDisableException;
        }
        catch (FirebaseAuthException firebaseAuthException){
            logger.error("An error occurred when getting resource " + resource + " - Could not found Email - \n Error message: \n" + firebaseAuthException.getMessage());
            throw new AuthorizationException();
        }
        catch (ExecutionException | InterruptedException e) {
            logger.error("An error occurred when getting resource " + resource + " - Execution interrupted - \n Error message: \n" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/new/{code}")
    public ResponseEntity<String> createUser(@RequestBody User user, @PathVariable("code") String code, HttpServletRequest request) {
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("getUserEmail", resource);
        try {
            System.out.println(code);
            if(verificationCodeTask.checkCode(user.getEmail(), code)){
                logger.info(prompt);
                String uid = userServices.createUser(user);
                logger.info(prompt + " - Completed");
                return ResponseEntity.status(HttpStatus.OK).body(uid);
            }else
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Confirmation Code Is Incorrect.");
        } catch (FirebaseAuthException | AuthorizationException authorizationException) {
            logger.error("An error occurred when getting resource " + resource + " - Unauthorized - \n Error message: \n" + authorizationException.getMessage());
            throw new AuthorizationException();
        } catch (ExecutionException | InterruptedException | RuntimeException e) {
            logger.error("An error occurred when getting resource " + resource + " - Execution interrupted - \n Error message: \n" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/verify")
    public ResponseEntity<String> sendCode(@RequestBody String email) {
        try{
            System.out.println(email);
            if(verificationCodeTask.sendCodeToEmail(email))
                return ResponseEntity.status(HttpStatus.OK).body("Verification code sent.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        catch(MessagingException | UnsupportedEncodingException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUserInfo(@RequestBody UserAuth userAuth, @PathVariable("id") String id, HttpServletRequest request) {
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("getUserEmail", resource);
        try {
            logger.info(prompt);
            if (userServices.updateUserInfo(userAuth, id) != null) {
                logger.info(prompt + " - Completed");
                return ResponseEntity.status(HttpStatus.OK).build();
            }
            logger.error("An error occurred when getting resource " + resource + " - Unauthorized" );
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        catch (FirebaseAuthException | ExecutionException | InterruptedException e) {
            logger.error("An error occurred when getting resource " + resource + " - Unauthorized - \n Error message: \n" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<String> updateUserProfile(@RequestBody User user, @PathVariable("id") String id, HttpServletRequest request) {
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("getUserEmail", resource);
        try {
            logger.info(prompt);
            userServices.updateUserProfile(user, id);
            logger.info(prompt + " - Completed");
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch (InterruptedException | ExecutionException e) {
            logger.error("An error occurred when getting resource " + resource + " - Execution interrupted - \n Error message: \n" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/profile/files/{id}", consumes = {"*/*"})
    public ResponseEntity<String> uploadFile(@RequestBody MultipartFile file, @PathVariable("id") String id, HttpServletRequest request) {
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("getUserEmail", resource);
        try {
            logger.info(prompt);
            userServices.uploadFile(file, id);
            logger.info(prompt + " - Completed");
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (IOException e) {
            logger.error("An error occurred when getting resource " + resource + " - Execution interrupted - \n Error message: \n" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/profile/avatar/{id}")
    public ResponseEntity<String> showFile(@PathVariable("id") String id, HttpServletRequest request) {
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("getUserEmail", resource);
        try {
            logger.info(prompt);
            String response = userServices.getFileAccessToken(id);
            logger.info(prompt + " - Completed");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (AuthorizationException authorizationException) {
            logger.error("An error occurred when getting resource " + resource + " - Unauthorized - \n Error message: \n" + authorizationException.getMessage());
            throw authorizationException;
        } catch (FirebaseAuthException e) {
            logger.error("An error occurred when getting resource " + resource + " - Unauthorized - \n Error message: \n" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/gender")
    public ResponseEntity<Map<String, String>> getGender(){
        try{
            Map<String, String> genderMap = userServices.getAllGender();
            return ResponseEntity.ok(genderMap);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/occupation")
    public ResponseEntity<Map<String, String>> getOccupation(){
        try{
            Map<String, String> genderMap = userServices.getAllOccupation();
            return ResponseEntity.ok(genderMap);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/level")
    public ResponseEntity<Map<String, String>> getLevel(){
        try{
            Map<String, String> genderMap = userServices.getAllLevel();
            return ResponseEntity.ok(genderMap);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/{id}/profile/notify")
    public ResponseEntity<Boolean> getUserNotifyOption(@PathVariable("id") String id){
        try{
            return ResponseEntity.ok(userServices.getUserNotify(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/{id}/profile/notify")
    public ResponseEntity<Boolean> updateUserNotifyOption(@PathVariable("id") String id){
        try{
            return ResponseEntity.ok(userServices.updateNotify(id));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/reset/password/{code}")
    public ResponseEntity<String> resetPassword(@PathVariable("code") String code, @RequestBody UserAuth userAuth){
        try{
            if(verificationCodeTask.checkCode(userAuth.getEmail(), code)){
                userServices.passwordReseter(userAuth.getEmail(), userAuth.getPassword());
                return ResponseEntity.status(HttpStatus.OK).body("password successfully reset");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
        } catch (ExecutionException | FirebaseAuthException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
