package ausfin.api.records;

public record ProfileInfoDTO(
        Integer expenses,
        Integer deductions,
        Integer fringeBenefits,
        Boolean privateHospitalCover) {
}
