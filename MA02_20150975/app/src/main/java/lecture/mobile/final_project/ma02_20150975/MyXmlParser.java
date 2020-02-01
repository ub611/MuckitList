package lecture.mobile.final_project.ma02_20150975;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by 유빈 on 2017-12-28.
 */

public class MyXmlParser {

    public enum TagType { NONE, ADDR, TEL, TITLE, MAPX, MAPY };

    public MyXmlParser(){

    }

    public ArrayList<APIResultDTO> parse(String xml) {

        ArrayList<APIResultDTO> resultList = new ArrayList();
        APIResultDTO dbo = null;

//        태그를 구분하기 위한 enum 변수 초기화
        TagType tagType = TagType.NONE;

        try {
//            파서 준비
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));

//            태그 유형 구분 변수 준비
            int eventType = parser.getEventType();

//            parsing 수행 - 강의자료 예제는 for 문으로 구성
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.END_DOCUMENT:
                        break;

                    // 태그명은 상수로 선언하는 것을 고려해볼 것
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("item")) {    // 새로운 항목을 표현하는 태그를 만났을 경우 dto 객체 생성
                            dbo = new APIResultDTO();
                        } else if (parser.getName().equals("addr1")) {
                            tagType = TagType.ADDR;
                        } else if (parser.getName().equals("tel")) {
                            tagType = TagType.TEL;
                        } else if (parser.getName().equals("title")){
                            tagType = TagType.TITLE;
                        }else if (parser.getName().equals("mapx")){
                            tagType = tagType.MAPX;
                        }else if (parser.getName().equals("mapy")){
                            tagType = tagType.MAPY;
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("item")) {
                            resultList.add(dbo);
                        }
                        break;

                    case XmlPullParser.TEXT:
                        switch(tagType) {       // 태그의 유형에 따라 dto 에 값 저장
                            case ADDR:
                                dbo.setAddress(parser.getText());
                                break;
                            case MAPX:
                                dbo.setX(Float.parseFloat(parser.getText()));
                                break;
                            case MAPY:
                                dbo.setY(Float.parseFloat(parser.getText()));
                                break;
                          case TEL:
                                dbo.setTell(parser.getText());
                                break;
                            case TITLE:
                                dbo.setTitle(parser.getText());
                        }
                        tagType = TagType.NONE;
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }
}
