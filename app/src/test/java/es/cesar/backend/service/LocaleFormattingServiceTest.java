package es.cesar.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class LocaleFormattingServiceTest {

    private MessageSource messageSource;
    private LocaleFormattingService localeFormattingService;

    @BeforeEach
    void setUp() {
        messageSource = mock(MessageSource.class);
        localeFormattingService = new LocaleFormattingService(messageSource);
    }

    @Test
    void testGetMessage() {
        String key = "button.save";
        String expectedMessage = "Save";
        Locale locale = Locale.ENGLISH;

        LocaleContextHolder.setLocale(locale);
        when(messageSource.getMessage(eq(key), any(), eq(locale))).thenReturn(expectedMessage);

        String result = localeFormattingService.getMessage(key);

        assertEquals(expectedMessage, result, "Returned message does not match expected value");
        verify(messageSource, times(1)).getMessage(eq(key), any(), eq(locale));
    }

    @Test
    void testFormatDate() throws ParseException {
        LocaleContextHolder.setLocale(Locale.US);
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2024-05-12");

        String formatted = localeFormattingService.formatDate(date, DateFormat.MEDIUM);
        assertEquals("May 12, 2024", formatted, "Formatted date does not match expected value");
    }

    @Test
    void testFormatTime() throws ParseException {
        LocaleContextHolder.setLocale(Locale.US);
        Date time = new SimpleDateFormat("HH:mm").parse("15:30");

        String formatted = localeFormattingService.formatTime(time, DateFormat.SHORT);
        assertEquals("3:30 PM", formatted, "Formatted time does not match expected value");
    }

    @Test
    void testFormatDateTime() throws ParseException {
        LocaleContextHolder.setLocale(Locale.US);
        Date dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2024-05-12 15:30");

        String formatted = localeFormattingService.formatDateTime(dateTime, DateFormat.SHORT, DateFormat.SHORT);
        assertEquals("5/12/24, 3:30 PM", formatted, "Formatted datetime does not match expected value");
    }

    @Test
    void testFormatDateTimeEs() throws ParseException {
        LocaleContextHolder.setLocale(Locale.forLanguageTag("es"));
        Date dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2024-05-12 15:30");

        String formatted = localeFormattingService.formatDateTime(dateTime, DateFormat.SHORT, DateFormat.SHORT);
        assertEquals("12/5/24, 15:30", formatted, "Formatted datetime does not match expected value");
    }

}
