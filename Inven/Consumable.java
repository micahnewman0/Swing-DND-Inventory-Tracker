package Inven;

public class Consumable extends Item{
    private int uses;

    public Consumable(int itemID, String name, int value, String desc, String type,
    int uses){
    super(itemID, name, value, desc, type);
    this.uses = uses;
    }
    
    public void setUses(int uses){
        this.uses = uses;
    }

    public int getUses(){
        return uses;
    }

    @Override 
    public String toString(){
        return itemID + "," + type + "," +  name + "," + value+ "," + desc+ ",,,,,,,," + uses;
    }
}
