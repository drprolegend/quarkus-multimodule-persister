package it.abs.ms.mongopersister;

import com.mongodb.MongoSecurityException;
import com.mongodb.client.model.*;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.smallrye.mutiny.Uni;
import it.abs.ms.common.builder.BaseBuilder;
import it.abs.ms.common.builder.BuilderInstances;
import it.abs.ms.common.config.listener.BuilderConfigListener;
import it.abs.ms.common.config.model.BuilderConfig;
import it.abs.ms.common.enums.Operation;
import it.abs.ms.common.model.PayloadBase;
import it.abs.ms.common.model.ResponseDataBase;
import it.abs.ms.common.service.CommonService;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class MongoPersister extends CommonService implements BaseBuilder {

    @ConfigProperty(name = "mongodb.database")
    String mongodbDatabase;

    @Inject
    ReactiveMongoClient reactiveMongoClient;

    @Inject
    BuilderConfigListener builderConfigListener;

    @Inject
    BuilderInstances builderInstances;

    @Override
    public Uni<ResponseDataBase> bulk(
            Stream<Map<String, Object>> data,
            String uuid,
            String collection,
            Operation operation,
            String operation_id,
            Map<String, Object> filters,
            List<String> attributesToRemove) {

        Jsonb jsonb = JsonbBuilder.create();

        List<WriteModel<Document>> writeModels;

        if (operation.equals(Operation.REMOVE) && attributesToRemove != null && !attributesToRemove.isEmpty()) {
            writeModels = new ArrayList<>();

            Bson filter = Filters.and(filters.entrySet().stream()
                    .map(entry -> Filters.eq(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList()));

            for (String attribute : attributesToRemove) {
                writeModels.add(new UpdateManyModel<>(
                        filter,
                        Updates.unset(attribute)
                ));
            }

        } else {
            Boolean upsert;

            if (operation.equals(Operation.UPSERT)) {
                upsert = Boolean.TRUE;
            } else {
                upsert = Boolean.FALSE;
            }


            writeModels = data.map(
                    obj -> {
                        try {
                            String jsonString = jsonb.toJson(obj);
                            Document document = Document.parse(jsonString);
                            Document filter;
                            if (uuid != null) {
                                buildObjectId(document, uuid);
                                filter = new Document(uuid, document.get(uuid));
                            } else {
                                if (filters != null) {
                                    filter = mapToDocument(filters);
                                } else {
                                    throw new RuntimeException("Fields uuid and filters can not be null");
                                }
                            }
                            Document transformedDocument = transformDocument(document);
                            UpdateOneModel<Document> updateOneModel = new UpdateOneModel<>(
                                    filter,
                                    new Document("$set", transformedDocument),
                                    new UpdateOptions().upsert(upsert));
                            return updateOneModel;
                        } catch (Exception e) {
                            throw new RuntimeException(operation_id);
                        }

                    }
            ).collect(Collectors.toList());
        }


        return reactiveMongoClient.getDatabase(mongodbDatabase)
                .listCollectionNames()
                .collect().asList()
                .onItem().transformToUni(listCollectionNames -> {
                    if (!collectionIsPresent(listCollectionNames, collection)) {
                        throw new RuntimeException("Error while inserting: Collection " + collection + " not found in database " + mongodbDatabase);
                    }
                    return reactiveMongoClient.getDatabase(mongodbDatabase).getCollection(collection)
                            .bulkWrite(writeModels)
                            .onItem().transform(bulkWriteResult -> {
                                try {
                                    LOG.info("Bulk operation completed successfully");
                                    LOG.info("Inserted: " + bulkWriteResult.getUpserts()
                                            .stream()
                                            .count() + " documents");
                                    LOG.info("Updated: " + bulkWriteResult.getModifiedCount() + " documents");

                                    ResponseDataBase responseData = new ResponseDataBase();
                                    responseData.operation = operation.getValue();
                                    responseData.operation_id = operation_id;
                                    responseData.id = bulkWriteResult.getUpserts()
                                            .stream()
                                            .map(bulkWriteUpsert -> bulkWriteUpsert.getId()
                                                    .asObjectId()
                                                    .getValue()
                                                    .toHexString())
                                            .collect(Collectors.toList());
                                    return responseData;
                                } catch (Exception e) {
                                    throw new RuntimeException("Error while inserting: error parsing mongo response for error: " + e.getMessage());
                                }
                            });
                }).onFailure().transform(e -> {
                    e.printStackTrace();
                    if (e instanceof MongoSecurityException) {
                        return new RuntimeException("Authentication failed on database");
                    }
                    return new RuntimeException("Error while inserting: error from db: " + e.getMessage());
                });
    }


    private boolean collectionIsPresent(
            List<String> listCollectionNames,
            String collection) {
        Optional<List<String>> collectionNamesOpt = Optional.ofNullable(listCollectionNames);
        return collectionNamesOpt.isPresent() && collectionNamesOpt.get().contains(collection);
    }

    private void buildObjectId(
            Document document,
            String uuid) {
        Object _id = document.get(uuid);
        if (_id != null && _id.getClass().equals(String.class)) {
            String idString = _id.toString();
            document.put(uuid, new ObjectId(idString));
        } else if (_id == null) {
            document.put(uuid, new ObjectId());
        }

        for (String key : document.keySet()) {
            Object value = document.get(key);
            if (value instanceof Document) {
                buildObjectId((Document) value, uuid); // Recursive call
            }
        }
    }

    private Document transformDocument(Document doc) {
        Document paths = new Document();
        for (String key : doc.keySet()) {
            Object value = doc.get(key);
            if (value instanceof Document) {
                Document nestedPaths = transformDocument((Document) value);
                for (String nestedKey : nestedPaths.keySet()) {
                    paths.put(key + "." + nestedKey, nestedPaths.get(nestedKey));
                }
            } else {
                paths.put(key, value);
            }
        }
        return paths;
    }

    private Document mapToDocument(Map<String, Object> map) {
        Document doc = new Document();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            // to extend to all possible complex type
            if (value instanceof Map) {
                doc.append(key, mapToDocument((Map<String, Object>) value));
            } else {
                doc.append(key, value);
            }
        }
        return doc;
    }


}
