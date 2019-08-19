package server;

public enum  MessageType {
    USER_REQUEST,   // - запрос имени.
    USERS_LIST,     // - список пользователей
    USER_NAME,      // - имя пользователя.
    ACCESS_GRANTED,  // - доступ разрешен.
    ACCESS_DENIED,  // - доступ разрешен.
    TEXT,           // - текстовое сообщение.
    RACK_UPDATE,
    CHECK_ACCESS,   // - проверка пароля
    REFERENCE_REQUEST,
    CELL_UPDATE,
    REFERENCE_UPDATE,
    LOAD_PALLET,      // - размещение паллета
    GOODBYE,
    SERVER_IS_STOPPED,
    PICKUP_PALLET,
    SETTINGS,
    FORCED_PICKUP,
    CHANGE_RACK,
    CHANGE_REFERENCE,
    CHANGE_USER,
    CHANGE_LINK_RACK_TO_REF,
    USERS_UPDATE,
    IMPORT_EXPORT
}
