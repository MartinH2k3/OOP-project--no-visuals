package users;

import venueData.Venue;

import java.util.ArrayList;
import java.util.HashMap;

public class Traveler extends User {

    public Traveler(String name, String password) {
        super(name, password);
    }

    HashMap<String, Path> paths = new HashMap<>();

    /** Nested class to represent a path */
    private class Path{
        private String name;
        private ArrayList<Venue> venues = new ArrayList<>();

        public void addToPath(Venue property){
            venues.add(property);
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName(){
            return name;
        }

        public void removeFromPath(Venue property){ venues.remove(property); }

        @Override
        public String toString() {
            String output = "";
            for (var venue: venues){
                output += "->" + venue.getName();
            }
            return output;
        }
    }

    public void rateVenue(Venue venue, float rating){
        venue.incrementRatingCount();
        var previousRating = venue.getRating();
        venue.setRating(((previousRating == null ? 0 : previousRating) * (venue.getRatingCount() - 1) + rating) / venue.getRatingCount());
    }

    public void makePath(String name, Venue...venues){
        var output = new Path();
        output.setName(name);
        for (var venue: venues){
            output.addToPath(venue);
        }
        paths.put(name, output);
    }

    public void addToPath(String name, Venue venue){
        paths.get(name).addToPath(venue);
    }

    public String getPath(String name){
        return paths.get(name).toString();
    }

    public HashMap<String, Path> getAllPaths() { return paths; }

    public ArrayList<String> getPathNames(){
        return new ArrayList<>(paths.keySet());
    }
}
