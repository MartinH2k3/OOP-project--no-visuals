package Universal;

public class Address{
    /** class for address encapsulation */
    public Address(String city, String street, Integer streetNumber){
        this.city = city;
        this.street = street;
        this.streetNumber = streetNumber;
    }

    private String city;
    private String street;
    private Integer streetNumber;

    public String toString(){
        return String.format("%s %d, %s", street, streetNumber, city);
    }
    public String getCity(){ return city; }
    public String getStreet(){ return street; }
    public Integer getStreetNumber(){ return streetNumber; }
}

