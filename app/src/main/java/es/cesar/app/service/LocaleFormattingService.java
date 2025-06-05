package es.cesar.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * The class LocaleFormattingService, that provides localization and formatting services.
 */
@Service
public class LocaleFormattingService {

    private final MessageSource messageSource;

    /**
     * Instantiates a new Locale formatting service.
     *
     * @param messageSource the message source
     */
    @Autowired
    public LocaleFormattingService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Gets message.
     *
     * @param key  the key
     * @param args the args
     *
     * @return the message
     */
    public String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    /**
     * Format date string.
     *
     * @param date  the date
     * @param style the style
     *
     * @return the string
     */
    public String formatDate(Date date, int style) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        DateFormat dateFormatter = DateFormat.getDateInstance(style, currentLocale);
        return dateFormatter.format(date);
    }

    /**
     * Format time string.
     *
     * @param date  the date
     * @param style the style
     *
     * @return the string
     */
    public String formatTime(Date date, int style) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        DateFormat timeFormatter = DateFormat.getTimeInstance(style, currentLocale);
        return timeFormatter.format(date);
    }

    /**
     * Format date time string.
     *
     * @param date      the date
     * @param dateStyle the date style
     * @param timeStyle the time style
     *
     * @return the string
     */
    public String formatDateTime(Date date, int dateStyle, int timeStyle) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        DateFormat formatter = DateFormat.getDateTimeInstance(
                dateStyle, timeStyle, currentLocale);
        return formatter.format(date);
    }
}
