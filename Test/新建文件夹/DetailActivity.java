package news.onon.yaowen;

import java.util.HashMap;

import news.onon.yaowen.app.AppApplication;
import news.onon.yaowen.bean.CommentBeanData.CommentBeanDataNews;
import news.onon.yaowen.bean.NewsBean;
import news.onon.yaowen.db.DbService;
import news.onon.yaowen.dialog.CommentDialog;
import news.onon.yaowen.dialog.CommentDialog.ButtonClickListener;
import news.onon.yaowen.dialog.DetailDialog;
import news.onon.yaowen.dialog.FontDialog;
import news.onon.yaowen.dialog.ShareDialog;
import news.onon.yaowen.fragment.DetailCommentFragment;
import news.onon.yaowen.fragment.DetailWebViewFragment;
import news.onon.yaowen.setting.international.InternationalLoadActivity;
import news.onon.yaowen.tool.ActionAnalysisQeq;
import news.onon.yaowen.tool.LogUtil;
import news.onon.yaowen.tool.NetworkTool;
import news.onon.yaowen.tool.PreferenceUtil;
import news.onon.yaowen.tool.RequestTool;
import news.onon.yaowen.tool.ToastTool;
import news.onon.yaowen.tool.UrlProviderTool;
import news.onon.yaowen.view.DragLayout;
import news.onon.yaowen.view.DragLayout.ShowNextPageNotifier;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.readystatesoftware.systembartint.SystemBarTintManager;

@SuppressLint("SetJavaScriptEnabled")
public class DetailActivity extends FragmentActivity {
	private TextView action_comment_count;
	private TextView bt_back;
	private String url;
	private NewsBean news;
	private ImageView iv_collect;
	private DbService dbService;
	private TextView write_comment_layout;
	private View v_gauze;
	private TextView bt_more;
	private ImageView iv_share;
	private ImageView iv_comment;
	DetailDialog menuWindow;
	private View v_background;
	private CommentDialog commentDialog;
	private Context mContext;
	protected RequestQueue httpRequestQueue;
	private DetailCommentFragment commentFragment;
	private DetailWebViewFragment detailFragment;
	private String newsId;
	private ShowNextPageNotifier nextPageListener;
	private FrameLayout firstFragment;
	private DragLayout draglayout;
	private FrameLayout secondFragment;
	private String commentCount;
	private boolean isFirst = true;
	private View title_bar;
	private View tool_bar;
	private ProgressBar pb_progress;
	private ShareDialog shareDialog;
	private GestureDetector gestureDetector;
	private SystemBarTintManager tintManager;
	private RelativeLayout detailActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initWindow();
		setContentView(R.layout.activity_detail);
		// detailActivity = (RelativeLayout) findViewById(R.id.detailActivity);
		// detailActivity.getViewTreeObserver().addOnGlobalLayoutListener(onglobalFocusChangeListener);
		mContext = this;
		AppApplication.getApp().addActivity(this);
		httpRequestQueue = ((AppApplication) getApplication())
				.getRequestQueen();
		dbService = new DbService(this);
		initGesture();
		LogUtil.log("newsId=========!!!!!!!!!!!!!!!!!!!!!!!!!!");
		initData();
		initView();
	}

	@TargetApi(19)
	private void initWindow() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			tintManager = new SystemBarTintManager(this);
			if (PreferenceUtil.readBoolean(this, "night")) {
				tintManager.setStatusBarTintColor(Color.rgb(0x11, 0x33, 0x4E));
			} else {
				tintManager
						.setStatusBarTintResource(R.drawable.bg_detail_title);
			}
			tintManager.setStatusBarTintEnabled(true);
		}
	}

	public void onPause() {
		super.onPause();
	}

	private void initGesture() {
		gestureDetector = new GestureDetector(getApplicationContext(),
				new OnGestureListener() {

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public void onShowPress(MotionEvent e) {
						// TODO Auto-generated method stub

					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public void onLongPress(MotionEvent e) {
						// TODO Auto-generated method stub

					}

					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						if ((e2.getX() - e1.getX()) > 150) {
							if (Math.abs(e2.getY() - e1.getY()) < 50) {
								DetailActivity.this.finish();
								overridePendingTransition(R.anim.slide_in_left,
										R.anim.slide_out_right);
								return true;
							}
							return false;
						}
						return false;
					}

					@Override
					public boolean onDown(MotionEvent e) {
						// TODO Auto-generated method stub
						return false;
					}
				});

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		gestureDetector.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	private void initData() {

		// 数据库
		dbService = new DbService(mContext);
		//
		Intent intent = getIntent();
		newsId = intent.getStringExtra("newsId");
		if (!TextUtils.isEmpty(newsId)) {
			news = dbService.getNewsById(newsId);
			LogUtil.log("newsId=========" + news.getId());
		} else {
			Bundle bundleExtra = intent.getExtras();
			news = (NewsBean) bundleExtra.getSerializable("news");
			if (news != null) {
				newsId = news.getId();
			}
		}
		if (news != null) {
			url = news.getSrc_link();
		} else {
			url = intent.getStringExtra("url");
		}
	}

	private void initView() {
		// webview 页面
		initFragment();
		draglayout = (DragLayout) findViewById(R.id.draglayout);
		pb_progress = (ProgressBar) findViewById(R.id.pb_progress);
		draglayout.setnextPageListener(nextPageListener);
		// 底部栏目
		action_comment_count = (TextView) findViewById(R.id.action_comment_count);
		bt_back = (TextView) findViewById(R.id.back);
		bt_more = (TextView) findViewById(R.id.top_more_setting);
		iv_collect = (ImageView) findViewById(R.id.iv_collect);
		write_comment_layout = (TextView) findViewById(R.id.write_comment_layout);
		v_gauze = findViewById(R.id.v_gauze);
		v_background = findViewById(R.id.v_background);
		title_bar = findViewById(R.id.title_bar);
		tool_bar = findViewById(R.id.tool_bar);
		iv_share = (ImageView) findViewById(R.id.action_share);
		iv_comment = (ImageView) findViewById(R.id.action_view_comment);
		setView();
		setListener();
	}

	/**
	 * 更新进度条
	 * 
	 * */
	public void renewProgress(int progress) {
		pb_progress.setProgress(progress);
		if (progress == 100) {
			pb_progress.setVisibility(View.GONE);
		}
	}

	/**
	 * 初始化fragment
	 * 
	 * */
	private void initFragment() {
		detailFragment = new DetailWebViewFragment();
		commentFragment = new DetailCommentFragment(newsId, httpRequestQueue);
		Bundle data = new Bundle();
		if (news != null) {
			data.putSerializable("news", news);
		} else {
			data.putString("url", url);
		}
		detailFragment.setArguments(data);
		firstFragment = (FrameLayout) findViewById(R.id.first);
		secondFragment = (FrameLayout) findViewById(R.id.second);
		getSupportFragmentManager().beginTransaction()
				.add(R.id.first, detailFragment)
				.add(R.id.second, commentFragment).commit();
		nextPageListener = new ShowNextPageNotifier() {
			@Override
			public void onDragNext() {
				if (NetworkTool.checkNetState(DetailActivity.this) && isFirst) {
					commentFragment.getDataFromNet(0, 0);
					isFirst = false;
				}
			}
		};
	}

	private void setView() {
		if (!TextUtils.isEmpty(news.getCommentCount())) {
			action_comment_count.setText(Integer.parseInt(news
					.getCommentCount()) > 100 ? "99+" : news.getCommentCount()
					+ "");
		} else {
			action_comment_count.setVisibility(View.INVISIBLE);
		}
		// 如果已经收藏该新闻为选中状态
		if (dbService.hasCollect(news.getId())) {
			iv_collect.setImageResource(R.drawable.ic_collection_checked);
		}
		initMode(PreferenceUtil.readBoolean(mContext, "night"));

	}

	// 初始化夜间模式
	public void initMode(boolean checked) {
		v_background.setVisibility(checked ? View.VISIBLE : View.GONE);
		v_gauze.setBackgroundColor(checked ? 0x881c1c1c : 0x00000000);
		title_bar.setBackgroundResource(checked ? R.drawable.bg_titlebar_night
				: R.drawable.bg_detail_title);
		tool_bar.setBackgroundColor(checked ? 0xff000000 : 0xfff5f5f5);
		write_comment_layout
				.setBackgroundResource(checked ? R.drawable.bg_detail_comment_night
						: R.drawable.bg_comment);
		iv_comment
				.setImageResource(checked ? R.drawable.ic_action_comment_night
						: R.drawable.ic_action_comment);
		iv_share.setImageResource(checked ? R.drawable.ic_action_repost_night
				: R.drawable.ic_action_repost);
	}

	@Override
	public void onBackPressed() {
		if (commentDialog != null && commentDialog.isShowing()) {
			commentDialog.dismiss();
		}
		// if (!detailFragment.goBack()) {
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		super.onBackPressed();
		// }
	}

	private void setListener() {
		// 返回
		bt_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (commentDialog != null && commentDialog.isShowing()) {
					commentDialog.dismiss();
				}
				// if (!detailFragment.goBack()) {
				overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_right);
				finish();
				// }
			}
		});
		// 右上角
		bt_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				menuWindow = new DetailDialog(DetailActivity.this, itemsOnClick);
				menuWindow.showAtLocation(
						DetailActivity.this.findViewById(R.id.detailActivity),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			}
		});
		// 分享
		iv_share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				shareDialog = new ShareDialog(DetailActivity.this, itemsOnClick);
				shareDialog.showAtLocation(
						DetailActivity.this.findViewById(R.id.detailActivity),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
				// final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[] {
				// SHARE_MEDIA.TWITTER, SHARE_MEDIA.FACEBOOK,
				// SHARE_MEDIA.GOOGLEPLUS };
				// ShareContent shareContent = new ShareContent();
				// shareContent.mTargetUrl = news.getSrc_link();
				// shareContent.mTitle = news.getTitle();
				// new ShareAction(DetailActivity.this)
				// .setDisplayList(displaylist).withMedia(image)
				// .setShareContent(shareContent)
				// .setListenerList(umShareListener, umShareListener)
				// .open();
			}
		});
		// 评论
		iv_comment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (firstFragment.getTop() == 0) {
					detailFragment.webScrollToEnd();
					draglayout.animTopOrBottom((View) firstFragment, -300);
				} else {
					draglayout.animTopOrBottom((View) secondFragment, 300);
					detailFragment.webScrollToLast();
				}

			}
		});
		// 收藏按钮事件设置
		iv_collect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (dbService.hasCollect(news.getId())) {
					dbService.removeCollection(news.getId());
					showToast(getString(R.string.hn_collect_cancel_already));
					iv_collect.setImageResource(R.drawable.ic_collection);
				} else {
					dbService.insertCollection(news);
					iv_collect
							.setImageResource(R.drawable.ic_collection_checked);
					HashMap<String, String> hashMap = new HashMap<String, String>();
					hashMap.put("cid", newsId);
					LogUtil.log(newsId + "cid==");
					ActionAnalysisQeq.actionAnalysis(mContext,
							ActionAnalysisQeq.ACTION_COLLECT,
							ActionAnalysisQeq.CONTENT_NEWS, hashMap);
				}
			}
		});
		// 写评论
		write_comment_layout.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				showCommentDialog();
			}

		});

	}

	private ViewTreeObserver.OnGlobalLayoutListener onglobalFocusChangeListener = new OnGlobalLayoutListener() {

		@Override
		public void onGlobalLayout() {
			Rect rect = new Rect();
			detailActivity.getWindowVisibleDisplayFrame(rect);
			int height = detailActivity.getRootView().getHeight();
			LogUtil.log_z("屏幕的高度" + height + "显示的高度" + rect.top + rect.bottom);

		}

	};

	/**
	 * 弹出评论窗口
	 * 
	 * */
	public void showCommentDialog() {
		if (!PreferenceUtil.readBoolean(mContext, "login")) {
			mContext.startActivity(new Intent(mContext,
					InternationalLoadActivity.class));
			return;
		}
		commentDialog = new CommentDialog(mContext, R.style.dialog);

		commentDialog.setCommitButtonClickListener(new ButtonClickListener() {
			@Override
			public void onCommitButtonClick(String comment, EditText et_comment) {
				if (comment.length() == 0) {
					return;
				}
				InputMethodManager inputMethodManager = (InputMethodManager) mContext
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(
						et_comment.getWindowToken(), 0);
				commentNews(comment);

			}

			@Override
			public void onCancelButtonClick(EditText et_comment) {
				InputMethodManager inputMethodManager = (InputMethodManager) mContext
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(
						et_comment.getWindowToken(), 0);

			}
		});

		commentDialog.show();
	}

	/**
	 * 提交评论
	 * 
	 * @param comment
	 *            评论的内容
	 * 
	 * */
	private void commentNews(final String comment) {

		// 设置请求参数
		HashMap<String, String> commitCommentRequestBaseParams = UrlProviderTool
				.getNetRequestBaseParams_5(mContext);
		commitCommentRequestBaseParams.put("token",
				PreferenceUtil.readString(mContext, "tbMemberId"));
		LogUtil.log(commitCommentRequestBaseParams.get("token"));
		commitCommentRequestBaseParams.put("newsId", news.getId());
		commitCommentRequestBaseParams.put("content", comment);
		commitCommentRequestBaseParams.put("isAnonymous",
				PreferenceUtil.readBoolean(mContext, "comment_no_name") + "");
		// 请求成功的监听
		Listener<String> listener = new Listener<String>() {

			@Override
			public void onResponse(String arg0) {
				LogUtil.log(arg0);
				try {
					JSONObject jsonObject = new JSONObject(arg0);
					switch (jsonObject.getInt("code")) {
					case 0:
						JSONObject jsonObject2 = jsonObject
								.getJSONObject("data");
						int commentCount = jsonObject2.getInt("commentCount");
						action_comment_count.setText(commentCount > 100 ? "99+"
								: commentCount + "");
						dbService.changeCollectCommentCount(news.getId(),
								commentCount);
						dbService.changeNewsCommentCount(news.getId(),
								commentCount);
						action_comment_count.setVisibility(View.VISIBLE);
						setResult(2,
								new Intent().putExtra("newsId", news.getId())
										.putExtra("commentCount", commentCount)
										.putExtra("comment", true));
						if (!isFirst) {
							CommentBeanDataNews commentBean = new news.onon.yaowen.bean.CommentBeanData()
									.getCommentBeanDataNews();
							commentBean.setContent(comment);
							commentBean.setCreateTime(mContext.getResources()
									.getString(R.string.hn_moment_now));
							commentBean.setId(jsonObject2.getInt("id") + "");
							commentBean.setPraiseCount(0 + "");
							commentBean.setUserHeadImgUrl(PreferenceUtil
									.readString(mContext, "userPic"));
							commentBean.setUserNickname(PreferenceUtil
									.readString(mContext, "username"));
							commentBean.setCanDelete(true);
							commentFragment.addMyComment(commentBean);
							commentFragment.removeNoCommentNote();
						}
						// cid:"内容id",
						// tid:"评论id",
						// time:"点击时间",
						// comment:"评论内容"
						HashMap<String, String> hashMap = new HashMap<String, String>();
						hashMap.put("cid", newsId);
						hashMap.put("tid", jsonObject2.getInt("id") + "");
						hashMap.put("comment", comment);
						ActionAnalysisQeq.actionAnalysis(mContext,
								ActionAnalysisQeq.ACTION_COMMENT,
								ActionAnalysisQeq.CONTENT_NEWS, hashMap);
						break;
					default:
						break;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		};
		// 请求失败的监听
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				arg0.printStackTrace();
			}

		};
		// 创建请求
		StringRequest commitCommentRequest = RequestTool.creatStringRequest(
				Request.Method.POST, UrlProviderTool.COMMIT_COMMENT,
				commitCommentRequestBaseParams, listener, errorListener);
		httpRequestQueue.add(commitCommentRequest);

	}

	/**
	 * 夜间模式切换
	 * 
	 * */
	public void setMode(boolean checked) {
		detailFragment.setMode(checked);
		commentFragment.setMode(checked);
		initMode(checked);
	}

	/**
	 * popwindow 监听
	 * 
	 * */
	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			if (menuWindow != null && menuWindow.isShowing()) {
				menuWindow.dismiss();
			}
			if (shareDialog != null && shareDialog.isShowing()) {
				shareDialog.dismiss();
			}
			switch (v.getId()) {
			// 字体大小
			case R.id.iv_textsize:
				showSetTextsizeDialog();
				break;
			// 举报
			case R.id.iv_worning:
				// report();
				Intent intent = new Intent(mContext, ReportActivity.class);
				intent.putExtra("newsid", news.getId());
				startActivity(intent);
				break;
			// 夜间模式
			case R.id.iv_mode:
				PreferenceUtil.write(mContext, "night",
						!PreferenceUtil.readBoolean(mContext, "night"));
				setStatebar();
				for (Activity activity : AppApplication.getApp()
						.getActivityList()) {
					if (activity instanceof DetailActivity) {
						((DetailActivity) activity).setMode(PreferenceUtil
								.readBoolean(mContext, "night"));
					}
				}
				sendBroadcast(new Intent("CHANGEMODE"));
				break;
			// facebook分享
			case R.id.iv_facebook:
				break;
			// twitter分享
			case R.id.iv_twitter:
				break;
			// google分享
			case R.id.iv_google:
				break;
			default:
				break;
			}
		}

		private void setStatebar() {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				if (PreferenceUtil.readBoolean(mContext, "night")) {
					tintManager.setStatusBarTintColor(Color.rgb(0x11, 0x33,
							0x4E));
				} else {
					tintManager
							.setStatusBarTintResource(R.drawable.bg_detail_title);
				}
			}
		}

	};

	/**
	 * 分享回调
	 * 
	 * */

	/**
	 * 弹出字体大小dialog
	 * */
	private void showSetTextsizeDialog() {
		FontDialog fontDialog = new FontDialog(mContext, R.style.dialog);
		fontDialog.show();
		fontDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				detailFragment.setTextsize();
				mContext.sendBroadcast(new Intent("CHANGETEXTSIZE"));
			}
		});
	}

	/**
	 * 显示toast
	 * 
	 * @param content
	 *            所要显示的字符串
	 * */
	protected void showToast(String content) {
		ToastTool.showToast(mContext, content);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refresh();
	}

	private void refresh() {
		// 设置请求参数
		HashMap<String, String> commitCommentRequestBaseParams = UrlProviderTool
				.getNetRequestBaseParams(mContext);
		// 请求成功的监听
		Listener<String> listener = new Listener<String>() {

			@Override
			public void onResponse(String arg0) {
				LogUtil.log(arg0);
				try {
					JSONObject jsonObject = new JSONObject(arg0);
					switch (jsonObject.getInt("code")) {
					case 0:
						String data = jsonObject.getString("data");
						JSONObject dataObject = new JSONObject(data);
						commentCount = dataObject.getString("commentCount");
						action_comment_count.setText(Integer
								.parseInt(commentCount) > 100 ? "99+"
								: commentCount);
						dbService.changeCollectCommentCount(news.getId(),
								Integer.parseInt(commentCount));
						dbService.changeNewsCommentCount(news.getId(),
								Integer.parseInt(commentCount));
						setResult(
								2,
								new Intent()
										.putExtra("newsId", news.getId())
										.putExtra("commentCount",
												Integer.parseInt(commentCount))
										.putExtra("comment", false));
						break;
					default:
						break;
					}
				} catch (JSONException e) {
					e.printStackTrace();
					LogUtil.log(e.toString());
				}

			}
		};
		// 请求失败的监听
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				Log.d("code", arg0.toString());
			}

		};
		// 创建请求
		StringRequest creatStringRequest = RequestTool.creatStringRequest(
				Method.GET, UrlProviderTool.getUrl(UrlProviderTool.GETNEWSINFO
						+ news.getId() + "/related",
						commitCommentRequestBaseParams), null, listener,
				errorListener);
		httpRequestQueue.add(creatStringRequest);
	}

	public void setCommentDel(String count) {
		if (TextUtils.isEmpty(count) || Integer.parseInt(count) == 0) {
			action_comment_count.setText(0 + "");
			action_comment_count.setVisibility(View.INVISIBLE);
			setResult(2, new Intent().putExtra("newsId", news.getId())
					.putExtra("comment", true));
			return;
		}
		if (!TextUtils.isEmpty(count)) {
			action_comment_count.setVisibility(View.VISIBLE);
			action_comment_count.setText(count);
			setResult(2, new Intent().putExtra("newsId", news.getId())
					.putExtra("commentCount", Integer.parseInt(count))
					.putExtra("comment", true));
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			LogUtil.log_z("返回返回返回返回");
			InputMethodManager inputMethodManager = (InputMethodManager) mContext
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(
					v_background.getWindowToken(), 0);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
