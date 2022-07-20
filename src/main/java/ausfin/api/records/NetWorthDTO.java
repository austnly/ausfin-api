package ausfin.api.records;

public class NetWorthDTO {
    private Integer netIncome;
    private Integer helpBalance;
    private Integer superBalance;
    private Integer investmentsBalance;

    public NetWorthDTO(Integer netIncome, Integer helpBalance, Integer superBalance, Integer investmentsBalance) {
        this.netIncome = netIncome;
        this.helpBalance = helpBalance;
        this.superBalance = superBalance;
        this.investmentsBalance = investmentsBalance;
    }

    public NetWorthDTO(Integer netIncome) {
        this.netIncome = netIncome;
        this.helpBalance = 0;
        this.superBalance = 0;
        this.investmentsBalance = 0;
    }

    public NetWorthDTO(NetWorthDTO netWorthDTO) {
        this.netIncome = netWorthDTO.netIncome;
        this.helpBalance = netWorthDTO.helpBalance;
        this.superBalance = netWorthDTO.superBalance;
        this.investmentsBalance = netWorthDTO.investmentsBalance;
    }

    public void update(Integer netIncome, Integer helpBalance, Integer superBalance, Integer investmentsBalance) {
        this.netIncome = netIncome;
        this.helpBalance = helpBalance;
        this.superBalance = superBalance;
        this.investmentsBalance = investmentsBalance;
    }

    public Integer result() {
        return this.superBalance + this.investmentsBalance - this.helpBalance;
    }

    public Integer getNetIncome() {
        return netIncome;
    }

    public void setNetIncome(Integer netIncome) {
        this.netIncome = netIncome;
    }

    public Integer getHelpBalance() {
        return helpBalance;
    }

    public void setHelpBalance(Integer helpBalance) {
        this.helpBalance = helpBalance;
    }

    public Integer getSuperBalance() {
        return superBalance;
    }

    public void setSuperBalance(Integer superBalance) {
        this.superBalance = superBalance;
    }

    public Integer getInvestmentsBalance() {
        return investmentsBalance;
    }

    public void setInvestmentsBalance(Integer investmentsBalance) {
        this.investmentsBalance = investmentsBalance;
    }
}
