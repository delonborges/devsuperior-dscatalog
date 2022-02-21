package com.delonborges.dscatalog.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private static final String[] PUBLIC = {"/oauth/token"};
    private static final String[] RESTRICTED = {"/products/**", "/categories/**"};
    private static final String[] ADMIN = {"/users/**"};

    private final JwtTokenStore tokenStore;

    public ResourceServerConfig(JwtTokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.tokenStore(tokenStore);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(PUBLIC).permitAll()
                .antMatchers(HttpMethod.GET, RESTRICTED).permitAll()
                .antMatchers(RESTRICTED).hasAnyRole("OPERATOR", "ADMIN")
                .antMatchers(ADMIN).hasRole("ADMIN")
                .anyRequest().authenticated();
    }
}
