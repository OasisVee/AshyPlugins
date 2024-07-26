package com.github.aesh;

import static com.adjust.sdk.Adjust.setEnabled;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aliucord.Logger;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.api.CommandsAPI;
import com.aliucord.api.SettingsAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.Hook;
import com.aliucord.patcher.InsteadHook;
import com.aliucord.utils.DimenUtils;
import com.aliucord.utils.ReflectUtils;
import com.discord.stores.StoreStream;
import com.discord.stores.StoreUserTyping;
import com.discord.widgets.chat.input.MessageDraftsRepo;
import com.discord.widgets.chat.input.WidgetChatInputEditText;
import com.discord.widgets.chat.input.WidgetChatInputEditText$setOnTextChangedListener$1;
import com.lytefast.flexinput.widget.FlexEditText;

import java.util.Locale;
import java.util.Random;

@SuppressWarnings("unused")
@AliucordPlugin
public class neutered extends Plugin {


    public static SettingsAPI Settings;

    FrameLayout carViewLayout;
    Logger logger = new Logger("meow");
    public static Drawable car;

    ImageView carView;
    ImageView keyboardView;

    // Silent Typing
    public static Drawable keyboard;
    public static Drawable disableImage;
    FrameLayout keyboardViewLayout;
    Boolean hideKeyboard = settings.getBool("hideKeyboard",false);
    String[] sp = new String[0];

    public neutered() { needsResources=true; }

    @Override
    public void start(Context context) throws NoSuchMethodException {

        if(!esLang(context)) {
            sp = new String[] {
                    "Hides/Shows Silent Typing Keyboard Icon",
                    "Error initializing drawable",
                    "Toggles Silent Typing, It's Currently ",
                    "You are Now "
            };
        } else {
            sp = new String[] {
                    "Oculta/Muestra el ícono del teclado de Silent Typing",
                    "Error al inicializar drawable",
                    "Alterna Silent Typing, actualmente está ",
                    "Ahora estás "
            };
        }

        commands.registerCommand("togglekeyboard",sp[0], commandContext -> {
            Boolean bool = settings.getBool("hideKeyboard",false);
            setHideKeyboard(!bool);
            return new CommandsAPI.CommandResult();
        });
        registerCommand();

        Settings = settings;
        settingsTab = new SettingsTab(Settings.class, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings, this);

        // Draw the emoji
        patcher.patch(WidgetChatInputEditText.class.getConstructor(FlexEditText.class, MessageDraftsRepo.class), new Hook(callFrame -> {
            WidgetChatInputEditText thisobj = ((WidgetChatInputEditText) callFrame.thisObject);
            try {

                car = neutered.emojiToDrawable(context, "🚗");

                // Ignore the warning it'll be fine trust :3
                FlexEditText etext = (FlexEditText) ReflectUtils.getField(thisobj, "editText");
                LinearLayout group = (LinearLayout) etext.getParent();

                carViewLayout = new FrameLayout(context);
                carView = new ImageView(context);
                ImageView imageView1 = new ImageView(context);
                carView.setImageDrawable(car);

                carViewLayout.addView(carView);

                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) carView.getLayoutParams();
                params.width = DimenUtils.dpToPx(40);
                params.height = DimenUtils.dpToPx(30);

                // SILENT TYPING
                disableImage = neutered.emojiToDrawable(context, "❌");
                keyboard = neutered.emojiToDrawable(context, "⌨");

                keyboardViewLayout = new FrameLayout(context);
                keyboardView = new ImageView(context);
                ImageView imageView2 = new ImageView(context);
                keyboardView.setImageDrawable(keyboard);

                imageView2.setImageDrawable(disableImage);
                keyboardViewLayout.addView(keyboardView);
                keyboardViewLayout.addView(imageView2);

                FrameLayout.LayoutParams karams = (FrameLayout.LayoutParams) keyboardView.getLayoutParams();
                karams.width=DimenUtils.dpToPx(30);
                karams.height=DimenUtils.dpToPx(35);

                keyboardView.setLayoutParams(karams);
                imageView2.setLayoutParams(karams);

                if(hideKeyboard) {
                    keyboardViewLayout.setVisibility(View.GONE);
                    params.width=DimenUtils.dpToPx(0);
                }

                // Running on a diff thread fixes instability ~
                Utils.threadPool.execute(() -> {
                    keyboardViewLayout.setOnClickListener(f -> { setEnabled(!settings.getBool("isEnabled",false)); });
                });

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

                View one = group.getChildAt(1);
                group.removeView(one);

                // the actual point of this plugin -- order that views are added
                group.addView(carViewLayout);

                // Don't ask it works
                ViewGroup.LayoutParams larams = one.getLayoutParams();
                larams.height += .01;
                carViewLayout.setLayoutParams(larams);

                group.addView(keyboardViewLayout);
                keyboardViewLayout.setLayoutParams(one.getLayoutParams());
                updateButton();
                group.addView(one);
                // [ Enviar mensaje a #pl... CAR KEYBOARD EMOJI ]

            } catch (Exception e) {
                Utils.showToast(sp[1]);
                logger.error(e);
            }
            if(settings.getBool("hideOnText",false)){
                patchHideKeyboardOnText();
            }
        }));
    }

    Runnable hideKeyboardOnTextPatch;
    public void patchHideKeyboardOnText() {
        try {
            hideKeyboardOnTextPatch=patcher.patch(WidgetChatInputEditText$setOnTextChangedListener$1.class.getDeclaredMethod("afterTextChanged", Editable.class),new Hook(
                    (cf)->{
                        Editable et = (Editable) cf.args[0];
                        hideButton(!et.toString().isEmpty());
                    }
            ));
        } catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    public void unpatchHideKeyboardOnText(){
        if (hideKeyboardOnTextPatch!=null)hideKeyboardOnTextPatch.run();
    }
    public void registerCommand(){
        var isEnabled = settings.getBool("isEnabled",false);
        var val = isEnabled ? "Enabled" : "Disabled";
        commands.registerCommand("silentTyping",sp[2] + val,commandContext -> {

            setEnabled(!isEnabled);
            return new CommandsAPI.CommandResult();
        });
    }
    Runnable patch = null;
    public void updateButton(){
        if (keyboardViewLayout !=null){
            if (settings.getBool("isEnabled",false)){
                keyboardViewLayout.getChildAt(1).setVisibility(View.VISIBLE);

            } else { keyboardViewLayout.getChildAt(1).setVisibility(View.GONE);}
        }
        patchUserTyping();
    }
    public void hideButton(Boolean bool){
        int status ;
        if(bool){
            status = View.GONE;
        } else {status=View.VISIBLE;}
        Utils.mainThread.post(()->{
            try {
                keyboardViewLayout.setVisibility(status);
            } catch (Exception e){logger.error(e);}
        });
    }
    public void setHideKeyboard(Boolean bool) {
        hideButton(bool);
        hideKeyboard= bool;
        settings.setBool("hideKeyboard", bool);
    }
    public void patchUserTyping(){
        if (settings.getBool("isEnabled", false)) {
            try {
                patch = patcher.patch(StoreUserTyping.class.getDeclaredMethod("setUserTyping", long.class), InsteadHook.DO_NOTHING);
            } catch (NoSuchMethodException e) {
                logger.error(e);
            }
        }
        else
        {
            if (patch!=null){
                patch.run();
            }
        }
    }
    public void setEnabled(boolean val){
        settings.setBool("isEnabled", val);
        patchUserTyping();
        updateButton();
        commands.unregisterCommand("silentTyping");
        registerCommand();

        if (settings.getBool("showToast",false)) {
            var value = ((val) ? "Enabled" : "Disabled");
            var status = ((val) ? "Invisible" : "Visible");
            Utils.mainThread.post(() -> {
                try {
                    Toast.makeText(keyboardViewLayout.getContext(), value + " [ SilentTyping ] " + sp[3] + status, Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    logger.error(e);
                }

            });
        }
    }

    public void stop(Context context) throws Throwable {
        patcher.unpatchAll();
        commands.unregisterAll();
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

    public Locale getPrimaryLocale(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getResources().getConfiguration().getLocales().get(0);
        } else {
            return context.getResources().getConfiguration().locale;
        }
    }

    public boolean esLang(Context context) {
        Locale primaryLocale = getPrimaryLocale(context);
        String languageCode = primaryLocale.getLanguage();
        return languageCode.equalsIgnoreCase("es");
    }
}
