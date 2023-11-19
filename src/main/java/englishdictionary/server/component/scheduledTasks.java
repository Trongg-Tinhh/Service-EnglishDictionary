package englishdictionary.server.component;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.FirestoreClient;
import englishdictionary.server.gateways.AdminController;
import englishdictionary.server.models.User;
import englishdictionary.server.services.AdminService;
import englishdictionary.server.services.UserService;
import englishdictionary.server.utils.ControllerUtilities;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class scheduledTasks {
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserService userService;
    @Autowired
    private JavaMailSender emailSender;
    final int oneDay = 24 * 60 * 60 * 1000;

    @Autowired
    private ControllerUtilities utilFuncs;
    private final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private String getFunctionCall(String functionName, String resource) {
        return "[ScheduledTasks] call [" + functionName + "] to resource " + resource;
    }

    @Scheduled(fixedRate = oneDay)
    public void runTask() throws ExecutionException, InterruptedException, FirebaseAuthException, MessagingException {
        List<String> userIds = adminService.getAllUserId();
        for (String userId : userIds) {
            try {
                if(userService.getUserNotify(userId)){
                    notifier(userId);
                }
            }catch (NullPointerException e){
                System.out.println("Task Fail at "+ userId + "-Notify is null");
            }
        }
        System.out.println("Task executed");
    }
    public void notifier(String id) throws ExecutionException, InterruptedException, FirebaseAuthException, MessagingException {
        try{
            Timestamp timestamp = userService.getDate(id);
            long thresholdSeconds = 7 * 24 * 60 * 60; // 7 days converted to seconds
            long currentTimeSeconds = Instant.now().getEpochSecond();
            long documentTimeSeconds = timestamp.toDate().toInstant().getEpochSecond();
            long timeDifference = currentTimeSeconds - documentTimeSeconds;
            if (timeDifference > thresholdSeconds) {
                sendNotification(id);
                System.out.println("Message Sent For "+userService.getUserEmail(id));
            }
        }catch (NullPointerException ignored){
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendNotification(String id) throws ExecutionException, InterruptedException, MessagingException, UnsupportedEncodingException {
        String userEmail = userService.getUserEmail(id);
        String subject = "Hi "+ userService.getUserFullname(id)+"! Got 5 minutes? Time for a tiny lesson.";
        String message = "<html><body><h1>It is time for us to get back on track!!!!!</h1></body></html>";

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        messageHelper.setTo(userEmail);
        messageHelper.setSubject(subject);
        messageHelper.setText(message, true );
        messageHelper.setFrom(new InternetAddress("23trongtinh@gmail.com", "Dictionyari"));

        emailSender.send(mimeMessage);
    }
}
