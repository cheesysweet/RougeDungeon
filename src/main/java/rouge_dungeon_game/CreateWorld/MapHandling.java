package rouge_dungeon_game.CreateWorld;

import rouge_dungeon_game.collider.Collider;
import rouge_dungeon_game.entity.Tile;

import java.io.*;
import java.util.ArrayList;

/**
 * Saves and loads the map object
 */
public class MapHandling {

    private final static String saveLocation = "src/main/resources/mapSections/";

    public MapHandling() {
    }

    public static void save(String name, Tile[][] background, Tile[][] middleGround, Tile[][] foreground, String texture,
                            int width, int height, ArrayList<Collider> colliders, ArrayList<Collider> interactions, ArrayList<Collider> enemies) {
        saveFile(name, new SaveMap(background, middleGround, foreground, width, height, colliders, interactions, enemies));
    }

    public static SaveMap load(String fileName) {
        File file;
        if(fileName.contains(".ser"))
            file = new File(String.format("%s/%s", saveLocation, fileName));
        else
            file = new File(String.format("%s/%s.ser", saveLocation, fileName));
        return load(file);
    }

    /**
     * loads file
     *
     * @param read File to load
     * @return hashmap of components
     */
    public static SaveMap load(File read) {
        try {
            FileInputStream fis = new FileInputStream(read);
            ObjectInputStream ois = new ObjectInputStream(fis);

            var hashMap = (SaveMap) ois.readObject();

            ois.close();
            fis.close();

            return hashMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * saves a hashmap
     *
     * @param name    name of file
     * @param hashMap to save
     */
    private static void saveFile(String name, SaveMap hashMap) {
        File file = new File(saveLocation + name + ".ser");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));) {
            oos.writeObject(hashMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
