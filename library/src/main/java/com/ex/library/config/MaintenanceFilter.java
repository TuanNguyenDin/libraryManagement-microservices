package com.ex.library.config;

import com.ex.library.exception.ErrorCode;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.ApplicationAvailabilityBean;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Slf4j
@Configuration
public class MaintenanceFilter implements Filter {

    static final String[] API_MAINTENANCE_URI = {"/user/*", "/book/*", "/auth/*"};

    private final ApplicationAvailability applicationAvailability;

    public MaintenanceFilter(ApplicationAvailability applicationAvailability) {
        this.applicationAvailability = applicationAvailability;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("Server status is :" + applicationAvailability.getReadinessState());
        log.info("Request is :" + ((HttpServletRequest) request).getRequestURI());
        if (applicationAvailability.getReadinessState().equals(ReadinessState.REFUSING_TRAFFIC)
                && !((HttpServletRequest) request).getRequestURI().startsWith("/maintenance")
        ) {

            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setContentType("application/json"); // Set content type to JSON
            httpResponse.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE); // Set HTTP status code
            httpResponse.getWriter().write(ErrorCode.SERVER_IN_MAINTENANCE.getMessage()); // Write the JSON response

        } else {
            chain.doFilter(request, response);
        }
    }

    @Bean
    public FilterRegistrationBean<MaintenanceFilter> maintenanceModeFilter() {
        FilterRegistrationBean<MaintenanceFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new MaintenanceFilter(applicationAvailability));
        registrationBean.addUrlPatterns(API_MAINTENANCE_URI);
        return registrationBean;
    }
}
