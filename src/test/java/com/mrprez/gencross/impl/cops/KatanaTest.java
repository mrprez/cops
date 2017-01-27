package com.mrprez.gencross.impl.cops;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.mrprez.gencross.Property;
import com.mrprez.gencross.history.HistoryItem;
import com.mrprez.gencross.test.HistoryTest;

public class KatanaTest extends HistoryTest {

	public KatanaTest() {
		super("Katana.xml");
	}

	protected void creation(HistoryItem historyItem) throws Exception {
		String motherPropertyAbsoluteName = historyItem.getAbsoluteName().substring(0, historyItem.getAbsoluteName().lastIndexOf("#"));
		Property motherProperty = personnage.getProperty(motherPropertyAbsoluteName);
		String propertyName = historyItem.getAbsoluteName().substring(historyItem.getAbsoluteName().lastIndexOf("#") + 1);
		String specification = null;
		if (propertyName.contains(Property.SPECIFICATION_SEPARATOR)) {
			specification = propertyName.substring(propertyName.indexOf(Property.SPECIFICATION_SEPARATOR) + 3);
			propertyName = propertyName.substring(0, propertyName.indexOf(Property.SPECIFICATION_SEPARATOR));
		}
		if (motherProperty.getSubProperties().isFixe()) {
			fail("Mother property " + motherProperty.getAbsoluteName() + " is fixe");
		}
		String propertyKey = specification != null ? propertyName + Property.SPECIFICATION_SEPARATOR + specification : propertyName;
		if (motherProperty.getSubProperties().getOptions() != null && motherProperty.getSubProperties().getOptions().containsKey(propertyKey)) {
			Property newProperty = motherProperty.getSubProperties().getOptions().get(propertyKey).clone();
			if (newProperty.getSpecification() != null) {
				newProperty.setSpecification(specification);
			}
			boolean success = personnage.addPropertyToMotherProperty(newProperty);
			assertTrue("Cannot add property " + historyItem.getAbsoluteName() + " " + personnage.getActionMessage(), success);
		} else if (motherProperty.getSubProperties().isOpen()) {
			Property newProperty = motherProperty.getSubProperties().getDefaultProperty().clone();
			newProperty.setName(propertyName);
			newProperty.setSpecification(specification);
			boolean success = personnage.addPropertyToMotherProperty(newProperty);
			assertTrue("Cannot add property " + historyItem.getAbsoluteName() + " " + personnage.getActionMessage(), success);
		} else {
			fail("Cannot add property " + historyItem.getAbsoluteName() + ", property list is not opened");
		}
		HistoryItem newHistoryItem = personnage.getHistory().get(personnage.getHistory().size() - 1);
		System.out.println("Add    property " + historyItem.getAbsoluteName() + " : " + historyItem.getNewValue() + " (" + newHistoryItem.getCost() + " " + newHistoryItem.getPointPool() + ")");
	}

}
