package Inven;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;

public class Inventory {
    // itemID & quantity stored in HashMap (dictionary)//
    private final LinkedHashMap<Integer, Integer> contents = new LinkedHashMap<>();

    private String owner;

    public Inventory(String owner){
        this.owner = owner;
    }

    public Inventory(String owner, LinkedHashMap<Integer, Integer> loadedContents) {
        this.owner = owner;
        if (loadedContents != null) {
            this.contents.putAll(loadedContents);
    }
}

    public String getOwner(){
        return this.owner;
    }

    public void setOwner(String owner){
        this.owner = owner;
    }

    public LinkedHashMap<Integer, Integer> getContents(){
        return contents;
    }

    //Add item with qty//
    //if item exists in inventory, add qty//
    public boolean addItem(int itemID, int qty){
        if (qty <= 0) return false;
        if (contents.containsKey(itemID)){
            int current = contents.get(itemID);
            contents.put(itemID, current + qty);
        } else {
            contents.put(itemID, qty);
        }
        return true;
    }

    //remove qty of item from qty held//
    //if qty removed >= qty held, remove item completely//
    public boolean removeItem(int itemID, int qty){
        if (qty<=0) return false;
        if (contents.containsKey(itemID)){
            int current = contents.get(itemID);
            if (qty >= current){
                contents.remove(itemID);
            }
            else{
                contents.put(itemID, current - qty);
            }
        } else {
            return false;
        }
        return true;
    }
    
    public int getQuantity(int itemID){
        return contents.getOrDefault(itemID, 0);
    }

    public static LinkedHashMap<Integer, Integer> loadCSV(String CSV){
        LinkedHashMap<Integer, Integer> backpack = new LinkedHashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(CSV))){
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null){
                if (line.isBlank()) continue;

                // -1 keeps trailing empty fields (otherwise "uses" can vanish)
                String[] f = line.split(",");
                if (f.length < 2){
                    System.err.println("Skipping malformed row: " + line);
                    continue;
                }
                    int id        = Integer.parseInt(f[0].trim());
                    int qty       = Integer.parseInt(f[1].trim());
                    backpack.put(id,qty);
            }
            System.out.println("CSV Loaded! (" + backpack.size() + " items)");
        } catch (FileNotFoundException e){
            System.out.println("No CSV found, starting with an empty list.");
        } catch (IOException e){
            System.err.println("Error loading CSV file: " + e.getMessage());
        }
        return backpack;
    }

    public static void saveCSV(String CSV, LinkedHashMap<Integer, Integer> backpack){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV))){
            writer.write("itemID,qty");
            writer.newLine();
            
            backpack.forEach((Integer itemID, Integer qty) ->{
                try {
                    writer.write(itemID + "," + qty);
                    writer.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            System.out.println("CSV Saved!");
        } catch (IOException | RuntimeException e) {
            System.err.println("Error saving CSV file: " + e.getMessage());
        }
}

}
