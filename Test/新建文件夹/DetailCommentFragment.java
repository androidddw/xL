package news.onon.yaowen.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import news.onon.yaowen.AdvertisementActivity;
import news.onon.yaowen.DetailActivity;
import news.onon.yaowen.R;
import news.onon.yaowen.adapter.NewsCommentAdapter;
import news.onon.yaowen.adapter.NewsCommentAdapter.OnDelListener;
import news.onon.yaowen.bean.Advertis;
import news.onon.yaowen.bean.CommentBean;
import news.onon.yaowen.bean.CommentBeanData.CommentBeanDataNews;
import news.onon.yaowen.bean.NewsBean;
import news.onon.yaowen.bean.NewsDetailsBean;
import news.onon.yaowen.db.DbService;
import news.onon.yaowen.tool.ActionAnalysisQeq;
import news.onon.yaowen.tool.DataTools;
import news.onon.yaowen.tool.LogUtil;
import news.onon.yaowen.tool.Options;
import news.onon.yaowen.tool.PreferenceUtil;
import news.onon.yaowen.tool.RequestTool;
import news.onon.yaowen.tool.UrlProviderTool;
import news.onon.yaowen.view.CustListView;
import news.onon.yaowen.view.CustListView.MyPullUpListViewCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DetailCommentFragment extends Fragment {

	private View rootView;
	private String newsId;
	private RequestQueue httpRequestQueue;
	private CustListView listview;
	private boolean hasGetAboutnews = false;
	private LayoutInflater mInflater;
	private DbService dbService;
	private NewsCommentAdapter newsCommentAdapter;
	private Context mContext;
	private LinearLayout baseView;
	private TextView tv_no_comment_note;

	public DetailCommentFragment(String newsId, RequestQueue httpRequestQueue) {
		this.newsId = newsId;
		this.httpRequestQueue = httpRequestQueue;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		this.mInflater = inflater;
		rootView = inflater.inflate(R.layout.detailcommentfragment, null);
		initData();
		initView();
		return rootView;
	}

	private void initData() {
		newsCommentAdapter = new NewsCommentAdapter(mContext, newsId);
	}

	/**
	 * 初始化ListView
	 * 
	 * @param rootView
	 *            根View
	 */
	public void initView() {
		listview = (CustListView) rootView
				.findViewById(R.id.fragment2_listview);
		rootView.setBackgroundColor(PreferenceUtil.readBoolean(mContext,
				"night") ? Color.BLACK : Color.WHITE);
		setView();
		setListener();
	}

	private void setListener() {
		// 上拉回调设置
		listview.setMyPullUpListViewCallBack(new MyPullUpListViewCallBack() {

			@Override
			public void scrollBottomState() {
				ArrayList<CommentBeanDataNews> data = newsCommentAdapter
						.getData();
				String maxTime = data.get(data.size() - 1).getCreateTimestamp();
				if (maxTime != null) {
					getDataFromNet(0, Long.parseLong(maxTime));
				} else {
					getDataFromNet(0, 0);
				}
			}
		});
		newsCommentAdapter.setOnDelListener(new OnDelListener() {
			@Override
			public void onDelListener(int position) {
				delComment(position);
			}
		});

	}

	private void setView() {
		listview.setAdapter(newsCommentAdapter);
	}

	/**
	 * 从网络加载数据
	 * 
	 * @param minTime
	 * @param maxTime
	 * 
	 * */
	public void getDataFromNet(long minTime, final long maxTime) {
		if (!hasGetAboutnews) {
			getAboutNews();
			hasGetAboutnews = true;
		}
		HashMap<String, String> params = UrlProviderTool
				.getNetRequestBaseParams_5(mContext);
		params.put("token", PreferenceUtil.readString(mContext, "tbMemberId"));
		params.put("count", 10 + "");
		if (minTime != 0) {
			params.put("minTime", minTime + "");
		}
		if (maxTime != 0) {
			params.put("maxTime", maxTime + "");
		}
		String url = UrlProviderTool.GET_NEWS_COMMNET + newsId + "/comments";
		String url2 = UrlProviderTool.getUrl(url, params);
		StringRequest creatStringRequest = RequestTool.creatStringRequest(
				Request.Method.GET, url2, null, new Listener<String>() {

					@Override
					public void onResponse(String arg0) {
						LogUtil.log(arg0);
						if (listview != null) {
							listview.onPullUpComplet();
						}
						Gson gson = new Gson();
						CommentBean commentBean = gson.fromJson(arg0,
								CommentBean.class);
						if (commentBean.getCode() == 0) {
							ArrayList<CommentBeanDataNews> commentList = commentBean
									.getData().getList();
							if (commentList != null && commentList.size() > 0) {
								if (maxTime != 0) {
									newsCommentAdapter.addToFoot(commentList);
								} else {
									newsCommentAdapter.addToHead(commentList);
								}
							} else if (newsCommentAdapter.getData().size() == 0) {
							}
							if (null != newsCommentAdapter.getData()
									&& newsCommentAdapter.getData().size() == 0) {
								tv_no_comment_note = new TextView(mContext);
								tv_no_comment_note
										.setText(R.string.hn_news_clink_sofa);
								tv_no_comment_note.setTextSize(
										TypedValue.COMPLEX_UNIT_SP, 16);
								tv_no_comment_note.setGravity(Gravity.CENTER);
								tv_no_comment_note
										.setOnClickListener(new OnClickListener() {

											@Override
											public void onClick(View v) {
												((DetailActivity) mContext)
														.showCommentDialog();
											}
										});

								listview.addFooterView(tv_no_comment_note);
							}
						}

					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
					}
				});
		httpRequestQueue.add(creatStringRequest);

	}

	/**
	 * 相关阅读
	 * 
	 * */
	public void getAboutNews() {
		// 设置请求参数
		HashMap<String, String> about_news_RequestBaseParams = UrlProviderTool
				.getNetRequestBaseParams(mContext);
		// 请求成功的监听
		Listener<String> listener = new Listener<String>() {

			@Override
			public void onResponse(String arg0) {
				LogUtil.log(arg0);
				try {
					org.json.JSONObject jsonObject = new org.json.JSONObject(
							arg0);
					switch (jsonObject.getInt("code")) {
					case 0:
						add_about_news(jsonObject.getString("data"));
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
				Log.d("code", arg0.toString());
			}

		};
		StringRequest about_news_Request = RequestTool.creatStringRequest(
				Request.Method.GET,
				UrlProviderTool.getUrl(UrlProviderTool.GETNEWSINFO + newsId
						+ "/related", about_news_RequestBaseParams), null,
				listener, errorListener);
		httpRequestQueue.add(about_news_Request);

	}

	/**
	 * 添加‘相关阅读’
	 * 
	 * */
	private void add_about_news(String data_json) throws JSONException {
		org.json.JSONObject jsonObject1 = new org.json.JSONObject(data_json);
		String advert = jsonObject1.getString("advert");
		String string = jsonObject1.getString("articlelist");
		LogUtil.log(advert);
		if (string != null && string.startsWith("{")) {
			return;
		}
		List<NewsDetailsBean> parseArray = JSONArray.parseArray(string,
				NewsDetailsBean.class);
		View inflate = mInflater.inflate(R.layout.comment_header, null);
		baseView = (LinearLayout) inflate.findViewById(R.id.ll_about_news);
		ImageView imageView = (ImageView) inflate
				.findViewById(R.id.iv_advertise);
		TextView tv_description = (TextView) inflate
				.findViewById(R.id.tv_description);
		if (!TextUtils.isEmpty(advert)) {
			final Advertis advertis = com.alibaba.fastjson.JSONObject
					.parseObject(advert, Advertis.class);
			if (null != advertis.getImgUrl()) {
				ImageLoader.getInstance().displayImage(advertis.getImgUrl(),
						imageView, Options.getListOptions());
				tv_description.setText(advertis.getContent());
				imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						startActivity(new Intent(mContext,
								AdvertisementActivity.class).putExtra("ad_url",
								advertis.getLinkUrl()));
					}
				});
			} else {
				imageView.setVisibility(View.GONE);
				tv_description.setVisibility(View.GONE);
			}
		} else {
			imageView.setVisibility(View.GONE);
			tv_description.setVisibility(View.GONE);
		}

		if (PreferenceUtil.readBoolean(mContext, "night")) {
			baseView.setBackgroundResource(R.drawable.inserttable5);
		} else {
			baseView.setBackgroundResource(R.drawable.inserttable6);
		}
		TextView textView_about = new TextView(mContext);
		setAboutNewsTitle(textView_about);
		textView_about.setTextColor(0xff535353);
		textView_about.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		textView_about.setPadding(DataTools.dip2px(mContext, 10),
				DataTools.dip2px(mContext, 5), DataTools.dip2px(mContext, 5),
				DataTools.dip2px(mContext, 5));
		ArrayList<NewsBean> arrayList = new ArrayList<NewsBean>();
		baseView.addView(textView_about);

		for (NewsDetailsBean newsDetailsBean : parseArray) {
			NewsBean newsBean = new NewsBean();
			newsBean.setId(newsDetailsBean.getNewsId());
			newsBean.setTitle(newsDetailsBean.getTitle());
			newsBean.setSrc_link(newsDetailsBean.getArticle_alt_url());
			newsBean.setCreateTime(newsDetailsBean.getCreateTime());
			newsBean.setSource(newsDetailsBean.getSource());
			newsBean.setCommentCount(newsDetailsBean.getCommentCount());
			arrayList.add(newsBean);
		}
		for (int i = 0; i < arrayList.size(); i++) {
			final NewsBean newsBean = arrayList.get(i);
			TextView textView = new TextView(mContext);
			textView.setText("• " + newsBean.getTitle());
			textView.setTextColor(0xff535353);
			textView.setPadding(DataTools.dip2px(mContext, 10),
					DataTools.dip2px(mContext, 10),
					DataTools.dip2px(mContext, 10),
					DataTools.dip2px(mContext, 10));
			textView.setSingleLine();
			textView.setEllipsize(TruncateAt.MIDDLE);
			textView.setGravity(Gravity.CENTER_VERTICAL);
			baseView.addView(textView);
			if (i != arrayList.size() - 1) {
				View view = new View(mContext);
				LayoutParams params = new LayoutParams(
						android.view.ViewGroup.LayoutParams.MATCH_PARENT, 2);
				params.setMargins(DataTools.dip2px(mContext, 6), 0,
						DataTools.dip2px(mContext, 6), 0);
				view.setLayoutParams(params);
				view.setBackgroundColor(PreferenceUtil.readBoolean(mContext,
						"night") ? 0xff393939 : 0xffc9c9c9);
				view.setTag("DEVIDER");
				baseView.addView(view);

			}
			textView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent in = new Intent(mContext, DetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("news", newsBean);
					in.putExtras(bundle);
					startActivity(in);
					HashMap<String, String> hashMap = new HashMap<String, String>();
					hashMap.put("sid", newsId);
					hashMap.put("tid", newsBean.getId());
					ActionAnalysisQeq.actionAnalysis(mContext,
							ActionAnalysisQeq.ACTION_ABOUT,
							ActionAnalysisQeq.CONTENT_NEWS, hashMap);
				}
			});
		}
		if (null != arrayList && arrayList.size() != 0) {
			baseView.setVisibility(View.VISIBLE);
		}
		listview.addHeaderView(inflate);
		if (newsCommentAdapter != null) {
			newsCommentAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * “相关新闻” 国际化
	 * 
	 * */
	private void setAboutNewsTitle(TextView textView_about) {
		textView_about.setText(R.string.hn_about_news);
		String lang = PreferenceUtil.readString(mContext, "lang");
		if (TextUtils.equals(lang, "en_US")) {
			textView_about.setText("Related News");
		} else if (TextUtils.equals(lang, "zh_CN")) {
			textView_about.setText(R.string.hn_about_news);
		} else if (TextUtils.equals(lang, "ja_JP")) {
			textView_about.setText("関連ニュース");
		} else if (TextUtils.equals(lang, "de_DE")) {
			textView_about.setText("Weitere Nachrichten");
		} else if (TextUtils.equals(lang, "fr_FR")) {
			textView_about.setText("Actualités associées");
		} else if (TextUtils.equals(lang, "pt_PT")) {
			textView_about.setText("Notícias relacionadas");
		} else if (TextUtils.equals(lang, "ru_RU")) {
			textView_about.setText("Подобные новости");
		} else if (TextUtils.equals(lang, "it_IT")) {
			textView_about.setText("Notizie correlate");
		} else if (TextUtils.equals(lang, "hi_IN")) {
			textView_about.setText("संबंधित समाचार");
		} else if (TextUtils.equals(lang, "es_ES")) {
			textView_about.setText("Noticias relacionadas");
		} else if (TextUtils.equals(lang, "ko_KR")) {
			textView_about.setText("상관뉴스");
		} else if (TextUtils.equals(lang, "in_ID")) {
			textView_about.setText("Berita terkait");
		} else if (TextUtils.equals(lang, "ar_SA")) {
			textView_about.setText("أخبار ذات صلة");
		} else if (TextUtils.equals(lang, "zh_TW")) {
			textView_about.setText("相關新聞");
		} else if (TextUtils.equals(lang, "th_TH")) {
			textView_about.setText("ข่าวที่เกี่ยวข้อง");
		} else if (TextUtils.equals(lang, "ms_MY")) {
			textView_about.setText("Berita berkaitan");
		} else if (TextUtils.equals(lang, "vi_VN")) {
			textView_about.setText("Tin liên quan");
		} else if (TextUtils.equals(lang, "sw_SW")) {
			textView_about.setText("Habari Husiani");
		} else if (TextUtils.equals(lang, "nl_NL")) {
			textView_about.setText("Gerelateerd nieuws");
		} else if (TextUtils.equals(lang, "pl_PL")) {
			textView_about.setText("Związane wiadomości");
		} else if (TextUtils.equals(lang, "zh_HK")) {
			textView_about.setText("相關新聞");
		}
	}

	public void addMyComment(CommentBeanDataNews commentBeanDataNews) {
		newsCommentAdapter.addToHead(commentBeanDataNews);
	}

	/**
	 * 删除评论
	 * 
	 * */
	private void delComment(final int position) {
		// 设置请求参数
		HashMap<String, String> delCommentRequestBaseParams = UrlProviderTool
				.getNetRequestBaseParams_5(mContext);
		delCommentRequestBaseParams.put("token",
				PreferenceUtil.readString(mContext, "tbMemberId"));
		// 请求成功的监听
		Listener<String> listener = new Listener<String>() {

			@Override
			public void onResponse(String arg0) {
				LogUtil.log(arg0);
				try {
					org.json.JSONObject jsonObject = new org.json.JSONObject(
							arg0);
					switch (jsonObject.getInt("code")) {
					case 0:
						newsCommentAdapter.delData(position);
						JSONObject jsonObject2 = jsonObject
								.getJSONObject("data").getJSONArray("results")
								.getJSONObject(0);
						int commentCount = jsonObject2.getInt("commentCount");
						String newsId_del = jsonObject2.getString("newsId");
						dbService = new DbService(mContext);
						dbService.changeCollectCommentCount(newsId_del,
								commentCount);
						dbService.changeNewsCommentCount(newsId_del,
								commentCount);
						// 更改评论数量
						DetailActivity activity = (DetailActivity) mContext;
						activity.setCommentDel(commentCount + "");
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
				Log.d("code", arg0.toString());
			}

		};
		StringRequest delCommentRequest = RequestTool.creatStringRequest(
				Request.Method.DELETE, UrlProviderTool.getUrl(
						UrlProviderTool.DEl_MY_COMMENT
								+ newsCommentAdapter.getData().get(position)
										.getId(), delCommentRequestBaseParams),
				null, listener, errorListener);
		httpRequestQueue.add(delCommentRequest);

	}

	public void setMode(boolean checked) {
		rootView.setBackgroundColor(checked ? Color.BLACK : Color.WHITE);
		if (null != baseView) {
			baseView.setBackgroundResource(PreferenceUtil.readBoolean(mContext,
					"night") ? R.drawable.inserttable5
					: R.drawable.inserttable6);
			for (int i = 0; i < baseView.getChildCount(); i++) {
				if (baseView.getChildAt(i).getTag() != null
						&& baseView.getChildAt(i).getTag().equals("DEVIDER")) {
					baseView.getChildAt(i)
							.setBackgroundColor(
									PreferenceUtil.readBoolean(mContext,
											"night") ? 0xff393939 : 0xffc9c9c9);
				}
			}
		}
	}

	public void removeNoCommentNote() {
		listview.removeFooterView(tv_no_comment_note);
	}
}
