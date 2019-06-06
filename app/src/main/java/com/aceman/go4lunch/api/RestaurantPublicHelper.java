package com.aceman.go4lunch.api;

import com.aceman.go4lunch.models.History;
import com.aceman.go4lunch.models.Restaurant;
import com.aceman.go4lunch.models.RestaurantPublic;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;

/**
 * Created by Lionel JOFFRAY - on 02/05/2019.
 */
public class RestaurantPublicHelper {

    private static final String COLLECTION_NAME = "restaurant";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getRestaurantCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---
    public static Task<Void> createPublicUser(String uid, String username, String urlPicture) {
        RestaurantPublic userToCreate = new RestaurantPublic(username, urlPicture);
        return RestaurantPublicHelper.getRestaurantCollection().document(uid).set(userToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String ID) {
        return RestaurantPublicHelper.getRestaurantCollection().document(ID).get();
    }

    // --- UPDATE ---

    public static Task<Void> restaurantPublic(String uid, String restaurantID, String restaurantName, Restaurant restaurant, String date) {
        return RestaurantPublicHelper.getRestaurantCollection().document(uid)
                .update("restaurantID", restaurantID, "restaurantName", restaurantName,"details",restaurant, "date", date);
    }

    public static Task<Void> updateUsername(String username, String uid) {
        return RestaurantPublicHelper.getRestaurantCollection().document(uid).update("username", username);
    }

    public static Task<Void> updateLikeRestaurant(String uid, String likeID) {
        return RestaurantPublicHelper.getRestaurantCollection().document(uid).update("like", likeID);
    }
    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return RestaurantPublicHelper.getRestaurantCollection().document(uid).delete();
    }

}
