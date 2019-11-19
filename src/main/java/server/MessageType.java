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
    LOG_REQUEST,
    LOG_UPDATED,
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
    USERS_UPDATE,
    IMPORT_EXPORT,
    EVENT,
    PALLET_UPDATE
}
