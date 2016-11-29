package sample.web.secure;

import org.springframework.security.access.expression.SecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

public class PmCustomMethodSecurityExpressionHandler extends DefaultWebSecurityExpressionHandler {

  private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

  public PmCustomMethodSecurityExpressionHandler() {
  }
  
  @Override
  protected SecurityExpressionOperations createSecurityExpressionRoot(
          Authentication authentication, FilterInvocation fi) {
    
    SecurityExpressionOperations root = super.createSecurityExpressionRoot(authentication, fi);    
    return root;
  }

//  @Override
//  protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
//      Authentication authentication, MethodInvocation invocation) {
//    PmCustomMethodSecurityExpressionRoot root = new PmCustomMethodSecurityExpressionRoot(authentication);
//    root.setPermissionEvaluator(getPermissionEvaluator());
//    root.setTrustResolver(this.trustResolver);
//    root.setRoleHierarchy(getRoleHierarchy());
//    return root;
//  }

}
