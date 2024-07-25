package com.github.aesh;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aliucord.Logger;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.api.SettingsAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.Hook;
import com.aliucord.utils.DimenUtils;
import com.aliucord.utils.ReflectUtils;
import com.discord.stores.StoreStream;
import com.discord.widgets.chat.input.MessageDraftsRepo;
import com.discord.widgets.chat.input.WidgetChatInputEditText;
import com.lytefast.flexinput.widget.FlexEditText;

import java.util.Random;

@SuppressWarnings("unused")
@AliucordPlugin
public class meow extends Plugin {
    public static SettingsAPI Settings;

    FrameLayout carViewLayout;
    Logger logger = new Logger("meow");

    ImageView carView;

    @Override
    public void start(Context context) throws NoSuchMethodException {

        Settings = settings;
        settingsTab = new SettingsTab(Settings.class, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings, this);

        // Draw the emoji
        patcher.patch(WidgetChatInputEditText.class.getConstructor(FlexEditText.class, MessageDraftsRepo.class), new Hook(callFrame -> {
            WidgetChatInputEditText thisobj = ((WidgetChatInputEditText) callFrame.thisObject);
            try {
                Drawable car = meow.emojiToDrawable(context, "🚗");

                // Ignore the warning it'll be fine trust :3
                FlexEditText etext = (FlexEditText) ReflectUtils.getField(thisobj, "editText");
                LinearLayout group = (LinearLayout) etext.getParent();

                carViewLayout = new FrameLayout(context);
                carView = new ImageView(context);
                ImageView imageView1 = new ImageView(context);
                carView.setImageDrawable(car);

                carViewLayout.addView(carView);

                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) carView.getLayoutParams();
                params.width = DimenUtils.dpToPx(30);
                params.height = DimenUtils.dpToPx(30);

                carViewLayout.setOnClickListener(v -> {
                    String meow = "";
                    int randFaceShort = new Random().nextInt(100);

                    if(settings.getBool("extraMeows", false) && !settings.getBool("forceMiau", false)) {

                        int randFaceLong = new Random().nextInt(100);
                        int randW = new Random().nextInt(100);
                        int randTilde = new Random().nextInt(100);
                        int randPurrOverride = new Random().nextInt(100);
                        int randNyanOverride = new Random().nextInt(100);
                        int randMiauOverride = new Random().nextInt(100);

                        meow = "meow";
                        if(randW < settings.getInt("chanceExtraW", 0) -1) { meow += "w"; }

                        if(randPurrOverride < settings.getInt("chancePurr", 0) -1) {
                            meow = "*purr*";
                            if(randW < settings.getInt("chanceExtraW", 0) -1) { meow = "*purrr*"; }
                        }
                        if(randNyanOverride < settings.getInt("chanceNyan", 0) -1) {
                            meow = "nyan";
                            if(randW < settings.getInt("chanceExtraW", 0) -1) { meow += "n"; }
                        }
                        if(randMiauOverride < settings.getInt("chanceMiau", 0) -1) {
                            meow = "miau";
                            if(randW < settings.getInt("chanceExtraW", 0) -1) { meow += "u"; }
                        }

                        if(randTilde < settings.getInt("chanceTilde", 0) -1) { meow += " ~"; }

                        if(randFaceShort <= settings.getInt("chanceShortFace", 0)) {
                            if(randFaceLong <= settings.getInt("chanceLongFace", 0)) {
                                meow += " ≽^•⩊•^≼";
                            } else { meow += " :3"; }
                        }
                    }

                    else if(!settings.getBool("extraMeows", false) && !settings.getBool("forceMiau", false)) {
                        meow = "meow";
                        if(randFaceShort < settings.getInt("chanceShortFace", 0) -1) { meow += " :3"; }
                    }

                    else if(!settings.getBool("extraMeows", false) && settings.getBool("forceMiau", false)) {
                        meow = "miau";
                        if(randFaceShort < settings.getInt("chanceShortFace", 0) -1) { meow += " :3"; }
                    }

                    else if(settings.getBool("extraMeows", false) && settings.getBool("forceMiau", false)) {
                        int randFaceLong = new Random().nextInt(100);
                        int randW = new Random().nextInt(100);
                        int randTilde = new Random().nextInt(100);
                        meow = "miau";

                        if(randW < settings.getInt("chanceExtraW", 0) -1) { meow += "u"; }
                        if(randTilde < settings.getInt("chanceTilde", 0) -1) { meow += " ~"; }
                        if(randFaceShort < settings.getInt("chanceShortFace", 0) -1) {
                            if(randFaceLong < settings.getInt("chanceLongFace", 0) -1) {
                                meow += " ≽^•⩊•^≼";
                            } else { meow += " :3"; }
                        }
                    }

                    sendMeow nyan = new sendMeow();
                    long channelId = StoreStream.getChannelsSelected().getId();
                    nyan.sendMessage(channelId, meow);
                });

                View v = group.getChildAt(1);
                group.removeView(v); // Find and kill the child [ Emoji button ]

                group.addView(carViewLayout);
                group.addView(v);

            } catch (Exception e) {
                Utils.showToast("Error initializing drawable");
                logger.error(e);
            }
        }));
    }

    public void stop(Context context) throws Throwable {
        patcher.unpatchAll();
    }

    public static Drawable emojiToDrawable(Context context, String emoji) {
        TextView textView = new TextView(context);
        textView.setText(emoji);

        textView.measure(
                TextView.MeasureSpec.makeMeasureSpec(0, TextView.MeasureSpec.UNSPECIFIED),
                TextView.MeasureSpec.makeMeasureSpec(0, TextView.MeasureSpec.UNSPECIFIED)
        );
        textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());

        /*
        Literally just draw the emoji on the new bitmap.
        [ I sure hope this doesn't slow the client down ! :3 ]
        */
        Bitmap bitmapcar = Bitmap.createBitmap(textView.getWidth(), textView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapcar);
        textView.draw(canvas);

        return new BitmapDrawable(context.getResources(), bitmapcar);
    }
}
