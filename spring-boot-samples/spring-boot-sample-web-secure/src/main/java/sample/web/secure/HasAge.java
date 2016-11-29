package sample.web.secure;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@pm.isOlderThan50()")
public @interface HasAge {

  int age();
  
}
