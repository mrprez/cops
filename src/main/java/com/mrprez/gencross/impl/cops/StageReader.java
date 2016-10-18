package com.mrprez.gencross.impl.cops;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;

public class StageReader {

	public static void main(String[] args) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("toto.html"), "UTF-8"));
		try{
			String line;
			Stage stage;
			while((line=reader.readLine())!=null){
				if(line.startsWith("<h3>")){
					stage = new Stage(extractStageName(line));
				} else if(line.startsWith("<p><b>Pré-requis</b>")){
					extractRequirement(line);
				} else if(line.startsWith("</p><p><b>Possibilité de cumul</b>&nbsp;: ")){
					extractCumul(line);
				}
			}
		}finally {
			reader.close();
		}

	}
	
	private static boolean extractCumul(String line){
		return line.replace("</p><p><b>Possibilité de cumul</b>&nbsp;: ", "").trim().startsWith("oui");
	}
	
	private static String extractStageName(String line){
		String text = Jsoup.parse(line).body().getElementsByTag("h3").first().getElementsByTag("span").first().ownText();
		text = text.replaceAll("Stage d’", "");
		text = text.replaceAll("Stage de ", "");
		text = text.replaceAll("Stages de ", "");
		text = text.replaceAll("Stage ", "");
		text = text.substring(0, 1).toUpperCase()+text.substring(1);
		if(text.contains(" – ")){
			text = text.substring(0, text.indexOf(" – "));
		}
		System.out.println(text);
		return text;
	}
	
	private static Map<String, Integer> extractRequirement(String line) throws IOException, Exception{
		Map<String, Integer> requirement = new HashMap<String, Integer>();
		
		line = line.replace("<p><b>Pré-requis</b>&nbsp;:", "").trim();
		for(String text : line.split(",")){
			text = text.trim();
			if(text.matches("N[1-3]")){
				
			}else{
				String name = text.substring(0, text.lastIndexOf(' '));
				String level = text.substring(text.lastIndexOf(' ')+1);
				System.out.println("\t"+name+": "+level);
			}
		}
		
		return requirement;
	}
	
	public static class Stage{
		private String name;
		private int level;
		private boolean cumul;
		private List<String> capacity = new ArrayList<String>();
		private Map<String,Integer> requirement = new HashMap<String, Integer>();
		
		public Stage(String name) {
			super();
			this.name = name;
		}
		
		
		
	}

}
