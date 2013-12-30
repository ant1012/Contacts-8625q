package edu.bupt.contacts.dialpad;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import android.provider.Contacts.Phones;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;

/**
 * 北邮ANT实验室
 * ddd
 * 号码预处理
 * 
 * */


public class BaseUtil {

	public final static String[] PHONES_PROJECTION = new String[] {
			Phones.DISPLAY_NAME, Phones.NUMBER };

	public static String STRS[] = { "", "", "[abc]", "[def]", "[ghi]", "[jkl]",
			"[mno]", "[pqrs]", "[tuv]", "[wxyz]" };

	/**
	 * 
	 * 获取中文的汉语拼音
	 * @param inputString
	 * @return
	 */
	public static String getPingYin(String inputString) {
		if (TextUtils.isEmpty(inputString)) {
			return "";
		}
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_V);

		char[] input = inputString.trim().toCharArray();
		String output = "";

		try {
			for (int i = 0; i < input.length; i++) {
				if (java.lang.Character.toString(input[i]).matches(
						"[\\u4E00-\\u9FA5]+")) {                  //判断是否是中文
					String[] temp = PinyinHelper.toHanyuPinyinStringArray(
							input[i], format);
					if (temp == null || TextUtils.isEmpty(temp[0])) {
						continue;
					}
					output += temp[0].replaceFirst(temp[0].substring(0, 1),
							temp[0].substring(0, 1).toUpperCase());
				} else
					output += java.lang.Character.toString(input[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}
	
	/*
	 * by yuan, 将初始电话号码去掉空格“ ”和横杠“-”
	 */
	public static String getSearchPhoneNumber(String originalNumber){
		
		String tmpstr=originalNumber.replace(" ","");
		tmpstr = tmpstr.replace("-", "");
		return tmpstr;
	}

}
