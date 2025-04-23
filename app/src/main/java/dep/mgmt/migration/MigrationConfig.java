package dep.mgmt.migration;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import dep.mgmt.util.ConstantUtils;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

public class MigrationConfig {

  private static MongoDatabase oldDatabase;
  private static MongoDatabase newDatabase;

  public static MongoDatabase getOldDatabase() {
    if (oldDatabase == null) {
      final String dbHost =
          CommonUtilities.getSystemEnvProperty(MigrationConstants.ENV_DB_HOST_OLD);
      final String dbName =
          CommonUtilities.getSystemEnvProperty(MigrationConstants.ENV_DB_NAME_OLD);
      final String dbUser =
          CommonUtilities.getSystemEnvProperty(MigrationConstants.ENV_DB_USER_OLD);
      final String dbPwd = CommonUtilities.getSystemEnvProperty(MigrationConstants.ENV_DB_PWD_OLD);
      final String connectionString =
          String.format(dbHost, dbUser, dbPwd, dbName.toLowerCase(), dbName);

      CodecRegistry pojoCodecRegistry =
          CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build());
      CodecRegistry codecRegistry =
          CodecRegistries.fromRegistries(
              MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
      MongoClient mongoClient = MongoClients.create(connectionString);
      oldDatabase =
          mongoClient
              .getDatabase(ConstantUtils.MONGODB_DATABASE_NAME)
              .withCodecRegistry(codecRegistry);
    }
    return oldDatabase;
  }

  public static MongoDatabase getNewDatabase() {
    if (newDatabase == null) {
      final String dbHost = CommonUtilities.getSystemEnvProperty(ConstantUtils.ENV_DB_HOST);
      final String dbName = CommonUtilities.getSystemEnvProperty(ConstantUtils.ENV_DB_NAME);
      final String dbUser = CommonUtilities.getSystemEnvProperty(ConstantUtils.ENV_DB_USER);
      final String dbPwd = CommonUtilities.getSystemEnvProperty(ConstantUtils.ENV_DB_PWD);
      final String connectionString =
          String.format(dbHost, dbUser, dbPwd, dbName, dbName.toUpperCase());

      CodecRegistry pojoCodecRegistry =
          CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build());
      CodecRegistry codecRegistry =
          CodecRegistries.fromRegistries(
              MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
      MongoClient mongoClient = MongoClients.create(connectionString);
      newDatabase =
          mongoClient
              .getDatabase(ConstantUtils.MONGODB_DATABASE_NAME)
              .withCodecRegistry(codecRegistry);
    }
    return newDatabase;
  }
}
