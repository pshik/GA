package model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

@JsonAutoDetect
public class Rack implements Serializable,Comparable<Rack> {
    private String name;
    private int row;
    private int col;
    private Cell[][] cells;

    public Rack(String name, int row, int col, Cell[][] cells) {
        this.name = name;
        this.row = row;
        this.col = col;
        if (cells == null){
          initCell();
        } else {
            this.cells = cells;
        }
    }

    private void initCell() {
        cells = new Cell[row][col];
        for (int i = 0; i < row; i++){
            for (int j = 0; j < col; j++){
                String cellName = String.valueOf((char) (65 + j)) + (row-i);
                cells[i][j] = new Cell(cellName,i, j, null);
            }
        }
    }

    public Cell[][] getCells() {
        return cells;
    }

    @Override
    public String toString() {
        return "Rack{" +
                "name='" + name + '\'' +
                ", row=" + row +
                ", col=" + col +
                ", cells=" + Arrays.toString(cells) +
                '}';
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }
    public Cell getCellByName(String name){
        if (name.length() == 2) {
            char[] chars = name.toCharArray();
            int iRow = row - Character.getNumericValue(chars[1]);
            int iCol = chars[0] - 65;
            Cell cell = cells[iRow][iCol];
            return cell;
        }
        return null;
    }
    public void setCellByAddress(int row, int col,Cell cell){
        cells[row][col] = cell;
    }
    public String getName() {
        return name;
    }

    public int getCol() {
        return col;
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

    @Override
    public int compareTo(@NotNull Rack o) {
        return name.compareTo(o.name);
    }
}