package users;

import venueData.Venue;

public class Casual extends Traveler{
    /** Casual user class
     * @param name the name of the user
     * @param password the password of the user
     */
    public Casual(String name, String password) {
        super(name, password);
    }
    /** limit how many venues user can create */
    private Integer venueLimit = 5;

    public void rateVenue(Venue venue, float rating){
        super.rateVenue(venue, rating);
        if (venueLimit == 0) venueLimit++;
    }

    public void decrementVenueLimit(){
        venueLimit--;
    }

    public void setVenueLimit(Integer venueLimit){
        this.venueLimit = venueLimit;
    }

    public Integer getVenueLimit(){
        return venueLimit;
    }
}
