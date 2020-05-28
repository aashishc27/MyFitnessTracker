package activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.myfitnesstracker.R;


import androidx.fragment.app.FragmentManager;

import fragments.MenuFragment;

public class ActionBar extends RelativeLayout {
    LinearLayout ll_description;
    RelativeLayout main;
    boolean fromDashboard = false;
    Activity mcontext;
    private ToggleButton buttonSave;
    private ToggleButton buttonClear;
    private ToggleButton buttonHelp;
    private ImageView buttonBack, delete_image, menuButton;
    private FrameLayout sepparator;
    private TextView textview, textView_discription, page_count, actionbar_head_text;
    private ProgressBar progressBar;
    private ImageView imageViewIcon;

    public ActionBar(Context context) {
        super(context);

        init(context);
    }

    public ActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ActionBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(final Context context) {
        final View rootView = inflate(context, R.layout.actionbar, this);

        main = rootView.findViewById(R.id.main);
        buttonSave = rootView.findViewById(R.id.actionbar_togglebutton_save);
        buttonBack = rootView.findViewById(R.id.actionbar_togglebutton_back);
        textview = rootView.findViewById(R.id.actionbar_textview);
        textView_discription = rootView.findViewById(R.id.actionbar_textview_2);
        imageViewIcon = rootView.findViewById(R.id.actionbar_imageview_icon);
        buttonClear = rootView.findViewById(R.id.actionbar_togglebutton_clear);
        sepparator = rootView.findViewById(R.id.actionbar_sepparator);
        ll_description = rootView.findViewById(R.id.ll_description);

        delete_image = rootView.findViewById(R.id.delete_image);
        menuButton = rootView.findViewById(R.id.menu_image);
        page_count = rootView.findViewById(R.id.page_count);
        actionbar_head_text = rootView.findViewById(R.id.actionbar_head_text);

    }

    public void showPageCount() {
        page_count.setVisibility(VISIBLE);
    }

    public void setPage_count(int currentPage, int maxVal) {
        page_count.setText(currentPage + " of " + maxVal);
    }

    public void cancelButtonEnable() {
        delete_image.setVisibility(VISIBLE);
        menuButton.setVisibility(GONE);

    }

    public void showDashBoardLogo() {
        fromDashboard = true;
        imageViewIcon.setVisibility(VISIBLE);
    }

    public void setMenuClick(final Activity activity, final FragmentManager supportFragmentManager) {
        menuButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                new MenuFragment(menuButton, fromDashboard, activity).show(supportFragmentManager, "FullScreen");
//                menuButton.setVisibility(View.GONE);
            }
        });


    }

    public void setBackgroundColor(Drawable drawable) {
        main.setBackground(drawable);
    }

    public void setOnSaveButtonClickListener(OnClickListener listener) {
        buttonSave.setOnClickListener(listener);
    }

    public void setSaveButtonDrawable(Drawable d) {
        buttonSave.setBackgroundDrawable(d);
        buttonSave.setVisibility(View.VISIBLE);
    }

    public void setOnHelpButtonClickListener(OnClickListener listener) {
        buttonHelp.setOnClickListener(listener);
    }

    public void setHelpButtonDrawable(Drawable d) {
        buttonHelp.setBackgroundDrawable(d);
        buttonHelp.setVisibility(View.VISIBLE);
    }

    public void setProgressBar() {
        progressBar.setVisibility(VISIBLE);
    }

    public void setProgress(int val) {
        progressBar.setProgress(val);
    }

    public void setOnBackButtonClickListener(final Activity activity) {

        buttonBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });
    }

    public void setBackButtonDrawable() {
        buttonBack.setVisibility(View.VISIBLE);
    }

    public void setClearButtonDrawable(Drawable d) {
        buttonClear.setBackgroundDrawable(d);
        buttonClear.setVisibility(View.VISIBLE);
    }

    public void hideMenu() {
        menuButton.setVisibility(GONE);
    }

    public void showMenu() {
        menuButton.setVisibility(VISIBLE);
    }


    public void setOnClearButtonClickListener(OnClickListener listener) {
        buttonClear.setOnClickListener(listener);
    }

    public void setTitle(String value) {
        textview.setText(value);
    }

    public void setLeftTitle(String value) {

        actionbar_head_text.setText(value);
    }

    public void setCustomTitle(CharSequence text, TextView.BufferType type) {

        actionbar_head_text.setText(text, type);
    }

    public void setTitleDiscription(boolean fromDocument) {
        ll_description.setVisibility(VISIBLE);
//        String title=getResources().getString(R.string.head_upload_description_2);
//
//        if(fromDocument){
//            String string= getResources().getString(R.string.head_uplod_doc);
//
//            SpannableStringBuilder str = new SpannableStringBuilder(string);
//            str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            textView_discription.append(str);
//        }else{
//            String string= getResources().getString(R.string.head_uplod_doc);
//
//            SpannableStringBuilder str = new SpannableStringBuilder(string);
//            str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            textView_discription.append(str);
//        }
//
//
//
//
//        textView_discription.append(title);


    }




    public void hideDescription() {
        ll_description.setVisibility(GONE);
    }


    public void setSaveButtonChecked(boolean value) {
        buttonSave.setChecked(value);
    }

    public void setSeporatorVisible(int visibility) {
        sepparator.setVisibility(visibility);
    }


}
