package com.aceman.go4lunch.api;

import com.aceman.go4lunch.data.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by Lionel JOFFRAY - on 02/05/2019.
 * <p>
 * User Helper class contains all CRUD request to Firestore User (private) Collection.
 */
public class UserHelper {

    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---

    /**
     * Get User Collection
     *
     * @return collection
     */
    public static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    /**
     * Create new user (private).
     *
     * @param uid        user id
     * @param username   user name
     * @param urlPicture picture url
     * @param email      email
     * @return new user
     */
    public static Task<Void> createUser(String uid, String username, String urlPicture, String email) {
        User userToCreate = new User(uid, username, urlPicture, email);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- GET ---

    /**
     * Get actual user details.
     *
     * @param uid user id
     * @return user details
     */
    public static Task<DocumentSnapshot> getUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).get();
    }

    // --- UPDATE ---

    /**
     * Update username.
     *
     * @param username new username
     * @param uid      user id
     * @return update
     */
    public static Task<Void> updateUsername(String username, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("username", username);
    }

    /**
     * Update restaurant choose.
     *
     * @param uid        user id
     * @param restaurant place id
     * @param name       place name
     * @return update
     */
    public static Task<Void> updateRestaurantID(String uid, String restaurant, String name) {
        return UserHelper.getUsersCollection().document(uid).update("restaurant", restaurant, "restaurantName", name);
    }

    /**
     * Update liked place.
     *
     * @param uid  user id
     * @param like liked id
     * @return update
     */
    public static Task<Void> updateLikeRestaurant(String uid, String like) {
        return UserHelper.getUsersCollection().document(uid).update("like", like);
    }

    /**
     * Update Email.
     *
     * @param email new email
     * @param uid   user id
     * @return update
     */
    public static Task<Void> updateEmail(String email, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("email", email);
    }
    // --- DELETE ---

    /**
     * Delete user account.
     *
     * @param uid user id
     * @return delete account
     */
    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }

}
