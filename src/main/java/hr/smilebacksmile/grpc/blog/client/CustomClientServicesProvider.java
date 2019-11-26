package hr.smilebacksmile.grpc.blog.client;

import hr.smilebacksmile.blog.*;

import java.util.ArrayList;
import java.util.List;

import static hr.smilebacksmile.blog.BlogServiceGrpc.*;

public class CustomClientServicesProvider {

    private static Blog getNewBlog(final String id, final String author, final String title, final String content) {
        return getNewBlog(author, title, content).toBuilder().setId(id).build();
    }

    private static Blog getNewBlog(final String author, final String title, final String content) {
        return Blog.newBuilder()
                .setAuthorId(author)
                .setTitle(title)
                .setContent(content)
                .build();
    }

    private static Blog getNewBlog(final String id) {
        return Blog.newBuilder()
                .setId(id)
                .build();
    }

    public static Blog requestNewBlogCreation(final BlogServiceBlockingStub unaryClient) {

        final Blog newBlog = getNewBlog("Author", "First blog ever!", "Nice to meet you");

        // Make the Request containing the prepared Blog
        final CreateBlogRequest request = CreateBlogRequest.newBuilder().setBlog(newBlog).build();

        System.out.println("UNARY request for creating new blog prepared on CLIENT side: " + request);

        CreateBlogResponse createResponse = unaryClient.createBlog(request);

        System.out.println("UNARY response for creating new blog received from SERVER side: " + createResponse);
        return createResponse.getBlog();
    }

    public static Blog requestBlog(final BlogServiceBlockingStub unaryClient, final String id) {

        // Make the Request containing the prepared Blog
        final ReadBlogRequest request = ReadBlogRequest.newBuilder()
                .setBlog(getNewBlog(id))
                .build();

        System.out.println("UNARY request for fetching blog prepared on CLIENT side: " + request);

        ReadBlogResponse readBlogResponse = unaryClient.readBlog(request);

        System.out.println("UNARY response for fetching blog received from SERVER side: " + readBlogResponse);
        return readBlogResponse.getBlog();

    }

    public static Blog updateBlog(final BlogServiceBlockingStub unaryClient, final Blog blog) {

        // Make the Request containing the prepared Blog
        final UpdateBlogRequest request = UpdateBlogRequest.newBuilder().setBlog(blog).build();

        System.out.println("UNARY request for updating blog prepared on CLIENT side: " + request);

        UpdateBlogResponse updateBlogResponse = unaryClient.updateBlog(request);

        System.out.println("UNARY response for updating blog received from SERVER side: " + updateBlogResponse);
        return updateBlogResponse.getBlog();

    }

    public static boolean deleteBlog(final BlogServiceBlockingStub unaryClient, final String id) {

        // Make the Request containing the prepared Blog
        final DeleteBlogRequest request = DeleteBlogRequest.newBuilder().setId(id).build();

        System.out.println("UNARY request for deleting blog prepared on CLIENT side: " + request);

        DeleteBlogResponse deleteBlogResponse = unaryClient.deleteBlog(request);

        System.out.println("UNARY response for deleting blog received from SERVER side: " + deleteBlogResponse);
        return deleteBlogResponse.getDeleted();

    }

    public static List<Blog> findAllBlogs(final BlogServiceBlockingStub unaryClient) {

        final List<Blog> allBlogs = new ArrayList<>();

        System.out.println("UNARY request for getting all blogs prepared on CLIENT side");
        unaryClient.listBlog(null).forEachRemaining(
                listBlogResponse -> {
                    allBlogs.add(listBlogResponse.getBlog());
                    System.out.println("STREAMING response for blog received from SERVER side: " + listBlogResponse);
                }
        );

        return allBlogs;

    }
}
