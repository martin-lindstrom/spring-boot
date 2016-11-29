package sample.web.secure;

import java.lang.annotation.Annotation;

public class PmCustomAnnotationInstaller {
  
  private Class<? extends Annotation> type;

  public PmCustomAnnotationInstaller() {
  }

  public Class<? extends Annotation> getType() {
    return this.type;
  }

  public void setType(Class<? extends Annotation> type) {
    this.type = type;
  }

}
