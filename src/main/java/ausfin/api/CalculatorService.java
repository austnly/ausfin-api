package ausfin.api;

import ausfin.api.records.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;

@Service
public class CalculatorService {
    @Autowired
    TablesService tablesService;

    public TaxResult calculateTax(Integer income) {
        ArrayList<Float> threshold = tablesService.getTaxTable().get(0);
        ArrayList<Float> percent = tablesService.getTaxTable().get(1);
        ArrayList<Float> base = tablesService.getTaxTable().get(2);

        Integer total = 0;
        Integer baseTax = 0;
        Float taxRate = 0f;

        for (int i = threshold.size() - 1; i >= 0; i--) {
            if (income >= threshold.get(i)) {
                // if greater than threshold,
                baseTax = Math.round(base.get(i));
                taxRate = percent.get(i);
                total = Math.round(
                        base.get(i) + (income - threshold.get(i) + 1) * (percent.get(i) / 100));
                break;
            }
        }

        return new TaxResult(baseTax, taxRate, total, income-total);
    }

    public SuperResult calculateSuper(IncomeSuperDTO data) {
        Integer income = data.income();
        Float rate = data.rate();
        Boolean superInclusive = data.superInclusive();
        Boolean maxSuperContributions = data.maxSuperContributions();

        Integer totalSup;
        Integer incExclSup;
        Integer maxContrib = 27500;

        if (maxSuperContributions) {
            totalSup = maxContrib;
            if (superInclusive) {
                incExclSup = income - maxContrib;
            } else {
                incExclSup = Math.round(
                        income - (maxContrib - income * (rate / 100)));
            }
        } else {
            if (superInclusive) {
                incExclSup = Math.round(income / (1 + rate / 100));
                // e.g. 90k income incl 10% super gives 90/1.1 = 81.8k excl super
                totalSup = income - incExclSup;
            } else {
                // income doesn't include super
                totalSup = Math.round(income * (rate / 100));
                incExclSup = income;
            }
        }

        Integer reportableContributions = totalSup - Math.round(incExclSup * 0.095f);

        return new SuperResult(totalSup, reportableContributions, incExclSup);
    }

    public HelpResult calculateHELP(Integer income) {
        ArrayList<Float> threshold = tablesService.getHelpTable().get(0);
        ArrayList<Float> rates = tablesService.getHelpTable().get(1);

        Float rate = 0f;

        for (int i = threshold.size() - 1; i >= 0; i--) {
            if (income >= threshold.get(i)) {
                rate = rates.get(i);
                break;
            }
        }
        return new HelpResult(Math.round((income * rate) / 100), rate);
    }

    public MlsResult calculateMLS(Integer income) {
        ArrayList<Float> threshold = tablesService.getMlsTable().get(0);
        ArrayList<Float> rates = tablesService.getMlsTable().get(2);

        Float rate = 0f;

        for (int i = threshold.size() - 1; i >= 0; i--) {
            if (income >= threshold.get(i)) {
                rate = rates.get(i);
                break;
            }
        }

        return new MlsResult(Math.round((income * rate) / 100), rate);
    }

    private HelpBalanceDTO updateHelp(Integer current, Integer repayment) {
        Integer bal = Math.round(current * 1.02f - repayment);

        if (bal > 0) {
            return new HelpBalanceDTO(bal, 0);
        } else {
            return new HelpBalanceDTO(0, -bal);
        }
    }

    private Integer updateSuper(Integer current, Integer superContribution, Float growth) {
        return Math.round(current * (1 + (growth * 0.85f)/100) + superContribution * 0.85f);
    }

    private InvestmentsDTO updateInvestments(Integer current, Integer invContribution, Float growth) {
        return new InvestmentsDTO(
          Math.round(current * (1 + growth/100)),
          Math.round(current * growth/100)
        );
    }

    public AnnualResult taxTime(IncomeProfileDTO incomeProfileDTO, Boolean maxSuper, Float growth, Boolean drawingPhase, Boolean paySuper) {
        int income = incomeProfileDTO.getNetWorth().getNetIncome();
        int superCont = 0, reportableSup = 0;
        SuperResult superResult;

        // Set income after any super contributions
        if (paySuper) {
            int netIncome = income;
            boolean superInclusive = incomeProfileDTO.getSuperInfo().superInclusive();
            float superContributionRate = incomeProfileDTO.getSuperInfo().rate();

            superResult = calculateSuper(new IncomeSuperDTO(netIncome, superInclusive, superContributionRate, maxSuper));

            income = superResult.incomeAfterSuper();
            superCont = superResult.superContribution();
            reportableSup = superResult.reportableContributions();
        } else {
            superResult = calculateSuper(new IncomeSuperDTO(income, false, 0f, false));
            income = superResult.incomeAfterSuper();
        }

        // Update super balance
        int newSuper = updateSuper(incomeProfileDTO.getNetWorth().getSuperBalance(), superCont, growth);

        // Calculate investment income from growth
        int invBalGrowth = updateInvestments(
                incomeProfileDTO.getNetWorth().getInvestmentsBalance(),
                0,
                growth
            ).increaseAmt();

        // Taxable income if not in drawing phase is income + investments growth,
        // If drawing phase, will be investments growth if greater than required income,
        // Otherwise will just be the required income
        income = !drawingPhase
                ? income + invBalGrowth
                : invBalGrowth > incomeProfileDTO.getNetWorth().getNetIncome()
                ? invBalGrowth
                : income;

        // Income minus deductions
        income -= incomeProfileDTO.getProfile().deductions();

        // HELP repayment and new balance based on net income, reportable Super and fringe benefits
        HelpResult help = calculateHELP(
                income
                + reportableSup
                + incomeProfileDTO.getProfile().fringeBenefits());
        int helpRepayment = help.helpRepayment();
        HelpBalanceDTO newHelp = updateHelp(incomeProfileDTO.getNetWorth().getHelpBalance(), helpRepayment);
        int helpBalance = newHelp.balance();
        int helpOver = newHelp.additional();

        // Medicare levy
        // low-income - medicare levy is 10% of amount over exempt threshold 23,266, above 29,033 is 2% of income
        int medicare =
                income < 23226
                        ? 0
                        : income < 29033
                        ? (int)Math.round((income - 23266) * 0.1)
                        : (int)Math.round(0.02 * income);

        // Income tax
        TaxResult taxResult = calculateTax(income);
        int tax = taxResult.totalTax();

        // MLS
        int mlsRepay = 0;
        MlsResult mls;
        if (income >= 90000 && !incomeProfileDTO.getProfile().privateHospitalCover()) {
            mls = calculateMLS(income + incomeProfileDTO.getProfile().fringeBenefits());
            mlsRepay = mls.medicareLevySurcharge();
        } else {
            mls = calculateMLS(0);
        }

        // Net income after ATO
        income = Math.round(
                income - tax - helpRepayment + helpOver - medicare - mlsRepay
                );

        // Are we adding to investments or drawing income from investments?
        int invContrib = 0;
        if (!drawingPhase) {
            invContrib = income - incomeProfileDTO.getProfile().expenses();
        } else {
            invContrib = -(
                    tax +
                            helpRepayment -
                            helpOver +
                            medicare +
                            mlsRepay +
                            incomeProfileDTO.getProfile().expenses()
            );}

        // Update investments
        int newInv = updateInvestments(
                incomeProfileDTO.getNetWorth().getInvestmentsBalance(),
                invContrib,
                growth
                ).invBal();

        return new AnnualResult(
                new NetWorthDTO(income, helpBalance, newSuper, newInv),
                taxResult,
                superResult,
                help,
                mls,
                medicare,
                invContrib
        );
    }
}
