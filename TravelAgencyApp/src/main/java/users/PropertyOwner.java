package users;

import java.util.HashMap;
import java.util.HashSet;

public class PropertyOwner extends User{
    /** Creates a new type of user, that owns a property.
     * @param name the name of the PropertyOwner
     * @param password the password of the PropertyOwner
     */
    public PropertyOwner(String name, String password) {
        super(name, password);
    }

    private HashMap<String, HashSet<Long>> ownedVenues = new HashMap<>();

    public void addOwnedVenue(String type, Long id){
        if (ownedVenues.containsKey(type)){
            ownedVenues.get(type).add(id);
        } else {
            ownedVenues.put(type, new HashSet<>());
            ownedVenues.get(type).add(id);
        }
    }

    public HashSet<Long> getOwnedVenues(String type){
        return ownedVenues.get(type);
    }

    /** Displays the venues owned by the PropertyOwner
     * @return a String representation of the venues owned by the PropertyOwner
     */

    public String showOwnedVenues() {
        if (ownedVenues == null) return "";
        String output = "";
        for (String type : ownedVenues.keySet()) {
            output += type + ": ";
            for (Long id : ownedVenues.get(type)) {
                output += id + ", ";
            }
            output += "\n";
        }
        return output;
    }

    public void removeOwnedVenue(String type, Long id){
        ownedVenues.get(type).remove(id);
    }
}
