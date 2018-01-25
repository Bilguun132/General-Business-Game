package fyp.generalbusinessgame.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fyp.generalbusinessgame.Models.GamePeriodModel;
import fyp.generalbusinessgame.R;


public class  GameSummaryFragment extends Fragment {

    public int gamePeriodId = 0;
    public int firmId;
    public GamePeriodModel gamePeriodModel = new GamePeriodModel();
    private AppCompatTextView gameInformationText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_summary, container, false);
        gameInformationText  = (AppCompatTextView) view.findViewById(R.id.game_information);
        getActivity().setTitle("Game Summary. Period: " + gamePeriodModel.periodNumber);
        UpdateGameInformation();
        return view;
    }

    public void UpdateGameInformation() {
        switch (gamePeriodModel.status) {
            case 0:
                if (gamePeriodModel.previousPeriodId == 0)  gameInformationText.setText("The game has not been started. Please contact the admin to start the game.");
                else
                    gameInformationText.setText("The game is in a break mode. Please view the income statement.");
                break;
            case 1:
                gameInformationText.setText("The game is in progress. Navigate to make decisions.");
                break;
            case 2:
                if (gamePeriodModel.endTime == null) gameInformationText.setText("The game is in a break mode. Please view the income statement.");
                else
                    gameInformationText.setText("The game has ended.");
                break;
        }
    }
}