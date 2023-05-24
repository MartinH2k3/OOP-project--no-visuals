package dataStorage;

public interface ObjectMap {
    /**
     * Uploads/loads the object to/from the database
     * @param filePath the path to the file to upload
     */
    void upload(String filePath);
    void load(String filePath);

    default void upload(Object o) {
        System.out.println("Upload this object manually: " + o);
    }

    default void load(Object o) {
        System.out.println("Load this object manually: " + o);
    }
}
