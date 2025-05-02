package UNLa.PabloGalvan;
import UNLa.PabloGalvan.config.MongoDBConfig;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

public class Main {

    private static final String COLLECTION_NAME = "personas";
    private static MongoCollection<Document> collection;

    public static void main(String[] args) {
        String connectionString = MongoDBConfig.getMongoURI();
        String dbName = MongoDBConfig.getDatabaseName();

        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase(dbName);
            collection = database.getCollection(COLLECTION_NAME);

            // CREATE - Insertar un documento
            Document newPerson = createPerson("Pablo Galvan", 22, "Programador");
            System.out.println("Persona creada: " + newPerson.toJson());

            newPerson = createPerson("Juan Alvaréz", 23, "Ventas");
            System.out.println("Persona creada: " + newPerson.toJson());

            newPerson = createPerson("Maria Roberts", 21, "Programador");
            System.out.println("Persona creada: " + newPerson.toJson());

            // READ - Buscar todos los documentos
            List<Document> allPeople = readAllPeople();
            System.out.println("\nTodas las personas:");
            allPeople.forEach(doc -> System.out.println(doc.toJson()));

            // READ - Buscar por nombre
            String searchName = "Juan Perez";
            List<Document> peopleByName = readPeopleByName(searchName);
            System.out.println("\nPersonas llamadas " + searchName + ":");
            peopleByName.forEach(doc -> System.out.println(doc.toJson()));

            // UPDATE - Actualizar edad de una persona
            String personName = "Juan Alvaréz";
            int newAge = 31;
            long updatedCount = updatePersonAge(personName, newAge);
            System.out.println("\nActualizadas " + updatedCount + " personas con nombre " + personName);

            // DELETE - Eliminar una persona
            String personToDelete = "Pablo Galvan";
            long deletedCount = deletePersonByName(personToDelete);
            System.out.println("\nEliminadas " + deletedCount + " personas con nombre " + personToDelete);

            // READ - Buscar todos los documentos
            allPeople = readAllPeople();
            System.out.println("\nTodas las personas:");
            allPeople.forEach(doc -> System.out.println(doc.toJson()));

        } catch (Exception e) {
            System.err.println("Error al conectar con MongoDB: " + e.getMessage());
        }
    }

    // CREATE - Insertar un nuevo documento
    public static Document createPerson(String nombre, int edad, String profesion) {
        Document doc = new Document("nombre", nombre)
                .append("edad", edad)
                .append("profesion", profesion);
        collection.insertOne(doc);
        return doc;
    }

    // READ - Obtener todos los documentos
    public static List<Document> readAllPeople() {
        return collection.find().into(new ArrayList<>());
    }

    // READ - Buscar documentos por nombre
    public static List<Document> readPeopleByName(String nombre) {
        Bson filter = eq("nombre", nombre);
        return collection.find(filter).into(new ArrayList<>());
    }

    // READ - Buscar documento por ID
    public static Document readPersonById(String id) {
        Bson filter = eq("_id", new ObjectId(id));
        return collection.find(filter).first();
    }

    // UPDATE - Actualizar la edad de una persona por nombre
    public static long updatePersonAge(String nombre, int nuevaEdad) {
        Bson filter = eq("nombre", nombre);
        Bson update = set("edad", nuevaEdad);
        UpdateResult result = collection.updateMany(filter, update);
        return result.getModifiedCount();
    }

    // UPDATE - Actualizar múltiples campos
    public static long updatePerson(String nombre, int nuevaEdad, String nuevaProfesion) {
        Bson filter = eq("nombre", nombre);
        Bson update = combine(
                set("edad", nuevaEdad),
                set("profesion", nuevaProfesion)
        );
        UpdateResult result = collection.updateMany(filter, update);
        return result.getModifiedCount();
    }

    // DELETE - Eliminar documento por nombre
    public static long deletePersonByName(String nombre) {
        Bson filter = eq("nombre", nombre);
        DeleteResult result = collection.deleteMany(filter);
        return result.getDeletedCount();
    }

    // DELETE - Eliminar documento por ID
    public static boolean deletePersonById(String id) {
        Bson filter = eq("_id", new ObjectId(id));
        DeleteResult result = collection.deleteOne(filter);
        return result.getDeletedCount() > 0;
    }
}