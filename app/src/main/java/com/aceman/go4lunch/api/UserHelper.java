package com.aceman.go4lunch.api;

import com.aceman.go4lunch.BuildConfig;
import com.aceman.go4lunch.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by Lionel JOFFRAY - on 02/05/2019.
 */
public class UserHelper {

    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String username, String urlPicture, String email) {
        User userToCreate = new User(uid, username, urlPicture, email);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).get();
    }

    // --- UPDATE ---

    public static Task<Void> updateUsername(String username, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("username", username);
    }

    public static Task<Void> updateIsPrivate(String uid, Boolean isMentor) {
        return UserHelper.getUsersCollection().document(uid).update("isPrivate", isMentor);
    }

    public static Task<Void> updateRestaurantID(String uid, String restaurant, String name) {
        return UserHelper.getUsersCollection().document(uid).update("restaurant", restaurant,"restaurantName", name);
    }

    public static Task<Void> updateLikeRestaurant(String uid, String like) {
        return UserHelper.getUsersCollection().document(uid).update("like", like);
    }
    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }

    public static Task<Void> updateEmail(String email, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("email", email);
    }
}
