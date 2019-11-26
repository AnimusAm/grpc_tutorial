package hr.smilebacksmile.grpc.blog.client;

import hr.smilebacksmile.blog.BlogServiceGrpc;

import java.util.function.Function;

public interface BlogClientService {

    <T> T runAsUnary(Function<BlogServiceGrpc.BlogServiceBlockingStub, T> run);

    void shutDownTheChannel();
}
