package com.aceman.go4lunch.api;

import com.aceman.go4lunch.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by Lionel JOFFRAY - on 02/05/2019.
 */
public class RestaurantHelper {

    private static final String COLLECTION_NAME = "restaurant";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getRestaurantCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String username, String urlPicture) {
        User userToCreate = new User(uid, username, urlPicture);
        return RestaurantHelper.getRestaurantCollection().document(uid).set(userToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid) {
        return RestaurantHelper.getRestaurantCollection().document(uid).get();
    }

    // --- UPDATE ---

    public static Task<Void> updateUsername(String username, String uid) {
        return RestaurantHelper.getRestaurantCollection().document(uid).update("username", username);
    }

    public static Task<Void> updateIsPrivate(String uid, Boolean isMentor) {
        return RestaurantHelper.getRestaurantCollection().document(uid).update("isPrivate", isMentor);
    }

    public static Task<Void> updateRestaurant(String uid, String restaurant) {
        return RestaurantHelper.getRestaurantCollection().document(uid).update("restaurant", restaurant);
    }

    public static Task<Void> updateLikeRestaurant(String uid, String like) {
        return RestaurantHelper.getRestaurantCollection().document(uid).update("like", like);
    }
    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return RestaurantHelper.getRestaurantCollection().document(uid).delete();
    }

}
