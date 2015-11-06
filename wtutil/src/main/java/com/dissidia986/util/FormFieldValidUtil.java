package com.dissidia986.util;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormFieldValidUtil {
	private static final String MOBILE_PATTERN="^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final String USERNAME_PATTERN="^[a-zA-Z0-9_-]{3,15}";
	private static final String PASSWORD_PATTERN="^[a-zA-Z0-9_-]{6,18}";
	private static final String IDCARD_PATTERN="\\d{15}|\\d{18}|\\d{17,17}X";
	private static final String LIMIT_TXT_PATTERN="^.{2,20}$";
	private static final String POSITIVE_NUM_PATTERN="^\\+?[1-9][0-9]*$";
	private static final String SMS_VERIFYCODE="\\d{6}";
	private static final String CHINESE_NAME_PATTERN="[\u4E00-\u9FA5]{2,12}";
	private static final String BANK_CARD_NUM_PATTERN="\\d{16}|\\d{19}";
	

	public static boolean isUsername(String txt){
		Pattern p = Pattern.compile(USERNAME_PATTERN);  
		Matcher m = p.matcher(txt);  
		return m.matches();
	}
	public static boolean isPassword(String txt){
		Pattern p = Pattern.compile(PASSWORD_PATTERN);  
		Matcher m = p.matcher(txt);  
		return m.matches();
	}
	public static boolean isMobileNO(String mobiles){
		Pattern p = Pattern.compile(MOBILE_PATTERN);  
		Matcher m = p.matcher(mobiles);  
		return m.matches();
	}
	
	public static boolean isEmail(String email){
		Pattern p = Pattern.compile(EMAIL_PATTERN);  
		Matcher m = p.matcher(email);  
		return m.matches();
	}
	public static boolean isIDCard(String idnum){
		return checkIdCardInfo(idnum).isTrue;
	}
	public static boolean isLimitTxt(String txt){
		Pattern p = Pattern.compile(LIMIT_TXT_PATTERN);  
		Matcher m = p.matcher(txt);  
		return m.matches();
	}
	public static boolean isPositiveNum(String txt){
		Pattern p = Pattern.compile(POSITIVE_NUM_PATTERN);  
		Matcher m = p.matcher(txt);  
		return m.matches();
	}
	public static boolean isSMSVerifyCode(String txt){
		Pattern p = Pattern.compile(SMS_VERIFYCODE);  
		Matcher m = p.matcher(txt);  
		return m.matches();
	}
	public static boolean isChineseName(String txt){
		Pattern p = Pattern.compile(CHINESE_NAME_PATTERN);  
		Matcher m = p.matcher(txt);  
		return m.matches();
	}
	public static boolean isBankCard(String cardnum){
		Pattern p = Pattern.compile(BANK_CARD_NUM_PATTERN);  
		Matcher m = p.matcher(cardnum);  
		return m.matches();
	}

	public static class Info {
		private boolean isTrue = false;
		private String year = null;
		private String month = null;
		private String day = null;
		private boolean isMale = false;
		private boolean isFemale = false;
		@Override
		public String toString() {
			return "Info [isTrue=" + isTrue + ", year=" + year + ", month=" + month + ", day=" + day + ", isMale="
					+ isMale + ", isFemale=" + isFemale + "]";
		}
		
	}
	public static Info checkIdCardInfo(String cardNo){
		if(!cardNo.isEmpty())
			cardNo = cardNo.toUpperCase();
		Info info = new Info();
		Pattern pt = Pattern.compile(IDCARD_PATTERN);  
		Matcher m = pt.matcher(cardNo);
		if (!m.matches()) {
			info.isTrue = false;
			return info;
		}
		if (15 == cardNo.length()) {
			String year = cardNo.substring(6, 8);
			String month = cardNo.substring(8, 10);
			String day = cardNo.substring(10, 12);
			String p = cardNo.substring(14, 15); //性别位
			Date birthday = new Date();
			birthday.setYear(Integer.valueOf(year));
			birthday.setMonth(Integer.parseInt(month)-1);
			birthday.setDate(Integer.parseInt(day));
			// 对于老身份证中的年龄则不需考虑千年虫问题而使用getYear()方法  
			if (birthday.getYear() != Integer.parseInt(year)
					|| birthday.getMonth() != Integer.parseInt(month) - 1
					|| birthday.getDate() != Integer.parseInt(day)) {
				info.isTrue = false;
			} else {
				info.isTrue = true;
				info.year = birthday.getYear()+"";
				info.month = (birthday.getMonth() + 1)+"";
				info.day = birthday.getDate()+"";
				if (Integer.valueOf(p) % 2 == 0) {
					info.isFemale = true;
					info.isMale = false;
				} else {
					info.isFemale = false;
					info.isMale = true;
				}
			}
			return info;
		}
		
		if (18 == cardNo.length()) {
			String year = cardNo.substring(6, 10);
			String month = cardNo.substring(10, 12);
			String day = cardNo.substring(12, 14);
			String p = cardNo.substring(14, 17);
			Date birthday = new Date();
			birthday.setYear(Integer.valueOf(year));
			birthday.setMonth(Integer.parseInt(month)-1);
			birthday.setDate(Integer.parseInt(day));
			// 这里用getFullYear()获取年份，避免千年虫问题
			if (birthday.getYear() != Integer.parseInt(year)
					|| birthday.getMonth() != Integer.parseInt(month) - 1
					|| birthday.getDate() != Integer.parseInt(day)) {
				info.isTrue = false;
				return info;
			}
			int[] Wi = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 };// 加权因子  
			int[] Y = { 1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2 };// 身份证验证位值.10代表X 
			// 验证校验位
			int sum = 0; // 声明加权求和变量
			String[] _cardNo2 = cardNo.split("");
			String[] _cardNo = new String[_cardNo2.length-1];
			for(int i=0;i<_cardNo.length;i++){
				_cardNo[i]=_cardNo2[i+1];
			}
			if (_cardNo[17].toUpperCase().equals("X") ) {
				_cardNo[17] = "10";// 将最后位为x的验证码替换为10方便后续操作  
			}
			for ( int i = 0; i < 17; i++) {
//				System.out.println(_cardNo[i]);
				sum += Wi[i] * Integer.valueOf(_cardNo[i]);// 加权求和  
			}
			int i = sum % 11;// 得到验证码所位置
			if (!_cardNo[17].equals(Y[i]+"") ) {
				info.isTrue = false;
				return info;
			}
			info.isTrue = true;
			info.year = birthday.getYear()+"";
			info.month = (birthday.getMonth() + 1)+"";
			info.day = birthday.getDate()+"";
			if (Integer.valueOf(p) % 2 == 0) {
				info.isFemale = true;
				info.isMale = false;
			} else {
				info.isFemale = false;
				info.isMale = true;
			}
			return info;
		}
		return info;
	}
	
	 /**
     * 校验银行卡卡号
     * @param cardId
     * @return
     */
    public static boolean checkBankCard(String cardId) {
        char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
        if(bit == 'N'){
            return false;
        }
        return cardId.charAt(cardId.length() - 1) == bit;
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     * @param nonCheckCodeCardId
     * @return
     */
    public static char getBankCardCheckCode(String nonCheckCodeCardId){
        if(nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            //如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for(int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if(j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char)((10 - luhmSum % 10) + '0');
    }
}
