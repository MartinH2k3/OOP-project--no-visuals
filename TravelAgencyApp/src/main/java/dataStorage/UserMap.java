package dataStorage;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import users.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

public class UserMap implements ObjectMap {
    private HashMap<String, User> userMap;
    private final Object userThreadLock = new Object(); // so the map doesn't get loaded and uploaded at the same time

    /** upload the user data to the given folder
     * @param filePath the path to the folder where the user data is stored
     */
    public synchronized void upload(String filePath){
        synchronized (userThreadLock){
            HashMap<String, User> casualUsers = new HashMap<>();
            HashMap<String, User> ProfessionalUsers = new HashMap<>();
            HashMap<String, User> PropertyOwners = new HashMap<>();
            HashMap<String, User> Admins = new HashMap<>();
            for (var username: userMap.keySet()){
                var user = userMap.get(username);
                var type = user.getClass();
                if (type == Casual.class){
                    casualUsers.put(username, user);
                }
                else if (type == Professional.class){
                    ProfessionalUsers.put(username, user);
                }
                else if (type == PropertyOwner.class){
                    PropertyOwners.put(username, user);
                }
                else if (type == Admin.class){
                    Admins.put(username, user);
                }
            }
            upload(filePath + "/casualUsers.json", casualUsers);
            upload(filePath + "/ProfessionalUsers.json", ProfessionalUsers);
            upload(filePath + "/PropertyOwners.json", PropertyOwners);
            upload(filePath + "/Admins.json", Admins);
        }
    }

    private void upload(String filePath, HashMap<String, User> userMap) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(userMap);
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** load the user data from the given folder
     * @param filePath the path to the folder where the user data is stored
     */
    public synchronized void load(String filePath){
        synchronized (userThreadLock) {
            userMap = new HashMap<>();
            try {userMap.putAll(load(filePath + "/casualUsers.json", Casual.class));
            } catch (NullPointerException ignored){}
            try {userMap.putAll(load(filePath + "/ProfessionalUsers.json", Professional.class));
            } catch (NullPointerException ignored){}
            try {userMap.putAll(load(filePath + "/PropertyOwners.json", PropertyOwner.class));
            } catch (NullPointerException ignored){}
            try {userMap.putAll(load(filePath + "/Admins.json", Admin.class));
            } catch (NullPointerException ignored){}
        }
    }

    private HashMap<String, ? extends User> load(String filePath, Class<? extends User> userType) {
        Gson gson = new Gson();
        Type type = TypeToken.getParameterized(HashMap.class, String.class, userType).getType();
        try (FileReader fileReader = new FileReader(filePath)) {
            HashMap<String, ? extends User> temp = gson.fromJson(fileReader, type);
            return temp;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Create a new user. Verify that the given password is valid. Same for username.
     * @param password the password to be verified
     * @return true iff the password is valid
     */
    public boolean register(String name, String password, String type){
        if (!verifyPasswordRegister(password) || userMap.containsKey(name)) return false;
        userMap.put(name, makeUser(name, password, type));
        return true;
    }

/** Login the user with the given username and password
     * @param name the username of the user
     * @param password the password of the user
     * @return the user iff the username and password are correct
     */
    public User login(String name, String password){
        if (!userMap.containsKey(name) || !userMap.get(name).getPassword().equals(password)){
            return null;
        }
        return userMap.get(name);
    }

    public User getUser(String name){
        return userMap.get(name);
    }

    public ArrayList<String> getUsernames(){
        return new ArrayList(userMap.keySet());
    }

    public void removeUser(String name){
        userMap.remove(name);
    }

    private User makeUser(String name, String password, String type){
        switch (type){
            case "Admin":
                return new Admin(name, password);
            case "Casual":
                return new Casual(name, password);
            case "Professional":
                return new Professional(name, password);
            case "Property Owner":
                return new PropertyOwner(name, password);
            default:
                return null;
        }
    }

    /** Helper functions to verify the password
     * @param password the password to be verified
     * @return true iff the password is valid
     */
    private boolean verifyPasswordRegister(String password){
        return getPasswordComplexity.apply(password) >= 3;
    }

    private static Function<String, Integer> getPasswordComplexity = (password) -> {
        int output = 0;
        if (password.length() < 4 || password.length() > 30) return -1;
        boolean containsLowerCase = false;
        boolean containsUpperCase = false;
        boolean containsNumber = false;
        boolean containsSpecialChar = false;
        for (char i: password.toCharArray()){
            if (i >= 'a' && i <= 'z') containsLowerCase = true;
            else if (i >= 'A' && i <= 'Z') containsUpperCase = true;
            else if (i >= '0' && i <= '9') containsNumber = true;
            else containsSpecialChar = true;
        }
        output += (containsLowerCase?1:0) + (containsUpperCase?1:0) + (containsNumber?1:0) + (containsSpecialChar?1:0);
        return output;
    };
}
