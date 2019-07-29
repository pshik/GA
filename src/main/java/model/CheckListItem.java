package model;

import org.jetbrains.annotations.NotNull;

public class CheckListItem implements Comparable<CheckListItem>{

    private String label;
    private boolean isSelected = false;

    public CheckListItem(String label) {
        this.label = label;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public int compareTo(@NotNull CheckListItem o) {
        return label.compareTo(o.label);
    }
}