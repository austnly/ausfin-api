package ausfin.api;

import ausfin.api.records.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = {"http://localhost:3000","http://localhost:3001","https://www.astnly.com/ausfin/"})
@RestController
@RequestMapping
public class CalculatorController {
    @Autowired
    CalculatorService calculatorService;
    @Autowired
    TablesService tablesService;

    @PostMapping(path="tax")
    public TaxResult tax(@RequestBody @Valid IncomeDTO data) {
        return calculatorService.calculateTax(data.income());
    }

    @PostMapping(path="super")
    public SuperResult superann(@RequestBody @Valid IncomeSuperDTO data) {
        return calculatorService.calculateSuper(data);
    }

    @PostMapping(path="help-repay")
    public HelpResult help(@RequestBody @Valid HelpDTO data) {
        return calculatorService.calculateHELP(
                data.income() +
                data.reportableFringeBenefits() +
                data.reportableSuperContributions());
    }

    @PostMapping(path="mls")
    public MlsResult mls(@RequestBody @Valid MlsDTO data) {
        return calculatorService.calculateMLS(data.income() + data.reportableFringeBenefits());
    }

    @PostMapping(path="detailed-tax")
    public AnnualResult detailedTax(@RequestBody @Valid DetailedTaxDTO data) {
        NetWorthDTO netWorth = new NetWorthDTO(
                data.grossIncome(),
                data.helpBalance(),
                data.superBalance(),
                data.investmentsBalance());

        IncomeSuperDTO superInfo = new IncomeSuperDTO(
                data.grossIncome(),
                data.superInclusive() != null ? data.superInclusive() : false,
                data.superContributionRate(),
                data.maxSuperContributions());

        ProfileInfoDTO profileInfo = new ProfileInfoDTO(
                data.expenses(),
                data.deductions(),
                data.fringeBenefits(),
                data.privateHospitalCover() != null ? data.privateHospitalCover() : false
        );

        IncomeProfileDTO incomeProfile = new IncomeProfileDTO(netWorth, superInfo, profileInfo);

        AnnualResult detailedResult = calculatorService.taxTime(
                incomeProfile,
                data.maxSuperContributions() != null ? data.maxSuperContributions() : false,
                data.assumedGrowth(),
                false,
                true
        );

        return detailedResult;
    }

    @PostMapping(path="fire")
    public FireResult fire(@RequestBody @Valid FireInputDTO data) {
        NetWorthDTO netWorth = new NetWorthDTO(
                data.income(),
                data.helpBalance(),
                data.superBalance(),
                data.investmentsBalance());

        IncomeSuperDTO superInfo = new IncomeSuperDTO(
                data.income(),
                data.superInclusive() != null ? data.superInclusive() : false,
                data.superContributionRate(),
                data.maxSuperContributions() != null ? data.maxSuperContributions() : false);

        ProfileInfoDTO profileInfo = new ProfileInfoDTO(
                data.expenses(),
                data.deductions(),
                data.fringeBenefits(),
                data.privateHospitalCover() != null ? data.privateHospitalCover() : false
        );

        IncomeProfileDTO incomeProfile = new IncomeProfileDTO(netWorth, superInfo, profileInfo);

        FireResult fireResult = calculatorService.timeToFire(incomeProfile, data.age(), data.assumedGrowth());

        return fireResult;
    }

//    @GetMapping(path="ptt")
//    public Integer ptt(@RequestBody IncomeDTO income) {
//        return calculatorService.preTaxTarget(income.income());
//    }


}
