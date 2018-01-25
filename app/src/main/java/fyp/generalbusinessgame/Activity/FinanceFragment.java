package fyp.generalbusinessgame.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import fyp.generalbusinessgame.Models.GamePeriodModel;
import fyp.generalbusinessgame.R;


public class FinanceFragment extends Fragment {

    public int gamePeriodId = 0;
    public int firmId = 0;
    public GamePeriodModel gamePeriodModel = new GamePeriodModel();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_finance, container, false);
        Button balanceSheetButton = view.findViewById(R.id.balance_sheet_button);
        balanceSheetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), BalanceSheetActivity.class);
                intent.putExtra(getString(R.string.firm_id), firmId);
                startActivity(intent);
            }
        });

        Button incomeStatementButton = view.findViewById(R.id.income_statement_button);
        incomeStatementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), IncomeStatementActivity.class);
                intent.putExtra("firmId", firmId);
                intent.putExtra("gamePeriodModel", gamePeriodModel);
                startActivity(intent);
            }
        });

        getActivity().setTitle("Finance Summary. Period: " + gamePeriodModel.periodNumber);

        LinearLayout financeInfoLayout = view.findViewById(R.id.finance_view_financial_information);

        switch (gamePeriodModel.status) {
            case 0:
                financeInfoLayout.setVisibility(View.VISIBLE);
                break;
            case 1:
                financeInfoLayout.setVisibility(View.GONE);
                break;
            case 2:
                financeInfoLayout.setVisibility(View.VISIBLE);
                break;
        }

        return view;
    }

}
