package model;

public enum UserRoles {
    ADMIN( "Administrator"),
    MANAGER("LogisticManager"),
    KEEPER("StoreKeeper"),
    DRIVER("Driver");

    public final String label;

    private UserRoles(String role) {
        this.label = role;
    }
}
