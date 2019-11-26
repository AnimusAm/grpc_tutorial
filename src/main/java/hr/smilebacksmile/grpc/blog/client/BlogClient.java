package hr.smilebacksmile.grpc.blog.client;

import hr.smilebacksmile.blog.Blog;
import hr.smilebacksmile.grpc.provider.ClientChannelProvider;

import java.util.List;

import static hr.smilebacksmile.grpc.blog.client.CustomClientServicesProvider.*;

public class BlogClient {

    public static void main(String[] args) {
        System.out.println("Blog client side says hello");

        final BlogClientService client = new BlogClientServiceImpl(ClientChannelProvider.getStandardChannelOnPort(50054));

        try {

            final Blog newBlog = client.runAsUnary(CustomClientServicesProvider::requestNewBlogCreation);

            final String firstEverBlogId = newBlog.getId();
            // final String firstEverBlogId = "5ddd18fd1c44b933a4e45606";

            final Blog retrievedBlog = client.runAsUnary(clientChannel -> requestBlog(clientChannel, firstEverBlogId));

            final Blog blogToUpdate = retrievedBlog.toBuilder().setContent("Now it's not the first ever blog, it's actually been living for a while").build();

            final Blog updatedBlog = client.runAsUnary(clientChannel -> updateBlog(clientChannel, blogToUpdate));

            // final boolean deleted = client.runAsUnary(clientChannel -> CustomClientServicesProvider.deleteBlog(clientChannel, updatedBlog.getId()));

            final List<Blog> allBlogs = client.runAsUnary(CustomClientServicesProvider::findAllBlogs);
            allBlogs.forEach(
                    b ->
                    client.runAsUnary(clientChannel -> CustomClientServicesProvider.deleteBlog(clientChannel, b.getId()))
            );

        } finally {
            client.shutDownTheChannel();
        }
    }
}
