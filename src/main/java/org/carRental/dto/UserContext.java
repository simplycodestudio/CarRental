package org.carRental.dto;


import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class UserContext {
   private Long userId;
   private String poswiadczeniaUzytkownika="";

   public Long getUserId() {
      return userId;
   }

   public String getPoswiadczeniaUzytkownika() {
      return poswiadczeniaUzytkownika;
   }
}
