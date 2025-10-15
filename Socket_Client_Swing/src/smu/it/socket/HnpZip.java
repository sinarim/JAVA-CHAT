package smu.it.socket;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class HnpZip {

    /**
     * 공공데이터포털 우편번호 조회 API 호출
     * @param s 검색어 (도로명 또는 지번)
     * @param p 읽어올 페이지(1부터)
     * @param l 한 페이지당 출력 개수 (최대 50)
     * @param v 결과 리스트 (zip, 도로명, 지번 순으로 추가됨)
     * @param n 출력 정보 배열 [0]=총 검색 수, [1]=현재 페이지
     * @return 오류 메시지, null이면 성공
     */
    public static String find(String s, int p, int l, List<String> v, int[] n) {
        HttpURLConnection con = null;

        try {
            @SuppressWarnings("deprecation")
            URL url = new URL(
                "http://openapi.epost.go.kr/postal/retrieveNewAdressAreaCdSearchAllService/retrieveNewAdressAreaCdSearchAllService/getNewAddressListAreaCdSearchAll"
                + "?ServiceKey=e05a75253d67b6521a31ed0534cfac03fcb96ce431adf66c80c6c0e0fe63919a" 
                + "&countPerPage=" + l
                + "&currentPage=" + p
                + "&srchwrd=" + URLEncoder.encode(s, "UTF-8")
            );

            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Accept-language", "ko");

            DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
            DocumentBuilder bd = fac.newDocumentBuilder();
            Document doc = bd.parse(con.getInputStream());

            boolean bOk = false;
            s = null; // 에러 메시지 초기화

            NodeList ns = doc.getElementsByTagName("cmmMsgHeader");
            if (ns.getLength() > 0) {
                Node nd;
                for (nd = ns.item(0).getFirstChild(); nd != null; nd = nd.getNextSibling()) {
                    String nn = nd.getNodeName();
                    String val = nd.getTextContent() != null ? nd.getTextContent().trim() : "";

                    if (!bOk) {
                        if ("successYN".equals(nn)) {
                            if ("Y".equals(val)) bOk = true;
                        } else if ("errMsg".equals(nn)) {
                            s = val;
                        }
                    } else {
                        if ("totalCount".equals(nn) && !val.isEmpty()) {
                            try { n[0] = Integer.parseInt(val); } catch (NumberFormatException e) { n[0] = 0; }
                        } else if ("currentPage".equals(nn) && !val.isEmpty()) {
                            try { n[1] = Integer.parseInt(val); } catch (NumberFormatException e) { n[1] = 1; }
                        }
                    }
                }
            }

            if (bOk) {
                ns = doc.getElementsByTagName("newAddressListAreaCdSearchAll");
                for (int i = 0; i < ns.getLength(); i++) {
                    Node nd = ns.item(i).getFirstChild();
                    String zip = "", roadAddr = "", jibunAddr = "";

                    for (; nd != null; nd = nd.getNextSibling()) {
                        String nn = nd.getNodeName();
                        String val = nd.getTextContent() != null ? nd.getTextContent().trim() : "";

                        if ("zipNo".equals(nn)) zip = val;
                        else if ("lnmAdres".equals(nn)) roadAddr = val;
                        else if ("rnAdres".equals(nn)) jibunAddr = val;
                    }

                    v.add(zip);
                    v.add(roadAddr);
                    v.add(jibunAddr);
                }
            }

            if (s == null && v.size() < 3) s = "검색결과가 없습니다.";

        } catch (Exception e) {
            s = e.getMessage();
        }

        if (con != null) con.disconnect();
        return s;
    }
}
