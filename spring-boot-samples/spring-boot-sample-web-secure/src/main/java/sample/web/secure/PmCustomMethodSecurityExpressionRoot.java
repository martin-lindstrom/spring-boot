package sample.web.secure;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.WebSecurityExpressionRoot;

public class PmCustomMethodSecurityExpressionRoot extends WebSecurityExpressionRoot {
  
  /** Allows direct access to the request object */
  public final HttpServletRequest request;

  public PmCustomMethodSecurityExpressionRoot(Authentication a, FilterInvocation fi) {
    super(a, fi);
    this.request = fi.getRequest();
  }
  
  public boolean hasLoa(String loa) {
    
    Object principal = this.getPrincipal();
    if (principal == null) {
      return false;
    }
    
    if (principal instanceof PmUserDetails) {
      // Dummy impl
      String theLoa = ((PmUserDetails) principal).getLevelOfAssurance();
      if (theLoa == null) {
        return false;
      }
      if ("loa1".equals(loa) && (theLoa.equals("loa1") || theLoa.equals("loa2") || theLoa.equals("loa3") || theLoa.equals("loa4"))) {
        return true;
      }
      if ("loa2".equals(loa) && (theLoa.equals("loa2") || theLoa.equals("loa3") || theLoa.equals("loa4"))) {
        return true;
      }
      if ("loa3".equals(loa) && (theLoa.equals("loa3") || theLoa.equals("loa4"))) {
        return true;
      }
      if ("loa4".equals(loa) && theLoa.equals("loa4")) {
        return true;
      }      
    }
    
    return false;
  }

}
