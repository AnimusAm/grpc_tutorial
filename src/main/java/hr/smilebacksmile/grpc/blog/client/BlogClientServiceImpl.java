package hr.smilebacksmile.grpc.blog.client;

import hr.smilebacksmile.blog.BlogServiceGrpc;
import io.grpc.ManagedChannel;

import java.util.function.Function;

public class BlogClientServiceImpl implements BlogClientService {

    private final ManagedChannel managedChannel;

    public BlogClientServiceImpl(ManagedChannel managedChannel) {
        this.managedChannel = managedChannel;
    }

    private static <T> T doUnaryCall(final ManagedChannel managedChannel, final Function<BlogServiceGrpc.BlogServiceBlockingStub, T> serverCall) {

        // Create clientStub - blocking, synchronous
        final BlogServiceGrpc.BlogServiceBlockingStub synchronousCalculatorClient =
                BlogServiceGrpc.newBlockingStub(managedChannel);

        System.out.println("Preparing UNARY call on CLIENT side");
        // Call method on server using custom service <- RPC:
        return serverCall.apply(synchronousCalculatorClient);
    }

    @Override
    public <T> T runAsUnary(final Function<BlogServiceGrpc.BlogServiceBlockingStub, T> run) {
        T result = doUnaryCall(this.managedChannel, run);
        return result;
    }

    @Override
    public void shutDownTheChannel() {
        System.out.println("Shutting down the client");
        managedChannel.shutdown();
    }


}


