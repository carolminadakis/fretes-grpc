package br.com.zup

import io.grpc.health.v1.HealthCheckRequest
import io.grpc.health.v1.HealthCheckResponse
import io.grpc.health.v1.HealthCheckResponse.*
import io.grpc.health.v1.HealthGrpc
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
class HealthCheckService : HealthGrpc.HealthImplBase() {

    override fun check(request: HealthCheckRequest?, responseObserver: StreamObserver<HealthCheckResponse>?) {
        responseObserver?.onNext(
            newBuilder()
                .setStatus(ServingStatus.SERVING)
                .build()
        )
        responseObserver?.onCompleted()
    }

    // deveria ficar de tempos em tempos notificando o cliente sobre a saúde da aplicação
    override fun watch(request: HealthCheckRequest?, responseObserver: StreamObserver<HealthCheckResponse>?) {
        responseObserver?.onNext(
            newBuilder()
                .setStatus(ServingStatus.SERVING)
                .build()
        )
    }
}
