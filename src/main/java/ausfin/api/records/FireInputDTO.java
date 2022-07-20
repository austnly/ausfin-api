package ausfin.api.records;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record FireInputDTO(
        @NotNull @Positive Integer income,
        @NotNull Integer helpBalance,
        @NotNull Integer superBalance,
        @NotNull Integer investmentsBalance,
        @NotNull Boolean superInclusive,
        @NotNull @Positive Float rate,
        @NotNull Boolean maxSuperContributions,
        @NotNull Integer expenses,
        @NotNull Integer deductions,
        @NotNull Integer fringeBenefits,
        @NotNull Boolean privateHospitalCover,
        @NotNull Boolean maxSuper,
        @NotNull Float growth,
        @NotNull Boolean drawingPhase,
        @NotNull Boolean paySuper,
        @NotNull @Positive Integer age
) {
}
