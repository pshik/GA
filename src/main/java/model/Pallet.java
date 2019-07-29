package model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.io.Serializable;
import java.time.LocalDateTime;

@JsonAutoDetect
public class Pallet implements Serializable,Comparable {
    private String material;
    private int size;
    private LocalDateTime loadingDate;
    private int position;

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public LocalDateTime getLoadingDate() {
        return loadingDate;
    }
//size 1 - small, 2 - middle, 3 - big


    @Override
    public String toString() {
        return "Pallet{" +
                "material='" + material + '\'' +
                ", size=" + size +
                ", loadingDate=" + loadingDate +
                ", position=" + position +
                '}';
    }

    public Pallet(String material, int size, LocalDateTime loadingDate) {
        this.material = material;
        this.size = size;
        this.loadingDate = loadingDate;
        position = 0;
    }

    public String getMaterial() {
        return material;
    }

    public int getSize() {
        return size;
    }

    public Pallet() {

    }

    @Override
    public int compareTo( Object o) {
        Pallet tmp = (Pallet) o;
        if (this.getLoadingDate().equals(tmp.getLoadingDate())) return 0;
        return this.getLoadingDate().isBefore(tmp.getLoadingDate())? -1: 1;
    }
}
