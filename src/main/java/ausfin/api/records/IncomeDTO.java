package ausfin.api.records;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record IncomeDTO(@NotNull @Positive Integer income) {
}
