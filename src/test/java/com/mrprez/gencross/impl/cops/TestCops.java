package com.mrprez.gencross.impl.cops;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.mrprez.gencross.Personnage;
import com.mrprez.gencross.Property;
import com.mrprez.gencross.disk.PersonnageFactory;

public class TestCops {
	private final static List<String> BASE_SOCIAL_COMPETENCES = Arrays.asList("Éloquence", "Intimidation", "Rhétorique");
	
	@Test
	public void testBuildPersonnage() throws Exception{
		// Create personnage
		PersonnageFactory personnageFactory = new PersonnageFactory(false);
		Personnage personnage = personnageFactory.buildNewPersonnage("COPS");
		
		// Caracteristiques
		fillCaracteristiques(personnage);
		
		// Pass to phase "Compétences de bases"
		passToPhaseCompetenceDeBase(personnage);
		
		// Compétences de bases
		fillBaseCompetence(personnage);
		
		// Pass to phase "Compétences"
		passToPhaseCompetence(personnage);
	}
	
	private void passToPhaseCompetence(Personnage personnage) throws Exception{
		personnage.passToNextPhase();
		Assert.assertEquals("Compétences", personnage.getPhase());
	}
	
	private void fillBaseCompetence(Personnage personnage) throws Exception{
		personnage.setNewValue("Compétences#Éloquence", 7);
		Assert.assertEquals(1, personnage.getErrors().size());
		Property newProperty = personnage.getProperty("Compétences#Corps à Corps").getSubProperties().getOptions().get("projections");
		personnage.addPropertyToMotherProperty(newProperty);
		personnage.setNewValue("Compétences#Corps à Corps#projections", 7);
		Assert.assertTrue(personnage.phaseFinished());
	}
	
	private void passToPhaseCompetenceDeBase(Personnage personnage) throws Exception{
		personnage.passToNextPhase();
		Assert.assertEquals("Compétences de bases", personnage.getPhase());
		for(Property competence : personnage.getProperty("Compétences").getSubProperties()){
			if(BASE_SOCIAL_COMPETENCES.contains(competence.getName())){
				Assert.assertTrue(competence.isEditable());
			}else{
				Assert.assertFalse(competence.isEditable());
			}
			if(competence.getSubProperties()!=null){
				if(competence.getName().equals("Corps à Corps")){
					Assert.assertFalse(competence.getSubProperties().isFixe());
				}else{
					Assert.assertTrue(competence.getSubProperties().isFixe());
				}
			}
		}
	}
	
	private void fillCaracteristiques(Personnage personnage) throws Exception{
		personnage.setNewValue("Caracteristiques#Carrure", 5);
		personnage.setNewValue("Caracteristiques#Coordination", 5);
		personnage.setNewValue("Caracteristiques#Réflexe", 5);
		personnage.setNewValue("Caracteristiques#Charme", 5);
		personnage.setNewValue("Caracteristiques#Coordination", 5);
		personnage.setNewValue("Caracteristiques#Education", 4);
		Assert.assertEquals(35, personnage.getProperty("Points de vie").getValue().getInt());
		Assert.assertEquals(-2, personnage.getProperty("Caracteristiques#Init. min").getValue().getInt());
		Assert.assertEquals(0, personnage.getPointPools().get("Caractéristiques").getRemaining());
		Assert.assertTrue(personnage.phaseFinished());
	}
	
}
