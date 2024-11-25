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
