package com.besafx.app.config;
import com.besafx.app.component.LocationFinder;
import com.besafx.app.entity.Person;
import com.besafx.app.service.PersonService;
import com.besafx.app.service.RoleService;
import com.besafx.app.ws.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final static Logger log = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Autowired
    private PersonService personService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private LocationFinder locationFinder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/ui/**").permitAll()
                .antMatchers("/api/**").permitAll()
                .antMatchers("/company").access("hasRole('ROLE_COMPANY_CREATE') or hasRole('ROLE_COMPANY_UPDATE') or hasRole('ROLE_COMPANY_DELETE') or hasRole('ROLE_COMPANY_REPORT')")
                .antMatchers("/branch").access("hasRole('ROLE_BRANCH_CREATE') or hasRole('ROLE_BRANCH_UPDATE') or hasRole('ROLE_BRANCH_DELETE') or hasRole('ROLE_BRANCH_REPORT')")
                .antMatchers("/department").access("hasRole('ROLE_DEPARTMENT_CREATE') or hasRole('ROLE_DEPARTMENT_UPDATE') or hasRole('ROLE_DEPARTMENT_DELETE') or hasRole('ROLE_DEPARTMENT_REPORT')")
                .antMatchers("/employee").access("hasRole('ROLE_EMPLOYEE_CREATE') or hasRole('ROLE_EMPLOYEE_UPDATE') or hasRole('ROLE_EMPLOYEE_DELETE') or hasRole('ROLE_EMPLOYEE_REPORT')")
                .antMatchers("/team").access("hasRole('ROLE_TEAM_CREATE') or hasRole('ROLE_TEAM_UPDATE') or hasRole('ROLE_TEAM_DELETE') or hasRole('ROLE_TEAM_REPORT')")
                .antMatchers("/task").access("hasRole('ROLE_TASK_CREATE') or hasRole('ROLE_TASK_UPDATE') or hasRole('ROLE_TASK_DELETE') or hasRole('ROLE_TASK_REPORT')")
                .antMatchers("/person").access("hasRole('ROLE_PERSON_CREATE') or hasRole('ROLE_PERSON_UPDATE') or hasRole('ROLE_PERSON_DELETE') or hasRole('ROLE_PERSON_REPORT')")
                .anyRequest().authenticated();
        http.formLogin()
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/menu")
                .permitAll();
        http.logout()
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .logoutSuccessUrl("/")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
        http.rememberMe();
        http.csrf().disable();
        http.sessionManagement()
                .maximumSessions(2)
                .sessionRegistry(sessionRegistry());
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher() {
            @Override
            public void sessionCreated(HttpSessionEvent event) {
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
                log.info("RemoteUser " + request.getRemoteUser());
                log.info("RequestURI " + request.getRequestURI());
                log.info("LocalAddr " + request.getLocalAddr());
                log.info("LocalName " + request.getLocalName());
                log.info("RemoteAddr " + request.getRemoteAddr());
                try {
                    InetAddress inetAddress = InetAddress.getByName(request.getRemoteAddr());
                    log.info("PC Name: " + getHostName(inetAddress));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                log.info("RemoteHost " + request.getRemoteHost());
                log.info("ServerName " + request.getServerName());
                log.info("RemotePort " + request.getRemotePort());
                super.sessionCreated(event);
            }

            @Override
            public void sessionDestroyed(HttpSessionEvent event) {
                SecurityContextImpl securityContext = (SecurityContextImpl) event.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
                if (securityContext != null) {
                    UserDetails userDetails = (UserDetails) securityContext.getAuthentication().getPrincipal();
                    Person person = personService.findByEmail(userDetails.getUsername());
                    person.setActive(false);
                    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
                    person.setIpAddress(request.getRemoteAddr());
                    person.setHostName(request.getRemoteHost());
                    personService.save(person);
                }
                super.sessionDestroyed(event);
            }
        });
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService((String email) -> {
                    Person person = personService.findByEmail(email);
                    List<GrantedAuthority> authorities = new ArrayList<>();
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        if (person == null) {
                            throw new UsernameNotFoundException(email);
                        }
                        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
                        person.setLastLoginLocation(
                                (Optional.ofNullable(locationFinder.getCountry(request.getRemoteAddr())).isPresent() ? (locationFinder.getCountry(request.getRemoteAddr()).getName() + "، ") : "")
                                        + (Optional.ofNullable(locationFinder.getCity(request.getRemoteAddr())).isPresent() ? (locationFinder.getCity(request.getRemoteAddr()).getName() + "، ") : "")
                                        + (Optional.ofNullable(locationFinder.getMostSpecificSubdivision(request.getRemoteAddr())).isPresent() ? locationFinder.getMostSpecificSubdivision(request.getRemoteAddr()).getName() : ""));
                        person.setLastLoginDate(new Date());
                        person.setLastUpdate(new Date());
                        person.setActive(true);
                        person.setIpAddress(request.getRemoteAddr());
                        try {
                            InetAddress inetAddress = InetAddress.getByName(request.getRemoteAddr());
                            person.setHostName(getHostName(inetAddress));
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                        personService.save(person);
                        authorities.add(new SimpleGrantedAuthority("ROLE_PROFILE_UPDATE"));
                        roleService.findByTeam(person.getTeam()).stream().forEach(role -> {
                            if (role.getPermission().getCreateEntity()) {
                                SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_" + role.getPermission().getScreen().getCode() + "_CREATE");
                                authorities.add(simpleGrantedAuthority);
                            }
                            if (role.getPermission().getUpdateEntity()) {
                                SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_" + role.getPermission().getScreen().getCode() + "_UPDATE");
                                authorities.add(simpleGrantedAuthority);
                            }
                            if (role.getPermission().getDeleteEntity()) {
                                SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_" + role.getPermission().getScreen().getCode() + "_DELETE");
                                authorities.add(simpleGrantedAuthority);
                            }
                            if (role.getPermission().getReportEntity()) {
                                SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_" + role.getPermission().getScreen().getCode() + "_REPORT");
                                authorities.add(simpleGrantedAuthority);
                            }
                        });

                    }
                    return new org.springframework.security.core.userdetails.User(person.getEmail(), person.getPassword(),
                            person.getEnabled(), true, true, true, authorities);
                }
        ).passwordEncoder(passwordEncoder);

    }

    private String getHostName(InetAddress inaHost) throws UnknownHostException {
        try {
            Class clazz = Class.forName("java.net.InetAddress");
            Constructor[] constructors = clazz.getDeclaredConstructors();
            constructors[0].setAccessible(true);
            InetAddress ina = (InetAddress) constructors[0].newInstance();
            Field[] fields = ina.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals("nameService")) {
                    field.setAccessible(true);
                    Method[] methods = field.get(null).getClass().getDeclaredMethods();
                    for (Method method : methods) {
                        if (method.getName().equals("getHostByAddr")) {
                            method.setAccessible(true);
                            return (String) method.invoke(field.get(null), inaHost.getAddress());
                        }
                    }
                }
            }
        } catch (ClassNotFoundException cnfe) {
        } catch (IllegalAccessException iae) {
        } catch (InstantiationException ie) {
        } catch (InvocationTargetException ite) {
            throw (UnknownHostException) ite.getCause();
        }
        return null;
    }
}
