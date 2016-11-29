/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.web.secure;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@Controller
public class SampleWebSecureApplication extends WebMvcConfigurerAdapter {

  @GetMapping("/")
  public String home(Map<String, Object> model) {
    model.put("message", "Hello World");
    model.put("title", "Hello Home");
    model.put("date", new Date());
    model.put("loggedInAs", this.loggedInAs());
    return "home";
  }

  @GetMapping("/foo")
  public ModelAndView foo() {
    ModelAndView model = new ModelAndView("home");
    model.addObject("message", "Foo");
    model.addObject("title", "Foo");
    model.addObject("date", new Date());
    model.addObject("loggedInAs", this.loggedInAs());
    return model;
  }

  @HasAge(age = 60)
  @GetMapping("/bar")
  public ModelAndView bar() {
    ModelAndView model = new ModelAndView("home");
    model.addObject("message", "bar");
    model.addObject("title", "bar");
    model.addObject("date", new Date());
    model.addObject("loggedInAs", this.loggedInAs());
    return model;
  }

  @Authenticated
  @GetMapping("/frotz")
  public ModelAndView frotz() {
    ModelAndView model = new ModelAndView("home");
    
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();    
    
    model.addObject("message", "frotz: Details is of type: " + principal.getClass().getName());
    model.addObject("title", "frotz");
    model.addObject("date", new Date());
    model.addObject("loggedInAs", this.loggedInAs());
    return model;
  }

  @GetMapping("/a")
  public ModelAndView a() {
    ModelAndView model = new ModelAndView("home");
    model.addObject("message", "A");
    model.addObject("title", "A");
    model.addObject("date", new Date());
    model.addObject("loggedInAs", this.loggedInAs());
    return model;
  }
    
  @GetMapping("/b")
  @PreAuthorize("isFullyAuthenticated() and principal.levelOfAssurance == 'loa3'")
  public ModelAndView b() {
    ModelAndView model = new ModelAndView("home");
    model.addObject("message", "B - Requires LoA 3");
    model.addObject("title", "B");
    model.addObject("date", new Date());
    model.addObject("loggedInAs", this.loggedInAs());
    return model;
  }  

  private String loggedInAs() {
    try {
      return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    catch (Exception e) {
      return null;
    }
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/login").setViewName("login");
  }

  public static void main(String[] args) throws Exception {
    new SpringApplicationBuilder(SampleWebSecureApplication.class).run(args);
  }

  @Configuration
  @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
  @EnableGlobalMethodSecurity(prePostEnabled = true)
  protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {

    @Bean
    public FakeService fakeService() {
      return new FakeService();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

      http.rememberMe().disable();
            
      // AuthenticationProvider  ap;
      
            
      http
        .authorizeRequests()
          .antMatchers(HttpMethod.GET, "/foo").access("hasRole('USER') and @pm.hasAge('60')")
          .antMatchers(HttpMethod.GET, "/bar").fullyAuthenticated()
          .antMatchers("/b").access("isFullyAuthenticated() and principal.levelOfAssurance == 'loa3'")
        .and()
          .formLogin()
            .loginPage("/login")
            .failureUrl("/login?error")
            .permitAll()
        .and()
          .logout()
            .permitAll();
      
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
            
      auth.userDetailsService(new TestUserDetailsService());
//      auth
//        .inMemoryAuthentication()
//        .withUser("admin").password("admin").roles("ADMIN", "USER")
//        .and()
//        .withUser("user").password("user").roles("USER");
    }
  }
  
  public static class TestUserDetailsService implements UserDetailsService {
    
    private Map<String, UserDetails> repository;
    
    public TestUserDetailsService() {
      this.repository = new HashMap<String, UserDetails>();
      
      PmUserDetails u = new PmUserDetails("user", "user", "USER");
      u.setLevelOfAssurance("loa2");
      this.repository.put(u.getUsername(), u);
      
      u = new PmUserDetails("admin", "admin", "USER", "ADMIN");
      u.setLevelOfAssurance("loa3");
      this.repository.put(u.getUsername(), u);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      UserDetails u = this.repository.get(username);
      if (u == null) {
        throw new UsernameNotFoundException("User '" + username + "' was not found");
      }
      return u;
    }
    
  }
    
  @Component("pm")
  public class WebSecurity {

    @Autowired private FakeService service;

    public boolean hasAge(int age) {
      return this.service.getAge(SecurityContextHolder.getContext().getAuthentication().getName()) >= age;
    }

    public boolean isOlderThan50() {
      return this.hasAge(51);
    }
  }

  public static class FakeService {

    public int getAge(String userId) {
      if ("user".equals(userId)) {
        return 47;
      }
      if ("admin".equals(userId)) {
        return 64;
      }
      return -1;
    }

  }

}
