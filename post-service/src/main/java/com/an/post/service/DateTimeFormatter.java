package com.an.post.service;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class DateTimeFormatter {

    // Ý tưởng là thay vì thêm nhiều if else thì tạo 1 map để duyệt qua, nếu match thì chạy hàm

    Map<Long, Function<Instant, String>> strategyMap = new LinkedHashMap<>(); //giữ nguyên thứ tự thêm vào.

    // đưa vào constructor
    public DateTimeFormatter() {
        strategyMap.put(60L, this::formatInSeconds);
        strategyMap.put(3600L, this::formatInMinutes);
        strategyMap.put(86400L, this::formatInHours);
        strategyMap.put(Long.MAX_VALUE, this::formatInDate);
    }

    public String format(Instant instant){
        long elapseSeconds = ChronoUnit.SECONDS.between(instant, Instant.now());

        // duyệt từng entry trong map, nếu thỏa
        var strategy = strategyMap.entrySet()
                .stream()
                .filter(longFunctionEntry -> elapseSeconds < longFunctionEntry.getKey())
                .findFirst().get(); // lấy entry đầu tiên phù hợp
        return strategy.getValue().apply(instant);
    }

    private String formatInSeconds(Instant instant) {
        long elapsedSeconds = ChronoUnit.SECONDS.between(instant, Instant.now());
        return elapsedSeconds == 1 ? "1 second ago" : elapsedSeconds + " seconds ago";
    }

    private String formatInMinutes(Instant instant) {
        long elapsedMinutes = ChronoUnit.MINUTES.between(instant, Instant.now());
        return elapsedMinutes == 1 ? "1 minute ago" : elapsedMinutes + " minutes ago";
    }

    private String formatInHours(Instant instant) {
        long elapsedHours = ChronoUnit.HOURS.between(instant, Instant.now());
        return elapsedHours == 1 ? "1 hour ago" : elapsedHours + " hours ago";
    }

    private String formatInDate(Instant instant){
        LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        java.time.format.DateTimeFormatter dateTimeFormatter = java.time.format.DateTimeFormatter.ISO_DATE;

        return localDateTime.format(dateTimeFormatter);
    }
}