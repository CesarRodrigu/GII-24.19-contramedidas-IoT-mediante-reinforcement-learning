package es.cesar.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;
import java.util.TimeZone;

/**
 * The class of Locale config. That is used to configure the locale resolver and the locale change interceptor. It sets
 * the default locale to US and the default time zone to UTC.
 */
@Configuration
public class LocaleConfig implements WebMvcConfigurer {

    /**
     * Instantiates a new Locale config.
     */
    public LocaleConfig() {
        super();
    }

    /**
     * Locale resolver that resolves the locale based on a cookie. This method configures a {@link CookieLocaleResolver}
     * with a default locale of US and a default time zone of UTC.
     *
     * @return the configured locale resolver
     */
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver localeResolver = new CookieLocaleResolver("locale");
        localeResolver.setDefaultLocale(Locale.US);
        localeResolver.setDefaultTimeZone(TimeZone.getTimeZone("UTC"));
        return localeResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    /**
     * Locale change interceptor that changes the current locale based on a request parameter.
     *
     * @return the locale change interceptor with the parameter name set to "lang"
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }
}