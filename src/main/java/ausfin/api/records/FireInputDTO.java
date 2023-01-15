package ausfin.api.records;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

public record FireInputDTO(
                @NotNull @Positive Integer income,
                @NotNull @PositiveOrZero Integer helpBalance,
                @NotNull @PositiveOrZero Integer superBalance,
                @NotNull @PositiveOrZero Integer investmentsBalance,
                Boolean superInclusive,
                @NotNull @Positive Float superContributionRate,
                Boolean maxSuperContributions,
                @NotNull @PositiveOrZero Integer expenses,
                @NotNull @PositiveOrZero Integer retirementExpenses,
                @NotNull @PositiveOrZero Integer deductions,
                @NotNull @PositiveOrZero Integer fringeBenefits,
                Boolean privateHospitalCover,
                // @NotNull Boolean maxSuper,
                @NotNull @PositiveOrZero Float assumedGrowth,
                // @NotNull Boolean drawingPhase,
                // @NotNull Boolean paySuper,
                @NotNull @Positive Integer age) {
}
