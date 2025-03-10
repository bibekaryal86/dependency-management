package dep.mgmt.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import dep.mgmt.util.ConstantUtils;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;

public class MongoDbConfig {

  private static final MongoClient mongoClient;
  private static final MongoDatabase database;

  static {
    final String dbHost = CommonUtilities.getSystemEnvProperty(ConstantUtils.ENV_DB_HOST);
    final String dbName = CommonUtilities.getSystemEnvProperty(ConstantUtils.ENV_DB_NAME);
    final String dbUser = CommonUtilities.getSystemEnvProperty(ConstantUtils.ENV_DB_USER);
    final String dbPwd = CommonUtilities.getSystemEnvProperty(ConstantUtils.ENV_DB_PWD);
    final String connectionString = String.format(dbHost, dbUser, dbPwd, dbName, dbName);

    mongoClient = MongoClients.create(connectionString);
    database = mongoClient.getDatabase(ConstantUtils.MONGODB_DATABASE_NAME);
  }

  public static MongoDatabase getDatabase() {
    return database;
  }
}
