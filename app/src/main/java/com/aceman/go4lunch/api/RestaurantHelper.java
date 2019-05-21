package com.aceman.go4lunch.api;

import com.aceman.go4lunch.BuildConfig;
import com.aceman.go4lunch.models.User;
import com.aceman.go4lunch.models.UserPublic;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by Lionel JOFFRAY - on 02/05/2019.
 */
public class RestaurantHelper {

    private static final String COLLECTION_NAME = "restaurant";
    private static final String ID = BuildConfig.restaurant_ID;

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getRestaurantCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---
    public static Task<Void> createPublicUser(String uid, String username, String urlPicture) {
        UserPublic userToCreate = new UserPublic(username,urlPicture );
        return RestaurantHelper.getRestaurantCollection().document(uid).set(userToCreate);
    }
    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String ID) {
        return RestaurantHelper.getRestaurantCollection().document(ID).get();
    }

    // --- UPDATE ---

    public static Task<Void> restaurantPublic(String uid, String restaurantID) {
        return RestaurantHelper.getRestaurantCollection().document(uid).update("restaurantID", restaurantID, "uid", uid);
    }
    public static Task<Void> restaurantName(String uid, String restaurantName) {
        return RestaurantHelper.getRestaurantCollection().document(uid).update("restaurantName", restaurantName);
    }
    public static Task<Void> updateUsername(String username, String uid) {
        return RestaurantHelper.getRestaurantCollection().document(uid).update("username", username);
    }
    public static Task<Void> updateLikeRestaurant(String uid, String likeID) {
        return RestaurantHelper.getRestaurantCollection().document(uid).update("like", likeID);
    }
    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return RestaurantHelper.getRestaurantCollection().document(uid).delete();
    }

}
