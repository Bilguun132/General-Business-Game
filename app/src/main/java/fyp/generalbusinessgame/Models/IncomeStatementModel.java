package fyp.generalbusinessgame.Models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Bilguun on 31/10/17.
 */

public class IncomeStatementModel extends BaseModel implements Serializable  {


    public int firmId;
    public String firmName;
    public int periodId ;
    public int periodNumber ;
    public String totalRevenue;
    public String totalCost ;
    public String rndCost;
    public ArrayList<MarketingDecisionsModel> marketingDecisions = new ArrayList<MarketingDecisionsModel>();
    public ArrayList<ProductionDecisionsModel> productionDecisions = new ArrayList<ProductionDecisionsModel>();
    public String productionCost;
    public String otherCost ;
    public String totalProfit ;
}
