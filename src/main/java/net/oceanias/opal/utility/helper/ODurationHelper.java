package net.oceanias.opal.utility.helper;

import java.time.Duration;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@UtilityClass
public final class ODurationHelper {
    public String formatFullDuration(final @NotNull Duration duration) {
        final List<Pair<String, Long>> units = List.of(
            Pair.of("day", duration.toDays()),
            Pair.of("hour", duration.toHours() % 24),
            Pair.of("minute", duration.toMinutes() % 60),
            Pair.of("second", duration.toSeconds() % 60)
        );

        final List<String> parts = units.stream()
            .filter(unit -> unit.getRight() > 0)
            .map(unit -> {
                final String extension = (unit.getRight() == 1L)
                    ? ""
                    : "s";

                return unit.getRight() + " " + unit.getLeft() + extension;
            })
            .toList();

        return switch (parts.size()) {
            case 0 -> "less than a second";
            case 1 -> parts.get(0);
            case 2 -> parts.get(0) + " and " + parts.get(1);
            default -> String.join(", ", parts.subList(0, parts.size() - 1)) + ", and " + parts.getLast();
        };
    }
}