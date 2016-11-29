package sample.web.secure;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.method.AbstractMethodSecurityMetadataSource;
import org.springframework.security.access.prepost.PrePostInvocationAttributeFactory;
import org.springframework.util.ClassUtils;

public class PmCustomAnnotationSecurityMetadataSource extends AbstractMethodSecurityMetadataSource {

  private final PrePostInvocationAttributeFactory attributeFactory;

  List<PmCustomAnnotationInstaller> pmCustomAnnotationInstallers;

  public PmCustomAnnotationSecurityMetadataSource(PrePostInvocationAttributeFactory attributeFactory) {
    this.attributeFactory = attributeFactory;
  }

  @Override
  public Collection<ConfigAttribute> getAttributes(Method method, Class<?> targetClass) {

    if (method.getDeclaringClass() == Object.class) {
      return Collections.emptyList();
    }
    logger.trace("Looking for PM custom annotations for method '" + method.getName() + "' on target class '" + targetClass + "'");

    List<ConfigAttribute> attrs = new ArrayList<ConfigAttribute>();

    for (PmCustomAnnotationInstaller installer : this.pmCustomAnnotationInstallers) {
      Annotation a = this.findAnnotation(method, targetClass, installer.getType());
    }

    return null;
  }

  @Override
  public Collection<ConfigAttribute> getAllConfigAttributes() {
    return null;
  }

  private <A extends Annotation> A findAnnotation(Method method, Class<?> targetClass, Class<A> annotationClass) {
    
    // The method may be on an interface, but we need attributes from the target class.
    // If the target class is null, the method will be unchanged.
    //
    Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
    A annotation = AnnotationUtils.findAnnotation(specificMethod, annotationClass);

    if (annotation != null) {
      logger.debug(annotation + " found on specific method: " + specificMethod);
      return annotation;
    }

    // Check the original (e.g. interface) method
    //
    if (specificMethod != method) {
      annotation = AnnotationUtils.findAnnotation(method, annotationClass);

      if (annotation != null) {
        logger.debug(annotation + " found on: " + method);
        return annotation;
      }
    }

    // Check the class-level (note declaringClass, not targetClass, which may not actually implement the method)
    annotation = AnnotationUtils.findAnnotation(specificMethod.getDeclaringClass(), annotationClass);

    if (annotation != null) {
      logger.debug(annotation + " found on: " + specificMethod.getDeclaringClass().getName());
      return annotation;
    }

    return null;
  }

}
