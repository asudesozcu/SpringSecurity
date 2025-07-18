package common;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.61.0)",
    comments = "Source: email.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class EmailServiceGrpc {

  private EmailServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "EmailService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<common.EmailRequest,
      common.EmailResponse> getFetchLatestEmailsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "FetchLatestEmails",
      requestType = common.EmailRequest.class,
      responseType = common.EmailResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<common.EmailRequest,
      common.EmailResponse> getFetchLatestEmailsMethod() {
    io.grpc.MethodDescriptor<common.EmailRequest, common.EmailResponse> getFetchLatestEmailsMethod;
    if ((getFetchLatestEmailsMethod = EmailServiceGrpc.getFetchLatestEmailsMethod) == null) {
      synchronized (EmailServiceGrpc.class) {
        if ((getFetchLatestEmailsMethod = EmailServiceGrpc.getFetchLatestEmailsMethod) == null) {
          EmailServiceGrpc.getFetchLatestEmailsMethod = getFetchLatestEmailsMethod =
              io.grpc.MethodDescriptor.<common.EmailRequest, common.EmailResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "FetchLatestEmails"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  common.EmailRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  common.EmailResponse.getDefaultInstance()))
              .setSchemaDescriptor(new EmailServiceMethodDescriptorSupplier("FetchLatestEmails"))
              .build();
        }
      }
    }
    return getFetchLatestEmailsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static EmailServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<EmailServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<EmailServiceStub>() {
        @java.lang.Override
        public EmailServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new EmailServiceStub(channel, callOptions);
        }
      };
    return EmailServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static EmailServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<EmailServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<EmailServiceBlockingStub>() {
        @java.lang.Override
        public EmailServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new EmailServiceBlockingStub(channel, callOptions);
        }
      };
    return EmailServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static EmailServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<EmailServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<EmailServiceFutureStub>() {
        @java.lang.Override
        public EmailServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new EmailServiceFutureStub(channel, callOptions);
        }
      };
    return EmailServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void fetchLatestEmails(common.EmailRequest request,
        io.grpc.stub.StreamObserver<common.EmailResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getFetchLatestEmailsMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service EmailService.
   */
  public static abstract class EmailServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return EmailServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service EmailService.
   */
  public static final class EmailServiceStub
      extends io.grpc.stub.AbstractAsyncStub<EmailServiceStub> {
    private EmailServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected EmailServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new EmailServiceStub(channel, callOptions);
    }

    /**
     */
    public void fetchLatestEmails(common.EmailRequest request,
        io.grpc.stub.StreamObserver<common.EmailResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getFetchLatestEmailsMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service EmailService.
   */
  public static final class EmailServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<EmailServiceBlockingStub> {
    private EmailServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected EmailServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new EmailServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public common.EmailResponse fetchLatestEmails(common.EmailRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getFetchLatestEmailsMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service EmailService.
   */
  public static final class EmailServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<EmailServiceFutureStub> {
    private EmailServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected EmailServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new EmailServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<common.EmailResponse> fetchLatestEmails(
        common.EmailRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getFetchLatestEmailsMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_FETCH_LATEST_EMAILS = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_FETCH_LATEST_EMAILS:
          serviceImpl.fetchLatestEmails((common.EmailRequest) request,
              (io.grpc.stub.StreamObserver<common.EmailResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getFetchLatestEmailsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              common.EmailRequest,
              common.EmailResponse>(
                service, METHODID_FETCH_LATEST_EMAILS)))
        .build();
  }

  private static abstract class EmailServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    EmailServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return common.Email.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("EmailService");
    }
  }

  private static final class EmailServiceFileDescriptorSupplier
      extends EmailServiceBaseDescriptorSupplier {
    EmailServiceFileDescriptorSupplier() {}
  }

  private static final class EmailServiceMethodDescriptorSupplier
      extends EmailServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    EmailServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (EmailServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new EmailServiceFileDescriptorSupplier())
              .addMethod(getFetchLatestEmailsMethod())
              .build();
        }
      }
    }
    return result;
  }
}
