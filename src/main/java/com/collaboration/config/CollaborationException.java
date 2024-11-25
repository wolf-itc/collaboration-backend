/**
 *  CollaborationException
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.config;

public class CollaborationException extends Exception {
  private static final long serialVersionUID = 16324457847354L;

	public enum CollaborationExceptionReason {
    USERNAME_ALREADY_EXISTS( 1 ),
    EMAIL_ALREADY_EXISTS( 2 ),
    INVALID_ACTIVATIONKEY( 3 ),
    ACCOUNT_IS_NOT_ENABLED( 4 ), // never has been enabled before
    UNKNOWN_USERNAME( 5 ),
    WRONG_PASSWORD( 6 ),
    ACCOUNT_HAS_BEEN_LOCKED( 7 ), // actual done because of too much fail logins
    ACCOUNT_IS_LOCKED( 8 ), // done before because of too much fail logins
    UNKNOWN_EMAIL( 9),
    PW_NOT_CHANGEABLE(10),
	  NOT_FOUND(11),
    ACCESS_DENIED(12),
    INVALID_VALUE(13);

    private final int exceptionCode;

    CollaborationExceptionReason(int exceptionCode ) {
      this.exceptionCode = exceptionCode;
    }

    public int getExceptionCode()
    {
      return exceptionCode;
    }
  }

  private CollaborationExceptionReason exceptionReason;

  public CollaborationException( CollaborationExceptionReason exceptionReason )
  {
    super( "ExceptionReason="+ exceptionReason+ "; ExceptionCode="+ exceptionReason.getExceptionCode() );
    this.exceptionReason = exceptionReason;
  }

  public CollaborationExceptionReason getExceptionReason() {
    return exceptionReason;
  }
  
  @Override
  public String getMessage() {
    return "{ \"ExceptionType\":\"CollaborationException\", " +
         "\"ExceptionReason\":\""+ exceptionReason.toString()+ "\"," +
         "\"ExceptionCode\":\""+ exceptionReason.getExceptionCode()+ "\"}";
  }
}
