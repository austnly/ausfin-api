package ausfin.api.records;

public record ProfileInfoDTO(
                Integer expenses,
                Integer retirementExpenses,
                Integer deductions,
                Integer fringeBenefits,
                Boolean privateHospitalCover) {
}
