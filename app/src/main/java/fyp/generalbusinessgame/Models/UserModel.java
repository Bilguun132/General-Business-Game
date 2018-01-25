package fyp.generalbusinessgame.Models;

import java.io.Serializable;

/**
 * Created by Arcstone on 08-Apr-17.
 */

public class UserModel extends BaseModel implements Serializable {
    public int id;
    public String userName;
    public String firstName;
    public String lastName;
    public String position;
    public String department;
    public String group;
    public String mobilePersonal;
    public String mobileOffice;
    public String laneLineOffice;
    public String laneLineHome;
    public String emailPrimary;
    public String emailSecondary;
    public String userPicture;
    public Integer securityLevel;
    public Boolean loginAllowed;
    public String passwordHash;
    public String passwordSalt;
    public Boolean isAvailable;
    public Boolean isDeleted;
    public Integer timesheetProfileId;
    public String pinNumberHash;
    public String pinNumberSalt;
    public String saleSchedulingName;
}
