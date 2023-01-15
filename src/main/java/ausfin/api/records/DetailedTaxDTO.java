package ausfin.api.records;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

public record DetailedTaxDTO(
                @NotNull @Positive Integer grossIncome,
                @NotNull @PositiveOrZero Integer helpBalance,
                @NotNull @PositiveOrZero Integer superBalance,
                @NotNull @PositiveOrZero Integer investmentsBalance,
                Boolean superInclusive,

                @NotNull @PositiveOrZero Float superContributionRate,
                Boolean maxSuperContributions,
                @NotNull @PositiveOrZero Integer retirementExpenses,
                @NotNull @PositiveOrZero Integer expenses,
                @NotNull @PositiveOrZero Integer deductions,
                @NotNull @PositiveOrZero Integer fringeBenefits,
                Boolean privateHospitalCover,
                // @NotNull Boolean maxSuper,
                @NotNull @PositiveOrZero Float assumedGrowth
// @NotNull Boolean drawingPhase,
// @NotNull Boolean paySuper
) {
}
