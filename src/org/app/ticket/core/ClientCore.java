package org.app.ticket.core;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.app.ticket.bean.LoginDomain;
import org.app.ticket.bean.OrderRequest;
import org.app.ticket.bean.TrainQueryInfo;
import org.app.ticket.bean.UserInfo;
import org.app.ticket.constants.Constants;
import org.app.ticket.util.ImageFilter;
import org.app.ticket.util.StringUtil;
import org.app.ticket.util.TicketUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jhlabs.image.DiffuseFilter;

public class ClientCore {
	private static final Logger logger = LoggerFactory
			.getLogger(ClientCore.class);

	private static X509TrustManager tm = new X509TrustManager() {
		public void checkClientTrusted(X509Certificate[] xcs, String string)
				throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] xcs, String string)
				throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	};

	public static HttpClient getHttpClient() throws KeyManagementException,
			NoSuchAlgorithmException {
		SSLContext sslcontext = SSLContext.getInstance("TLS");
		sslcontext.init(null, new TrustManager[] { tm }, null);
		SSLSocketFactory ssf = new SSLSocketFactory(sslcontext);
		ClientConnectionManager ccm = new DefaultHttpClient()
				.getConnectionManager();
		SchemeRegistry sr = ccm.getSchemeRegistry();
		sr.register(new Scheme("https", 443, ssf));
		HttpParams params = new BasicHttpParams();
		params.setParameter("http.connection.timeout", Integer.valueOf(8000));
		params.setParameter("http.socket.timeout", Integer.valueOf(8000));
		
		
		
		HttpClient httpclient = new DefaultHttpClient(ccm);
	
		httpclient
				.getParams()
				.setParameter("User-Agent",
						"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; BOIE9;ZHCN)");
		return httpclient;
	}

	public static HttpPost getHttpPost(String url) {
		HttpPost post = new HttpPost(url);
		if ((!StringUtil.isEmptyString(Constants.JSESSIONID_VALUE))
				&& (!StringUtil
						.isEmptyString(Constants.BIGIPSERVEROTSWEB_VALUE))) {
			post.addHeader("Cookie", "JSESSIONID=" + Constants.JSESSIONID_VALUE
					+ ";BIGipServerotsweb=" + Constants.BIGIPSERVEROTSWEB_VALUE
					+ ";");
		}
		return post;
	}

	public static HttpGet getHttpGet(String url) {
		HttpGet get = new HttpGet(url);
		if ((!StringUtil.isEmptyString(Constants.JSESSIONID_VALUE))
				&& (!StringUtil
						.isEmptyString(Constants.BIGIPSERVEROTSWEB_VALUE))) {
			get.addHeader("Cookie", "JSESSIONID=" + Constants.JSESSIONID_VALUE
					+ ";BIGipServerotsweb=" + Constants.BIGIPSERVEROTSWEB_VALUE
					+ ";");
		}
		return get;
	}

	public static void getCookie() throws KeyManagementException,
			NoSuchAlgorithmException {
		logger.debug("-------------------getCookie start-------------------");
		HttpClient httpclient = getHttpClient();
		HttpGet get = getHttpGet("http://dynamic.12306.cn/otsweb/main.jsp");
		try {
			HttpResponse response = httpclient.execute(get);
			String responseBody = readInputStream(response.getEntity()
					.getContent());

			Header[] headers = response.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				if (headers[i].getName().equals("Set-Cookie")) {
					String cookie = headers[i].getValue();
					String cookieName = cookie.split("=")[0];
					String cookieValue = cookie.split("=")[1].split(";")[0];
					if (cookieName.equals("JSESSIONID")) {
						Constants.JSESSIONID_VALUE = cookieValue;
					}
					if (cookieName.equals("BIGipServerotsweb")) {
						Constants.BIGIPSERVEROTSWEB_VALUE = cookieValue;
					}
				}
			}
			logger.debug("jessionid = " + Constants.JSESSIONID_VALUE
					+ ";bigipserverotsweb = "
					+ Constants.BIGIPSERVEROTSWEB_VALUE);
		} catch (Exception e) {
			logger.warn(e.getMessage());
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		logger.debug("-------------------getCookie end-------------------");
	}

	public static String loginAysnSuggest() throws KeyManagementException,
			NoSuchAlgorithmException {
		logger.debug("-------------------loginAysnSuggest start-------------------");

		HttpClient httpclient = getHttpClient();
		HttpPost post = getHttpPost("http://dynamic.12306.cn/otsweb/loginAction.do?method=loginAysnSuggest");

		ResponseHandler responseHandler = new BasicResponseHandler();
		String responseBody = null;
		try {
			responseBody = (String) httpclient.execute(post, responseHandler);
			logger.debug(responseBody);
		} catch (Exception e) {
			logger.warn(e.getMessage());
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		logger.debug("-------------------loginAysnSuggest end-------------------");
		return responseBody;
	}

	public static String Login(LoginDomain loginDomain)
			throws KeyManagementException, NoSuchAlgorithmException {
		logger.debug("-------------------Login start-------------------");

		HttpClient httpclient = getHttpClient();
		HttpPost post = getHttpPost("http://dynamic.12306.cn/otsweb/loginAction.do?method=login");

		List parameters = new ArrayList();
		parameters.add(new BasicNameValuePair("loginRand", loginDomain
				.getLoginRand()));
		parameters.add(new BasicNameValuePair("loginUser.user_name",
				loginDomain.getUser_name()));
		parameters.add(new BasicNameValuePair("nameErrorFocus", loginDomain
				.getNameErrorFocus()));
		parameters.add(new BasicNameValuePair("passwordErrorFocus", loginDomain
				.getPasswordErrorFocus()));
		parameters.add(new BasicNameValuePair("randCode", loginDomain
				.getRandCode()));
		parameters.add(new BasicNameValuePair("randErrorFocus", loginDomain
				.getLoginRand()));
		parameters.add(new BasicNameValuePair("refundFlag", loginDomain
				.getRefundFlag()));
		parameters.add(new BasicNameValuePair("refundLogin", loginDomain
				.getRefundLogin()));
		parameters.add(new BasicNameValuePair("user.password", loginDomain
				.getPassword()));

		String responseBody = null;
		try {
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters,
					"UTF-8");
			post.setEntity(uef);
			logger.debug("http://dynamic.12306.cn/otsweb/loginAction.do?method=login&"
					+ URLEncodedUtils.format(parameters, "UTF-8"));
			HttpResponse response = httpclient.execute(post);
			HttpEntity entity = response.getEntity();
			responseBody = readInputStream(entity.getContent());
			logger.debug(responseBody);
		} catch (Exception e) {
			logger.warn(e.getMessage());
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		logger.debug("-------------------Login end-------------------");
		return responseBody;
	}

	public static List<TrainQueryInfo> queryTrain(OrderRequest orderRequest)
			throws KeyManagementException, NoSuchAlgorithmException {
		logger.debug("-------------------query train start-------------------");

		HttpClient httpclient = getHttpClient();
		if (StringUtil.isEmptyString(orderRequest.getStart_time_str())) {
			orderRequest.setStart_time_str("00:00--24:00");
		}
		List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
		parameters.add(new BasicNameValuePair("orderRequest.train_date",
				orderRequest.getTrain_date()));
		
		parameters.add(new BasicNameValuePair(
				"orderRequest.from_station_telecode", TicketUtil
						.getCityCode(orderRequest.getFrom())));

		
		parameters.add(new BasicNameValuePair(
				"orderRequest.to_station_telecode", TicketUtil
						.getCityCode(orderRequest.getTo())));
		
		parameters.add(new BasicNameValuePair("orderRequest.train_no",
				orderRequest.getTrain_no()));
		
		
		parameters.add(new BasicNameValuePair("trainPassType", orderRequest
				.getTrainPassType()));
		
		parameters.add(new BasicNameValuePair("trainClass", orderRequest
				.getTrainClass()));

		
		parameters.add(new BasicNameValuePair("includeStudent", orderRequest
				.getIncludeStudent()));
		
		
		
		parameters.add(new BasicNameValuePair("seatTypeAndNum", orderRequest
				.getSeatTypeAndNum()));

		
		parameters.add(new BasicNameValuePair("orderRequest.start_time_str",
				orderRequest.getStart_time_str()));


		logger.info("https://dynamic.12306.cn/otsweb/order/querySingleAction.do?method=queryLeftTicket&"
				+ URLEncodedUtils.format(parameters, "UTF-8"));
		HttpGet get = getHttpGet("https://dynamic.12306.cn/otsweb/order/querySingleAction.do?method=queryLeftTicket&"
				+ URLEncodedUtils.format(parameters, "UTF-8"));
		ResponseHandler responseHandler = new BasicResponseHandler();

		String responseBody = null;
		List all = new ArrayList();
		try {
			responseBody = (String) httpclient.execute(get, responseHandler);
			all = TicketUtil.parserQueryInfo(responseBody);
		} catch (Exception e) {
			logger.warn(e.getMessage());
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		logger.debug("-------------------query train end-------------------");
		return all;
	}

	public static void submitOrderRequest(TrainQueryInfo trainQueryInfo,
			OrderRequest orderRequest) throws KeyManagementException,
			NoSuchAlgorithmException {
		logger.debug("-------------------submitOrderRequest start-------------------");

		HttpClient httpclient = getHttpClient();
		HttpPost post = getHttpPost("http://dynamic.12306.cn/otsweb/order/querySingleAction.do?method=submutOrderRequest");

		List<NameValuePair> parameters = new ArrayList();
		parameters.add(new BasicNameValuePair("arrive_time", trainQueryInfo
				.getEndTime()));
		parameters.add(new BasicNameValuePair("from_station_name",
				trainQueryInfo.getFromStation()));
		parameters.add(new BasicNameValuePair("from_station_no", trainQueryInfo
				.getFormStationNo()));
		parameters.add(new BasicNameValuePair("from_station_telecode",
				trainQueryInfo.getFromStationCode()));
		parameters.add(new BasicNameValuePair("from_station_telecode_name",
				trainQueryInfo.getFromStationName()));
		parameters.add(new BasicNameValuePair("include_student", orderRequest
				.getIncludeStudent()));
		parameters.add(new BasicNameValuePair("lishi", trainQueryInfo
				.getTakeTime()));
		parameters.add(new BasicNameValuePair("locationCode", trainQueryInfo
				.getLocationCode()));
		parameters.add(new BasicNameValuePair("mmStr", trainQueryInfo
				.getMmStr()));
		parameters.add(new BasicNameValuePair("round_start_time_str",
				orderRequest.getStart_time_str()));
		parameters.add(new BasicNameValuePair("round_train_date", orderRequest
				.getTrain_date()));
		parameters.add(new BasicNameValuePair("seattype_num", orderRequest
				.getSeatTypeAndNum()));
		parameters.add(new BasicNameValuePair("single_round_type",
				trainQueryInfo.getSingle_round_type()));
		parameters.add(new BasicNameValuePair("start_time_str", orderRequest
				.getStart_time_str()));
		parameters.add(new BasicNameValuePair("station_train_code",
				trainQueryInfo.getTrainNo()));
		parameters.add(new BasicNameValuePair("to_station_name", trainQueryInfo
				.getToStation()));
		parameters.add(new BasicNameValuePair("to_station_no", trainQueryInfo
				.getToStationNo()));
		parameters.add(new BasicNameValuePair("to_station_telecode",
				trainQueryInfo.getToStationCode()));
		parameters.add(new BasicNameValuePair("to_station_telecode_name",
				trainQueryInfo.getToStationName()));
		parameters.add(new BasicNameValuePair("train_class_arr", orderRequest
				.getTrainClass()));
		parameters.add(new BasicNameValuePair("train_date", orderRequest
				.getTrain_date()));
		parameters.add(new BasicNameValuePair("train_pass_type", orderRequest
				.getTrainPassType()));
		parameters.add(new BasicNameValuePair("train_start_time",
				trainQueryInfo.getStartTime()));
		parameters.add(new BasicNameValuePair("trainno4", trainQueryInfo
				.getTrainno4()));
		parameters.add(new BasicNameValuePair("ypInfoDetail", trainQueryInfo
				.getYpInfoDetail()));
		try {
			String p = "";
			for (NameValuePair n : parameters) {
				p = p + n.getName() + " => " + n.getValue() + " ";
			}
			logger.debug("submitOrderRequest params : " + p);
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters,
					"UTF-8");
			logger.debug("http://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=confirmSingleForQueueOrder&"
					+ URLEncodedUtils.format(parameters, "UTF-8"));
			post.setEntity(uef);
			HttpResponse response = httpclient.execute(post);
			HttpEntity entity = response.getEntity();
			String responseBody = readInputStream(entity.getContent());
			logger.debug(responseBody);
			int statusCode = response.getStatusLine().getStatusCode();
			logger.debug("statusCode = " + statusCode);

			if ((statusCode == 301) || (statusCode == 302)) {
				Header locationHeader = response.getFirstHeader("location");
				String redirectUrl = locationHeader.getValue();
				post = getHttpPost(redirectUrl);
				response = httpclient.execute(post);
				entity = response.getEntity();
				responseBody = readInputStream(entity.getContent());
				TicketUtil.getCredential(responseBody);
				TicketUtil.getToken(responseBody);
				logger.debug(responseBody);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		logger.debug("-------------------submitOrderRequest end-------------------");
	}

	public static void confirmPassenger() throws KeyManagementException,
			NoSuchAlgorithmException {
		logger.debug("-------------------confirmPassenger start-------------------");

		HttpClient httpclient = getHttpClient();
		HttpGet get = getHttpGet("http://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=init");
		ResponseHandler responseHandler = new BasicResponseHandler();
		try {
			String responseBody = (String) httpclient.execute(get,
					responseHandler);
			logger.info("Response is " + responseBody);
			TicketUtil.getCredential(responseBody);
		} catch (Exception e) {
			logger.warn(e.getMessage());
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		logger.debug("-------------------confirmPassenger end-------------------");
	}

	
	public static void getPassCode(String url, String path)
			throws KeyManagementException, NoSuchAlgorithmException {
		logger.debug("-------------------getPassCode start-------------------");

		HttpClient httpclient = getHttpClient();
	
		HttpGet get =getHttpGet(url);
		try {			
			
			
			HttpResponse response = httpclient.execute(get);
		
			logger.debug("返回的数据类型:"+response.getEntity().getContentLength());
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				OutputStream out = new FileOutputStream(new File(path));
				
				int byteread = 0;
				byte[] tmp = new byte[10];
				while ((byteread = instream.read(tmp)) != -1)
					out.write(tmp);
				
				processImage(path);
				
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		logger.debug("-------------------getPassCode end-------------------");
	}
	
	
	
	public static void processImage(String path){
		BufferedImage bufferedImage;
		try {
			bufferedImage = ImageIO.read(new File(path));
			ImageFilter img = new ImageFilter(bufferedImage);
			
			bufferedImage = img.changeGrey();
			
			bufferedImage = img.grayFilter();

			bufferedImage = img.getBrighten();
			
			ImageIO.write(bufferedImage, "jpg", new File(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}
	
//	public static void getPassCode(String url, String path)
//			throws KeyManagementException, NoSuchAlgorithmException {
//		logger.debug("-------------------getPassCode start-------------------");
//
//		HttpClient httpclient = getHttpClient();
//		HttpGet get = getHttpGet(url);
//		try {
//			HttpResponse response = httpclient.execute(get);
//			HttpEntity entity = response.getEntity();
//			if (entity != null) {
//				InputStream instream = entity.getContent();
//				OutputStream out = new FileOutputStream(new File(path));
//				
//				int byteread = 0;
//				byte[] tmp = new byte[10];
//				while ((byteread = instream.read(tmp)) != -1)
//					out.write(tmp);
//				
////				  JPEGImageDecoder decoder;
////					try {
////						decoder = JPEGCodec.createJPEGDecoder(new FileInputStream(imageFile));
////						BufferedImage image1 = decoder.decodeAsBufferedImage();
////						ImageIO.write(image1, "jpg", imageFile);  
////					} catch (FileNotFoundException e1) {
////						// TODO Auto-generated catch block
////						e1.printStackTrace();
////					} catch (ImageFormatException e) {
////						// TODO Auto-generated catch block
////						e.printStackTrace();
////					} catch (IOException e) {
////						// TODO Auto-generated catch block
////						e.printStackTrace();
////					}
//
//				
//				
//			}
//		} catch (Exception e) {
//			logger.warn(e.getMessage());
//			e.printStackTrace();
//		} finally {
//			httpclient.getConnectionManager().shutdown();
//		}
//		logger.debug("-------------------getPassCode end-------------------");
//	}

	public static void getQueueCount(String url, OrderRequest req,
			List<UserInfo> userInfos, TrainQueryInfo trainQueryInfo)
			throws KeyManagementException, NoSuchAlgorithmException {
		logger.debug("-------------------getQueueCount start-------------------");

		HttpClient httpclient = getHttpClient();
		String responseBody = null;
		List parameters = new ArrayList();
		parameters.add(new BasicNameValuePair("from", TicketUtil
				.getCityCode(req.getFrom())));
		parameters.add(new BasicNameValuePair("seat", ((UserInfo) userInfos
				.get(0)).getSeatType()));
		parameters.add(new BasicNameValuePair("station", trainQueryInfo
				.getTrainNo()));
		parameters
				.add(new BasicNameValuePair("ticket", Constants.LEFTTICKETSTR));
		parameters.add(new BasicNameValuePair("to", TicketUtil.getCityCode(req
				.getTo())));
		parameters
				.add(new BasicNameValuePair("train_date", req.getTrain_date()));
		logger.info("http://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=getQueueCount&"
				+ URLEncodedUtils.format(parameters, "UTF-8"));
		HttpGet get = getHttpGet("http://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=getQueueCount&"
				+ URLEncodedUtils.format(parameters, "UTF-8"));
		ResponseHandler responseHandler = new BasicResponseHandler();
		try {
			responseBody = (String) httpclient.execute(get, responseHandler);
			logger.debug(responseBody);
		} catch (Exception e) {
			logger.warn(e.getMessage());
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		logger.debug("-------------------getQueueCount end-------------------");
	}

	public static String confirmSingleForQueueOrder(
			TrainQueryInfo trainQueryInfo, OrderRequest orderRequest,
			List<UserInfo> userInfoList, String randCode, String url)
			throws KeyManagementException, NoSuchAlgorithmException {
		logger.debug("-------------------confirmSingleForQueueOrder start-------------------");

		HttpClient httpclient = getHttpClient();

		boolean checkRand = checkRand(url);
		if (checkRand) {
			url = url + randCode;
		}

		logger.debug("url = " + url);

		HttpPost post = getHttpPost(url);

		ResponseHandler responseHandler = new BasicResponseHandler();

		List<NameValuePair> parameters = new ArrayList();

		parameters.add(new BasicNameValuePair("checkbox9", "Y"));
		parameters.add(new BasicNameValuePair("checkbox9", "Y"));
		parameters.add(new BasicNameValuePair("checkbox9", "Y"));
		parameters.add(new BasicNameValuePair("checkbox9", "Y"));
		parameters.add(new BasicNameValuePair("checkbox9", "Y"));
		for (int i = 0; i < 5 - userInfoList.size(); i++) {
			parameters.add(new BasicNameValuePair("oldPassengers", ""));
		}
		parameters.add(new BasicNameValuePair("leftTicketStr",
				Constants.LEFTTICKETSTR));
		parameters.add(new BasicNameValuePair(
				"orderRequest.bed_level_order_num",
				"000000000000000000000000000000"));
		parameters.add(new BasicNameValuePair("orderRequest.cancel_flag", "1"));
		parameters.add(new BasicNameValuePair("orderRequest.end_time",
				trainQueryInfo.getEndTime()));
		parameters.add(new BasicNameValuePair("orderRequest.from_station_name",
				trainQueryInfo.getFromStation()));
		parameters.add(new BasicNameValuePair(
				"orderRequest.from_station_telecode", trainQueryInfo
						.getFromStationCode()));
		parameters.add(new BasicNameValuePair("orderRequest.id_mode", "Y"));
		parameters
				.add(new BasicNameValuePair("orderRequest.reserve_flag", "A"));
		parameters.add(new BasicNameValuePair(
				"orderRequest.seat_detail_type_code", ""));
		parameters.add(new BasicNameValuePair("orderRequest.start_time",
				trainQueryInfo.getStartTime()));
		parameters
				.add(new BasicNameValuePair("orderRequest.station_train_code",
						trainQueryInfo.getTrainNo()));
		parameters.add(new BasicNameValuePair(
				"orderRequest.ticket_type_order_num", ""));
		parameters.add(new BasicNameValuePair("orderRequest.to_station_name",
				trainQueryInfo.getToStation()));
		parameters.add(new BasicNameValuePair(
				"orderRequest.to_station_telecode", trainQueryInfo
						.getToStationCode()));
		parameters
				.add(new BasicNameValuePair("orderRequest.seat_type_code", ""));
		parameters.add(new BasicNameValuePair("orderRequest.train_date",
				orderRequest.getTrain_date()));
		parameters.add(new BasicNameValuePair("orderRequest.train_no",
				trainQueryInfo.getTrainno4()));
		parameters.add(new BasicNameValuePair(
				"org.apache.struts.taglib.html.TOKEN", Constants.TOKEN));
		String responseBody = "";
		for (int i = 0; i < userInfoList.size(); i++) {
			parameters.add(new BasicNameValuePair("checkbox" + i, Integer
					.toString(i)));

			parameters.add(new BasicNameValuePair("oldPassengers",
					((UserInfo) userInfoList.get(i)).getSimpleText()));

			parameters.add(new BasicNameValuePair("passenger_" + (i + 1)
					+ "_cardno", ((UserInfo) userInfoList.get(i)).getCardID()));
			parameters.add(new BasicNameValuePair("passenger_" + (i + 1)
					+ "_cardtype", ((UserInfo) userInfoList.get(i))
					.getCardType()));
			parameters
					.add(new BasicNameValuePair("passenger_" + (i + 1)
							+ "_mobileno", ((UserInfo) userInfoList.get(i))
							.getPhone()));
			parameters.add(new BasicNameValuePair("passenger_" + (i + 1)
					+ "_name", ((UserInfo) userInfoList.get(i)).getName()));
			parameters.add(new BasicNameValuePair("passenger_" + (i + 1)
					+ "_seat", ((UserInfo) userInfoList.get(i)).getSeatType()));
			parameters.add(new BasicNameValuePair("passenger_" + (i + 1)
					+ "_seat_detail", "0"));
			parameters
					.add(new BasicNameValuePair("passenger_" + (i + 1)
							+ "_ticket", ((UserInfo) userInfoList.get(i))
							.getTickType()));
			parameters.add(new BasicNameValuePair("passengerTickets",
					((UserInfo) userInfoList.get(i)).getText()));
		}
		parameters.add(new BasicNameValuePair("randCode", randCode));
		parameters.add(new BasicNameValuePair("textfield", "中文或拼音首字母"));

		if (checkRand)
			parameters.add(new BasicNameValuePair("dc", "dc"));
		try {
			String p = "";
			for (NameValuePair n : parameters) {
				p = p + n.getName() + " => " + n.getValue() + " ";
			}
			logger.debug("confirmSingleForQueueOrder params : " + p);
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters,
					"UTF-8");
			logger.debug("http://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=confirmSingleForQueueOrder&"
					+ URLEncodedUtils.format(parameters, "UTF-8"));
			post.setEntity(uef);
			responseBody = (String) httpclient.execute(post, responseHandler);
			logger.info("Response is " + responseBody);
		} catch (Exception e) {
			logger.warn(e.getMessage());
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}

		logger.debug("-------------------confirmSingleForQueueOrder end-------------------");
		return responseBody;
	}

	private static String readInputStream(InputStream is) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(is,
				"UTF-8"));
		StringBuffer buffer = new StringBuffer();
		String line;
		while ((line = in.readLine()) != null)
			buffer.append(line + "\n");
		is.close();
		return buffer.toString();
	}

	private static boolean checkRand(String url) {
		return url.contains("rand");
	}
}