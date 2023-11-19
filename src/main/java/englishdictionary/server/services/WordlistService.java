package englishdictionary.server.services;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import englishdictionary.server.errors.DuplicateWordlistException;
import englishdictionary.server.errors.UserNotFoundException;
import englishdictionary.server.errors.WordNotFoundException;
import englishdictionary.server.errors.WordlistNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

import englishdictionary.server.models.Word;
import englishdictionary.server.models.Wordlist;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WordlistService {
    private Firestore firestore;

    public List<Wordlist> getAllUserWordLists(String userId) throws ExecutionException, InterruptedException {
        firestore = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = firestore.collection("word_lists").whereEqualTo("userId", userId).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Wordlist> wordlists = new ArrayList<>();
        for (DocumentSnapshot document : documents) {
            Wordlist wordlist = document.toObject(Wordlist.class);
            wordlist.setWordlistId(document.getId());
            // NOTE: somehow userId cannot be parsed
            //      manually set userId is required.
            wordlist.setUserId(document.get("userId").toString());
            wordlists.add(wordlist);
        }

        return wordlists;
    }

    public Wordlist getWordlistById(String wordListId) throws ExecutionException, InterruptedException {
        firestore = FirestoreClient.getFirestore();
        DocumentSnapshot documentSnapshot = firestore.collection("word_lists").document(wordListId).get().get();
        Wordlist wordlist = documentSnapshot.toObject(Wordlist.class);
        if (wordlist == null)
            throw new WordlistNotFoundException(wordListId);

        wordlist.setWordlistId(wordListId);
        return wordlist;
    }

    public Wordlist createWordlist(String wordlistName, String userId) throws ExecutionException, InterruptedException {
        if (wordlistName == null)
            throw new InvalidParameterException("Wordlist name cannot be null");

        firestore = FirestoreClient.getFirestore();

        List<Wordlist> wordlists = getAllUserWordLists(userId);
        for(Wordlist wordlist : wordlists)
            if (Objects.equals(wordlist.getName(), wordlistName))
                throw new DuplicateWordlistException(wordlistName);

        Wordlist wordlist = new Wordlist();
        wordlist.setName(wordlistName);
        wordlist.setUserId(userId);
        wordlist.setWords(new ArrayList<>());

        Map<String, Object> docData = new HashMap<>();
        docData.put("name", wordlist.getName());
        docData.put("userId", wordlist.getUserId());
        docData.put("words", wordlist.getWords());

        firestore.collection("word_lists").document().set(docData);
        return wordlist;
    }

    public Word getWordlistWord(String wordlistId, Integer wordId) throws ExecutionException, InterruptedException{
        firestore = FirestoreClient.getFirestore();
        DocumentSnapshot documentSnapshot = firestore.collection("word_lists").document(wordlistId).get().get();

        Wordlist wordlist = documentSnapshot.toObject(Wordlist.class);
        if (wordlist == null)
            throw new WordlistNotFoundException(wordlistId);

        Word word = wordlist.getWords().stream().filter(w -> Objects.equals(w.getId(), wordId)).findFirst().orElse(null);
        if (word == null)
            throw new WordNotFoundException(wordId);

        return word;
    }

    public List<Wordlist> searchForWordlist(String name, String word) throws ExecutionException, InterruptedException{
        firestore = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> query = firestore.collection("word_lists").get();
        List<QueryDocumentSnapshot> documentSnapshots = query.get().getDocuments();
        List<Wordlist> wordlists = new ArrayList<>();

        for (DocumentSnapshot document : documentSnapshots) {
            Wordlist wordlist = document.toObject(Wordlist.class);
            wordlist.setWordlistId(document.getId());
            // NOTE: somehow userId cannot be parsed
            //      manually set userId is required.
            wordlist.setUserId(document.get("userId").toString());

            if (
                    wordlist.getName().toLowerCase().contains(name.toLowerCase()) &&
                    wordlist.getWords().stream().filter(w -> w.getWord().toLowerCase().contains(word.toLowerCase())).findFirst().orElse(null) != null
            ) {
                wordlists.add(wordlist);
            }
        }
        return wordlists;
    }

    public boolean deleteWordlist(String wordlistId) throws ExecutionException, InterruptedException{
        firestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = firestore.collection("word_lists").document(wordlistId).delete();
        writeResult.get();

        return writeResult.isDone();
    }

    public boolean removeWordlistWord(String wordlistId, Integer wordId) throws ExecutionException, InterruptedException {
        firestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = firestore.collection("word_lists").document(wordlistId);
        DocumentSnapshot document = documentReference.get().get();

        Wordlist wordlist = document.toObject(Wordlist.class);

        if (wordlist == null)
            throw new WordlistNotFoundException(wordlistId);

        wordlist.setWordlistId(document.getId());
        // NOTE: somehow userId cannot be parsed
        //      manually set userId is required.
        wordlist.setUserId(document.get("userId").toString());

        wordlist.getWords().removeIf(word -> word.getId().equals(wordId));

        Map<String, Object> map = wordlist.toHashMap();
        ApiFuture<WriteResult> updateResult = documentReference.update(map);
        updateResult.get();

        return updateResult.isDone();
    }

    public boolean renameWordList(String wordlistId, String name) throws ExecutionException, InterruptedException {
        firestore = FirestoreClient.getFirestore();
        DocumentSnapshot documentSnapshot = firestore.collection("word_lists").document(wordlistId).get().get();
        if (!documentSnapshot.exists())
            throw new WordlistNotFoundException(wordlistId);
        DocumentReference documentReference = documentSnapshot.getReference();
        ApiFuture<WriteResult> updateResult = documentReference.update("name", name);
        updateResult.get();

        return updateResult.isDone();
    }

    public boolean addWordToWordlist(String wordlistId, Word word) throws ExecutionException, InterruptedException{
        firestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = firestore.collection("word_lists").document(wordlistId);
        DocumentSnapshot document = documentReference.get().get();

        Wordlist wordlist = document.toObject(Wordlist.class);

        if (wordlist == null)
            throw new WordlistNotFoundException(wordlistId);

        wordlist.setWordlistId(document.getId());
        // NOTE: somehow userId cannot be parsed
        //      manually set userId is required.
        wordlist.setUserId(document.get("userId").toString());

        // prevent adding the same word multiple times
        if (wordlist.getWords()
                .stream()
                .anyMatch(w ->
                        (w.getWord().equals(word.getWord()) && w.getDefinition().equals(word.getDefinition()))
                )
        )
            return false;

        if (wordlist.getWords().size() == 0)
            word.setId(1);
        else
            word.setId(wordlist.getWords().get(wordlist.getWords().size() - 1).getId()+1);
        wordlist.getWords().add(word);

        documentReference.update(wordlist.toHashMap());

        return true;
    }

}
