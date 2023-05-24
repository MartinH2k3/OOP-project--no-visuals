package venueData;

import Universal.AppObject;
import Universal.Address;

public abstract class Venue implements AppObject {
    /** Venue class
     * @param address the address of the venue
     * @param id the id of the venue
     * @param name the name of the venue
     * @param type the type of the venue
     */
    public Venue(Address address, Long id, String name, String type) {
        this.address = address;
        this.id = id;
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return id.toString() + ": " + name + ", " + type + " at " + address.toString() + (rating != null ? ", Rating: " + rating.toString() : "") + (verified ? "✓" : "");
    }

    private Address address; private Long id; private String name, type, description, owner;
    private Boolean verified = false; private Float rating; private Integer ratingCount = 0;

    public void setDescription(String description){ this.description = description; }

    public String getDescription(){ return description; }

    public void setOwner(String owner){ this.owner = owner; }

    public String getOwner(){ return owner; }

    public void setVerified(boolean verified){
        this.verified = verified;
    }

    public Boolean getVerified(){ return verified;}

    public void setRating(float rating){ this.rating = rating; }

    public Float getRating(){ return rating; }

    public void incrementRatingCount(){ ratingCount++; }

    public int getRatingCount(){ return ratingCount; }

    public Address getAddress(){ return address; }

    public Long getId(){ return id; }

    public String getName(){ return name; }

    public String getType(){ return type; }

}
