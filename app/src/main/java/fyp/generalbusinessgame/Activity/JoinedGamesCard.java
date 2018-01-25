package fyp.generalbusinessgame.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import fyp.generalbusinessgame.Models.GameInformationListModel;
import fyp.generalbusinessgame.R;
import fyp.generalbusinessgame.Service.ImageHolder;


public class JoinedGamesCard extends Fragment {
    private FrameLayout frameLayout;
    private ProgressBar progressBar;
    public GameInformationListModel gameInformationListModel = new GameInformationListModel();
    public GameViewActivity gameViewActivity = new GameViewActivity();

    private RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        frameLayout = (FrameLayout) inflater.inflate(
                R.layout.recycler_view, container, false);
        recyclerView = frameLayout.findViewById(R.id.my_recycler_view);
        progressBar = frameLayout.findViewById(R.id.progressBar);
        JoinedGamesCard.ContentAdapter adapter = new JoinedGamesCard.ContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        return frameLayout;
    }

    public void UpdateGameTypes() {
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView picture;
        public TextView name;
        public TextView description;
        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.fragment_available_games_card, parent, false));
            picture = (ImageView) itemView.findViewById(R.id.card_image);
            name = (TextView) itemView.findViewById(R.id.card_title);
            description = (TextView) itemView.findViewById(R.id.card_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, GameSummaryActivity.class);
                    int firmId = gameInformationListModel.joinedGameList.get(getAdapterPosition()).joinedFirmId;
                    int gameId = gameInformationListModel.joinedGameList.get(getAdapterPosition()).id;
                    intent.putExtra(getString(R.string.firm_id), firmId);
                    intent.putExtra(getString(R.string.game_id), gameId);
                    context.startActivity(intent);
                }
            });

            // Adding Snackbar to Action Button inside card
            Button button = (Button)itemView.findViewById(R.id.action_button);
            button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, "Action is pressed",
                            Snackbar.LENGTH_LONG).show();
                }
            });

            ImageButton favoriteImageButton =
                    (ImageButton) itemView.findViewById(R.id.favorite_button);
            favoriteImageButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, "Added to Favorite",
                            Snackbar.LENGTH_LONG).show();
                }
            });

            ImageButton shareImageButton = (ImageButton) itemView.findViewById(R.id.share_button);
            shareImageButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, "Share article",
                            Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * Adapter to display recycler view.
     */
    public class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        // Set numbers of Card in RecyclerView.
        private final int LENGTH = gameInformationListModel.joinedGameList.size();

        //        private final String[] mGameNames;
//        private final String[] mGameDescs;
//        private final Drawable[] mGamePictures;
//
        public ContentAdapter(Context context) {
            Resources resources = context.getResources();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (gameInformationListModel.joinedGameList.get(position).gameImageString != null) {
                byte[] imageBytes = Base64.decode(gameInformationListModel.joinedGameList.get(position).gameImageString, Base64.DEFAULT);
                Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                holder.picture.setImageBitmap(decodedImage);
                ImageHolder.addBitmapToMemoryCache(gameInformationListModel.joinedGameList.get(position).id, decodedImage);
                gameInformationListModel.joinedGameList.get(position).gameImageString = "";
            }
            holder.name.setText(gameInformationListModel.joinedGameList.get(position).gameName);
            holder.description.setText(gameInformationListModel.joinedGameList.get(position).gameDescription);
        }

        @Override
        public int getItemCount() {
            return gameInformationListModel.joinedGameList.size();
        }
    }

}
