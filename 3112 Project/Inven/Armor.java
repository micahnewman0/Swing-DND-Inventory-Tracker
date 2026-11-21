package Inven;

public class Armor extends Item{
    
private int ac;
private String size;
private String weight;

public Armor(int itemID, String name, int value, String desc, String type,
    int ac, String size, String weight){
    super(itemID, name, value, desc, type);

    this.ac = ac;
    this.size = size;
    this.weight = weight;
}
// Set Methods //
public void setAC(int ac){
    this.ac = ac;
}
public void setSize(String size){
    this.size = size;
}
public void setWeight(String weight){
    this.weight = weight;
}

// Get Methods //
public int getAC(){
    return ac;
}
public String getSize(){
    return size;
}
public String getWeight(){
    return weight;
}

@Override 
public String toString(){
    return itemID + "," + type + "," +  name + "," + value+ "," + desc+ ",,,,," +ac+ "," +size+ "," +weight+",";
}
}