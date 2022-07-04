package ausfin.api.records;

public class IncomeProfileDTO {
    private NetWorthDTO netWorth;
    private IncomeSuperDTO superInfo;
    private ProfileInfoDTO profile;

    public IncomeProfileDTO(NetWorthDTO netWorth, IncomeSuperDTO superInfo, ProfileInfoDTO profile) {
        this.netWorth = netWorth;
        this.superInfo = superInfo;
        this.profile = profile;
    }

    public NetWorthDTO getNetWorth() {
        return netWorth;
    }

    public void setNetWorth(NetWorthDTO netWorth) {
        this.netWorth = netWorth;
    }

    public IncomeSuperDTO getSuperInfo() {
        return superInfo;
    }

    public void setSuperInfo(IncomeSuperDTO superInfo) {
        this.superInfo = superInfo;
    }

    public ProfileInfoDTO getProfile() {
        return profile;
    }

    public void setProfile(ProfileInfoDTO profile) {
        this.profile = profile;
    }
}
