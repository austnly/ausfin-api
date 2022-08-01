package ausfin.api.records;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record IncomeSuperDTO(@NotNull @Positive Integer income, Boolean superInclusive, @NotNull @Positive Float rate, Boolean maxSuperContributions) {
}
