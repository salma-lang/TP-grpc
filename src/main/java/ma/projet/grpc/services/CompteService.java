package ma.projet.grpc.services;

import ma.projet.grpc.entities.Compte;
import ma.projet.grpc.repositories.CompteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompteService {
    private final CompteRepository compteRepository;

    public CompteService(CompteRepository compteRepository) {
        this.compteRepository = compteRepository;
    }

    public List<Compte> findAllComptes() {
        return compteRepository.findAll();
    }

    public Compte findCompteById(Long id) {  // Change from String to Long
        return compteRepository.findById(id).orElse(null);
    }

    public Compte saveCompte(Compte compte) {
        return compteRepository.save(compte);
    }

    public List<Compte> findComptesByType(String type) {
        return compteRepository.findAll().stream()
                .filter(compte -> compte.getType().equals(type))
                .collect(Collectors.toList());
    }

    public boolean deleteCompte(Long id) {  // Change from String to Long
        if (compteRepository.existsById(id)) {
            compteRepository.deleteById(id);
            return true;
        }
        return false;
    }
}