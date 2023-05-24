package venueData;

public class Restaurant extends Venue {
    public Restaurant(Universal.Address address, Long id, String name, String type) {
        super(address, id, name, type);
    }

    private String contact, cuisineType;

    public void setContact(String contact){ this.contact = contact; }

    public String getContact(){ return contact; }

    public void setCuisineType(String cuisineType){ this.cuisineType = cuisineType; }

    public String getCuisineType(){ return cuisineType; }

    public String getReservation(){
        if (contact == null) return "Contact us in person to make a reservation";
        return "Message us at " + contact + " to make a reservation";
    }
}
