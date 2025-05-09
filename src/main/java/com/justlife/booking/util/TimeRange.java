package com.justlife.booking.util;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Data
public class TimeRange implements Comparable<TimeRange> {
    private final LocalDateTime start;
    private final LocalDateTime end;

    public TimeRange(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public boolean overlapsWith(TimeRange other) {
        return this.start.isBefore(other.end) && other.start.isBefore(this.end);
    }


    @Override
    public int compareTo(TimeRange other) {
        return this.start.compareTo(other.start);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeRange that = (TimeRange) o;
        return Objects.equals(start, that.start) && Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return start + " → " + end;
    }
}