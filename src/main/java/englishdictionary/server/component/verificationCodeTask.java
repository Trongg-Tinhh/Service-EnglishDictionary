package englishdictionary.server.component;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import englishdictionary.server.services.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class verificationCodeTask{
    @Autowired
    private JavaMailSender emailSender;
    public  String verificationCodeFactory() {
        int codeLength = 6;
        String characters = "0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < codeLength; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        return code.toString();
    }
    public void saveCode(String userEmail, String code){
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection("verify_code").document(userEmail);
        Map<String, Object> data = new HashMap<>();
        data.put("code", code);
        ApiFuture<WriteResult> result = documentReference.set(data);
        try {
            result.get();
            System.out.println("Code saved successfully for user: " + userEmail);
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error saving code: " + e.getMessage());
        }
    }
    @Async
    public void scheduleDocumentDeletion(String documentPath) {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection("verify_code").document(documentPath);
        Date currentTime = new Date();
        Date deletionTime = new Date(currentTime.getTime() + TimeUnit.MINUTES.toMillis(2));

        Map<String, Object> data = new HashMap<>();
        data.put("deleteAt", deletionTime);

        ApiFuture<WriteResult> result = documentReference.set(data, SetOptions.merge());

        try {
            result.get();
            System.out.println("Deletion scheduled for document: " + documentPath);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    deleteDocument(documentPath);
                }
            }, deletionTime);
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error scheduling document deletion: " + e.getMessage());
        }
    }
    private void deleteDocument(String documentPath) {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection("verify_code").document(documentPath);

        ApiFuture<WriteResult> result = documentReference.delete();

        try {
            result.get();
            System.out.println("verify code for [ "+ documentPath +" ] deleted ");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error deleting code for [" + e.getMessage()+"]");
        }
    }
    public boolean sendCodeToEmail(String userEmail) throws MessagingException, UnsupportedEncodingException, InterruptedException {
        UserService userService = new UserService();
        /*if (!userService.isUserExist(userEmail))
            return false;*/
        String code = verificationCodeFactory();
        String subject = "This is your authorized code. Do not share !!!!!";
        String message = "<html><body><h1>"+code+"</h1><p>This code only valid for 2 minutes.<p></body></html>";
        saveCode(userEmail, code);

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        messageHelper.setTo(userEmail);
        messageHelper.setSubject(subject);
        messageHelper.setText(message, true );
        messageHelper.setFrom(new InternetAddress("23trongtinh@gmail.com", "Dictionyari"));

        emailSender.send(mimeMessage);
        scheduleDocumentDeletion(userEmail);
        return true;
    }

    public Boolean checkCode(String email, String code) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentSnapshot documentReference = dbFirestore.collection("verify_code").document(email).get().get();
        if(documentReference.exists()){
            System.out.println(documentReference.getString("code"));
            return Objects.equals(documentReference.getString("code"), code);
        }
        return false;
    }
}
