package com.aceman.go4lunch.api;

import com.aceman.go4lunch.data.models.History;
import com.aceman.go4lunch.data.models.Restaurant;
import com.aceman.go4lunch.data.models.RestaurantPublic;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by Lionel JOFFRAY - on 02/05/2019.
 * Restaurant Public Helper class contains all CRUD request to Firestore Restaurant Collection.
 */
public class RestaurantPublicHelper {

    private static final String COLLECTION_NAME = "restaurant";

    // --- COLLECTION REFERENCE ---

    /**
     * Get Restaurant Collection
     *
     * @return collection
     */
    public static CollectionReference getRestaurantCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    /**
     * Create a new user (Restaurant public).
     *
     * @param uid        user ID
     * @param username   name
     * @param urlPicture pic url
     * @return userToCreate
     */
    public static Task<Void> createPublicUser(String uid, String username, String urlPicture) {
        RestaurantPublic userToCreate = new RestaurantPublic(username, urlPicture);
        return RestaurantPublicHelper.getRestaurantCollection().document(uid).set(userToCreate);
    }

    // --- GET ---

    /**
     * Get User
     *
     * @param ID user id
     * @return document
     */
    public static Task<DocumentSnapshot> getUser(String ID) {
        return RestaurantPublicHelper.getRestaurantCollection().document(ID).get();
    }

    // --- UPDATE ---

    /**
     * Update restaurant public.
     *
     * @param uid            user id
     * @param restaurantID   place ID
     * @param restaurantName place Name
     * @param restaurant     place as restaurant object
     * @param history        history
     * @param date           actual date
     * @return update
     */
    public static Task<Void> restaurantPublic(String uid, String restaurantID, String restaurantName, Restaurant restaurant, History history, String date) {
        return RestaurantPublicHelper.getRestaurantCollection().document(uid)
                .update("restaurantID", restaurantID, "restaurantName", restaurantName, "details", restaurant, "history", history, "date", date);
    }

    /**
     * Reset user history.
     *
     * @param uid user id
     * @return null history
     */
    public static Task<Void> resetUserHistory(String uid) {
        return RestaurantPublicHelper.getRestaurantCollection().document(uid).update("history", null);
    }

    /**
     * Update username.
     *
     * @param username new username
     * @param uid      user id
     * @return update
     */
    public static Task<Void> updateUsername(String username, String uid) {
        return RestaurantPublicHelper.getRestaurantCollection().document(uid).update("username", username);
    }

    /**
     * Update liked restaurant.
     *
     * @param uid    user id
     * @param likeID restaurant id
     * @return update
     */
    public static Task<Void> updateLikeRestaurant(String uid, String likeID) {
        return RestaurantPublicHelper.getRestaurantCollection().document(uid).update("like", likeID);
    }
    // --- DELETE ---

    /**
     * Delete user (public).
     *
     * @param uid user id
     * @return delete user
     */
    public static Task<Void> deleteUser(String uid) {
        return RestaurantPublicHelper.getRestaurantCollection().document(uid).delete();
    }
}
