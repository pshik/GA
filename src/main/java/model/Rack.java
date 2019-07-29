package model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.io.Serializable;
import java.util.Objects;

@JsonAutoDetect
public class Rack implements Serializable {
    private String name;
    private int col;
    private int row;

    public Rack(String name, int col, int row) {
        this.name = name;
        this.col = col;
        this.row = row;
    }

    public String getName() {
        return name;
    }

    public int getCol() {
        return col;
    }

    @Override
    public String toString() {
        return "Rack{" +
                "name='" + name + '\'' +
                ", col=" + col +
                ", row=" + row +
                '}';
    }

    public int getRow() {
        return row;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rack person = (Rack) o;

        if (!Objects.equals(name, person.name)) return false;
        if (!Objects.equals(col, person.col)) return false;
        if (!Objects.equals(row, person.row)) return false;
        return true;
    }

    public Rack() {
    }
}