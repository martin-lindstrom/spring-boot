package sample.web.secure;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class PmUserDetails extends User {

  private static final long serialVersionUID = 2332456700464923590L;
  
  private String levelOfAssurance;

  public PmUserDetails(String username, String password, String...roles) {      
    super(username, password, Arrays.asList(roles).stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)).collect(Collectors.toList()));
  }

  public String getLevelOfAssurance() {
    return this.levelOfAssurance;
  }

  public void setLevelOfAssurance(String levelOfAssurance) {
    this.levelOfAssurance = levelOfAssurance;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((this.levelOfAssurance == null) ? 0 : this.levelOfAssurance.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    PmUserDetails other = (PmUserDetails) obj;
    if (this.levelOfAssurance == null) {
      if (other.levelOfAssurance != null) {
        return false;
      }
    }
    else if (!this.levelOfAssurance.equals(other.levelOfAssurance)) {
      return false;
    }
    return true;
  }

}
