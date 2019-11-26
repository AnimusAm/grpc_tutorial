package hr.smilebacksmile.grpc.provider;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ClientChannelProvider {

    public static ManagedChannel getStandardChannelOnPort(int port) {
        return ManagedChannelBuilder.forAddress("localhost", port)
                .usePlaintext() // needed not to invoke SSL usage
                .build();
    }
}
