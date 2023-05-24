package dataStorage;

import java.io.*;
import java.util.Scanner;

public class IdManager {
    /** keeps track of the last id of a venue */
    String filepath; long id;
    public IdManager(String filepath) {
        this.filepath = filepath;
        loadId();
    }

    private void loadId(){
        File file = new File(filepath);
        try {Scanner sc = new Scanner(file);
            id = Long.parseLong(sc.nextLine());
        }
        catch (FileNotFoundException e) { e.printStackTrace(); }
    }

    private void saveId(){
        try {FileWriter file = new FileWriter(filepath);
            file.write(Long.toString(id));
            file.close();
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    public long makeId(){
        long temp = id;
        id++;
        saveId();
        return temp;
    }
}
