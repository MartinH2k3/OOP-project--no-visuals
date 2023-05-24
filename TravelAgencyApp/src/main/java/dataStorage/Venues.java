package dataStorage;

import Universal.Address;
import venueData.*;
import java.util.HashMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;


public class Venues implements ObjectMap {
    /** constructor */
    public Venues() {
        venueMap = new HashMap<>();
        venueMap.put("Shop", new HashMap<>());
        venueMap.put("Restaurant", new HashMap<>());
        venueMap.put("Experience", new HashMap<>());
    }

    private HashMap<String, HashMap<Long, Venue>> venueMap;

    /** methods for better printing */
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        if (venueMap.get("Shop").size() > 0) {
            output.append("Shops:\n");
            for (Venue shop : venueMap.get("Shop").values()) {
                output.append(shop).append("\n");
            }
            output.append("\n");
        }

        if (venueMap.get("Restaurant").size() > 0) {
            output.append("Restaurants:\n");
            for (Venue restaurant : venueMap.get("Restaurant").values()) {
                output.append(restaurant).append("\n");
            }
            output.append("\n");
        }

        if (venueMap.get("Experience").size() > 0) {
            output.append("Experiences:\n");
            for (Venue experience : venueMap.get("Experience").values()) {
                output.append(experience).append("\n");
            }
            output.append("\n");
        }

        return output.toString();
    }

    public String toString(String type) {
        StringBuilder output = new StringBuilder();
        if (venueMap.get(type).size() > 0) {
            output.append(type).append("s:\n");
            for (Venue venue : venueMap.get(type).values()) {
                output.append(venue).append("\n");
            }
            output.append("\n");
        }

        return output.toString();
    }

    /** setters and getters */
    public void addVenue(Address address, Long id, String name, String type){
        Venue newVenue;
        switch (type){
            case "Shop":
                newVenue = new Shop(address, id, name, type);
                break;
            case "Restaurant":
                newVenue = new Restaurant(address, id, name, type);
                break;
            case "Experience":
                newVenue = new Experience(address, id, name, type);
                break;
            default:
                newVenue = null;
                break;
        }
        addVenue(newVenue);
    }

    public void addVenue(Address address, Long id, String name, String type, String description){
        Venue newVenue;
        switch (type){
            case "Shop":
                newVenue = new Shop(address, id, name, type);
                break;
            case "Restaurant":
                newVenue = new Restaurant(address, id, name, type);
                break;
            case "Experience":
                newVenue = new Experience(address, id, name, type);
                break;
            default:
                newVenue = null;
                break;
        }
        newVenue.setDescription(description);
        addVenue(newVenue);
    }

    private void addVenue(Venue venue){ venueMap.get(venue.getType()).put(venue.getId(), venue); }

    public Venue getVenue(String type, Long id){ return venueMap.get(type).get(id); }

    public void removeVenue(String type, Long id){ venueMap.get(type).remove(id); }

    private final Object venueThreadLock = new Object(); // so the map doesn't get loaded and uploaded at the same time

    /** methods for uploading and loading */

    public synchronized void load(String filePath){
        synchronized (venueThreadLock){
            venueMap = new HashMap<>();
            venueMap.put("Shop", new HashMap<>());
            venueMap.put("Restaurant", new HashMap<>());
            venueMap.put("Experience", new HashMap<>());
            try {venueMap.get("Shop").putAll(load(filePath + "/Shop.json", Shop.class));
            } catch (NullPointerException ignored){}
            try {venueMap.get("Restaurant").putAll(load(filePath + "/Restaurant.json", Restaurant.class));
            } catch (NullPointerException ignored){}
            try {venueMap.get("Experience").putAll(load(filePath + "/Experience.json", Experience.class));
            } catch (NullPointerException ignored){}
        }
    }

    private HashMap<Long, ? extends Venue> load(String filePath, Class<? extends Venue> venueType) {
        Gson gson = new Gson();
        Type venueMapType = TypeToken.getParameterized(HashMap.class, Long.class, venueType).getType();
        try (FileReader fileReader = new FileReader(filePath)) {
            HashMap<Long, ? extends Venue> temp = gson.fromJson(fileReader, venueMapType);
            return temp;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public synchronized void upload(String filePath){
        synchronized (venueThreadLock){
            upload(filePath + "/Shop.json", venueMap.get("Shop"));
            upload(filePath + "/Restaurant.json", venueMap.get("Restaurant"));
            upload(filePath + "/Experience.json", venueMap.get("Experience"));
        }
    }

    private void upload(String filePath, HashMap<Long, Venue> venueMap) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(venueMap);
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
