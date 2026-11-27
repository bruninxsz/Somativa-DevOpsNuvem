package br.com.senai.exercicio01.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.com.senai.exercicio01.dto.DadosDTO;
import br.com.senai.exercicio01.model.Dados;
import br.com.senai.exercicio01.model.Maquinas;
import br.com.senai.exercicio01.repository.DadosRepository;
import br.com.senai.exercicio01.repository.MaquinasRepository;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/producao")
public class DadosController {
    private final DadosRepository dadosRepository;
    private final MaquinasRepository maquinasRepository;

    public DadosController(DadosRepository dadosRepository, MaquinasRepository maquinasRepository) {
        this.dadosRepository = dadosRepository;
        this.maquinasRepository = maquinasRepository;
    }

    @PostMapping
    public ResponseEntity<?> registrar(@Valid @RequestBody DadosDTO dto) {
        Maquinas maquina = maquinasRepository.findById(dto.getMaquinaId()).orElse(null);
        if (maquina == null) {
            return maquinaNaoEncontrada();
        }
        Dados dado = new Dados();
        dado.setMaquina(maquina);
        copiar(dto, dado);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(dadosRepository.save(dado)));
    }

    @GetMapping
    public List<DadosDTO> consultar(@RequestParam(required = false) Long maquinaId) {
        List<Dados> dados = maquinaId == null ? dadosRepository.findAll() : dadosRepository.findByMaquina_Id(maquinaId);
        return dados.stream().map(DadosController::toDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        return dadosRepository.findById(id).<ResponseEntity<?>>map(d -> ResponseEntity.ok(toDto(d)))
                .orElseGet(DadosController::registroNaoEncontrado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> alterar(@PathVariable Long id, @Valid @RequestBody DadosDTO dto) {
        Dados dado = dadosRepository.findById(id).orElse(null);
        if (dado == null) {
            return registroNaoEncontrado();
        }
        Maquinas maquina = maquinasRepository.findById(dto.getMaquinaId()).orElse(null);
        if (maquina == null) {
            return maquinaNaoEncontrada();
        }
        dado.setMaquina(maquina);
        copiar(dto, dado);
        return ResponseEntity.ok(toDto(dadosRepository.save(dado)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        if (!dadosRepository.existsById(id)) {
            return registroNaoEncontrado();
        }
        dadosRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private static void copiar(DadosDTO dto, Dados dado) {
        dado.setConsumoKw(dto.getConsumoKw());
        dado.setHorarioAcionamento(dto.getHorarioAcionamento());
        dado.setPecasProduzidas(dto.getPecasProduzidas());
    }

    private static ResponseEntity<?> maquinaNaoEncontrada() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", "Máquina não encontrada"));
    }

    private static ResponseEntity<?> registroNaoEncontrado() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", "Registro de produção não encontrado"));
    }

    private static DadosDTO toDto(Dados dado) {
        DadosDTO dto = new DadosDTO();
        dto.setId(dado.getId());
        dto.setMaquinaId(dado.getMaquina().getId());
        dto.setConsumoKw(dado.getConsumoKw());
        dto.setHorarioAcionamento(dado.getHorarioAcionamento());
        dto.setPecasProduzidas(dado.getPecasProduzidas());
        return dto;
    }
}
