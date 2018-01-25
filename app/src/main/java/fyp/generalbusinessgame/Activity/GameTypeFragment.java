package fyp.generalbusinessgame.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import fyp.generalbusinessgame.Models.GameTypeInformationListModel;
import fyp.generalbusinessgame.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class GameTypeFragment extends Fragment {

    public GameTypeInformationListModel gameTypeInformationListModel = new GameTypeInformationListModel();
    private FrameLayout frameLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        frameLayout = (FrameLayout) inflater.inflate(
                R.layout.recycler_view, container, false);
        recyclerView = frameLayout.findViewById(R.id.my_recycler_view);
        progressBar = frameLayout.findViewById(R.id.progressBar);
        recyclerView.setVisibility(View.GONE);
        ContentAdapter adapter = new ContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
                    Intent intent = new Intent(context, MainView.class);
                    int position = getAdapterPosition();
                    String gameTypeId = Integer.toString( gameTypeInformationListModel.gameTypeList.get(position).id);
                    intent.putExtra("gameTypeId", gameTypeId);
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
        private final int LENGTH = getItemCount();
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
            byte[] imageBytes = Base64.decode(gameTypeInformationListModel.gameTypeList.get(position).gameTypeImageString, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.picture.setImageBitmap(decodedImage);
//            holder.picture.setImageDrawable(a.getDrawable(position));
            holder.name.setText(gameTypeInformationListModel.gameTypeList.get(position).gameTypeName);
            holder.description.setText(gameTypeInformationListModel.gameTypeList.get(position).gameTypeDescription);
        }



        @Override
        public int getItemCount() {
            return gameTypeInformationListModel.gameTypeList.size();
        }
    }
}