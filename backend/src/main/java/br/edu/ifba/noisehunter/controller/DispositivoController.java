package br.edu.ifba.noisehunter.controller;

import br.edu.ifba.noisehunter.dto.DispositivoRequest;
import br.edu.ifba.noisehunter.model.Dispositivo;
import br.edu.ifba.noisehunter.model.Usuario;
import br.edu.ifba.noisehunter.repository.DispositivoRepository;
import br.edu.ifba.noisehunter.repository.OcorrenciaRepository;
import br.edu.ifba.noisehunter.repository.UsuarioRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dispositivos")
public class DispositivoController {
    private final DispositivoRepository dispositivos;
    private final OcorrenciaRepository ocorrencias;
    private final UsuarioRepository usuarios;

    public DispositivoController(DispositivoRepository dispositivos, OcorrenciaRepository ocorrencias,
                                UsuarioRepository usuarios) {
        this.dispositivos = dispositivos;
        this.ocorrencias = ocorrencias;
        this.usuarios = usuarios;
    }

    @GetMapping
    public List<Dispositivo> listar() { return dispositivos.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Dispositivo> buscar(@PathVariable Integer id) {
        return dispositivos.findById(id).map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody DispositivoRequest r) {
        Usuario usuario = buscarUsuario(r.usuarioId());
        if (r.usuarioId() != null && usuario == null)
            return ResponseEntity.badRequest().body(Map.of("mensagem", "Usuário não encontrado."));
        Dispositivo d = new Dispositivo();
        aplicarCampos(d, r, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(dispositivos.save(d));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Integer id, @Valid @RequestBody DispositivoRequest r) {
        Dispositivo d = dispositivos.findById(id).orElse(null);
        if (d == null) return ResponseEntity.notFound().build();
        Usuario usuario = buscarUsuario(r.usuarioId());
        if (r.usuarioId() != null && usuario == null)
            return ResponseEntity.badRequest().body(Map.of("mensagem", "Usuário não encontrado."));
        aplicarCampos(d, r, usuario);
        return ResponseEntity.ok(dispositivos.save(d));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Integer id) {
        if (!dispositivos.existsById(id)) return ResponseEntity.notFound().build();
        if (ocorrencias.existsByDispositivo_Id(id))
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "mensagem", "Este dispositivo possui ocorrências vinculadas. Exclua-as antes."));
        dispositivos.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Usuario buscarUsuario(Integer id) {
        return id == null ? null : usuarios.findById(id).orElse(null);
    }

    private static void aplicarCampos(Dispositivo d, DispositivoRequest r, Usuario usuario) {
        d.setNome(r.nome().trim());
        d.setLocalizacao(r.localizacao().trim());
        d.setStatus(r.status());
        d.setUsuario(usuario);
    }
}
