package com.solpyra;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SolpyraWalletApplication {

  public static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone("GMT+07:00"));
    SpringApplication.run(SolpyraWalletApplication.class, args);
  }

}
