package vlad.pr.projectCRUD.util;

import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;

@Component
public class DataTimeUtil {
    public LocalDate getNextWorkingDay(LocalDate date) {
        LocalDate result = date;
        while (isWeekend(result)) {
            result = result.plusDays(1);
        }
        return result;
    }

    public boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    public long convertToUnix(String timezone, String jobTime, LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
        LocalTime clientTime = LocalTime.parse(jobTime, formatter);
        ZoneOffset offset = ZoneOffset.of(timezone.replace("UTC", ""));
        LocalDateTime clientDateTime = LocalDateTime.of(date, clientTime);
        OffsetDateTime clientOffsetDateTime = clientDateTime.atOffset(offset);
        return clientOffsetDateTime.toEpochSecond();
    }
}
