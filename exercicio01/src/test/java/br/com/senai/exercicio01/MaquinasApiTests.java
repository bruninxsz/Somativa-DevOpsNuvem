package br.com.senai.exercicio01;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.senai.exercicio01.model.Maquinas;
import br.com.senai.exercicio01.model.Status;
import br.com.senai.exercicio01.repository.MaquinasRepository;

@SpringBootTest
@AutoConfigureMockMvc
class MaquinasApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MaquinasRepository maquinasRepository;

    // a) pagina inicial responde corretamente
    @Test
    void paginaInicial() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    // b) /status retorna aplicacao online
    @Test
    void statusOnline() throws Exception {
        mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("online"));
    }

    // c) cadastrar uma maquina
    @Test
    void cadastrarMaquina() throws Exception {
        String json = """
                {"nome": "Maquina CNC 01", "tipo": "CNC", "setor": "Usinagem", "status": "em operação"}
                """;

        mockMvc.perform(post("/maquinas/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Maquina CNC 01"));
    }

    // d) listar maquinas cadastradas
    @Test
    void listarMaquinas() throws Exception {
        mockMvc.perform(get("/maquinas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // e) registrar dados de producao
    @Test
    void registrarProducao() throws Exception {
        Maquinas maquina = new Maquinas();
        maquina.setNome("Torno 01");
        maquina.setTipo("Torno");
        maquina.setSetor("Usinagem");
        maquina.setStatus(Status.em_operacao);
        Long id = maquinasRepository.save(maquina).getId();

        String json = """
                {"maquinaId": %d, "consumoKw": 12.5, "horarioAcionamento": "08:30", "pecasProduzidas": 150}
                """.formatted(id);

        mockMvc.perform(post("/producao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pecasProduzidas").value(150));
    }

    // f) codigo HTTP adequado para recurso inexistente
    @Test
    void producaoMaquinaInexistenteRetorna404() throws Exception {
        String json = """
                {"maquinaId": 999, "consumoKw": 1.0, "horarioAcionamento": "09:00", "pecasProduzidas": 1}
                """;

        mockMvc.perform(post("/producao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }
}
