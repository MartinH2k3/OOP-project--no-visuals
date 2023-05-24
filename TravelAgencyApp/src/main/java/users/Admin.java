package users;

import dataStorage.*;

public class Admin extends User {
    /** Creates a new Admin
     * @param name the name of the Admin
     * @param password the password of the Admin
     */
    public Admin(String name, String password) {
        super(name, password);
    }

    public void removeUser(UserMap users, String name){
        users.removeUser(name);
    }

    public void removeProperty(Venues venues, String type, Long id){
        venues.removeVenue(type, id);
    }

    public void setVenueRating(Venues venues, String type, Long id, Integer rating){
        venues.getVenue(type, id).setRating(rating);
    }
}
