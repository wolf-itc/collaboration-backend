/**
 *  MainApplication - SpringBoot-Start-Class
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration;

import java.util.Arrays;
import java.util.List;

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

  public static List<AccessType> getAllAccessTypes() {
    return Arrays.asList(AccessType.values());
  }
}
