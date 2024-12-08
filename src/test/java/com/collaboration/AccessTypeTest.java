/**
 *  AccessTypeTest
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class AccessTypeTest {

  @Test
  public void testGetAccessTypeId() {
    // Prüfen, ob die AccessType-IDs korrekt zurückgegeben werden
    assertEquals('C', AccessType.CREATE.getAccessTypeId());
    assertEquals('R', AccessType.READ.getAccessTypeId());
    assertEquals('U', AccessType.UPDATE.getAccessTypeId());
    assertEquals('D', AccessType.DELETE.getAccessTypeId());
    assertEquals('X', AccessType.EXECUTE.getAccessTypeId());
  }

  @Test
  public void testGetByAccessTypeId() {
    // Prüfen, ob die korrekten AccessType-Werte zurückgegeben werden
    assertEquals(AccessType.CREATE, AccessType.getByAccessTypeId('C'));
    assertEquals(AccessType.READ, AccessType.getByAccessTypeId('R'));
    assertEquals(AccessType.UPDATE, AccessType.getByAccessTypeId('U'));
    assertEquals(AccessType.DELETE, AccessType.getByAccessTypeId('D'));
    assertEquals(AccessType.EXECUTE, AccessType.getByAccessTypeId('X'));

    // Prüfen, ob eine IllegalArgumentException geworfen wird, wenn kein passender AccessType gefunden wird
    assertThrows(IllegalArgumentException.class, () -> AccessType.getByAccessTypeId('Y'));
  }

  @Test
  public void testGetAllAccessTypes() {
    // Prüfen, ob alle AccessType-Werte in der korrekten Reihenfolge zurückgegeben werden
    List<AccessType> allAccessTypes = AccessType.getAllAccessTypes();
    assertEquals(5, allAccessTypes.size());
    assertEquals(AccessType.CREATE, allAccessTypes.get(0));
    assertEquals(AccessType.READ, allAccessTypes.get(1));
    assertEquals(AccessType.UPDATE, allAccessTypes.get(2));
    assertEquals(AccessType.DELETE, allAccessTypes.get(3));
    assertEquals(AccessType.EXECUTE, allAccessTypes.get(4));
  }
}
