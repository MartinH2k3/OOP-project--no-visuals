package users;

import Universal.AppObject;

import java.util.HashMap;

public abstract class User implements AppObject {
    public User(String name, String password){
        this.name = name;
        this.password = password;
        this.permissions = new HashMap<>();
        permissions.put("verifiedReview", false);
    }

    private String name, password;
    private HashMap<String, Boolean> permissions;

    public String getName(){
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void grantPermission(String permission){
        permissions.put(permission, true);
    }

    public boolean hasPermission(String permission){
        return permissions.get(permission);
    }

}
