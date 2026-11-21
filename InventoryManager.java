// Goals //
// Create  a project that aims to take 
// both scraped and user inputted data
// and create a full inventory system
// 
// This system should not be locally stored
// 
// The system should be displayed on a website 
// (website likely powered by github)
//
// Information should be available to many people
// 
// Information should be searchable via Regular Expressions
//
// GMAIL sign in?
import Inven.Armor;
import Inven.Consumable;
import Inven.FullItemList;
import Inven.Inventory;
import Inven.Item;
import Inven.Weapon;
import UI.UserInterface;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.SwingUtilities;

public class InventoryManager{
public static void main(String[] args) {
     //Load all the items in Full_List and Player 1's inventory//
    String items = "full_list.csv";
    String p1 = "player_1_inven.csv";
    FullItemList itemList = new FullItemList(loadCSV(items));
    Inventory p1_Inventory = new Inventory("Player 1" , Inventory.loadCSV(p1));

    //Saves everything to the CSVs for future use//
    Runnable saveAll = () -> {
        saveCSV(items, itemList.getItems());
        Inventory.saveCSV(p1, p1_Inventory.getContents());
    };

    //Open the window//
    SwingUtilities.invokeLater(() -> new UserInterface(
        itemList,
        p1_Inventory,
        saveAll,
        () -> getNewID(itemList.getItems())
    ));
}

public static ArrayList<Item> loadCSV(String CSV){
    ArrayList<Item> items = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(CSV))){
        reader.readLine();

        String line;
        while ((line = reader.readLine()) != null){
            if (line.isBlank()) continue;

            // -1 keeps trailing empty fields (otherwise "uses" can vanish)
            String[] f = line.split(",", -1);
            if (f.length < 13){
                System.err.println("Skipping malformed row: " + line);
                continue;
            }

            try {
                int id        = Integer.parseInt(f[0].trim());
                String type   = f[1].trim();
                String name   = f[2].trim();
                int value     = Integer.parseInt(f[3].trim());
                String desc   = f[4].trim();

                switch (type){
                    case "Weapon":
                        items.add(new Weapon(id, name, value, desc, type,
                            f[5].trim(),
                            Integer.parseInt(f[6].trim()),
                            f[7].trim(),
                            Integer.parseInt(f[8].trim())));
                        break;
                    case "Armor":
                        items.add(new Armor(id, name, value, desc, type,
                            Integer.parseInt(f[9].trim()),
                            f[10].trim(),
                            f[11].trim()));
                        break;
                    case "Consumable":
                        items.add(new Consumable(id, name, value, desc, type,
                            Integer.parseInt(f[12].trim())));
                        break;
                    default:
                        items.add(new Item(id, name, value, desc, type));
                }
            } catch (NumberFormatException e){
                System.err.println("Bad number in row, skipping: " + line);
            }
        }
        System.out.println("CSV Loaded! (" + items.size() + " items)");
    } catch (FileNotFoundException e){
        System.out.println("No CSV found, starting with an empty list.");
    } catch (IOException e){
        System.err.println("Error loading CSV file: " + e.getMessage());
    }
    return items;
}

public static void saveCSV(String CSV, ArrayList<Item> itemList){
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV))){
        writer.write("itemID,type,name,value,desc,dmg,hands,wpnType,hitMod,ac,size,weight,uses");
        writer.newLine();
        
        for (Item item : itemList){
            writer.write(item.toString());
            writer.newLine();
        }

        System.out.println("CSV Saved!");
    } catch (IOException e) {
        System.err.println("Error saving CSV file: " + e.getMessage());
    }
}

// Random Item ID maker //
public static int getNewID(ArrayList<Item> itemList){
    Random random = new Random();
    int count = 0;
    int fin = 0;
    // 6 randomly generated digits //
    while (count < 6){
        int zero2Nine = random.nextInt(10);
        fin += (zero2Nine * Math.pow(10,count));
        count+=1;
    }
    // Checks to ensure that ID has not been used before //
    // The recursive function is rare but is here as a safeguard //
    // ** Alternatively you could use numbers so large that you do 
    // not need the recursive function at all** //
    for (Item item : itemList){
        if (item.getItemID() == (fin)){
            return getNewID(itemList);
        }
    }
    return fin;
}
}