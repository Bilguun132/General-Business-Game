package fyp.generalbusinessgame.Models;

import java.io.Serializable;

/**
 * Created by pc on 21/12/2017.
 */

public class CostModel extends BaseModel implements Serializable {
    public int id;
    public int cashflowType;
    public int paymentType;
    public int paymentTargetType;
    public int businessAspect;
    public Double paymentAmount;
    public String name;
    public String description;
    public int numberOfPayment;
    public String firstPaymentDate;
    public Integer periodNumber;
}
