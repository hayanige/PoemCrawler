package com.tasanuma.crawler;

import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PoemCrawler {
  public static void main(String[] args) throws IOException {
    Document doc = Jsoup.connect("https://www.no-ichigo.jp/koizora/poem/").get();
    Elements ppoems = doc.select("div.poemList").select("div.card");
    List<Element> poems = ImmutableList.copyOf(ppoems).reverse();

    StringBuilder sb = new StringBuilder();
    sb.append("ポエム").append("\t");
    sb.append("名前").append("\t");
    sb.append("日付").append("\t");
    sb.append("いいね数").append("\t");
    sb.append("カードの種類").append("\t");
    sb.append("文の向き").append("\n");

    for (Element poem: poems) {
      Element textElement = poem.select("div.txt").select("p").first();
      String name = poem.select("div.name").first().text();
      String date = poem.select("div.date").first().text();
      String like = poem.select("button").select("em").first().text();
      String cardType = poem.attr("class").substring(9, 10);
      String angle = poem.attr("class").contains("rl") ? "たて" : "よこ";

      sb.append("\"" + escapeDoubleQuote(br2nl(textElement.html())) + "\"").append("\t");
      sb.append(name).append("\t");
      sb.append(date).append("\t");
      sb.append(like).append("\t");
      sb.append(cardType).append("\t");
      sb.append(angle).append("\n");
    }

    FileOutputStream fop = null;
    File file;
    try {
      file = new File("/tmp/koizora_poems.csv");
      fop = new FileOutputStream(file);

      if (!file.exists()) {
        file.createNewFile();
      }

      fop.write(sb.toString().getBytes());
      fop.flush();
      fop.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (fop != null) {
          fop.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static String br2nl(String html) {
    return html.replaceAll("<br> ", "\n").replaceAll("<br>", "\n");
  }

  public static String escapeDoubleQuote(String str) {
    return str.replaceAll("\"", "\"\"");
  }
}
