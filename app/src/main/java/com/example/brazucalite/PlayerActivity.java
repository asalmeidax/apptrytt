package com.example.brazucalite;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

public class PlayerActivity extends Activity {
    private ExoPlayer player;
    private PlayerView view;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_player);
        String url = getIntent().getStringExtra("url");
        setTitle(getIntent().getStringExtra("title"));
        player = new ExoPlayer.Builder(this).build();
        view = findViewById(R.id.playerView);
        view.setPlayer(player);
        view.setUseController(true);
        view.requestFocus();
        player.setMediaItem(MediaItem.fromUri(url));
        player.prepare();
        player.play();
    }

    @Override public boolean dispatchKeyEvent(KeyEvent event) {
        if (player != null && event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    if (player.isPlaying()) player.pause(); else player.play();
                    view.showController();
                    return true;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    player.seekTo(Math.max(0, player.getCurrentPosition() - 10000));
                    view.showController();
                    return true;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    player.seekTo(player.getCurrentPosition() + 10000);
                    view.showController();
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override protected void onStop() {
        super.onStop();
        if (player != null) { player.release(); player = null; }
    }
}
