package UNLa.PabloGalvan.config;
import java.io.InputStream;
import java.util.Properties;

public class MongoDBConfig {
    private static final String PROPERTIES_FILE = "mongodb.properties";
    private static Properties properties;

    static {
        properties = new Properties();
        try (InputStream input = MongoDBConfig.class.getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {

            if (input == null) {
                throw new RuntimeException("No se pudo encontrar " + PROPERTIES_FILE +
                        ". Asegúrate de que esté en src/main/resources");
            }

            properties.load(input);
        } catch (Exception ex) {
            throw new RuntimeException("Error al cargar " + PROPERTIES_FILE, ex);
        }
    }

    public static String getMongoURI() {
        return properties.getProperty("mongodb.uri");
    }

    public static String getDatabaseName() {
        return properties.getProperty("mongodb.database");
    }

}
