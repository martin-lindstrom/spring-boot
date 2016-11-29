package sample.web.secure;

import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.access.prepost.PrePostInvocationAttributeFactory;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

  // PrePostAnnotationSecurityMetadataSource j;
  
  PrePostInvocationAttributeFactory f;


  // For support of own annotations
  @Override
  protected MethodSecurityMetadataSource customMethodSecurityMetadataSource() {
    
    return super.customMethodSecurityMetadataSource();
  }
  
  // TODO: add parser of own SPEL

}
