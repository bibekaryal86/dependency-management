package dep.mgmt.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import dep.mgmt.util.ConstantUtils;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

public class MongoDbConfig {

  private static final MongoDatabase database;

  static {
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
    database =
            mongoClient
                    .getDatabase(ConstantUtils.MONGODB_DATABASE_NAME)
                    .withCodecRegistry(codecRegistry);
  }

  public static MongoDatabase getDatabase() {
    return database;
  }

  public static void init() {
    database.getName();
  }
}
