package com.mrprez.gencross.impl.cops;

import com.mrprez.gencross.Version;
import com.mrprez.gencross.migration.MigrationPersonnage;
import com.mrprez.gencross.migration.Migrator;

public class MigrateFrom1_1 implements Migrator {

	@Override
	public MigrationPersonnage migrate(MigrationPersonnage migrationPersonnage) throws Exception {
		migrationPersonnage.getPluginDescriptor().setVersion(new Version(1, 2));
		return migrationPersonnage;
	}


}
