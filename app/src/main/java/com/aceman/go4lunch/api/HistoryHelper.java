package com.aceman.go4lunch.api;

import com.aceman.go4lunch.models.History;
import com.aceman.go4lunch.models.Restaurant;
import com.aceman.go4lunch.models.RestaurantPublic;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by Lionel JOFFRAY - on 06/06/2019.
 */
public class HistoryHelper {


    private static final String COLLECTION_NAME = "history";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getHistoryCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---
    public static Task<Void> addNewPlaceToHistory(String uid, History history){
        return  HistoryHelper.getHistoryCollection().document(uid).collection(history.getDate()).document().set(history);
    }

    // --- GET ---
    public static Task<DocumentSnapshot> getHisroy(String ID) {
        return HistoryHelper.getHistoryCollection().document(ID).get();
    }

    // --- UPDATE ---
    public static Task<Void> updateHistory(History history, String uid) {
        return HistoryHelper.getHistoryCollection().document(uid).update("history", history);
    }

    // --- DELETE ---
    public static Task<Void> deleteUserHistory(String uid) {
        return HistoryHelper.getHistoryCollection().document(uid).delete();
    }

}
