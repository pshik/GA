package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ClientGuiModel {
    private final Set<User> users = new HashSet<>();
    private final Set<Rack> racks = new HashSet<>();
    private final Set<Cell> cells = new HashSet<>();
    private final Set<SAPReference> references = new HashSet<>();

    public Set<User> getUsers() {
        return users;
    }

    public Set<Rack> getRacks() {
        return racks;
    }

    public Set<Cell> getCells() {
        return cells;
    }

    public Set<SAPReference> getReferences() {
        return references;
    }

    public void updateUsers(ArrayList<User> list) {
        if (users.isEmpty()) {
            users.addAll(list);
        }else {
            users.clear();
            users.addAll(list);
        }
    }

    public void updateRack(ArrayList<Rack> list) {
        if (racks.isEmpty()) {
            racks.addAll(list);
        }else {
            racks.clear();
            racks.addAll(list);
        }
    }
    public void updateCells(ArrayList<Cell> list) {
        if (cells.isEmpty()) {
            cells.addAll(list);
        }else {
            cells.clear();
            cells.addAll(list);
        }
    }
    public void updateReferences(ArrayList<SAPReference> list) {
        if (references.isEmpty()) {
            references.addAll(list);
        }else {
            references.clear();
            references.addAll(list);
        }
    }
}
