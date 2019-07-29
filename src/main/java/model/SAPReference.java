package model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

@JsonAutoDetect
public class SAPReference implements Serializable,Comparable<SAPReference> {
    private String reference;
    private int size;
    private String description;
    private String[] allowedRacks;

    public SAPReference() {
    }

    public SAPReference(String reference, String description, int size, String... list) {
        this.reference = reference;
        this.size = size;
        this.description = description;
        this.allowedRacks = list;
    }

    public String getReference() {
        return reference;
    }
    public void removeAllowedRack(String rackName){
        ArrayList<String> tmp = new ArrayList<>();

        for (int i = 0; i < allowedRacks.length; i++){
            if (!allowedRacks[i].equals(rackName)){
                tmp.add(allowedRacks[i]);
            }
        }
        String[] temp = new String[tmp.size()];
        for (int i = 0; i < tmp.size(); i++){
            temp[i] = tmp.get(i);
        }
        allowedRacks = temp;
    }

    public int getSize() {
        return size;
    }

    public void addAllowedRack(String rackName){
        String[] tmp = new String[allowedRacks.length + 1];
        for (int i = 0; i < allowedRacks.length;i++){
            tmp[i] = allowedRacks[i];
        }
        tmp[allowedRacks.length] = rackName;
        allowedRacks = tmp;
    }
    public String[] getAllowedRacks() {
        return allowedRacks;
    }

    public void setAllowedRacks(String[] allowedRacks) {
        this.allowedRacks = allowedRacks;
    }


    @Override
    public String toString() {
        return "SAPReference{" +
                "reference='" + reference + '\'' +
                ", size=" + size +
                ", description='" + description + '\'' +
                ", allowedRacks=" + Arrays.toString(allowedRacks) +
                '}';
    }

    public String getDescription() {
        return description;
    }

    public boolean isAllowedRack(String rackNameForChecking) {
        for (String s: allowedRacks){
            if (s.equals(rackNameForChecking)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(@NotNull SAPReference o) {
        return reference.compareTo(o.reference);
    }
}
