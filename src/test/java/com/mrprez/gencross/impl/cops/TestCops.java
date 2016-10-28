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
		
		// Compétences
		fillCompetences(personnage);
		
		// Pass to phase "Origine sociale"
		passToPhaseOrigineSociale(personnage);
		
		// Origine sociale
		fillOrigineSociale(personnage);
		
		// Pass to phase "Études"
		passToEtudes(personnage);
		
		// Études
		fillEtudes(personnage);
	}
	
	private void fillEtudes(Personnage personnage) throws Exception {
		Property relation = personnage.getProperty("Relations").getSubProperties().getDefaultProperty().clone();
		relation.setName("Tati");
		personnage.addPropertyToMotherProperty(relation);
		Assert.assertEquals(1, personnage.getErrors().size());
		personnage.setNewValue("Compétences#Informatique#piratage", 3);
		Assert.assertTrue(personnage.phaseFinished());
	}
	
	private void passToEtudes(Personnage personnage) throws Exception {
		personnage.passToNextPhase();
		Assert.assertEquals("Études", personnage.getPhase());
		Assert.assertEquals(2, personnage.getErrors().size());
		for(Property competence : personnage.getProperty("Compétences").getSubProperties()){
			if(competence.getValue()!=null){
				Assert.assertEquals(2, competence.getValue().getInt());
			}
			if(competence.getSubProperties()!=null){
				for(Property spe : competence.getSubProperties()){
					Assert.assertEquals(2, spe.getValue().getInt());
				}
				for(Property spe : competence.getSubProperties().getOptions().values()){
					Assert.assertEquals(2, spe.getValue().getInt());
				}
				Assert.assertEquals(2, competence.getSubProperties().getDefaultProperty().getValue().getInt());
			}
		}
	}
	
	private void fillOrigineSociale(Personnage personnage) throws Exception {
		Property equipement = personnage.getProperty("Equipement").getSubProperties().getDefaultProperty().clone();
		equipement.setName("Katana");
		personnage.addPropertyToMotherProperty(equipement);
		Assert.assertEquals(1, personnage.getErrors().size());
		Property relation = personnage.getProperty("Relations").getSubProperties().getDefaultProperty().clone();
		relation.setName("Tonton");
		personnage.addPropertyToMotherProperty(relation);
		personnage.setNewValue("Relations#Tonton", 2);
		Assert.assertTrue(personnage.phaseFinished());
	}

	private void passToPhaseOrigineSociale(Personnage personnage) throws Exception {
		personnage.passToNextPhase();
		Assert.assertEquals(2, personnage.getErrors().size());
		Assert.assertTrue(personnage.getErrors().contains("Vous avez le droit à un et un seul équipement supplémentaire"));
		Assert.assertTrue(personnage.getErrors().contains("Il reste des Relations à dépenser"));
	}

	private void fillCompetences(Personnage personnage) throws Exception{
		personnage.setNewValue("Compétences#Déguisement", 8);
		Assert.assertEquals(8, personnage.getPointPools().get("Compétences").getRemaining());
		
		personnage.setNewValue("Compétences#Éloquence", 5);
		Assert.assertEquals(6, personnage.getPointPools().get("Compétences").getRemaining());
		
		Property newConnaissance1 = personnage.getProperty("Compétences#Connaissance").getSubProperties().getDefaultProperty().clone();
		newConnaissance1.setName("physique");
		personnage.addPropertyToMotherProperty(newConnaissance1);
		personnage.setNewValue("Compétences#Connaissance#physique", 8);
		Assert.assertEquals(4, personnage.getPointPools().get("Compétences").getRemaining());
		
		Property newConnaissance2 = personnage.getProperty("Compétences#Connaissance").getSubProperties().getDefaultProperty().clone();
		newConnaissance2.setName("chimie");
		personnage.addPropertyToMotherProperty(newConnaissance2);
		personnage.setNewValue("Compétences#Connaissance#chimie", 9);
		Assert.assertEquals(3, personnage.getPointPools().get("Compétences").getRemaining());
		
		personnage.setNewValue("Compétences#Informatique", 6);
		Assert.assertEquals(2, personnage.getPointPools().get("Compétences").getRemaining());
		
		Property newSpe = personnage.getProperty("Compétences#Informatique").getSubProperties().getDefaultProperty().clone();
		newSpe.setName("piratage");
		personnage.addPropertyToMotherProperty(newSpe);
		personnage.setNewValue("Compétences#Informatique#piratage", 5);
		Assert.assertEquals(1, personnage.getPointPools().get("Compétences").getRemaining());
		
		personnage.setNewValue("Compétences#Arme de Poing", 6);
		Assert.assertTrue(personnage.phaseFinished());
	}
	
	private void passToPhaseCompetence(Personnage personnage) throws Exception{
		personnage.passToNextPhase();
		Assert.assertEquals("Compétences", personnage.getPhase());
		Assert.assertFalse(personnage.getProperty("Compétences#Arme Lourde").isEditable());
		Assert.assertFalse(personnage.getProperty("Compétences#Arme Lourde").getSubProperties().isFixe());
		Assert.assertTrue(personnage.getProperty("Compétences#Arme d’Épaule").isEditable());
		Assert.assertTrue(personnage.getProperty("Compétences#Arme d’Épaule").getSubProperties().isFixe());
		Assert.assertTrue(personnage.getProperty("Compétences#Déguisement").isEditable());
		Assert.assertEquals(personnage.getProperty("Compétences#Éloquence").getMin().getInt(), 5);
		Assert.assertEquals(personnage.getProperty("Compétences#Éloquence").getMax().getInt(), 7);
		Assert.assertFalse(personnage.getProperty("Compétences#Conduite").isEditable());
		Assert.assertEquals(personnage.getProperty("Compétences#Corps à Corps#projections").getMax().getInt(), 7);
		Assert.assertEquals(personnage.getProperty("Compétences#Corps à Corps#projections").getMin().getInt(), 5);
		Assert.assertEquals(personnage.getProperty("Compétences#Corps à Corps").getSubProperties().getDefaultProperty().getMin().getInt(), 6);
		Assert.assertEquals(personnage.getProperty("Compétences#Corps à Corps").getSubProperties().getOptions().get("coups").getMin().getInt(), 6);
		Assert.assertTrue(personnage.getProperty("Compétences#Informatique").getSubProperties().isFixe());
	}
	
	private void fillBaseCompetence(Personnage personnage) throws Exception{
		personnage.setNewValue("Compétences#Éloquence", 7);
		Assert.assertEquals(1, personnage.getErrors().size());
		personnage.setNewValue("Compétences#Intimidation", 8);
		Assert.assertEquals(2, personnage.getErrors().size());
		personnage.setNewValue("Compétences#Intimidation", 10);
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
		Assert.assertTrue(personnage.getErrors().contains("Vous ne pouvez avoir plus d'une Caractéristique à 5 à la création"));
		personnage.setNewValue("Caracteristiques#Coordination", 3);
		personnage.setNewValue("Caracteristiques#Réflexe", 4);
		Assert.assertEquals(35, personnage.getProperty("Points de vie").getValue().getInt());
		Assert.assertEquals(-1, personnage.getProperty("Caracteristiques#Init. min").getValue().getInt());
		
		Assert.assertTrue(personnage.getErrors().contains("Vous devez avoir autant de Langues que votre Caractéristique d'Education"));
		Property langue1 = personnage.getProperty("Langues").getSubProperties().getDefaultProperty().clone();
		langue1.setName("Langue 1");
		personnage.addPropertyToMotherProperty(langue1);
		Assert.assertFalse(personnage.getErrors().contains("Vous devez avoir autant de Langues que votre Caractéristique d'Education"));
		personnage.setNewValue("Caracteristiques#Education", 3);
		Assert.assertTrue(personnage.getErrors().contains("Vous devez avoir autant de Langues que votre Caractéristique d'Education"));
		Property langue2 = personnage.getProperty("Langues").getSubProperties().getDefaultProperty().clone();
		langue2.setName("Langue 2");
		personnage.addPropertyToMotherProperty(langue2);
		Assert.assertFalse(personnage.getErrors().contains("Vous devez avoir autant de Langues que votre Caractéristique d'Education"));
		
		Assert.assertEquals(0, personnage.getPointPools().get("Caractéristiques").getRemaining());
		Assert.assertTrue(personnage.phaseFinished());
	}
	
}
