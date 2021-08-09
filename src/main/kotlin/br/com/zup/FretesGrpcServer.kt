package br.com.zup

import com.google.protobuf.Any
import com.google.rpc.Code
import io.grpc.Status
import io.grpc.protobuf.StatusProto
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class FretesGrpcServer : FretesServiceGrpc.FretesServiceImplBase() {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun calculaFrete(request: CalculaFreteRequest?, responseObserver: StreamObserver<CalculaFreteResponse>?) {

        //TODO: lógica para calcular o frete
        logger.info("Calculando frete para $request")

        val cep = request!!.cep
        if (cep == null || cep.isBlank()) {
            var error = Status.INVALID_ARGUMENT
                .withDescription("cep deve ser informado")
                .asRuntimeException()
            responseObserver?.onError(error)
            return
        }
        if (!cep.matches("[0-9]{5}-[\\d]{3}".toRegex())) {
            responseObserver?.onError(
                Status.INVALID_ARGUMENT
                    .withDescription("cep inválido")
                    .augmentDescription("formato esperado é 99999-999")
                    .asRuntimeException()
            )
            return
        }

        // simula erro de segurança
        if (cep.endsWith("333")) {
            val statusProto: com.google.rpc.Status = com.google.rpc.Status.newBuilder()
                .setCode(Code.PERMISSION_DENIED.getNumber())
                .setMessage("sem permissão para acessar esse recurso")
                .addDetails(
                    Any.pack(
                        ErrorDetails.newBuilder()
                            .setCode(401).setMessage("token expirado")
                            .build()
                    )
                )
                .build()

            responseObserver?.onError(StatusProto.toStatusRuntimeException(statusProto))
            return
        }

        var valor = 0.0
        try{
            valor = Random.nextDouble(from = 0.0, until = 140.0)
            if(valor > 100.0) {
                throw IllegalStateException("Erro inesperado simulado...")
            }
        }catch (e: Exception){
            responseObserver?.onError(Status.INTERNAL
                .withDescription(e.message)
                .withCause(e) // anexado ao Status, mas não é enviado para o cient
                .asRuntimeException())
            return
        }

        val response = CalculaFreteResponse.newBuilder()
            .setCep(request!!.cep)
            .setValor(valor)
            .build()

        logger.info("Frete calculado: $response")

        responseObserver!!.onNext(response)
        responseObserver.onCompleted()
    }
}