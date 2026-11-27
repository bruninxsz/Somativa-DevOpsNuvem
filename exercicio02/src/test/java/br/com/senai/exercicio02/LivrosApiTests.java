package br.com.senai.exercicio02;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.senai.exercicio02.model.Livro;
import br.com.senai.exercicio02.repository.LivroRepository;

@SpringBootTest
@AutoConfigureMockMvc
class LivrosApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LivroRepository livroRepository;

    private Livro salvarLivro() {
        Livro livro = new Livro();
        livro.setTitulo("Redes de Computadores");
        livro.setAutor("Carlos Mendes");
        livro.setEditora("Net Books");
        livro.setAno(2021);
        livro.setCategoria("Redes");
        livro.setStatus("Disponível");
        return livroRepository.save(livro);
    }

    // 1. pagina inicial
    @Test
    void paginaInicial() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    // 2. listagem de livros
    @Test
    void listarLivros() throws Exception {
        mockMvc.perform(get("/livros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // 3. cadastro
    @Test
    void cadastrarLivro() throws Exception {
        String json = """
                {"titulo": "Banco de Dados para Iniciantes", "autor": "Maria Souza", "editora": "EduTech",
                 "ano": 2023, "categoria": "Informática", "status": "Disponível"}
                """;

        mockMvc.perform(post("/livros/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    // 4. consulta por ID
    @Test
    void consultarLivroPorId() throws Exception {
        Livro livro = salvarLivro();

        mockMvc.perform(get("/livros/{id}", livro.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Redes de Computadores"));
    }

    // 5. atualizacao
    @Test
    void alterarLivro() throws Exception {
        Livro livro = salvarLivro();
        String json = """
                {"titulo": "Banco de Dados Essencial", "autor": "Maria Souza", "editora": "EduTech",
                 "ano": 2024, "categoria": "Banco de Dados", "status": "Emprestado"}
                """;

        mockMvc.perform(put("/livros/alterar/{id}", livro.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Banco de Dados Essencial"));
    }

    // 6. livro inexistente
    @Test
    void livroInexistenteRetorna404() throws Exception {
        mockMvc.perform(get("/livros/{id}", 999))
                .andExpect(status().isNotFound());
    }

    // 7. documentacao
    @Test
    void documentacao() throws Exception {
        mockMvc.perform(get("/documentacao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endpoints").isArray());
    }
}
