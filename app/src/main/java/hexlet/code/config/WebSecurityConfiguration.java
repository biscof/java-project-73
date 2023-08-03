package hexlet.code.config;

import hexlet.code.filter.JwtAuthenticationFilter;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;

import java.util.Optional;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    public final UserDetailsService userDetailsService;
    public final UserRepository userRepository;
    public final ApplicationContext applicationContext;

    @Value("${base-url}")
    private String baseUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // Set session management to stateless.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) -> requests
                                .requestMatchers(HttpMethod.POST, baseUrl + "/users").permitAll()
                                .requestMatchers(HttpMethod.POST, baseUrl + "/login").permitAll()
                                .requestMatchers(HttpMethod.GET, baseUrl + "/users").permitAll()
                                .requestMatchers(
                                        new NegatedRequestMatcher(
                                                new AntPathRequestMatcher(baseUrl + "/**"))).permitAll()
                                .requestMatchers(HttpMethod.DELETE, baseUrl + "/users/*")
                                .access((authentication, context) ->
                                        new AuthorizationDecision(hasSelfId(context.getRequest())))
                                .requestMatchers(HttpMethod.PUT, baseUrl + "/users/*")
                                .access((authentication, context) ->
                                        new AuthorizationDecision(hasSelfId(context.getRequest())))
                                .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        // Default SpringBoot AuthenticationManager implementation.
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private boolean hasSelfId(HttpServletRequest request) {
        String[] uriItems = request.getRequestURI().split("/");
        Long idClaim = Long.parseLong(uriItems[uriItems.length - 1]);

        if (request.getUserPrincipal() != null) {
            Optional<User> user = userRepository.findUserByEmail(request.getUserPrincipal().getName());
            return user.isPresent() && user.get().getId().equals(idClaim);
        } else {
            return false;
        }
    }
}