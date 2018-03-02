package fyp.generalbusinessgame.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fyp.generalbusinessgame.Activity.dummy.DummyContent;
import fyp.generalbusinessgame.Models.GameFirmInfoModel;
import fyp.generalbusinessgame.Models.GamePeriodModel;
import fyp.generalbusinessgame.Models.IncomeStatementModel;
import fyp.generalbusinessgame.R;


public class FinanceFragment extends Fragment {

    public int gamePeriodId = 0;
    public int firmId = 0;
    public GamePeriodModel gamePeriodModel = new GamePeriodModel();

    public String domainName = "";
    public String subdomainName = "";
    public View mainview;
    public int gameId;
    public int userId;
    public GameFirmInfoModel gameFirmInfoModel = new GameFirmInfoModel();
    IncomeStatementModel incomeStatementModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainview = inflater.inflate(R.layout.fragment_finance, container, false);

        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        if (sharedPref != null) {
            userId = sharedPref.getInt(getString(R.string.user_id), 0);
            domainName = sharedPref.getString(getString(R.string.domain_name_key), null);
            if (domainName == null || domainName.isEmpty()) {
                domainName = getString(R.string.domain_name);
            }
            subdomainName = sharedPref.getString(getString(R.string.subdomain_name_key), null);
            if (subdomainName == null || subdomainName.isEmpty()) {
                subdomainName = getString(R.string.subdomain_name);
            }
        }
        updateLoanLimits();
        getIncomeStatement();
        Button balanceSheetButton = mainview.findViewById(R.id.balance_sheet_button);
        balanceSheetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), BalanceSheetActivity.class);
                intent.putExtra(getString(R.string.firm_id), firmId);
                startActivity(intent);
            }
        });

        Button incomeStatementButton = mainview.findViewById(R.id.income_statement_button);
        incomeStatementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), IncomeStatementActivity.class);
                intent.putExtra("firmId", firmId);
                intent.putExtra("gamePeriodModel", gamePeriodModel);
                startActivity(intent);
            }
        });

        Button submitDecisionsButton = mainview.findViewById(R.id.finance_decision_button);
        submitDecisionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeFinanceDecision(view);
            }
        });

        getActivity().setTitle("Finance Summary. Period: " + gamePeriodModel.periodNumber);

        TableLayout financeInfoLayout = mainview.findViewById(R.id.finance_decisions_table);

//        switch (gamePeriodModel.status) {
//            case 0:
//                financeInfoLayout.setVisibility(View.VISIBLE);
//                break;
//            case 1:
//                financeInfoLayout.setVisibility(View.GONE);
//                break;
//            case 2:
//                financeInfoLayout.setVisibility(View.VISIBLE);
//                break;
//        }

        return mainview;
    }

    private void updateLoanLimits() {

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = domainName + subdomainName + getString(R.string.get_firm_info_by_firm_id) + firmId;

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Gson gson = new GsonBuilder().create();
                        gameFirmInfoModel = gson.fromJson(response, GameFirmInfoModel.class);
                        final EditText stLoanText = (EditText) mainview.findViewById(R.id.finance_short_term_loan);
                        final EditText ltLoanText = (EditText) mainview.findViewById(R.id.finance_long_term_loan);
                        stLoanText.setText("");
                        ltLoanText.setText("");
                        stLoanText.setHint(gameFirmInfoModel.stLoanLimit);
                        ltLoanText.setHint(gameFirmInfoModel.ltLoanLimit);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void getIncomeStatement() {
// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        int periodId;
        if (gamePeriodModel.endTime == null && gamePeriodModel.previousPeriodId != 0) periodId = gamePeriodModel.previousPeriodId;
        else periodId = gamePeriodModel.id;
        String url = domainName + subdomainName + getString(R.string.get_firm_info_by_firm_id) + firmId + "/incomeStatement/" + periodId;

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Gson gson = new GsonBuilder().create();
                        incomeStatementModel = gson.fromJson(response, IncomeStatementModel.class);
                        if (incomeStatementModel.financeDecisions.size() > 0) {
                            LineChart chart = (LineChart) mainview.findViewById(R.id.finance_history_chart);
                            List<Entry> stLoans = new ArrayList<Entry>();
                            List<Entry> ltLoans = new ArrayList<Entry>();
                            for (int i = 0; i < incomeStatementModel.financeDecisions.size(); i++) {
                                if (i > 0) {
                                    stLoans.add(new Entry( i+1, stLoans.get(i-1).getY() + Float.parseFloat(incomeStatementModel.financeDecisions.get(i).stLoan)));
                                    ltLoans.add(new Entry( i+1, ltLoans.get(i-1).getY() + Float.parseFloat(incomeStatementModel.financeDecisions.get(i).ltLoan)));
                                }
                                else {
                                    stLoans.add(new Entry( i+1, Float.parseFloat(incomeStatementModel.financeDecisions.get(i).stLoan)));
                                    ltLoans.add(new Entry( i+1, Float.parseFloat(incomeStatementModel.financeDecisions.get(i).ltLoan)));
                                }
                            }
                            LineDataSet stDataSet = new LineDataSet(stLoans, "ST Loans");
                            LineDataSet ltDataSet = new LineDataSet(ltLoans, "LT Loans");
                            stDataSet.setLineWidth(5);
                            stDataSet.setValueTextSize(10);
                            ltDataSet.setLineWidth(5);
                            ltDataSet.setValueTextSize(10);
                            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                            dataSets.add(stDataSet);
                            dataSets.add(ltDataSet);
                            ((LineDataSet) dataSets.get(0)).enableDashedLine(10, 10, 0);
                            ((LineDataSet) dataSets.get(0)).setColors(ColorTemplate.VORDIPLOM_COLORS);
                            ((LineDataSet) dataSets.get(0)).setCircleColors(ColorTemplate.VORDIPLOM_COLORS);
                            LineData lineData = new LineData(dataSets);
                            chart.setData(lineData);
                            chart.invalidate(); // refresh
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void makeFinanceDecision(final View view) {
        final EditText stLoanText = (EditText) mainview.findViewById(R.id.finance_short_term_loan);
        final EditText ltLoanText = (EditText) mainview.findViewById(R.id.finance_long_term_loan);
        final String stLoan = (stLoanText.getText().toString().equals("") ? "0" : stLoanText.getText().toString());
        final String ltLoan = (ltLoanText.getText().toString().equals("") ? "0" : ltLoanText.getText().toString());
        if (Float.parseFloat(stLoan) > Float.parseFloat(gameFirmInfoModel.stLoanLimit)) {
            Snackbar.make(view, "Limits exceeded",
                    Snackbar.LENGTH_LONG).show();
            return;
        }
        if (Float.parseFloat(ltLoan) > Float.parseFloat(gameFirmInfoModel.ltLoanLimit)) {
            Snackbar.make(view, "Limits exceeded",
                    Snackbar.LENGTH_LONG).show();
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = domainName + subdomainName + getString(R.string.get_firm_info_by_firm_id)  + firmId + "/financeDecision";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Snackbar.make(view, "Made Finance Decision",
                                Snackbar.LENGTH_LONG).show();
                        updateLoanLimits();
                        getIncomeStatement();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String asd = error.toString();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", Integer.toString(userId));
                params.put("stLoan", stLoan);
                params.put("ltLoan", ltLoan);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}
