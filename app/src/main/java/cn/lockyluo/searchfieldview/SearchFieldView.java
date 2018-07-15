package cn.lockyluo.searchfieldview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;

import cn.lockyluo.utils.HistoryDbHelper;

/**
 * Created by LockyLuo on 2018/6/30.
 */

public class SearchFieldView extends LinearLayout {

    public EditText editText;
    public AppCompatImageView btnSearch;
    private Context context;
    private Paint textPaint =new Paint();

    private String hint;
    private Drawable background;
    private Drawable editTextBackground;
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

    public SearchFieldView(Context context) {
        this(context, null);
    }

    public SearchFieldView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchFieldView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        LayoutInflater.from(context).inflate(R.layout.searchview_layout, this);
        setOrientation(VERTICAL);

        historyDbHelper=new HistoryDbHelper(context);

        initTypedArray(attrs);

        initView(context,attrs,defStyleAttr);
        ViewCompat.setBackground(this,background);
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
            Rect bounds=new Rect();
            textPaint.getTextBounds(hint,0,hint.length(),bounds);
            width = bounds.width()+10 + getPaddingLeft() +getPaddingRight();}

        if (heiMode == MeasureSpec.AT_MOST) {
            Rect bounds = new Rect();
            textPaint.getTextBounds(hint,0,hint.length(),bounds);
            height = bounds.height()+10 + getPaddingTop() +getPaddingBottom();
        }
        Log.i(TAG, "onMeasure: "+width+" "+height);
        setMeasuredDimension(width,height);
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

    public void initTypedArray(AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SearchFieldView);
        hint = typedArray.getString(R.styleable.SearchFieldView_searchViewHint);
        if (TextUtils.isEmpty(hint)){
            hint="请输入内容";
        }
        background = ContextCompat.getDrawable(context, typedArray.getResourceId(R.styleable.SearchFieldView_searchViewBackground, R.drawable.shape_colorbackground));
        editTextBackground = ContextCompat.getDrawable(context, typedArray.getResourceId(R.styleable.SearchFieldView_searchEditTextBackground, R.drawable.shape_white_radius10));
        editTextSize = typedArray.getDimensionPixelSize(R.styleable.SearchFieldView_searchEditTextSize, editTextSize);
        historyTextSize = typedArray.getDimensionPixelSize(R.styleable.SearchFieldView_historyTextSize, historyTextSize);
        editTextColor=typedArray.getColor(R.styleable.SearchFieldView_searchEditTextColor,editTextColor);
        historyTextColor=typedArray.getColor(R.styleable.SearchFieldView_historyTextColor,historyTextColor);
        typedArray.recycle();
    }

    public void initView(Context context,AttributeSet attributeSet,int defStyleAttr){
        editText =(EditText) findViewById(R.id.edittext_searchfield);
        btnSearch=(AppCompatImageView)findViewById(R.id.iv_btn_search);
        LayoutParams layoutParams=(LinearLayout.LayoutParams)editText.getLayoutParams();
        layoutParams.setMargins(15,15,15,15);
        editText.setLayoutParams(layoutParams);

        editText.setAlpha(0.80f);
        editText.setGravity(Gravity.CENTER_VERTICAL);
        editText.setTextColor(editTextColor);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX,editTextSize);
        ViewCompat.setBackground(editText,ContextCompat.getDrawable(context,R.drawable.shape_white_radius10));
        editText.setHint(hint);

        layoutParams=(LinearLayout.LayoutParams)btnSearch.getLayoutParams();
        layoutParams.setMargins(0,15,15,15);
        btnSearch.setLayoutParams(layoutParams);


    }


}
