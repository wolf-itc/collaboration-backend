/**
 *  CollaborationExceptionTest
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CollaborationExceptionTest {

    @Test
    public void testConstructorAndMessage() {
        // Erstellen einer CollaborationException mit einem bestimmten Grund
      CollaborationException.CollaborationExceptionReason reason = CollaborationException.CollaborationExceptionReason.USERNAME_ALREADY_EXISTS;
        CollaborationException exception = new CollaborationException(reason);

        // Überprüfen der Ausnahmegründe und -codes
        assertEquals(reason, exception.getExceptionReason());
        assertEquals(reason.getExceptionCode(), exception.getExceptionReason().getExceptionCode());

        // Überprüfen der formatierten Fehlermeldung
        String expectedMessage = "{ \"ExceptionType\":\"CollaborationException\", \"ExceptionReason\":\"USERNAME_ALREADY_EXISTS\", \"ExceptionCode\":\"1\"}";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testAllReasons() {
        // Überprüfen aller Ausnahmegründe
        for (CollaborationException.CollaborationExceptionReason reason : CollaborationException.CollaborationExceptionReason.values()) {
            CollaborationException exception = new CollaborationException(reason);
            assertEquals(reason, exception.getExceptionReason());
            assertEquals(reason.getExceptionCode(), exception.getExceptionReason().getExceptionCode());
            String expectedMessage = "{ \"ExceptionType\":\"CollaborationException\", \"ExceptionReason\":\"" + reason.toString() + "\", \"ExceptionCode\":\"" + reason.getExceptionCode() + "\"}";
            assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Test
    public void testSerialization() throws Exception {
        // Testen der Serialisierung und Deserialisierung
        CollaborationException.CollaborationExceptionReason reason = CollaborationException.CollaborationExceptionReason.USERNAME_ALREADY_EXISTS;
        CollaborationException originalException = new CollaborationException(reason);

        // Serialisieren
        byte[] bytes = serialize(originalException);

        // Deserialisieren
        CollaborationException deserializedException = (CollaborationException) deserialize(bytes);

        // Überprüfen der Ausnahmegründe und -codes nach der Deserialisierung
        assertEquals(originalException.getExceptionReason(), deserializedException.getExceptionReason());
        assertEquals(originalException.getExceptionReason().getExceptionCode(), deserializedException.getExceptionReason().getExceptionCode());
        assertEquals(originalException.getMessage(), deserializedException.getMessage());
    }

    // Hilfsmethoden für die Serialisierung und Deserialisierung
    private byte[] serialize(Object obj) throws Exception {
        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
        java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.close();
        return bos.toByteArray();
    }

    private Object deserialize(byte[] bytes) throws Exception {
        java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(bytes);
        java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bis);
        Object obj = ois.readObject();
        ois.close();
        return obj;
    }
}
