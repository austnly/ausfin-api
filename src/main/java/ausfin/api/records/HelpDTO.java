package ausfin.api.records;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record HelpDTO(@NotNull @Positive Integer income, @NotNull @Positive Integer reportableSuperContributions, @NotNull @Positive Integer reportableFringeBenefits) {
}
