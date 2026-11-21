package Inven;

public class Weapon extends Item{
    private String dmg;
    private int hands;
    private String wpnType;
    private int hitMod;

    public Weapon(int itemID, String name, int value, String desc, String type,
    String dmg, int hands, String wpnType, int hitMod){
        super(itemID, name, value, desc, type);

    this.dmg = dmg;
    this.hands = hands;
    this.wpnType = wpnType;
    this.hitMod = hitMod;
}
// Set Methods //
public void setDMG(String dmg){
    this.dmg = dmg;
}
public void setHands(int hands){
    this.hands = hands;
}
public void setWpnType(String wpnType){
    this.wpnType = wpnType;
}
public void setHitMod(int hitMod){
    this.hitMod = hitMod;
}

// Get Methods //
public String getDMG(){
    return dmg;
}
public int getHands(){
    return hands;
}
public String getWpnType(){
    return wpnType;
}
public int getHitMod(){
    return hitMod;
}

@Override
public String toString(){
    return itemID + "," + type + "," +  name + "," + value+ "," + desc+ "," + dmg + "," + hands + "," + wpnType + "," + hitMod+ ",,,,";
}
}
