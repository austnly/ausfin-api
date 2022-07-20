package ausfin.api.records;

import org.springframework.context.annotation.Profile;

public class IncomeProfileDTO {
    private NetWorthDTO netWorth;
    private IncomeSuperDTO superInfo;
    private ProfileInfoDTO profile;

    public IncomeProfileDTO(NetWorthDTO netWorth, IncomeSuperDTO superInfo, ProfileInfoDTO profile) {
        this.netWorth = netWorth;
        this.superInfo = superInfo;
        this.profile = profile;
    }

    public static IncomeProfileDTO testProfile(Integer income, Integer expenses) {
        NetWorthDTO netWorthDTO = new NetWorthDTO(income, 0, 0, 0);
        IncomeSuperDTO incomeSuperDTO = new IncomeSuperDTO(income, false, 0f, false);
        ProfileInfoDTO profileInfoDTO = new ProfileInfoDTO(expenses, 0, 0,false);
        return new IncomeProfileDTO(netWorthDTO, incomeSuperDTO, profileInfoDTO);
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
