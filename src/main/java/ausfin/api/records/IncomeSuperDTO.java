package ausfin.api.records;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record IncomeSuperDTO(@NotNull @Positive Integer income, @NotNull Boolean superInclusive, @NotNull @Positive Float rate, @NotNull Boolean maxSuperContributions) {
}
