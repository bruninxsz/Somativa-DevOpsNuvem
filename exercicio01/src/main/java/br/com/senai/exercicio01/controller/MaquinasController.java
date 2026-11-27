package br.com.senai.exercicio01.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import br.com.senai.exercicio01.dto.MaquinasDTO;
import br.com.senai.exercicio01.model.Maquinas;
import br.com.senai.exercicio01.repository.DadosRepository;
import br.com.senai.exercicio01.repository.MaquinasRepository;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/maquinas")
public class MaquinasController {
    private final MaquinasRepository repository;
    private final DadosRepository dadosRepository;

    public MaquinasController(MaquinasRepository repository, DadosRepository dadosRepository) {
        this.repository = repository;
        this.dadosRepository = dadosRepository;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<MaquinasDTO> cadastrar(@Valid @RequestBody MaquinasDTO dto) {
        Maquinas maquina = new Maquinas();
        copiar(dto, maquina);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(repository.save(maquina)));
    }

    @GetMapping
    public List<MaquinasDTO> listar() {
        return repository.findAll().stream().map(MaquinasController::toDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        return repository.findById(id).<ResponseEntity<?>>map(m -> ResponseEntity.ok(toDto(m)))
                .orElseGet(MaquinasController::naoEncontrada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> alterar(@PathVariable Long id, @Valid @RequestBody MaquinasDTO dto) {
        return repository.findById(id).<ResponseEntity<?>>map(maquina -> {
            copiar(dto, maquina);
            return ResponseEntity.ok(toDto(repository.save(maquina)));
        }).orElseGet(MaquinasController::naoEncontrada);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return naoEncontrada();
        }
        // remove primeiro o historico de producao ligado a maquina (chave estrangeira)
        dadosRepository.deleteAll(dadosRepository.findByMaquina_Id(id));
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private static void copiar(MaquinasDTO dto, Maquinas maquina) {
        maquina.setNome(dto.getNome());
        maquina.setTipo(dto.getTipo());
        maquina.setSetor(dto.getSetor());
        maquina.setStatus(dto.getStatus());
    }

    private static ResponseEntity<?> naoEncontrada() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", "Máquina não encontrada"));
    }

    static MaquinasDTO toDto(Maquinas maquina) {
        MaquinasDTO dto = new MaquinasDTO();
        dto.setId(maquina.getId());
        dto.setNome(maquina.getNome());
        dto.setTipo(maquina.getTipo());
        dto.setSetor(maquina.getSetor());
        dto.setStatus(maquina.getStatus());
        return dto;
    }
}
