package users;

import venueData.Venue;

public class Professional extends Traveler{
    /** Professional user class
     * @param name the name of the user
     * @param password the password of the user
     */
    public Professional(String name, String password) {
        super(name, password);
        grantPermission("verifiedReview");
    }

    Integer venuesRated = 0;

    /** overriden rateVenue method to allow for double rating */
    @Override
    public void rateVenue(Venue venue, float rating){
        venuesRated++;
        venue.incrementRatingCount(); venue.incrementRatingCount();
        var previousRating = venue.getRating();
        venue.setRating(((previousRating == null ? 0 : previousRating) * (venue.getRatingCount() - 2) + 2*rating) / venue.getRatingCount());
    }

    public int getVenuesRated(){
        return venuesRated != null ? venuesRated : 0;
    }
}
