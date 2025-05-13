package es.cesar.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

@Service
public class LocaleFormattingService {

    private final MessageSource messageSource;

    @Autowired
    public LocaleFormattingService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    public String formatDate(Date date, int style) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        DateFormat dateFormatter = DateFormat.getDateInstance(style, currentLocale);
        return dateFormatter.format(date);
    }

    public String formatTime(Date date, int style) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        DateFormat timeFormatter = DateFormat.getTimeInstance(style, currentLocale);
        return timeFormatter.format(date);
    }

    public String formatDateTime(Date date, int dateStyle, int timeStyle) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        DateFormat formatter = DateFormat.getDateTimeInstance(
                dateStyle, timeStyle, currentLocale);
        return formatter.format(date);
    }
}
