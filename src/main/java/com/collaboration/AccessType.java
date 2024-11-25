package com.collaboration;

public enum AccessType {

  CREATE('C'),
  READ('R'),
  UPDATE('U'),
  DELETE('D'),
  EXECUTE('X');
  
  private final char accessTypeId;
  
  AccessType(char accessTypeId) {
    this.accessTypeId = accessTypeId;
  }
  
  public char getAccessTypeId() {
    return accessTypeId;
  }

  public static AccessType getByAccessTypeId(char accessTypeId) {
    for (AccessType type : values()) {
        if (type.accessTypeId == accessTypeId) {
            return type;
        }
    }
    throw new IllegalArgumentException("No AccessType found with id: " + accessTypeId);
  }
}
