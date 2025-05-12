package es.cesar.backend.service;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

@Service
public class LocaleFormattingService {

    private final MessageSource messageSource;

    public LocaleFormattingService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    public String formatPlural(String key, long count) {
        return messageSource.getMessage(key, new Object[]{count},
                LocaleContextHolder.getLocale());
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

    public String formatCurrency(double amount) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        Currency currency;

        if (currentLocale.getCountry().equals("ES")) {
            currency = Currency.getInstance("EUR");
        } else if (currentLocale.getLanguage().equals("en")) {
            currency = Currency.getInstance("USD");
        } else {
            currency = Currency.getInstance("USD");
        }

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(currentLocale);
        currencyFormatter.setCurrency(currency);

        return currencyFormatter.format(amount);
    }

    public String formatNumber(double number) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        NumberFormat numberFormatter = NumberFormat.getNumberInstance(currentLocale);
        return numberFormatter.format(number);
    }

    public String formatPercentage(double number) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        NumberFormat percentFormatter = NumberFormat.getPercentInstance(currentLocale);
        return percentFormatter.format(number);
    }
}
