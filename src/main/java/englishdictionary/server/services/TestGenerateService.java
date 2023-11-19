package englishdictionary.server.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import englishdictionary.server.models.Question;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class TestGenerateService {
    private Firestore firestore;

    public List<Question> getListQuestions(Integer numberOfQuestions) throws ExecutionException, InterruptedException {
        firestore = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = firestore.collection("questions").get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Question> questions = new ArrayList<>();
        for (DocumentSnapshot document : documents) {
            Question question = document.toObject(Question.class);
            question.setId(document.getId());
            questions.add(question);
        }

        if (numberOfQuestions == null)
            return questions;

        Collections.shuffle(questions);
        return questions.subList(0, numberOfQuestions);
    }

}
