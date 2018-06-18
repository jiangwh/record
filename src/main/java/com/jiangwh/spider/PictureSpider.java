package com.jiangwh.spider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PictureSpider {

	public static void main(String[] args) throws ClientProtocolException, IOException {

		File file = new File("/Users/jiangwh/workextends/xueersi");
		if (file.exists() && file.isDirectory()) {
			File[] fs =file.listFiles();
			createPDF(fs);
		}

	}

	static void Spider() throws ClientProtocolException, IOException {
		HttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet("http://www.veone.cn/articles/ppt.html?from=timeline&isappinstalled=0");
		HttpResponse response = client.execute(httpGet);
		HttpEntity entity = response.getEntity();
		String web = EntityUtils.toString(entity, "UTF-8");
		Document doc = Jsoup.parse(web);
		Elements links = doc.select("img[src$=.jpg]");
		httpGet.releaseConnection();
		int i = 0;
		for (Element link : links) {
			httpGet.setHeader("User-Agent", "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");
			httpGet.setURI(URI.create(link.attr("src")));
			HttpResponse imgRespone = client.execute(httpGet);
			File f = null;
			if (1 == String.valueOf(i).length()) {
				f = new File("00".concat(String.valueOf(i)) + ".jpg");
			} else if (2 == String.valueOf(i).length()) {
				f = new File("0".concat(String.valueOf(i)) + ".jpg");
			} else {
				f = new File(String.valueOf(i) + ".jpg");
			}

			FileOutputStream output = new FileOutputStream(f);
			entity = imgRespone.getEntity();
			byte[] buff = new byte[1024];
			int index = 0;
			while ((index = entity.getContent().read(buff)) != -1) {
				output.write(buff, 0, index);
			}
			output.flush();
			output.close();
			httpGet.releaseConnection();
			i++;
		}
	}

	static void createPDF(File[] fs) throws IOException {
		PDDocument document = new PDDocument();
		for (File file : fs) {
			PDPage page = new PDPage();
			document.addPage(page);
			PDImageXObject pdImage = PDImageXObject.createFromFile(file.getAbsolutePath(), document);
			PDPageContentStream contents = new PDPageContentStream(document, page);
			contents.drawImage(pdImage,0,216,pdImage.getWidth()*48/100,pdImage.getHeight()/2);
			contents.close();
		}
		document.save("pdf.pdf");
	}
}
