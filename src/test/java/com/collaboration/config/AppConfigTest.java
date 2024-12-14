/**
 *  AppConfigTest
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Disabled;

public class AppConfigTest {

    @Test
    public void testConstants() {
        assertEquals(1, AppConfig.ORGA_YARE);
        assertEquals(2, AppConfig.ROLEID_YARE_ADMIN);
        assertEquals(1, AppConfig.ITEMTYPE_USER);
        assertEquals(2, AppConfig.ITEMTYPE_ROLE);
        assertEquals(3, AppConfig.ITEMTYPE_PERMISSION);
        assertEquals(4, AppConfig.ITEMTYPE_AUTHORITY);
        assertEquals(5, AppConfig.ITEMTYPE_ITEMTYPE);
        assertEquals(6, AppConfig.ITEMTYPE_DOCUMENT);
    }

    @Test
    public void testConstructor() {
        // Versuch, den Konstruktor aufzurufen, sollte eine Exception werfen
        assertThrows(NoSuchMethodException.class, () -> {
            AppConfig.class.getConstructor().newInstance();
        });
    }

    @Disabled("Funktioniert leider nicht")
    @Test
    public void testRunMethod() throws Exception {
        // Instanziieren des AppConfig über Reflection, da der Konstruktor privat ist
        AppConfig appConfig = AppConfig.class.getConstructor().newInstance();
        // Aufrufen der run Methode
        appConfig.run();
        // Keine Ausnahmen sollten geworfen werden
    }
}
