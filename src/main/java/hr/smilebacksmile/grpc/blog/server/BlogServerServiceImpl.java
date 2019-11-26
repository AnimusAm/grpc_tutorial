package hr.smilebacksmile.grpc.blog.server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import hr.smilebacksmile.blog.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;


public class BlogServerServiceImpl extends BlogServiceGrpc.BlogServiceImplBase {

    private MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    private MongoDatabase database = mongoClient.getDatabase("mydb");
    private MongoCollection<Document> collection = database.getCollection("blog");

    private Blog document2Blog(Document document){
        return Blog.newBuilder()
                .setAuthorId(document.getString("author_id"))
                .setTitle(document.getString("title"))
                .setContent(document.getString("content"))
                .setId(document.getObjectId("_id").toString())
                .build();
    }

    private Document blog2Document(Blog blog){
        final Document result = new Document()
                .append("author_id", blog.getAuthorId())
                .append("title", blog.getTitle())
                .append("content", blog.getContent());

         if (!"".equals(blog.getId())) {
             result.append("_id", new ObjectId(blog.getId()));
         }
        return result;
    }

    @Override
    public void createBlog(CreateBlogRequest request, StreamObserver<CreateBlogResponse> responseObserver) {

        System.out.println("UNARY request received on SERVER side - createBlog: " + request);

        final Blog blog = request.getBlog();
        final Document newBlog = blog2Document(blog);

        System.out.println("On SERVER side - prepared new blog: " + newBlog);

        collection.insertOne(newBlog);

        // we retrieve the MongoDB generated ID
        String id = newBlog.getObjectId("_id").toString();
        System.out.println("On SERVER side - inserted new blog with id: " + id);


        // new response is formed from received request to which newly generated id was added
        CreateBlogResponse response = CreateBlogResponse.newBuilder()
                .setBlog(blog.toBuilder().setId(id).build())
                .build();

        System.out.println("On SERVER side - prepared response: " + response);
        responseObserver.onNext(response);

        responseObserver.onCompleted();

    }

    @Override
    public void readBlog(ReadBlogRequest request, StreamObserver<ReadBlogResponse> responseObserver) {
        System.out.println("UNARY request received on SERVER side - readBlog: " + request);

        final String blogId = request.getBlog().getId();

        Document result = null;
        try {

            System.out.println("On SERVER side - fetching blog with id: " + blogId);
            result = collection.find(eq("_id", new ObjectId(blogId)))
                    .first();

        } catch (Exception e) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Exception occurred while fetching blog with id: " + blogId)
                            .augmentDescription(e.getLocalizedMessage())
                            .asRuntimeException()
            );
        }

        if (result == null) {
            System.out.println("On SERVER side - blog with id: " + blogId + " was not found -> can't be fetched");
            // we don't have a match
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("The blog with the id: " + blogId + " was not found")
                            .asRuntimeException()
            );
        } else {
            System.out.println("On SERVER side - found blog with id: " + blogId);
            Blog blog = document2Blog(result);

            final ReadBlogResponse response = ReadBlogResponse.newBuilder()
                    .setBlog(blog)
                    .build();
            System.out.println("On SERVER side - prepared response: " + response);
            responseObserver.onNext(response);

            responseObserver.onCompleted();
        }
    }

    @Override
    public void updateBlog(UpdateBlogRequest request, StreamObserver<UpdateBlogResponse> responseObserver) {
        System.out.println("UNARY request received on SERVER side - updateBlog: " + request);

        final Blog blogToUpdate = request.getBlog();
        final String blogId = blogToUpdate.getId();

        Document result = null;
        try {

            System.out.println("On SERVER side - fetching blog with id: " + blogId);
            result = collection.find(eq("_id", new ObjectId(blogId)))
                    .first();

        } catch (Exception e) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Exception occurred while fetching blog with id: " + blogId)
                            .augmentDescription(e.getLocalizedMessage())
                            .asRuntimeException()
            );
        }

        if (result == null) {
            System.out.println("On SERVER side - blog with id: " + blogId + " was not found -> can't be updated");
            // we don't have a match
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("The blog with the id : " + blogId + " was not found")
                            .asRuntimeException()
            );
        } else {
            System.out.println("On SERVER side - found blog with id: " + blogId + " -> updating");
            final Document documentToUpdate = blog2Document(blogToUpdate);

            System.out.println("On SERVER side - updating blog with id: " + blogId);
            collection.replaceOne(eq("_id", result.getObjectId("_id")), documentToUpdate);

            final Blog updatedBlog = document2Blog(documentToUpdate);
            final UpdateBlogResponse response = UpdateBlogResponse.newBuilder()
                    .setBlog(updatedBlog)
                    .build();

            System.out.println("On SERVER side - prepared response: " + response);
            responseObserver.onNext(response);

            responseObserver.onCompleted();
        }
    }

    @Override
    public void deleteBlog(DeleteBlogRequest request, StreamObserver<DeleteBlogResponse> responseObserver) {

        System.out.println("UNARY request received on SERVER side - deleteBlog: " + request);

        final String blogId = request.getId();
        DeleteResult result = null;
        try {
            result = collection.deleteOne(eq("_id", new ObjectId(blogId)));
        } catch (Exception e) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Exception occurred while deleting blog with id: " + blogId)
                            .augmentDescription(e.getLocalizedMessage())
                            .asRuntimeException()
            );
        }


        if (Optional.ofNullable(result).map(r -> r.getDeletedCount() == 0).orElse(false)) {
            System.out.println("On SERVER side - blog with id: " + blogId + " was not found -> can't be deleted");
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("The blog with the corresponding id was not found")
                            .asRuntimeException()
            );
        } else {
            System.out.println("On SERVER side - found blog with id: " + blogId + " -> deleting");
            responseObserver.onNext(DeleteBlogResponse.newBuilder()
                    .setDeleted(true)
                    .build());

            responseObserver.onCompleted();
        }

    }
    @Override
    public void listBlog(ListBlogRequest request, StreamObserver<ListBlogResponse> responseObserver) {
        System.out.println("UNARY request received on SERVER side - listBlog");

        System.out.println("On SERVER side - fetching and streaming all the blogs");
        collection.find().iterator().forEachRemaining(document -> responseObserver.onNext(
                ListBlogResponse.newBuilder().setBlog(document2Blog(document)).build()
        ));

        responseObserver.onCompleted();
    }
}
