package com.gowtham.library.ui;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.arthenica.ffmpegkit.FFmpegKit;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.gson.Gson;
import com.gowtham.library.R;
import com.gowtham.library.ui.seekbar.widgets.CrystalRangeSeekbar;
import com.gowtham.library.ui.seekbar.widgets.CrystalSeekbar;
import com.gowtham.library.utils.CompressOption;
import com.gowtham.library.utils.CustomProgressView;
import com.gowtham.library.utils.FileUtilKt;
import com.gowtham.library.utils.LocaleHelper;
import com.gowtham.library.utils.LogMessage;
import com.gowtham.library.utils.TrimVideo;
import com.gowtham.library.utils.TrimVideoOptions;
import com.gowtham.library.utils.TrimmerUtils;
import com.gowtham.library.utils.ViewUtil;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executors;
public class ActVideoTrimmer extends LocalizationActivity {

    private static final int PER_REQ_CODE = 115;
    private StyledPlayerView playerView;
    private ExoPlayer videoPlayer;

    private ImageView imagePlayPause;

    private ImageView[] imageViews;

    private long totalDuration;

    private Dialog dialog;

    private Uri filePath;

    private TextView txtStartDuration, txtEndDuration;

    private CrystalRangeSeekbar seekbar;

    private long lastMinValue = 0;

    private long lastMaxValue = 0;

    private MenuItem menuDone;

    private CrystalSeekbar seekbarController;

    private boolean isValidVideo = true, isVideoEnded;

    private android.os.Handler seekHandler;

    private Bundle bundle;

    private ProgressBar progressBar;

    private TrimVideoOptions trimVideoOptions;

    private long currentDuration, lastClickedTime;
    Runnable updateSeekbar = new Runnable() {
        @Override
        public void run() {
            try {
                currentDuration = videoPlayer.getCurrentPosition() / 1000;
                if (!videoPlayer.getPlayWhenReady())
                    return;
                if (currentDuration <= lastMaxValue)
                    seekbarController.setMinStartValue((int) currentDuration).apply();
                else
                    videoPlayer.setPlayWhenReady(false);
            } finally {
                seekHandler.postDelayed(updateSeekbar, 1000);
            }
        }
    };
    private CompressOption compressOption;
    private String outputPath;
    private String local;
    private int trimType;
    private long fixedGap, minGap, minFromGap, maxToGap;
    private boolean hidePlayerSeek, isAccurateCut, showFileLocationAlert;
    private CustomProgressView progressView;
    private String fileName;

    private TextView tvCancel,tvChoose;

    private ConstraintLayout videoMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_video_trimmer);
        tvCancel = findViewById(R.id.tv_cancel);
        tvChoose = findViewById(R.id.tv_choose);
        videoMessage = findViewById(R.id.cl_video);
        bundle = getIntent().getExtras();
        Gson gson = new Gson();
        String videoOption = bundle.getString(TrimVideo.TRIM_VIDEO_OPTION);
        long time = bundle.getLong("time");
        trimVideoOptions = gson.fromJson(videoOption, TrimVideoOptions.class);
        tvCancel.setOnClickListener(v-> finish());
        tvChoose.setOnClickListener(v->{
            if (SystemClock.elapsedRealtime() - lastClickedTime < 800)
                return;
            lastClickedTime = SystemClock.elapsedRealtime();
            trimVideo();
        });

        progressView = new CustomProgressView(this);

        if (time>30) {
            videoMessage.setVisibility(View.VISIBLE);
        } else {
            videoMessage.setVisibility(View.GONE);
        }
    }

    @Override
    protected void attachBaseContext(@NotNull Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        playerView = findViewById(R.id.player_view_lib);
        imagePlayPause = findViewById(R.id.image_play_pause);
        seekbar = findViewById(R.id.range_seek_bar);
        txtStartDuration = findViewById(R.id.txt_start_duration);
        txtEndDuration = findViewById(R.id.txt_end_duration);
        seekbarController = findViewById(R.id.seekbar_controller);
        progressBar = findViewById(R.id.progress_circular);
        ImageView imageOne = findViewById(R.id.image_one);
        ImageView imageTwo = findViewById(R.id.image_two);
        ImageView imageThree = findViewById(R.id.image_three);
        ImageView imageFour = findViewById(R.id.image_four);
        ImageView imageFive = findViewById(R.id.image_five);
        ImageView imageSix = findViewById(R.id.image_six);
        ImageView imageSeven = findViewById(R.id.image_seven);
        ImageView imageEight = findViewById(R.id.image_eight);
        ViewUtil.systemGestureExclusionRects(findViewById(R.id.root_view));
        imageViews = new ImageView[]{imageOne, imageTwo, imageThree,
                imageFour, imageFive, imageSix, imageSeven, imageEight};
        seekHandler = new Handler();
        initPlayer();
        if (checkStoragePermission())
            setDataInView();
    }
    private void initPlayer() {
        try {
            videoPlayer = new ExoPlayer.Builder(this).build();
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            playerView.setPlayer(videoPlayer);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.CONTENT_TYPE_MOVIE)
                        .build();
                videoPlayer.setAudioAttributes(audioAttributes, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDataInView() {
        try {
            Runnable fileUriRunnable = () -> {
                Uri uri = Uri.parse(bundle.getString(TrimVideo.TRIM_VIDEO_URI));
                String path = FileUtilKt.getValidatedFileUri(ActVideoTrimmer.this, uri);
                filePath = Uri.parse(path);
                runOnUiThread(() -> {
                    LogMessage.v("VideoUri:: " + uri);
                    LogMessage.v("VideoPath:: " + filePath);
                    progressBar.setVisibility(View.GONE);
                    totalDuration = TrimmerUtils.getDuration(ActVideoTrimmer.this, filePath);
                    imagePlayPause.setOnClickListener(v ->
                            onVideoClicked());
                    Objects.requireNonNull(playerView.getVideoSurfaceView()).setOnClickListener(v ->
                            onVideoClicked());
                    initTrimData();
                    buildMediaSource();
                    loadThumbnails();
                    setUpSeekBar();
                });
            };
            Executors.newSingleThreadExecutor().execute(fileUriRunnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initTrimData() {
        try {
            assert trimVideoOptions != null;
            trimType = TrimmerUtils.getTrimType(trimVideoOptions.trimType);
            fileName = trimVideoOptions.fileName;
            hidePlayerSeek = trimVideoOptions.hideSeekBar;
            isAccurateCut = trimVideoOptions.accurateCut;
            local = trimVideoOptions.local;
            compressOption = trimVideoOptions.compressOption;
            showFileLocationAlert = trimVideoOptions.showFileLocationAlert;
            fixedGap = trimVideoOptions.fixedDuration;
            fixedGap = fixedGap != 0 ? fixedGap : totalDuration;
            minGap = trimVideoOptions.minDuration;
            minGap = minGap != 0 ? minGap : totalDuration;
            if (trimType == 3) {
                minFromGap = trimVideoOptions.minToMax[0];
                maxToGap = trimVideoOptions.minToMax[1];
                minFromGap = minFromGap != 0 ? minFromGap : totalDuration;
                maxToGap = maxToGap != 0 ? maxToGap : totalDuration;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        setLanguage(new Locale(local != null ? local : "en"));
    }

    private void onVideoClicked() {
        try {
            if (isVideoEnded) {
                seekTo(lastMinValue);
                videoPlayer.setPlayWhenReady(true);
                return;
            }
            if ((currentDuration - lastMaxValue) > 0)
                seekTo(lastMinValue);
            videoPlayer.setPlayWhenReady(!videoPlayer.getPlayWhenReady());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void seekTo(long sec) {
        if (videoPlayer != null)
            videoPlayer.seekTo(sec * 1000);
    }

    private void buildMediaSource() {
        try {
            DataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(this);
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(filePath));
            videoPlayer.addMediaSource(mediaSource);
            videoPlayer.prepare();
            videoPlayer.setPlayWhenReady(true);
            videoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
                //    imagePlayPause.setVisibility(playWhenReady ? View.GONE : View.VISIBLE);
                    if(playWhenReady){
                        imagePlayPause.setBackgroundResource(R.drawable.ic_pause_icon);
                    }else{
                        imagePlayPause.setBackgroundResource(R.drawable.ic_video_play_lib);
                    }
                }

                @Override
                public void onPlaybackStateChanged(int state) {
                    switch (state) {
                        case Player.STATE_ENDED:
                            LogMessage.v("onPlayerStateChanged: Video ended.");
                        //    imagePlayPause.setVisibility(View.VISIBLE);
                            imagePlayPause.setBackgroundResource(R.drawable.ic_video_play_lib);

                            isVideoEnded = true;
                            break;
                        case Player.STATE_READY:
                            isVideoEnded = false;
                       //     imagePlayPause.setVisibility(View.GONE);
                            imagePlayPause.setBackgroundResource(R.drawable.ic_pause_icon);

                            startProgress();
                            LogMessage.v("onPlayerStateChanged: Ready to play.");
                            break;
                        default:
                            break;
                        case Player.STATE_BUFFERING:
                            LogMessage.v("onPlayerStateChanged: STATE_BUFFERING.");
                            break;
                        case Player.STATE_IDLE:
                            LogMessage.v("onPlayerStateChanged: STATE_IDLE.");
                            break;
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     *  loading thumbnails
     * */
    private void loadThumbnails() {
        try {
            long diff = totalDuration / 8;
            int sec = 1;
            File videoFile = new File(filePath.toString());
            for (ImageView img : imageViews) {
                long interval = (diff * sec) * 1000000;
                RequestOptions options = new RequestOptions().frame(interval);
                Glide.with(this)
                        .load(videoFile)
                        .apply(options)
                        .transition(DrawableTransitionOptions.withCrossFade(300))
                        .into(img);
                if (sec < totalDuration)
                    sec++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpSeekBar() {
        seekbar.setVisibility(View.VISIBLE);
        txtStartDuration.setVisibility(View.VISIBLE);
        txtEndDuration.setVisibility(View.VISIBLE);

        seekbarController.setMaxValue(totalDuration).apply();
        seekbar.setMaxValue(totalDuration).apply();
        seekbar.setMaxStartValue((float) totalDuration).apply();
        if (trimType == 1) {
            seekbar.setFixGap(fixedGap).apply();
            lastMaxValue = totalDuration;
        } else if (trimType == 2) {
            seekbar.setMaxStartValue((float) minGap);
            seekbar.setGap(minGap).apply();
            lastMaxValue = totalDuration;
        } else if (trimType == 3) {
            seekbar.setMaxStartValue((float) maxToGap);
            seekbar.setGap(minFromGap).apply();
            lastMaxValue = maxToGap;
        } else {
            seekbar.setGap(2).apply();
            lastMaxValue = totalDuration;
        }
        if (hidePlayerSeek)
            seekbarController.setVisibility(View.GONE);

        seekbar.setOnRangeSeekbarFinalValueListener((minValue, maxValue) -> {
            if (!hidePlayerSeek)
                seekbarController.setVisibility(View.VISIBLE);
        });

        seekbar.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
            long minVal = (long) minValue;
            long maxVal = (long) maxValue;
            if (lastMinValue != minVal) {
                seekTo((long) minValue);
                if (!hidePlayerSeek)
                    seekbarController.setVisibility(View.INVISIBLE);
            }
            lastMinValue = minVal;
            lastMaxValue = maxVal;
            txtStartDuration.setText(TrimmerUtils.formatSeconds(minVal));
            txtEndDuration.setText(TrimmerUtils.formatSeconds(maxVal));
            if (trimType == 3)
                setDoneColor(minVal, maxVal);
        });

        seekbarController.setOnSeekbarFinalValueListener(value -> {
            long value1 = (long) value;
            if (value1 < lastMaxValue && value1 > lastMinValue) {
                seekTo(value1);
                return;
            }
            if (value1 > lastMaxValue)
                seekbarController.setMinStartValue((int) lastMaxValue).apply();
            else if (value1 < lastMinValue) {
                seekbarController.setMinStartValue((int) lastMinValue).apply();
                if (videoPlayer.getPlayWhenReady())
                    seekTo(lastMinValue);
            }
        });
    }

    private void setDoneColor(long minVal, long maxVal) {
        try {
            if (menuDone == null)
                return;
            //changed value is less than maxDuration
            if ((maxVal - minVal) <= maxToGap) {
                menuDone.getIcon().setColorFilter(
                        new PorterDuffColorFilter(ContextCompat.getColor(this, R.color.colorWhite)
                                , PorterDuff.Mode.SRC_IN)
                );
                isValidVideo = true;
            } else {
                menuDone.getIcon().setColorFilter(
                        new PorterDuffColorFilter(ContextCompat.getColor(this, R.color.colorWhiteLt)
                                , PorterDuff.Mode.SRC_IN)
                );
                isValidVideo = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PER_REQ_CODE) {
            if (isPermissionOk(grantResults))
                setDataInView();
            else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.setPlayWhenReady(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (videoPlayer != null)
                videoPlayer.release();
            if (progressView != null && progressView.isShowing())
                progressView.dismiss();
            File f = new File(getCacheDir(), "temp_video_file");
            if (f.exists()) {
                f.delete();
            }
            stopRepeatingTask();
            FFmpegKit.cancel();
        } catch (Exception e) {
            LogMessage.e(Log.getStackTraceString(e));
        }
    }


    private void trimVideo() {
        if (isValidVideo) {
            //not exceed given maxDuration if has given
            outputPath = getFileName();
            LogMessage.v("outputPath::" + outputPath + new File(outputPath).exists());
            LogMessage.v("sourcePath::" + filePath);
            videoPlayer.setPlayWhenReady(false);
            showProcessingDialog();
            String[] complexCommand;
            if (compressOption != null)
                complexCommand = getCompressionCmd();
            else if (isAccurateCut) {
                //no changes in video quality
                //faster trimming command and given duration will be accurate
                complexCommand = getAccurateCmd();
            } else {
                //no changes in video quality
                //fastest trimming command however, result duration
                //will be low accurate(2-3 secs)
                complexCommand = new String[]{"-ss", TrimmerUtils.formatCSeconds(lastMinValue),
                        "-i", String.valueOf(filePath),
                        "-t",
                        TrimmerUtils.formatCSeconds(lastMaxValue - lastMinValue),
                        "-async", "1", "-strict", "-2", "-c", "copy", outputPath};
            }
            execFFmpegBinary(complexCommand, true);
        } else
            Toast.makeText(this, getString(R.string.txt_smaller) + " " + TrimmerUtils.getLimitedTimeFormatted(maxToGap), Toast.LENGTH_SHORT).show();
    }

    private String getFileName() {
        String path = getExternalFilesDir("TrimmedVideo").getPath();
        Calendar calender = Calendar.getInstance();
        String fileDateTime = calender.get(Calendar.YEAR) + "_" +
                calender.get(Calendar.MONTH) + "_" +
                calender.get(Calendar.DAY_OF_MONTH) + "_" +
                calender.get(Calendar.HOUR_OF_DAY) + "_" +
                calender.get(Calendar.MINUTE) + "_" +
                calender.get(Calendar.SECOND);
        String fName = "trimmed_video_";
        if (fileName != null && !fileName.isEmpty())
            fName = fileName;
        File newFile = new File(path + File.separator +
                (fName) + fileDateTime + "." + TrimmerUtils.getFileExtension(this, filePath));
        return String.valueOf(newFile);
    }

    private String[] getCompressionCmd() {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(String.valueOf(filePath));
        String height = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String width = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        int w = TrimmerUtils.clearNull(width).isEmpty() ? 0 : Integer.parseInt(width);
        int h = Integer.parseInt(height);
        int rotation = TrimmerUtils.getVideoRotation(this, filePath);
        if (rotation == 90 || rotation == 270) {
            int temp = w;
            w = h;
            h = temp;
        }
        //Default compression option
        if (compressOption.getWidth() != 0 || compressOption.getHeight() != 0
                || !compressOption.getBitRate().equals("0k")) {
            return new String[]{"-ss", TrimmerUtils.formatCSeconds(lastMinValue),
                    "-i", String.valueOf(filePath), "-s", compressOption.getWidth() + "x" +
                    compressOption.getHeight(),
                    "-r", String.valueOf(compressOption.getFrameRate()),
                    "-vcodec", "mpeg4", "-b:v",
                    compressOption.getBitRate(), "-b:a", "48000", "-ac", "2", "-ar",
                    "22050", "-t",
                    TrimmerUtils.formatCSeconds(lastMaxValue - lastMinValue), outputPath};
        }
        //Dividing high resolution video by 2(ex: taken with camera)
        else if (w >= 800) {
            w = w / 2;
            h = h / 2;
            return new String[]{"-ss", TrimmerUtils.formatCSeconds(lastMinValue),
                    "-i", String.valueOf(filePath),
                    "-s", w + "x" + h, "-r", "30",
                    "-vcodec", "mpeg4", "-b:v",
                    "1M", "-b:a", "48000", "-ac", "2", "-ar", "22050",
                    "-t",
                    TrimmerUtils.formatCSeconds(lastMaxValue - lastMinValue), outputPath};
        } else {
            return new String[]{"-ss", TrimmerUtils.formatCSeconds(lastMinValue),
                    "-i", String.valueOf(filePath), "-s", w + "x" + h, "-r",
                    "30", "-vcodec", "mpeg4", "-b:v",
                    "400K", "-b:a", "48000", "-ac", "2", "-ar", "22050",
                    "-t",
                    TrimmerUtils.formatCSeconds(lastMaxValue - lastMinValue), outputPath};
        }
    }

    private void execFFmpegBinary(final String[] command, boolean retry) {
        try {
            FFmpegKit.executeWithArgumentsAsync(command, session -> {
                int result = session.getReturnCode().getValue();
                if (result == 0) {
                    dialog.dismiss();
                    if (showFileLocationAlert) showLocationAlert();
                    else {
                        Intent intent = new Intent();
                        intent.putExtra(TrimVideo.TRIMMED_VIDEO_PATH, outputPath);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                } else if (result == 255) {
                    LogMessage.v("Command cancelled");
                    if (dialog.isShowing())
                        dialog.dismiss();
                } else {
                    // Failed case:
                    // line 489 command fails on some devices in
                    // that case retrying with accurateCmt as alternative command
                    if (retry && !isAccurateCut && compressOption == null) {
                        File newFile = new File(outputPath);
                        if (newFile.exists()) newFile.delete();
                        execFFmpegBinary(getAccurateCmd(), false);
                    } else {
                        if (dialog.isShowing()) dialog.dismiss();
                        runOnUiThread(() -> Toast.makeText(ActVideoTrimmer.this, "Failed to trim", Toast.LENGTH_SHORT).show());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showLocationAlert() {
        // dialog to ask user to open file location in file manager or not
        AlertDialog openFileLocationDialog = new AlertDialog.Builder(ActVideoTrimmer.this).create();
        openFileLocationDialog.setTitle(getString(R.string.open_file_location));
        openFileLocationDialog.setCancelable(true);

        // when user click yes
        openFileLocationDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.yes), (dialogInterface, i) -> {
            // open file location
            Intent chooser = new Intent(Intent.ACTION_GET_CONTENT);
            Uri uriFile = Uri.parse(outputPath);
            chooser.addCategory(Intent.CATEGORY_OPENABLE);
            chooser.setDataAndType(uriFile, "*/*");
            startActivity(chooser);
        });

        // when user click no and finish current activity
        openFileLocationDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.no), (dialogInterface, i) -> openFileLocationDialog.dismiss());

        // when user click no and finish current activity
        openFileLocationDialog.setOnDismissListener(dialogInterface -> {
            Intent intent = new Intent();
            intent.putExtra(TrimVideo.TRIMMED_VIDEO_PATH, outputPath);
            setResult(RESULT_OK, intent);
            finish();
        });
        openFileLocationDialog.show();
    }

    private String[] getAccurateCmd() {
        return new String[]{"-ss", TrimmerUtils.formatCSeconds(lastMinValue)
                , "-i", String.valueOf(filePath), "-t",
                TrimmerUtils.formatCSeconds(lastMaxValue - lastMinValue),
                "-async", "1", outputPath};
    }

    private void showProcessingDialog() {
        try {
            dialog = new Dialog(this);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_convert);
            TextView txtCancel = dialog.findViewById(R.id.txt_cancel);
            dialog.setCancelable(false);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            txtCancel.setOnClickListener(v -> {
                dialog.dismiss();
                FFmpegKit.cancel();
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkStoragePermission() {
        Uri uri = Uri.parse(bundle.getString(TrimVideo.TRIM_VIDEO_URI));
        String fileUri= FileUtilKt.getActualFileUri(this, uri);

        if(fileUri!=null && new File(fileUri).canRead()){
            // might have used photo picker or file picker. therefore have read access without permission.
            return true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            boolean hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
                    == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO)
                            == PackageManager.PERMISSION_GRANTED;
            if (hasPermission) {
                return true;
            } else {
                return checkPermission(
                        Manifest.permission.READ_MEDIA_VIDEO);
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            return checkPermission(
                    Manifest.permission.READ_MEDIA_VIDEO);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return checkPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE);
        } else
            return checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

    }

    private boolean checkPermission(String... permissions) {
        boolean allPermitted = false;
        for (String permission : permissions) {
            allPermitted = (ContextCompat.checkSelfPermission(this, permission)
                    == PackageManager.PERMISSION_GRANTED);
            if (!allPermitted)
                break;
        }
        if (allPermitted)
            return true;
        ActivityCompat.requestPermissions(this, permissions,
                PER_REQ_CODE);
        return false;
    }

    private boolean isPermissionOk(int... results) {
        boolean isAllGranted = true;
        for (int result : results) {
            if (PackageManager.PERMISSION_GRANTED != result) {
                isAllGranted = false;
                break;
            }
        }
        return isAllGranted;
    }

    void startProgress() {
        updateSeekbar.run();
    }

    void stopRepeatingTask() {
        seekHandler.removeCallbacks(updateSeekbar);
    }

}
