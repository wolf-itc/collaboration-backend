/**
 *  UserService
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.config;

import org.springframework.boot.CommandLineRunner;

public final class AppConfig implements CommandLineRunner {
  public static final long ORGA_YARE = 1;

  public static final long ROLEID_YARE_ADMIN = 2;

  public static final long ITEMTYPE_USER = 1;
  public static final long ITEMTYPE_ROLE = 2;
  public static final long ITEMTYPE_PERMISSION = 3;
  public static final long ITEMTYPE_AUTHORITY = 4;
  public static final long ITEMTYPE_ITEMTYPE = 5;
  public static final long ITEMTYPE_DOCUMENT = 6;

  private AppConfig() {
  }
  
  @Override
  public void run(String... args) throws Exception {
  }
}
