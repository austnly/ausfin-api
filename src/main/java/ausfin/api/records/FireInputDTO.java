package ausfin.api.records;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record FireInputDTO(
        @NotNull @Positive Integer income,
        @NotNull Integer helpBalance,
        @NotNull Integer superBalance,
        @NotNull Integer investmentsBalance,
        Boolean superInclusive,
        @NotNull @Positive Float superContributionRate,
        Boolean maxSuperContributions,
        @NotNull Integer expenses,
        @NotNull Integer deductions,
        @NotNull Integer fringeBenefits,
        Boolean privateHospitalCover,
//        @NotNull Boolean maxSuper,
        @NotNull Float assumedGrowth,
//        @NotNull Boolean drawingPhase,
//        @NotNull Boolean paySuper,
        @NotNull @Positive Integer age
) {
}
