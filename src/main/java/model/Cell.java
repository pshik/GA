package model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;

@JsonAutoDetect
public class Cell implements Serializable,Comparable<Cell> {
    private String name;
    private int row;
    private int col;
    private ArrayList<Pallet> pallets = new ArrayList<>();
    private boolean blocked;

    public Cell(String name, int row, int col, Pallet pallet) {
        this.name = name;
        this.row = row;
        this.col = col;
        pallets.add(pallet);
        blocked = false;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "name='" + name + '\'' +
                ", row='" + row + '\'' +
                ", col='" + col + '\'' +
                ", pallets=" + pallets +
                ", blocked=" + blocked +
                '}';
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public ArrayList<Pallet> getPallets() {
        if (pallets == null){
            return null;
        }else if (pallets.size() > 0) {
            if (pallets.get(0) == null) return null;
            return pallets;
        } else {
            pallets.add(null);
            return pallets;
        }
    }

    public void addPallet(Pallet pallet) {
        // if size < max size add pallet
        if (!isBlocked()) {
            if (pallets == null ){
                pallets = new ArrayList<>();
                pallets.add(pallet);
            } else if (pallets.get(0) == null) {
                pallets.clear();
                pallets.add(pallet);
            } else pallets.add(pallet);
        }//else sent error
    }

    public void pickUpPallet(int position,String pallet) {
        ArrayList<Pallet> tmp = new ArrayList<>();
        tmp.addAll(pallets);
        for (Pallet p : tmp) {
            if (p != null && p.getMaterial().equals(pallet) && p.getPosition() == position) {
                pallets.remove(p);
               // System.out.println(";");
            }
        }
        if (pallets.size() == 0) pallets.add(null);
    }

    public boolean isContainReference(String material) {
        if(pallets == null) return false;
        for (Pallet p : pallets){
            if (p.getMaterial().equals(material)) return true;
        }
        return false;
    }

    public Cell() {
    }

    @Override
    public int compareTo(@NotNull Cell o) {
        return name.compareTo(o.name);
    }
}
