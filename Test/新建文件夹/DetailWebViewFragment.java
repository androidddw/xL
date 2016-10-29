package news.onon.yaowen.fragment;

import java.io.Serializable;

import news.onon.yaowen.DetailActivity;
import news.onon.yaowen.PhotoAlbumActivity;
import news.onon.yaowen.R;
import news.onon.yaowen.bean.NewsBean;
import news.onon.yaowen.db.DbService;
import news.onon.yaowen.dialog.FontDialog;
import news.onon.yaowen.setting.YuanwenWebviewActivity;
import news.onon.yaowen.tool.HtmlString;
import news.onon.yaowen.tool.LogUtil;
import news.onon.yaowen.tool.NetworkTool;
import news.onon.yaowen.tool.PreferenceUtil;
import news.onon.yaowen.view.CustWebView_1;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

@SuppressLint("SetJavaScriptEnabled")
public class DetailWebViewFragment extends Fragment {
	private static final String APP_CACHE_DIRNAME = "/webcache";
	private View progressBar;
	private CustWebView_1 mWebView;
	private boolean hasInited = false;
	private WebSettings setting;
	private String cacheDirPath;
	private NewsBean news;
	private DbService dbService;
	private String url;
	private String id;
	private String content;
	private String js_day;
	private Context mContext;
	private String js_night;
	private View rootView;
	private RelativeLayout rl_back;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		rootView = inflater.inflate(R.layout.detailwebviewfragment, null);
		mWebView = (CustWebView_1) rootView
				.findViewById(R.id.fragment3_webview);
		rl_back = (RelativeLayout) rootView.findViewById(R.id.rl_back);
		progressBar = rootView.findViewById(R.id.progressbar);
		initData();
		initView();
		return rootView;
	}

	private void initData() {
		dbService = new DbService(mContext);
		// 黑白天切换
		js_night = "(function(){window.fontColor=document.getElementsByTagName(\"body\")[0];fontColor.style.color=\"white\";fontColor.style.background=\"#000000\"})()";
		js_day = "(function(){window.fontColor=document.getElementsByTagName(\"body\")[0];fontColor.style.color=\"black\";fontColor.style.background=\"#ffffff\"})()";

	}

	/**
	 * 对js进行注入
	 */
	private String[] file_strs;

	public class PayJavaScriptInterface {

		PayJavaScriptInterface() {
		}

		@JavascriptInterface
		public void runOnAndroidJavaScript(String[] str, int position) {
			file_strs = str;
			Intent intent = new Intent(mContext, PhotoAlbumActivity.class);
			intent.putExtra("data", (Serializable) file_strs);
			intent.putExtra("position", position);
			startActivity(intent);
			((DetailActivity) mContext).overridePendingTransition(
					R.anim.alpha_in, R.anim.alpha_out);
		}

		@JavascriptInterface
		public void readText(String str) {
			if (NetworkTool.checkNetState(mContext)) {
				Intent in = new Intent(
						((DetailActivity) mContext).getApplication(),
						YuanwenWebviewActivity.class);
				in.putExtra("url", str);
				startActivity(in);
				((DetailActivity) mContext).overridePendingTransition(
						R.anim.slide_in_left, R.anim.slide_out_right);
			}
		}
	}

	public void initView() {
		if (null != mWebView && !hasInited) {
			hasInited = true;
			progressBar.setVisibility(View.GONE);
			initWebView();
			news = (NewsBean) getArguments().getSerializable("news");
			if (news != null) {
				id = news.getId();
				url = news.getSrc_link();
			} else {
				url = getArguments().getString("url");
			}
			if (NetworkTool.checkNetState(mContext)) {
				mWebView.loadUrl(url);
				LogUtil.log_z("detai__url---" + url);
			} else {
				content = dbService.getContent(id);
				HtmlString html = new HtmlString(mContext,
						((NewsBean) news).getTitle(), content,
						news.getCreateTime(), news.getSource(),
						news.getSrc_link());
				String h5 = html.getHtml();
				if (h5 != null) {
					// mWebView.loadData(h5, "text/html;charset=utf-8", null);
					mWebView.loadDataWithBaseURL(null, h5, "text/html",
							"utf-8", null);
				}
			}
			// mWebView.setVisibility(View.VISIBLE);
		}
		setView();
	}

	private void setView() {
		setMode(PreferenceUtil.readBoolean(mContext, "night"));
		// mWebView.setVisibility(View.VISIBLE);
	}

	@SuppressWarnings("deprecation")
	public void initWebView() {
		setting = mWebView.getSettings();
		mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		mWebView.setBackgroundColor(0x00000000); // 设置背景色
		mWebView.getSettings().setJavaScriptEnabled(true); // 加上这句话才能使用javascript方法
		// 网页大小
		setting.setUseWideViewPort(true);
		setting.setLoadWithOverviewMode(true);
		setting.setPluginState(PluginState.ON);
		setting.setDefaultTextEncodingName("UTF-8");
		setting.setRenderPriority(RenderPriority.HIGH);
		setting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); // 设置缓存模式
		setting.setDomStorageEnabled(true);
		setting.setDatabaseEnabled(true);
		cacheDirPath = mContext.getCacheDir().getAbsolutePath()
				+ APP_CACHE_DIRNAME;
		String databasePath = mContext.getApplicationContext()
				.getDir("databases", Context.MODE_PRIVATE).getPath();
		setting.setDatabasePath(databasePath);
		setting.setAppCacheEnabled(true);
		setting.setAppCachePath((String) cacheDirPath);
		setting.setBlockNetworkImage(false);
		if (!NetworkTool.isWIFIConnected(mContext)) {
			boolean imageMode = PreferenceUtil.readBoolean(mContext,
					"imageMode");
			setting.setBlockNetworkImage(imageMode);
		}
		mWebView.addJavascriptInterface(new PayJavaScriptInterface(), "injs");
		mWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				super.onProgressChanged(view, newProgress);
				((DetailActivity) mContext).renewProgress(newProgress);
				mWebView.setVisibility(View.VISIBLE);
			}
		});
		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// 这里进行无网络或错误处理，具体可以根据errorCode的值进行判断，做跟详细的处理。
				content = dbService.getContent(id);
				HtmlString html = new HtmlString(mContext, ((NewsBean) news)
						.getTitle(), content, news.getCreateTime(), news
						.getSource(), news.getSrc_link());
				String h5 = html.getHtml();
				if (h5 != null) {
					// mWebView.loadData(h5, "text/html;charset=utf-8", null);
					mWebView.loadDataWithBaseURL(null, h5, "text/html",
							"utf-8", null);
				}
				mWebView.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				setTextsize();
				setView();
				if (PreferenceUtil.readBoolean(mContext, "imageMode")) {
					mWebView.loadUrl("javascript:getFromAndroid(" + 1 + ")");
					mWebView.postInvalidate();
					mWebView.loadUrl("javascript:APPNavigator()");
				} else {
					mWebView.loadUrl("javascript:getFromAndroid(" + 0 + ")");
					mWebView.loadUrl("javascript:APPNavigator()");
				}
				// 对js进行注入 js调用安卓方法
				mWebView.loadUrl("javascript:(function(){"
						+ "  var objs = document.getElementsByTagName(\"img\"); "
						+ "	 var file =new Array();"
						+ "  for(var i=0;i<objs.length;i++){"
						+ "     file[i]=objs[i].getAttribute('_src');"
						+ "     objs[i].index=i;"
						+ "     objs[i].onclick=function(){"
						+ "          window.injs.runOnAndroidJavaScript(file,this.index);  "
						+ "     }" + "  }" + "})()");
				mWebView.setVisibility(View.VISIBLE);
			}
		});

	}

	private int mScrollY;

	public void webScrollToEnd() {
		mScrollY = mWebView.getLastPostion();
		mWebView.scrollTo(0,
				(int) (mWebView.getContentHeight() * mWebView.getScale()));
	}

	/**
	 * 设置webview字体大小
	 * 
	 * @param textSize
	 *            字体缩放百分比
	 * 
	 * */
	private void setTextSize_js(int textSize) {
		String js = "change_Size("
				+ textSize
				+ ");function change_Size(format){var eleDom=document.querySelector('.article-content');var titleDom=document.body.getElementsByTagName('h1')[0];var childArr=new Array();if(format!=100){var lineDom=format/2}else{var lineDom=format/3};var totalDom=format+lineDom;for(var i=0,l=eleDom.children.length;i<l;i++){eleDom.children[i].style.fontSize=format+'%';eleDom.children[i].style.lineHeight=totalDom+'%';childArr.push(eleDom.children[i]);checkArr()}function checkArr(){childArr.forEach(function(value){if(childArr.length){for(var i=0,l=value.children.length;i<l;i++){value.children[i].style.fontSize=format+'%';value.children[i].style.lineHeight=totalDom+'%';childArr.length=0;childArr.push(value.children[i]);checkArr()}}})}titleDom.style.fontSize=format+'%';titleDom.style.lineHeight=totalDom+'%'};";
		mWebView.loadUrl("javascript:" + js);
		// String ad = "change_Size("
		// + textSize
		// +
		// ");function change_Size(format,forColor,forLine,paraLine){forColor=forColor||'#535353';forLine=forLine||format;paraLine=paraLine||format;var eleDom=document.querySelector('.article-content');var titleDom=document.body.getElementsByTagName('h1')[0];var childArr=new Array();for(var i=0,l=eleDom.children.length;i<l;i++){eleDom.children[i].style.fontSize=format+'px';eleDom.children[i].style.lineHeight=forLine+'px';eleDom.children[i].style.color=forColor;eleDom.children[i].style.marginTop=paraLine+'px';childArr.push(eleDom.children[i]);checkArr()}function checkArr(){childArr.forEach(function(value){if(childArr.length){for(var i=0,l=value.children.length;i<l;i++){value.children[i].style.fontSize=format+'px';value.children[i].style.lineHeight=forLine+'px';value.children[i].style.color=forColor;value.children[i].style.marginTop=paraLine+'px';childArr.length=0;childArr.push(value.children[i]);checkArr()}}})}titleDom.style.fontSize=format+'px';titleDom.style.lineHeight=forLine+'px';titleDom.style.color=forColor;titleDom.style.marginTop=paraLine+'px'};";

	}

	/**
	 * 设置字体大小
	 * 
	 * */
	public void setTextsize() {
		switch (PreferenceUtil.readInt(mContext, "fontsize")) {
		case FontDialog.LARGEST:
			break;
		case FontDialog.LARGER:
			setTextSize_js(110);
			break;
		case FontDialog.NORMAL:
			setTextSize_js(100);
			break;
		case FontDialog.SMALLER:
			setTextSize_js(98);
			break;
		case FontDialog.SMALLEST:
			break;
		default:
			break;
		}
	}

	/**
	 * 设置模式
	 * 
	 * */
	public void setMode(boolean isNight) {
		if (isNight) {
			if (mWebView != null) {
				mWebView.loadUrl("javascript:" + js_night);
				rl_back.setBackgroundColor(Color.BLACK);
			}
		} else {
			if (mWebView != null) {
				mWebView.loadUrl("javascript:" + js_day);
				rl_back.setBackgroundColor(Color.WHITE);
			}
		}
	}

	public void webScrollToLast() {
		mWebView.scrollTo(0, (int) (mScrollY));
	}

	/**
	 * webview 回退
	 * 
	 * */
	public boolean goBack() {
		if (mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		}
		return false;
	}
}
