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

        return new TaxResult(baseTax, taxRate, total, income - total);
    }

    public SuperResult calculateSuper(IncomeSuperDTO data) {
        Integer income = data.income();
        Float rate = data.rate();
        Boolean superInclusive = data.superInclusive() != null ? data.superInclusive() : false;
        Boolean maxSuperContributions = data.maxSuperContributions() != null ? data.maxSuperContributions() : false;

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
        return Math.round(current * (1 + (growth * 0.85f) / 100) + superContribution * 0.85f);
    }

    private InvestmentsDTO updateInvestments(Integer current, Integer invContribution, Float growth) {
        return new InvestmentsDTO(
                Math.round(current * (1 + growth / 100) + invContribution),
                Math.round(current * growth / 100));
    }

    public AnnualResult taxTime(IncomeProfileDTO incomeProfileDTO, Boolean maxSuper, Float growth, Boolean drawingPhase,
            Boolean paySuper) {
        int income = incomeProfileDTO.getNetWorth().getNetIncome();
        int superCont = 0, reportableSup = 0;
        SuperResult superResult;

        // Set income after any super contributions
        if (paySuper) {
            int netIncome = income;
            boolean superInclusive = incomeProfileDTO.getSuperInfo().superInclusive();
            float superContributionRate = incomeProfileDTO.getSuperInfo().rate();

            superResult = calculateSuper(
                    new IncomeSuperDTO(netIncome, superInclusive, superContributionRate, maxSuper));

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
                growth).increaseAmt();

        // Taxable income if not in drawing phase is income + investments growth,
        // If drawing phase, will be investments growth if greater than required income,
        // Otherwise will just be the required income
        income = !drawingPhase
                ? income + invBalGrowth
                : invBalGrowth > income
                        ? invBalGrowth
                        : income;

        // Income minus deductions
        income -= incomeProfileDTO.getProfile().deductions();

        // HELP repayment and new balance based on net income, reportable Super and
        // fringe benefits
        HelpResult help = calculateHELP(
                income
                        + reportableSup
                        + incomeProfileDTO.getProfile().fringeBenefits());
        int helpRepayment = help.helpRepayment();
        HelpBalanceDTO newHelp = updateHelp(incomeProfileDTO.getNetWorth().getHelpBalance(), helpRepayment);
        int helpBalance = newHelp.balance();
        int helpOver = newHelp.additional();

        // Medicare levy
        // low-income - medicare levy is 10% of amount over exempt threshold 23,266,
        // above 29,033 is 2% of income
        int medicare = income < 23226
                ? 0
                : income < 29033
                        ? (int) Math.round((income - 23266) * 0.1)
                        : (int) Math.round(0.02 * income);

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
                income - tax - helpRepayment + helpOver - medicare - mlsRepay);

        // Are we adding to investments or drawing income from investments?
        int invContrib = 0;
        if (!drawingPhase) {
            invContrib = income - incomeProfileDTO.getProfile().expenses();
        } else {
            invContrib = -(tax +
                    helpRepayment -
                    helpOver +
                    medicare +
                    mlsRepay +
                    incomeProfileDTO.getProfile().retirementExpenses());
        }

        // Update investments
        int newInv = updateInvestments(
                incomeProfileDTO.getNetWorth().getInvestmentsBalance(),
                invContrib,
                growth).invBal();

        return new AnnualResult(
                new NetWorthDTO(income, helpBalance, newSuper, newInv),
                taxResult,
                superResult,
                help,
                mls,
                medicare,
                invContrib);
    }

    private Integer preTaxTarget(Integer targetIncome) {
        int testAmt = 2 * targetIncome;
        int increment = (int) Math.round(targetIncome * 0.5);

        IncomeProfileDTO tester = IncomeProfileDTO.testProfile(testAmt, targetIncome);

        int postTax = (int) Math.round(
                taxTime(
                        tester,
                        false,
                        0f,
                        false,
                        false).netWorth().getNetIncome());

        while (postTax != targetIncome) {
            if (postTax > targetIncome) {
                tester.getNetWorth().setNetIncome(
                        Math.round(tester.getNetWorth().getNetIncome() - increment));

                increment *= 0.5;
            } else {
                tester.getNetWorth().setNetIncome(
                        Math.round(tester.getNetWorth().getNetIncome() + increment));
            }
            postTax = (int) Math.round(
                    taxTime(
                            tester,
                            false,
                            0f,
                            false,
                            false).netWorth().getNetIncome());
        }

        return tester.getNetWorth().getNetIncome();
    }

    private int getFireNumber(Integer income) {
        return (int) Math.round(income / 0.04);
    }

    private boolean invTargetReached(
            Integer balance,
            Float growth,
            Integer required,
            Integer currentAge) {
        int age = currentAge;
        int invBal = balance;
        int target = (int) Math.round(required * 2); // safety factor
        // int target = required;

        while (age <= 60) {
            age++;
            int invGrowth = updateInvestments(invBal, 0, growth).increaseAmt();
            int withdrawal = invGrowth > target ? invGrowth : target;
            invBal = Math.round(invBal * (1 + growth / 100) - withdrawal);

            // if at any point before reaching age 60, invBal reaches <0, then balance is
            // insufficient
            if (invBal < 0) {
                return false;
            }
        }
        return true;
    }

    private SuperTargetDTO superTargetReached(
            Integer balance,
            Integer expenses,
            Integer contrib,
            Integer age,
            Float growth) {
        int year = 0;
        int target = getFireNumber(expenses);

        // Balance by age 60 without further contributions (growth is taxed 15%)
        double retireBal = balance * Math.pow(1 + (growth / 100 * 0.85), 60 - age);

        // If super balance is not sufficient to reach target, increment by a year and
        // contribute to super
        while (retireBal < target) {
            year++;
            age++;
            balance = updateSuper(balance, contrib, growth);
            retireBal = balance * Math.pow(1 + (growth / 100 * 0.85), 60 - age);
        }

        return new SuperTargetDTO(age, balance, target, (int) Math.round(retireBal));
    }

    public FireResult timeToFire(
            IncomeProfileDTO fullProfile,
            Integer age,
            Float growth) {
        int reqIncome = preTaxTarget(fullProfile.getProfile().retirementExpenses());
        // System.out.println("Required income/Pre-tax target is: " + reqIncome);

        int fireNum = getFireNumber(reqIncome);
        // System.out.println("Fire number is: " + fireNum);
        int startAge = age;

        int year = 0;
        IncomeProfileDTO yearEnd = IncomeProfileDTO.copy(fullProfile);

        System.out.printf(
                "Starting HELP: %d\nStarting Investments: %d\nStarting Super: %d\nTotal Net Worth: %d\n",
                yearEnd.getNetWorth().getHelpBalance(),
                yearEnd.getNetWorth().getInvestmentsBalance(),
                yearEnd.getNetWorth().getSuperBalance(),
                yearEnd.getNetWorth().result());

        while (!invTargetReached(
                yearEnd.getNetWorth().getInvestmentsBalance(),
                growth,
                reqIncome,
                age)) {
            year++;
            age++;
            AnnualResult netPosition = taxTime(yearEnd, false, growth, false, true);
            yearEnd.getNetWorth().setHelpBalance(netPosition.netWorth().getHelpBalance());
            yearEnd.getNetWorth().setInvestmentsBalance(netPosition.netWorth().getInvestmentsBalance());
            yearEnd.getNetWorth().setSuperBalance(netPosition.netWorth().getSuperBalance());
            System.out.printf(
                    "Year %d\nHELP: %d\nInvestments: %d\nSuper: %d\nInvested this year: %d\nTotal Net Worth: %d\n",
                    year,
                    yearEnd.getNetWorth().getHelpBalance(),
                    yearEnd.getNetWorth().getInvestmentsBalance(),
                    yearEnd.getNetWorth().getSuperBalance(),
                    netPosition.availableToInvest(),
                    yearEnd.getNetWorth().result());
        }

        System.out.println("Investments will reach target balance at age " + age);
        System.out.println("Maximising super contributions");

        SuperTargetDTO targetSuper = superTargetReached(
                yearEnd.getNetWorth().getSuperBalance(),
                fullProfile.getProfile().retirementExpenses(),
                27500,
                age,
                growth);

        while (age < targetSuper.age() + 1) {
            year++;
            age++;

            AnnualResult netPosition = taxTime(yearEnd, true, growth, false, true);
            yearEnd.getNetWorth().setHelpBalance(netPosition.netWorth().getHelpBalance());
            yearEnd.getNetWorth().setInvestmentsBalance(netPosition.netWorth().getInvestmentsBalance());
            yearEnd.getNetWorth().setSuperBalance(netPosition.netWorth().getSuperBalance());
            System.out.printf(
                    "Year %d\nHELP: %d\nInvestments: %d\nSuper: %d\nInvested this year: %d\nTotal Net Worth: %d\n",
                    year,
                    yearEnd.getNetWorth().getHelpBalance(),
                    yearEnd.getNetWorth().getInvestmentsBalance(),
                    yearEnd.getNetWorth().getSuperBalance(),
                    netPosition.availableToInvest(),
                    yearEnd.getNetWorth().result());
        }

        int fireYears = year;
        System.out.printf("You will reach FIRE in %d years!\n", fireYears);

        IncomeProfileDTO fireProfile = new IncomeProfileDTO(
                new NetWorthDTO(
                        reqIncome,
                        yearEnd.getNetWorth().getHelpBalance(),
                        yearEnd.getNetWorth().getSuperBalance(),
                        yearEnd.getNetWorth().getInvestmentsBalance()),
                new IncomeSuperDTO(
                        reqIncome,
                        false,
                        0f,
                        false),
                new ProfileInfoDTO(
                        yearEnd.getProfile().retirementExpenses(),
                        yearEnd.getProfile().retirementExpenses(),
                        0,
                        0,
                        yearEnd.getProfile().privateHospitalCover()));

        while (age < 60) {
            year++;
            age++;

            AnnualResult netPosition = taxTime(fireProfile, false, growth, true, false);
            fireProfile.getNetWorth().setHelpBalance(netPosition.netWorth().getHelpBalance());
            fireProfile.getNetWorth().setInvestmentsBalance(netPosition.netWorth().getInvestmentsBalance());
            fireProfile.getNetWorth().setSuperBalance(netPosition.netWorth().getSuperBalance());
            System.out.printf(
                    "Year %d\nHELP: %d\nInvestments: %d\nSuper: %d\nInvested this year: %d\nTotal Net Worth: %d\n",
                    year,
                    fireProfile.getNetWorth().getHelpBalance(),
                    fireProfile.getNetWorth().getInvestmentsBalance(),
                    fireProfile.getNetWorth().getSuperBalance(),
                    netPosition.availableToInvest(),
                    fireProfile.getNetWorth().result());
        }

        System.out.println("Final Net Worth @ 60: " + fireProfile.getNetWorth().result());

        return new FireResult(
                fireProfile,
                fireYears);
    }
}
