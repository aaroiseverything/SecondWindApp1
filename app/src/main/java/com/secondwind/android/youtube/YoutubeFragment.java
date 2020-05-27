package com.secondwind.android.youtube;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.secondwind.android.R;


public class YoutubeFragment extends Fragment {
    private static final String YOUTUBE_API_KEY = YoutubeConfig.getApiKey();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String VIDEO_ID = "";

    // TODO: Rename and change types of parameters
    private String videoId;
    YouTubePlayer mYouTubePlayer;

    public YoutubeFragment() {
        // Required empty public constructor
    }

    public static YoutubeFragment newInstance(String videoId) {
        YoutubeFragment fragment = new YoutubeFragment();
        Bundle args = new Bundle();
        args.putString(VIDEO_ID, videoId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            videoId = getArguments().getString(VIDEO_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_youtube, container, false);

        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.flYoutubePlayer, youTubePlayerFragment).commit();

        youTubePlayerFragment.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider arg0, YouTubePlayer youTubePlayer, boolean b) {
                if (!b) {
                    mYouTubePlayer = youTubePlayer;
                    youTubePlayer.loadVideo(videoId);
                    youTubePlayer.play();
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
                // TODO Auto-generated method stub

            }
        });

        return rootView;
    }

    public void setFullScreen(Boolean fullScreen) {
        mYouTubePlayer.setFullscreen(fullScreen);
    }

    public void pauseVid() {
        if (mYouTubePlayer != null) {
            mYouTubePlayer.pause();
        }
        return;
    };
}