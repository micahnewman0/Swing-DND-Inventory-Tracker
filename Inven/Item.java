package Inven;

public class Item {
public int itemID;
public String name;
public int value;
public String desc;
public String type;


public Item(int itemID, String name, int value, String desc, String type){
    this.itemID = itemID;
    this.name = name;
    this.value = value;
    this.desc = desc;
    this.type = type;
}

// Set functions (ID is intentionally not changeable) //
public void setName(String name){
    this.name = name;
}

public void setValue(int value){
    this.value = value;
}

public void setDesc(String desc){
    this.desc = desc;
}

public void setType(String type){
    this.type = type;
}

// Get functions //
public int getItemID(){
    return itemID;
}

public String getName(){
    return name;
}

public int getValue(){
    return value;
}

public String getDesc(){
    return desc;
}

public String getType(){
    return type;
}

@Override
public String toString(){
    return itemID + "," + type + "," +  name + "," + value+ "," + desc+ ",,,,,,,,,";
}
}