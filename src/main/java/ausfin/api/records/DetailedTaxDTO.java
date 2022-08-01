package ausfin.api.records;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record DetailedTaxDTO(
        @NotNull @Positive Integer grossIncome,
        @NotNull Integer helpBalance,
        @NotNull Integer superBalance,
        @NotNull Integer investmentsBalance,
        @NotNull Boolean superInclusive,

        @NotNull @Positive Float superContributionRate,
        @NotNull Boolean maxSuperContributions,
        @NotNull Integer expenses,
        @NotNull Integer deductions,
        @NotNull Integer fringeBenefits,
        @NotNull Boolean privateHospitalCover,
        @NotNull Boolean maxSuper,
        @NotNull Float assumedGrowth
//        @NotNull Boolean drawingPhase,
//        @NotNull Boolean paySuper
) {
}
