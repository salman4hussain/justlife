package com.justlife.booking.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public record TimeRange(LocalDateTime from, LocalDateTime to) {

    public boolean overlapsWith(TimeRange other) {
        return !(this.to.isEqual(other.from) || this.to.isBefore(other.from) ||
                other.to.isEqual(this.from) || other.to.isBefore(this.from));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeRange that)) return false;
        return Objects.equals(from, that.from) && Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public String toString() {
        return from + " → " + to;
    }
}
