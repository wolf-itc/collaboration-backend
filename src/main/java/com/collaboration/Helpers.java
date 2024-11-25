/**
 *  MainApplication - SpringBoot-Start-Class
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration;

import com.collaboration.config.CollaborationException;
import com.collaboration.config.CollaborationException.CollaborationExceptionReason;

public class Helpers {
  
  static public void ensureAlphabetic(final String stringToBeChecked) throws CollaborationException {
    if (!stringToBeChecked.matches("^[a-zA-Z]+$")) {
      throw new CollaborationException(CollaborationExceptionReason.INVALID_VALUE);
    }
  }
}
