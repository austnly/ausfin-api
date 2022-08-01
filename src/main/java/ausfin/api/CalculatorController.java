package ausfin.api;

import ausfin.api.records.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "http://localhost:3001")
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
    public HelpResult help(@RequestBody @Valid IncomeDTO data) {
        return calculatorService.calculateHELP(data.income());
    }

    @PostMapping(path="mls")
    public MlsResult mls(@RequestBody @Valid IncomeDTO data) {
        return calculatorService.calculateMLS(data.income());
    }

    @PostMapping(path="detailed-tax")
    public AnnualResult detailedTax(@RequestBody @Valid DetailedTaxDTO data) {
        NetWorthDTO netWorth = new NetWorthDTO(
                data.income(),
                data.helpBalance(),
                data.superBalance(),
                data.investmentsBalance());

        IncomeSuperDTO superInfo = new IncomeSuperDTO(
                data.income(),
                data.superInclusive(),
                data.rate(),
                data.maxSuperContributions());

        ProfileInfoDTO profileInfo = new ProfileInfoDTO(
                data.expenses(),
                data.deductions(),
                data.fringeBenefits(),
                data.privateHospitalCover()
        );

        IncomeProfileDTO incomeProfile = new IncomeProfileDTO(netWorth, superInfo, profileInfo);

        AnnualResult detailedResult = calculatorService.taxTime(
                incomeProfile,
                data.maxSuper(),
                data.growth(),
                data.drawingPhase(),
                data.paySuper()
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
                data.superInclusive(),
                data.rate(),
                data.maxSuperContributions());

        ProfileInfoDTO profileInfo = new ProfileInfoDTO(
                data.expenses(),
                data.deductions(),
                data.fringeBenefits(),
                data.privateHospitalCover()
        );

        IncomeProfileDTO incomeProfile = new IncomeProfileDTO(netWorth, superInfo, profileInfo);

        FireResult fireResult = calculatorService.timeToFire(incomeProfile, data.age(), data.growth());

        return fireResult;
    }

//    @GetMapping(path="ptt")
//    public Integer ptt(@RequestBody IncomeDTO income) {
//        return calculatorService.preTaxTarget(income.income());
//    }


}
