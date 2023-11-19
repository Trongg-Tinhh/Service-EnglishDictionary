package englishdictionary.server.services;

import com.google.api.core.ApiFuture;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.ListUsersPage;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import englishdictionary.server.dtos.UserDto;
import englishdictionary.server.errors.UserNotFoundException;
import englishdictionary.server.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class AdminService {
    @Autowired
    private UserService userService;
    //=======================================Admin Action==========================================================
    public void editGender(String content, String docId) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentRef = dbFirestore.collection("genders").document(docId);
        DocumentSnapshot documentSnapshot = documentRef.get().get();

        if (!documentSnapshot.exists()) {
            documentRef.set(Collections.singletonMap("name", content));
        } else {
            documentRef.update("name", content);
        }
    }
    public void editLevel(String content, String docId) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentRef = dbFirestore.collection("levels").document(docId);
        DocumentSnapshot documentSnapshot = documentRef.get().get();

        if (!documentSnapshot.exists()) {
            documentRef.set(Collections.singletonMap("name", content));
        } else {
            documentRef.update("name", content);
        }
    }
    public void editOccupation(String content, String docId) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentRef = dbFirestore.collection("occupations").document(docId);
        DocumentSnapshot documentSnapshot = documentRef.get().get();

        if (!documentSnapshot.exists()) {
            documentRef.set(Collections.singletonMap("name", content));
        } else {
            documentRef.update("name", content);
        }
    }

    public List<String> getAllUserId() throws FirebaseAuthException {
        List<String> userIds = new ArrayList<>();
        ListUsersPage page = FirebaseAuth.getInstance().listUsers(null);
        for (UserRecord userRecord : page.iterateAll()) {
            userIds.add(userRecord.getUid());
        }
        return userIds;
    }

    public void deleteGender(String docId){
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentRef = dbFirestore.collection("genders").document(docId);
        documentRef.delete();
    }
    public void deleteLevel(String docId){
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentRef = dbFirestore.collection("levels").document(docId);
        documentRef.delete();
    }
    public void deleteOccupation(String docId){
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentRef = dbFirestore.collection("occupations").document(docId);
        documentRef.delete();
    }
    public void deleteUser(String id) throws ExecutionException, InterruptedException, NotFoundException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection("users").document(id);
        Boolean active = userService.isUserActive(id);
        WriteResult write = documentReference.update("active", !active).get();
        documentReference.update("notify", false);
    }

    public List<UserDto> getAllUser() throws ExecutionException, InterruptedException, UserNotFoundException, FirebaseAuthException {
        List<String> allUserId = getAllUserId();
        List<UserDto> ListOUser = new ArrayList<>();
        Firestore dbFirestore = FirestoreClient.getFirestore();
        for (String userId : allUserId) {
            DocumentReference documentReference = dbFirestore.collection("users").document(userId);
            ApiFuture<DocumentSnapshot> future = documentReference.get();
            DocumentSnapshot document = future.get();
            UserDto user;
            if (!document.exists()) {
                throw new UserNotFoundException(userId);
            }
            user = document.toObject(UserDto.class);
            user.setId(userId);
            ListOUser.add(user);
        }
        return ListOUser;
    }

    public UserDto getUser(String id) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection("users").document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        UserDto user;
        if (!document.exists()) {
            throw new UserNotFoundException(id);
        }
        user = document.toObject(UserDto.class);
        user.setId(id);
        return user;
    }
}
