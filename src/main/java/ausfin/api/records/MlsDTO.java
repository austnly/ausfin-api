package ausfin.api.records;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record MlsDTO(@NotNull @Positive Integer income, Integer reportableFringeBenefits) {
}
