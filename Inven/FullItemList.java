package Inven;
import java.util.ArrayList;

public class FullItemList {
    // Basic Item Methods //
    private final ArrayList<Item> items;

    public FullItemList(ArrayList<Item> items){
        this.items = items;
    }

    public ArrayList<Item> getItems(){
        return items;
    }

    public Item findByID(int ID){
        for (Item item : items){
            if(item.getItemID() == ID){
                return item;
            }
        }
        return null;
    }

    // Item change methods //
    public void addItem(Item item){
        items.add(item);
    }

    // Change the Item based on its type //
    public boolean changeItem(int id, String name, int value, String desc,
                          String dmg, int hands, String wpnType, int hitMod,
                          int ac, String size, String weight,
                          int uses){
        Item item = findByID(id);
        if (item == null) return false;
        item.setName(name);
        item.setValue(value);
        item.setDesc(desc);
        switch (item.getType()){
            case "Weapon":
                if (item instanceof Weapon){
                    Weapon w = (Weapon) item;
                    w.setDMG(dmg);
                    w.setHands(hands);
                    w.setWpnType(wpnType);
                    w.setHitMod(hitMod);
                }
                break;
            case "Armor":
                if (item instanceof Armor){
                    Armor a = (Armor) item;
                    a.setAC(ac);
                    a.setSize(size);
                    a.setWeight(weight);
                }
                break;
            case "Consumable":
                if (item instanceof Consumable){
                    ((Consumable) item).setUses(uses);
                }
                break;
            default:
                // Plain Item: nothing extra to change //
                break;
        }
        return true;
    }

    public boolean removeItem(int id){
        Item current = findByID(id);
        if(current instanceof Item){
            return items.remove(current);
        }
        return false;
    }

}