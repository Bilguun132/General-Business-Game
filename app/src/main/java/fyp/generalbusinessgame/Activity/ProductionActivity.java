package fyp.generalbusinessgame.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import fyp.generalbusinessgame.R;

public class ProductionActivity extends AppCompatActivity {

    public int gamePeriodId = 0;
    public int firmId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_production);
    }
}
