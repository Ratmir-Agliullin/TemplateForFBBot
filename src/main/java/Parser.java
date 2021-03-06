import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Аглиуллины on 03.09.2017.
 */
public class Parser {
    private String bufferFilePath = "buffer.dat";
    public Integer likesCount = 0;
    public Integer repostCount = 0;

//1507908600 1507905000
    public static void main(String[] args) {
        Parser parser = new Parser();
        Document doc = null;
        try {
            doc = Jsoup.connect(ConfigManager.getURL()).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(parser.getPostText(2, doc));
        parser.getPostImages(2,doc).stream().forEach(s-> System.out.println(s));
        System.out.println(parser.getPostId(2, doc));
    }

    public String getPostId(int index, Document document) {
        String result = null;
        Element element = document.select("div._3ccb").get(index);//clearfix
        Elements in = element.getElementsByClass("clearfix");
        return in.get(0).getElementsByClass("_5pcp").get(0).getElementsByTag("span").get(2)
                .getElementsByTag("a").get(0).attr("href");
    }

    public String getPostText(int index, Document document) {
        Element element = document.select("div._3ccb").get(index);
      Elements in = element.getElementsByClass("userContent");
      StringBuffer stringBuffer = new StringBuffer(" ");

      int i=0;
while (true){
    try {
        String link = getLinkFromText(in.get(0).getElementsByTag("p").get(i));

        if(link.equals(" "))
        stringBuffer.append(in.get(0).getElementsByTag("p").get(i).text());
        else {
            stringBuffer.append(getLinkFromText(in.get(0).getElementsByTag("p").get(i)));
          //  stringBuffer.append("\n");
          //  stringBuffer.append(in.get(0).getElementsByTag("p").get(i).text());
        }
        stringBuffer.append("\n");
        i++;
    } catch (IndexOutOfBoundsException e){
        break;
    }
}

        return stringBuffer.toString();
    }

    public boolean textFilter(String text){
     boolean result = false;
     if(text.substring(0,7).equals("/hashtag") || text.substring(8,14).equals("matchtv"))
         result=true;
     return result;
    }

    public String getLinkFromText(Element element){
        String result=null;
        try{
            result=element.getElementsByTag("a").get(0).attr("href");
        } catch (IndexOutOfBoundsException e){
            result=" ";
        }
        return result;
    }
//mtm
    public List<String> getPostImages(int index, Document document) {
        String result = null;
        List<String> imageList = new ArrayList<>();
        Element element = document.select("div._3ccb").get(index);
        Elements postContent = element.getElementsByClass("_3x-2");

            try {
                for (Element el : postContent
                        ) {
                    imageList.add(el.getElementsByTag("a").get(0)
                            .getElementsByTag("img").get(0).attr("src"));
                }
            } catch (IndexOutOfBoundsException e) {
                imageList.add(" ");

            }

        return imageList;
    }


    public void getTextPostFormIndex(int index, Document document) {
        Elements elements = document.select("div._post.post.page_block.all.own");
        Element wallText = elements.get(index).getElementsByClass("wall_post_text").get(0);
        System.out.println(wallText.text());
    }


    public void getCountsLikesAndReposts(int index, Document document) {

        Elements elements = document.select("div._post.post.page_block.all.own");
        Element likesInfo = elements.get(index).getElementsByClass("_post_content").get(0)
                .getElementsByClass("post_content").get(0)
                .getElementsByClass("post_info").get(0)
                .getElementsByClass("post_full_like_wrap").get(0)
                .getElementsByClass("post_like_count").get(0);
        likesCount = Integer.valueOf(likesInfo.text());
        System.out.println(likesCount);

        Element repostInfo = elements.get(index).getElementsByClass("_post_content").get(0)
                .getElementsByClass("post_content").get(0)
                .getElementsByClass("post_info").get(0)
                .getElementsByClass("post_full_like_wrap").get(0)
                .getElementsByClass("post_share_count").get(0);
        repostCount = Integer.valueOf(repostInfo.text());
        System.out.println(repostCount);
    }


    public String getVideoPostFormIndex(int index, Document document) {
        String result = null;
        Elements elements = document.select("div._post.post.page_block.all.own");
        Element wallVideo = null;
        try {
            wallVideo = elements.get(index).getElementsByClass("_post_content").get(0)
                    .getElementsByClass("post_content").get(0)
                    .getElementsByClass("post_info").get(0)
                    .getElementsByClass("wall_text").get(0)
                    .getElementsByClass("post_video_desc")
                    .get(0);
        } catch (IndexOutOfBoundsException e) {
            return " ";
        }
        result = parsingWihRegex(wallVideo.toString(), "href=\"\\/video(.*?)\\\"", 6, 1);
        //  System.out.println("https://vk.com"+result);
        return "https://vk.com" + result;
    }

    public String parsingWihRegex(String input, String regex, int start, int end) {
        String result = null;
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(input);
        if (m.find()) {
            result = m.group(0).substring(start, m.group(0).length() - end);


        }
        return result;
    }

    public void getFirstFivePosts(Document document) {
        Elements elements = document.select("div._post.post.page_block.all.own");
        for (int i = 0; i < 5; i++)
            System.out.println(elements.get(i).text());
    }


    public void writeStringInFile(String in) throws IOException {

        BufferedWriter myfile = new BufferedWriter(new FileWriter(bufferFilePath));
        myfile.write(in);
        myfile.close();
    }

    public String getStringFromFile() throws IOException {
        BufferedReader myfile = new BufferedReader(new FileReader(bufferFilePath));
        return myfile.readLine();
    }

}
