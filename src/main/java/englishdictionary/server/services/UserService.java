package englishdictionary.server.services;

import java.io.IOException;
import java.io.InputStream;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import englishdictionary.server.errors.UserDisableException;
import englishdictionary.server.models.User;
import englishdictionary.server.models.UserAuth;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import englishdictionary.server.errors.AuthorizationException;
import englishdictionary.server.errors.UserNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import englishdictionary.server.EnglishDictionaryServerApplication;

@Service
public class UserService {
    private Firestore dbfirestore;

    // ==================UpLoadFile=====================
    public Boolean uploadFile(MultipartFile file, String id) throws IOException {
        String bucketName = "englishdictionary-b3b23.appspot.com";
        InputStream serviceAccount = EnglishDictionaryServerApplication.class.getResourceAsStream("/service_account_key.json");
        String filename = id + ".jpg";

        Path tempFile = Files.createTempFile(id, filename);
        Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

        StorageOptions options = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
        Storage storage = options.getService();
        BlobId blobId = BlobId.of(bucketName, filename);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, Files.readAllBytes(tempFile));
        Files.delete(tempFile);

        return true;
    }

    public String getFileAccessToken(String id) throws FirebaseAuthException {
        String bucketName = "englishdictionary-b3b23.appspot.com";
        String fullFilePath = "gs://" + bucketName + "/" + id;
        String token = FirebaseAuth.getInstance().createCustomToken(fullFilePath);
        String url = "https://firebasestorage.googleapis.com/v0/b/englishdictionary-b3b23.appspot.com/o/" + id + ".jpg" + "?alt=media&token=" + token;
        return url;
    }

    //====================================================
//======================UserProfileInformation==================
    public Boolean isUserExist(String email){
        try {
            UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
            return true;
        } catch (FirebaseAuthException e) {
            return false;
        }
    }
    public User getUser(String userId) throws ExecutionException, InterruptedException, UserNotFoundException {
        dbfirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbfirestore.collection("users").document(userId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        User user;
        if (!document.exists()) {
            throw new UserNotFoundException(userId);
        }
        user = document.toObject(User.class);
        return user;
    }

    public String getUserId(UserAuth userAuth) throws FirebaseAuthException, ExecutionException, InterruptedException, AuthorizationException, UserDisableException{
        UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(userAuth.getEmail());
        if (userRecord == null)
            throw new AuthorizationException();
        String uid = userRecord.getUid();
        dbfirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbfirestore.collection("users").document(uid);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        String storedPass = document.getString("password");
        String hashedPass = hashPassword(userAuth.getPassword());

        if (MessageDigest.isEqual(storedPass.getBytes(StandardCharsets.UTF_8), hashedPass.getBytes(StandardCharsets.UTF_8)) ) {

            if(document.getBoolean("active") == false){
                throw new UserDisableException();
            }

            Map<String, Object> data = new HashMap<>();
            data.put("lastLog", com.google.cloud.Timestamp.now());
            WriteResult writeResult = documentReference.update(data).get();
            return uid;
        }
        throw new AuthorizationException();
    }

    public String getUserEmail(String userId) throws ExecutionException, InterruptedException, UserNotFoundException {
        User user = getUser(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        return user.getEmail();
    }

    public String getUserFullname(String userId) throws ExecutionException, InterruptedException, UserNotFoundException {
        User user = getUser(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        return user.getFullName();
    }

    public Integer getUserGender(String userId) throws ExecutionException, InterruptedException, UserNotFoundException {
        User user = getUser(userId);

        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        return user.getGender();
    }

    public Boolean getUserNotify(String id) throws ExecutionException, InterruptedException, UserNotFoundException {
        dbfirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbfirestore.collection("users").document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            Boolean notify = document.getBoolean("notify");
            return notify;
        }
        return false;
    }
    public Boolean isUserActive(String id) throws ExecutionException, InterruptedException {
        dbfirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbfirestore.collection("users").document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            Boolean notify = document.getBoolean("active");
            return notify;
        }
        return false;
    }
    public Boolean updateNotify(String id) throws ExecutionException, InterruptedException {
        dbfirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbfirestore.collection("users").document(id);
        Boolean updatedNotify = !getUserNotify(id);
        WriteResult write = documentReference.update("notify", updatedNotify).get();
        return updatedNotify;
    }
    public Integer getUserLevel(String userId) throws ExecutionException, InterruptedException, UserNotFoundException {
        User user = getUser(userId);

        if (user == null) {
            throw new UserNotFoundException(userId);
        }

        return user.getGender();
    }

    public Integer getUserOccupation(String userId) throws ExecutionException, InterruptedException, UserNotFoundException {
        User user = getUser(userId);

        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        return user.getOccupation();
    }

    private String hashPassword(String password) throws RuntimeException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("An unexpected error occurred");
        }
    }

    public Timestamp getDate(String id) throws ExecutionException, InterruptedException {
        dbfirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbfirestore.collection("users").document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            Timestamp timestamp = document.getTimestamp("lastLog");
            return timestamp;
        }
        return null;
    }



    //===============================================================
//========================Data===================================
    public Map<String, String> getAllOccupation() throws ExecutionException, InterruptedException {
        dbfirestore = FirestoreClient.getFirestore();
        CollectionReference gendersCollection = dbfirestore.collection("occupations");
        ApiFuture<QuerySnapshot> future = gendersCollection.get();
        QuerySnapshot querySnapshot = future.get();

        Map<String, String> occupationMap = new HashMap<>();
        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            String docId = document.getId();
            String gender = document.getString("name");
            occupationMap.put(docId, gender);
        }
        return occupationMap;
    }

    public Map<String, String> getAllLevel() throws ExecutionException, InterruptedException {
        dbfirestore = FirestoreClient.getFirestore();
        CollectionReference gendersCollection = dbfirestore.collection("levels");
        ApiFuture<QuerySnapshot> future = gendersCollection.get();
        QuerySnapshot querySnapshot = future.get();

        Map<String, String> levelMap = new HashMap<>();
        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            String docId = document.getId();
            String gender = document.getString("name");
            levelMap.put(docId, gender);
        }
        return levelMap;
    }

    public Map<String, String> getAllGender() throws ExecutionException, InterruptedException {
        dbfirestore = FirestoreClient.getFirestore();
        CollectionReference gendersCollection = dbfirestore.collection("genders");
        ApiFuture<QuerySnapshot> future = gendersCollection.get();
        QuerySnapshot querySnapshot = future.get();

        Map<String, String> genderMap = new HashMap<>();
        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            String docId = document.getId();
            String gender = document.getString("name");
            genderMap.put(docId, gender);
        }
        return genderMap;
    }

    //=========================UserAction===============================
    public String createUser(User user) throws FirebaseAuthException, ExecutionException, InterruptedException, RuntimeException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        String hashedPassword = hashPassword(user.getPassword());
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(user.getEmail())
                .setPassword(user.getPassword())
                .setEmailVerified(true)
                .setDisabled(false);
        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        String uid = userRecord.getUid();
        user.setPassword(hashedPassword);
        DocumentReference userDocRef = dbFirestore.collection("users").document(uid);
        ApiFuture<WriteResult> writeResult = userDocRef.set(user);
        writeResult.get();

        if (!writeResult.isDone())
            throw new RuntimeException("An unexpected error occurred");
        Map<String, Object> data = new HashMap<>();
        data.put("lastLog", com.google.cloud.Timestamp.now());
        data.put("notify", true);
        data.put("active", true);
        WriteResult write = userDocRef.update(data).get();
        return uid;
    }

    public String updateUserInfo(UserAuth userAuth, String id) throws FirebaseAuthException, ExecutionException, InterruptedException {
        UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(id)
                .setEmail(userAuth.getEmail())
                .setPassword(userAuth.getPassword());
        UserRecord userRecord = FirebaseAuth.getInstance().updateUser(request);
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection("users").document(id);
        documentReference.update("email", userAuth.getEmail());
        documentReference.update("password", hashPassword(userAuth.getPassword()));
        return userRecord.getUid();
    }

    public Boolean passwordReseter(String email, String password) throws FirebaseAuthException{
        Boolean isExist = isUserExist(email);
        if(isExist){
            UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
            userRecord.updateRequest().setPassword(password);
            String id = userRecord.getUid();
            dbfirestore = FirestoreClient.getFirestore();
            DocumentReference documentReference = dbfirestore.collection("users").document(id);
            documentReference.update("password", hashPassword(password));
            return true;
        }
        return false;
    }
    public Boolean updateUserProfile(User user, String id) throws ExecutionException, InterruptedException {

        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentSnapshot documentSnapshot = dbFirestore.collection("users").document(id).get().get();
        if (!documentSnapshot.exists())
            throw new UserNotFoundException(id);

        DocumentReference documentReference = documentSnapshot.getReference();
        String fullName = user.getFullName();
        Integer gender = user.getGender();
        Integer level = user.getLevel();
        Integer occupation = user.getOccupation();

        if (fullName != null)
            updateUserProfileFullName(documentReference, fullName);
        if (gender != null)
            updateUserProfileGender(documentReference, gender);
        if (level != null)
            updateUserProfileLevel(documentReference, level);
        if (occupation != null)
            updateUserProfileOccupation(documentReference, occupation);

        return true;
    }


    private void updateUserProfileFullName(DocumentReference documentReference, String fullName) {
        documentReference.update("fullName", fullName);
    }
    private void updateUserProfileGender(DocumentReference documentReference, Integer gender) {
        documentReference.update("gender", gender);
    }
    private void updateUserProfileLevel(DocumentReference documentReference, Integer level) {
        documentReference.update("level", level);
    }
    private void updateUserProfileOccupation(DocumentReference documentReference, Integer occupation) {
        documentReference.update("occupation", occupation);
    }

//=================================================================================================


}
