package cn.lockyluo.searchview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.appcompat.widget.AppCompatImageView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.lockyluo.utils.HistoryDbHelper;
import cn.lockyluo.utils.SimpleListener;

/**
 * Created by LockyLuo on 2018/6/30.
 */

public class SearchFieldView extends LinearLayout {
    private View view;
    public EditText editText;
    public AppCompatImageView btnSearch;
    public AppCompatImageView btnClear;
    private Context context;
    private Paint textPaint = new Paint();

    private String hint;
    private Drawable background;
    private Drawable editTextBackground;
    private Rect rect=new Rect();
    private @Dimension
    int editTextSize = 10;
    private @Dimension
    int historyTextSize = 10;

    private @ColorInt
    int editTextColor = Color.BLACK;
    private @ColorInt
    int historyTextColor = Color.BLACK;
    private static final String TAG = "SearchFieldView";
    private HistoryDbHelper historyDbHelper;
    private List<String> historyList = new ArrayList<>();
    private View popupView;
    private PopupWindow popupWindow;

    public SearchFieldView(Context context) {
        this(context, null);
    }

    public SearchFieldView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchFieldView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.searchview_layout, this);

        historyDbHelper = new HistoryDbHelper(context);
        historyDbHelper.getWritableDatabase();
        initTypedArray(attrs);

        initView(context, attrs, defStyleAttr);
        ViewCompat.setBackground(this, background);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SearchFieldView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widMode = MeasureSpec.getMode(widthMeasureSpec);
        int heiMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (widMode == MeasureSpec.AT_MOST) {
            Rect bounds = new Rect();
            textPaint.getTextBounds(hint, 0, hint.length(), bounds);
            width = bounds.width() + 10 + getPaddingLeft() + getPaddingRight();
        }

        if (heiMode == MeasureSpec.AT_MOST) {
            Rect bounds = new Rect();
            textPaint.getTextBounds(hint, 0, hint.length(), bounds);
            height = bounds.height() + 10 + getPaddingTop() + getPaddingBottom();
        }
        Log.i(TAG, "onMeasure: " + width + " " + height);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
//        int x = getPaddingLeft();
//        int dy = (fontMetrics.bottom - fontMetrics.top)/2 - fontMetrics.bottom;
//        int baseLine = getHeight()/2 + dy;
//
//
//        Log.i(TAG, "onDraw: "+getMeasuredWidth()+" "+getMeasuredHeight());
//
//        canvas.drawText(hint,x,baseLine, textPaint);
    }

    public void initTypedArray(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SearchFieldView);
        hint = typedArray.getString(R.styleable.SearchFieldView_searchViewHint);
        if (TextUtils.isEmpty(hint)) {
            hint = "请输入内容";
        }
        background = ContextCompat.getDrawable(context, typedArray.getResourceId(R.styleable.SearchFieldView_searchViewBackground, R.drawable.shape_colorbackground));
        editTextBackground = ContextCompat.getDrawable(context, typedArray.getResourceId(R.styleable.SearchFieldView_searchEditTextBackground, R.drawable.shape_white_radius10));
        editTextSize = typedArray.getDimensionPixelSize(R.styleable.SearchFieldView_searchEditTextSize, editTextSize);
        historyTextSize = typedArray.getDimensionPixelSize(R.styleable.SearchFieldView_historyTextSize, historyTextSize);
        editTextColor = typedArray.getColor(R.styleable.SearchFieldView_searchEditTextColor, editTextColor);
        historyTextColor = typedArray.getColor(R.styleable.SearchFieldView_historyTextColor, historyTextColor);
        typedArray.recycle();
    }

    public void initView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        editText = view.findViewById(R.id.edittext_searchfield);
        btnSearch = view.findViewById(R.id.iv_btn_search);
        btnClear = view.findViewById(R.id.iv_btn_clear);

        LayoutParams layoutParams = (LinearLayout.LayoutParams) editText.getLayoutParams();
        layoutParams.setMargins(15, 15, 15, 15);
        editText.setLayoutParams(layoutParams);

        editText.setAlpha(0.80f);
        editText.setGravity(Gravity.CENTER_VERTICAL);
        editText.setTextColor(editTextColor);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, editTextSize);
        ViewCompat.setBackground(editText, ContextCompat.getDrawable(context, R.drawable.shape_white_radius10));
        editText.setHint(hint);

        layoutParams = (LinearLayout.LayoutParams) btnSearch.getLayoutParams();
        layoutParams.setMargins(0, 15, 15, 15);
        btnSearch.setLayoutParams(layoutParams);
        btnClear.setVisibility(GONE);
        initListeners();

    }

    private void changeBtnClear() {
        if (TextUtils.isEmpty(editText.getText())) {
            btnClear.setVisibility(GONE);
        } else
            btnClear.setVisibility(VISIBLE);
    }

    private List<String> query(String s) {
        if (TextUtils.isEmpty(s)) {
            s = s.trim();
        }else
            s=null;
        Cursor cursor = historyDbHelper.query(s);
        if (cursor==null){
            return new ArrayList<>();
        }
        historyList.clear();
        while (cursor.moveToNext()) {
            historyList.add(cursor.getString(cursor.getColumnIndex("name")));
        }
        Collections.reverse(historyList);
        return historyList;
    }

    Thread queryThread;

    public void queryInBackground(final String s, final SimpleListener<List> simpleListener) {
        if (queryThread != null) {
            if (queryThread.isAlive())
                return;
            else
                queryThread = null;
        }
        queryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i(TAG, "run:queryThread done");
                    List list = query(s);
                    simpleListener.done(list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        queryThread.start();
    }

    private void insert(String s) {
        final String temp = s = s.trim();
        queryInBackground(s, new SimpleListener<List>() {
            @Override
            public void done(List data) {
                if (data.size() > 0) {
                    historyDbHelper.del(temp);
                }
                historyDbHelper.insert(temp);
            }
        });
    }


    public void initListeners() {

        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    queryAndShowPopupWindow();
                } else {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    }
                }
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                changeBtnClear();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changeBtnClear();
            }

            @Override
            public void afterTextChanged(Editable s) {
                queryAndShowPopupWindow();
                changeBtnClear();
            }
        });

        btnClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
                changeBtnClear();
                queryAndShowPopupWindow();
            }
        });

        btnSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow!=null&&popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
                String s = editText.getText().toString();
                if (!TextUtils.isEmpty(s)) {
                    insert(s);
                }
                SearchFieldView.this.requestFocus();


            }
        });


    }

    public void queryAndShowPopupWindow() {
        queryInBackground(editText.getText().toString(), new SimpleListener<List>() {
            @Override
            public void done(List data) {
                if (data!=null&&data.size()>0) {
                    showPopupDelay();
                }
//                Log.i(TAG, "afterTextChanged: " + Arrays.toString(historyList.toArray()));
            }
        });
    }

    public void showPopupDelay() {
        this.post(new Runnable() {
            @Override
            public void run() {
                showPopup();
            }
        });
    }

    private void showPopup() {
        if (popupWindow != null && popupWindow.isShowing()) {
        } else {
            popupView = LayoutInflater.from(context).inflate(R.layout.popuphistories_layout, null);
            popupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, false);
            popupWindow.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, R.color.white)));
            popupWindow.getBackground().setAlpha(200);
            popupWindow.setTouchable(true);
            popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                popupWindow.setElevation(5);
            }
        }


        int[] pos = new int[2];
        this.getLocationOnScreen(pos);
        if (getRootView() == null)
            return;
        popupWindow.showAtLocation(getRootView(), Gravity.TOP, pos[0], pos[1] + this.getHeight());

        if (historyList.size() > 0) {
            FlowLayout flowLayout=popupView.findViewById(R.id.flowlayout);
            flowLayout.setLables(historyList,false);
            flowLayout.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position, String text) {
                    editText.setText(text);
                }
            });

        }
        popupView.findViewById(R.id.btn_close).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popupView.findViewById(R.id.tv_clear).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                historyDbHelper.deleteAllData();
            }
        });
    }


}
