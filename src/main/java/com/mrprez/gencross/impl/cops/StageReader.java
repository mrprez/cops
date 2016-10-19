package com.mrprez.gencross.impl.cops;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mrprez.gencross.Personnage;
import com.mrprez.gencross.disk.PersonnageFactory;

public class StageReader {
	private static List<String> CARCACTERISTIQUES = Arrays.asList("Carrure", "Charme", "Coordination", "Education", "Perception", "Réflexe", "Sang froid");

	public static void main(String[] args) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("toto.html"), "UTF-8"));
		try{
			String line;
			Stage stage = null;
			while((line=reader.readLine())!=null){
				if(line.startsWith("<h3>")){
					stage = extractStageName(line);
				} else if(line.contains("<p><b>Pré-requis</b>")){
					extractRequirement(line, stage.getName());
				} else if(line.startsWith("</p><p><b>Possibilité de cumul</b>&nbsp;: ")){
					extractCumul(line);
				} else if(line.contains("</p><p>• ")){
					System.out.println(line.substring(7));
				}
			}
		}finally {
			reader.close();
		}
	}
	
	private static boolean extractCumul(String line){
		if(line.replace("</p><p><b>Possibilité de cumul</b>&nbsp;: ", "").trim().startsWith("oui")){
			System.out.println("\tCumul: true");
		}else{
			System.out.println("\tCumul: false");
		}
		return line.replace("</p><p><b>Possibilité de cumul</b>&nbsp;: ", "").trim().startsWith("oui");
	}
	
	private static Stage extractStageName(String line){
		String text = line.replace("<h3><span>", "").replace("</span>", "");
		text = text.replaceAll("Stage d’", "");
		text = text.replaceAll("Stage de ", "");
		text = text.replaceAll("Stages de ", "");
		text = text.replaceAll("Stage ", "");
		text = text.substring(0, 1).toUpperCase()+text.substring(1);
		String name = text.substring(0, text.indexOf("Niveau")).trim();
		int level = Integer.parseInt(text.substring(text.indexOf("Niveau ")+7, text.indexOf("Niveau ")+8));
		String surname = text.substring(text.indexOf(" – ")+3).replaceAll("[«»]", "").replaceAll("&nbsp;", " ").replaceAll("&amp;", "&").trim();
		System.out.println(name+" Niveau "+level+" - "+surname);
		return new Stage(name, level, surname);
	}
	
	private static Map<String, Integer> extractRequirement(String line, String stageName) throws IOException, Exception{
		Map<String, Integer> requirement = new HashMap<String, Integer>();
		Personnage personnage = new PersonnageFactory().buildNewPersonnage("COPS");
		
		line = line.replace("</p>", "").replace("<p><b>Pré-requis</b>&nbsp;:", "").trim();
		if(line.isEmpty()){
			return requirement;
		}
		for(String text : line.split(",")){
			text = text.trim();
			if(text.matches("N[1-3]")){
				int requiredLevel = Integer.parseInt(text.substring(1));
				String requiredStage = "Stages#"+stageName.replace(String.valueOf(requiredLevel+1), String.valueOf(requiredLevel));
				System.out.println("\t"+requiredStage);
				requirement.put(requiredStage, null);
			}else{
				String name = text.substring(0, text.lastIndexOf(' '));
				String level = text.substring(text.lastIndexOf(' ')+1);
				if(CARCACTERISTIQUES.contains(name)){
					System.out.println("\t"+"Caracteristiques#"+name+": "+extractLevel(level));
					requirement.put("Caracteristiques#"+name, extractLevel(level));
				} else if(name.toLowerCase().contains("ancienneté")) {
					System.out.println("\t"+"Points d'ancienneté"+": "+extractLevel(level));
					requirement.put("Points d'ancienneté", extractLevel(level));
				} else if(name.toLowerCase().contains("adrénaline")) {
					System.out.println("\t"+"Points d'adrénaline"+": "+extractLevel(level));
					requirement.put("Points d'adrénaline", extractLevel(level));
				} else if(name.matches(".+\\[.+\\]")) {
					String spe = name.substring(name.indexOf("[")+1, name.indexOf("]")).trim();
					String comp = name.substring(0, name.indexOf("[")).trim();
					if(personnage.getProperty("Compétences").getSubProperty(comp)!=null){
						System.out.println("\t"+"Compétences#"+comp+"#"+spe+": "+level);
						requirement.put("Compétences#"+comp+"#"+spe, extractLevel(level));
					}else{
						System.err.println("\t"+"Compétences#"+comp+"#"+spe+": "+level);
					}
				} else if(level.matches("[0-9][+]")) {
					if(personnage.getProperty("Compétences").getSubProperty(name)!=null){
						System.out.println("\t"+"Compétences#"+name+": "+level);
						requirement.put("Compétences#"+name, extractLevel(level));
					}else{
						System.err.println("\t"+"Compétences#"+name+": "+level);
					}
				} else {
					System.err.println("\t"+name+": "+level);
				}
			}
		}
		
		return requirement;
	}
	
	private static int extractLevel(String string){
		if(string.matches("[0-9][+]")){
			return Integer.parseInt(string.substring(0,1));
		} else if(string.matches("[(][0-9][)]")){
			return Integer.parseInt(string.substring(1,2));
		}
		return Integer.parseInt(string);
	}
	
	public static class Stage{
		private String name;
		private String surname;
		private int level;
		private boolean cumul;
		private List<String> capacity = new ArrayList<String>();
		private Map<String,Integer> requirement = new HashMap<String, Integer>();
		
		public Stage(String name, int level, String surname) {
			super();
			this.name = name;
			this.level = level;
			this.surname = surname;
		}

		public String getName() {
			return name;
		}
		
		
		
	}

}
