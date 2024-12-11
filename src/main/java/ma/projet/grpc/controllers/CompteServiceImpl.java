package ma.projet.grpc.controllers;

import io.grpc.stub.StreamObserver;
import ma.projet.grpc.services.CompteService;
import ma.projet.grpc.stubs.*;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.stream.Collectors;

@GrpcService
public class CompteServiceImpl extends CompteServiceGrpc.CompteServiceImplBase {

    private final CompteService compteService;

    public CompteServiceImpl(CompteService compteService) {
        this.compteService = compteService;
    }

    @Override
    public void allComptes(GetAllComptesRequest request, StreamObserver<GetAllComptesResponse> responseObserver) {
        var comptes = compteService.findAllComptes().stream()
                .map(compte -> Compte.newBuilder()
                        .setId(compte.getId().toString())  // Convert Long to String
                        .setSolde(compte.getSolde())
                        .setDateCreation(compte.getDateCreation())
                        .setType(TypeCompte.valueOf(compte.getType()))
                        .build())
                .collect(Collectors.toList());
        responseObserver.onNext(GetAllComptesResponse.newBuilder()
                .addAllComptes(comptes).build());
        responseObserver.onCompleted();
    }

    @Override
    public void saveCompte(SaveCompteRequest request, StreamObserver<SaveCompteResponse> responseObserver) {
        var compteReq = request.getCompte();
        var compte = new ma.projet.grpc.entities.Compte();
        compte.setSolde(compteReq.getSolde());
        compte.setDateCreation(compteReq.getDateCreation());
        compte.setType(compteReq.getType().name());
        var savedCompte = compteService.saveCompte(compte);
        var grpcCompte = Compte.newBuilder()
                .setId(savedCompte.getId().toString())  // Convert Long to String
                .setSolde(savedCompte.getSolde())
                .setDateCreation(savedCompte.getDateCreation())
                .setType(TypeCompte.valueOf(savedCompte.getType()))
                .build();
        responseObserver.onNext(SaveCompteResponse.newBuilder()
                .setCompte(grpcCompte).build());
        responseObserver.onCompleted();
    }

    @Override
    public void compteById(GetCompteByIdRequest request, StreamObserver<GetCompteByIdResponse> responseObserver) {
        var id = request.getId();
        var compte = compteService.findCompteById(Long.parseLong(id));  // Change from String to Long
        if (compte == null) {
            responseObserver.onError(new RuntimeException("Compte not found"));
        } else {
            var grpcCompte = Compte.newBuilder()
                    .setId(compte.getId().toString())  // Convert Long to String
                    .setSolde(compte.getSolde())
                    .setDateCreation(compte.getDateCreation())
                    .setType(TypeCompte.valueOf(compte.getType()))
                    .build();
            responseObserver.onNext(GetCompteByIdResponse.newBuilder()
                    .setCompte(grpcCompte).build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void totalSolde(GetTotalSoldeRequest request, StreamObserver<GetTotalSoldeResponse> responseObserver) {
        var comptes = compteService.findAllComptes();
        var count = comptes.size();
        var sum = comptes.stream().mapToDouble(ma.projet.grpc.entities.Compte::getSolde).sum();
        var average = count > 0 ? sum / count : 0;
        var stats = SoldeStats.newBuilder()
                .setCount(count)
                .setSum((float) sum)
                .setAverage((float) average)
                .build();
        responseObserver.onNext(GetTotalSoldeResponse.newBuilder()
                .setStats(stats).build());
        responseObserver.onCompleted();
    }

    @Override
    public void comptesByType(GetComptesByTypeRequest request, StreamObserver<GetComptesByTypeResponse> responseObserver) {
        var type = request.getType().name();
        var comptes = compteService.findComptesByType(type).stream()
                .map(compte -> Compte.newBuilder()
                        .setId(compte.getId().toString())  // Convert Long to String
                        .setSolde(compte.getSolde())
                        .setDateCreation(compte.getDateCreation())
                        .setType(TypeCompte.valueOf(compte.getType()))
                        .build())
                .collect(Collectors.toList());
        responseObserver.onNext(GetComptesByTypeResponse.newBuilder()
                .addAllComptes(comptes).build());
        responseObserver.onCompleted();
    }

    @Override
    public void deleteCompte(DeleteCompteRequest request, StreamObserver<DeleteCompteResponse> responseObserver) {
        var id = request.getId();
        var success = compteService.deleteCompte(Long.parseLong(id));  // Change from String to Long
        responseObserver.onNext(DeleteCompteResponse.newBuilder()
                .setSuccess(success).build());
        responseObserver.onCompleted();
    }
}