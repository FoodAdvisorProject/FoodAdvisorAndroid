package pro.rane.foodadvisor;

/**
 * Created by iscandar on 09/03/17.
 * per otterenere i dati
 * da altre classi
 * pro.rane.foodadvisor.SessionManager session; <--globale
 * session = new pro.rane.foodadvisor.SessionManager(getApplicationContext()); <-- dentro il create
 *session.checkLogin();

 // get user data from session
 HashMap<String, String> user = session.getUserDetails();

 // name
 String name = user.get(SessionManager.KEY_NAME);

 // email
 String email = user.get(SessionManager.KEY_EMAIL);
 */

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "FoodAdvisorPref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_AZIENDA = "aziendaName";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_NAME = "name";
    public static final String KEY_SECOND_NAME = "second_name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_ID = "id";
    public static final String KEY_PHOTO = "photo";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String azienda,String name,String second_name, String email,String description,String photo,String id){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_AZIENDA,azienda);
        editor.putString(KEY_SECOND_NAME,second_name);
        editor.putString(KEY_DESCRIPTION,description);
        editor.putString(KEY_PHOTO,photo);
        editor.putString(KEY_ID,id);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }
    //take photo
    public Bitmap photoUser(){
        return Rest.StringToBitMap(pref.getString(KEY_PHOTO,"photo"));
    }



//session data like hash
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_AZIENDA, pref.getString(KEY_AZIENDA, null));
        user.put(KEY_SECOND_NAME, pref.getString(KEY_SECOND_NAME, null));
        user.put(KEY_DESCRIPTION, pref.getString(KEY_DESCRIPTION, null));
        user.put(KEY_ID, pref.getString(KEY_ID, null));
        // return user
        return user;
    }

//close session
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
