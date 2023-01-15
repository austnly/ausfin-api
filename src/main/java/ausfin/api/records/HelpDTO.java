package ausfin.api.records;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

public record HelpDTO(@NotNull @Positive Integer income, @PositiveOrZero Integer reportableSuperContributions, @PositiveOrZero Integer reportableFringeBenefits) {
}
