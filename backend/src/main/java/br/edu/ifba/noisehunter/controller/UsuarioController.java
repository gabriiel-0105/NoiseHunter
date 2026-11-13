package br.edu.ifba.noisehunter.controller;

import br.edu.ifba.noisehunter.dto.UsuarioRequest;
import br.edu.ifba.noisehunter.model.Usuario;
import br.edu.ifba.noisehunter.repository.DispositivoRepository;
import br.edu.ifba.noisehunter.repository.UsuarioRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioRepository usuarios;
    private final DispositivoRepository dispositivos;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UsuarioController(UsuarioRepository usuarios, DispositivoRepository dispositivos) {
        this.usuarios = usuarios;
        this.dispositivos = dispositivos;
    }

    @GetMapping
    public List<Usuario> listar() { return usuarios.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscar(@PathVariable Integer id) {
        return usuarios.findById(id).map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody UsuarioRequest r) {
        if (r.senha() == null || r.senha().length() < 8)
            return ResponseEntity.badRequest().body(Map.of("mensagem", "A senha deve ter ao menos 8 caracteres."));
        String email = r.email().trim();
        if (usuarios.existsByEmailIgnoreCase(email))
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensagem", "Este e-mail já está cadastrado."));
        Usuario u = new Usuario();
        u.setNome(r.nome().trim());
        u.setEmail(email);
        u.setSenha(encoder.encode(r.senha()));
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarios.save(u));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Integer id, @Valid @RequestBody UsuarioRequest r) {
        Usuario u = usuarios.findById(id).orElse(null);
        if (u == null) return ResponseEntity.notFound().build();
        String email = r.email().trim();
        if (!email.equalsIgnoreCase(u.getEmail()) && usuarios.existsByEmailIgnoreCase(email))
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensagem", "Este e-mail já está cadastrado."));
        u.setNome(r.nome().trim());
        u.setEmail(email);
        if (r.senha() != null && !r.senha().isBlank()) {
            if (r.senha().length() < 8)
                return ResponseEntity.badRequest().body(Map.of("mensagem", "A senha deve ter ao menos 8 caracteres."));
            u.setSenha(encoder.encode(r.senha()));
        }
        return ResponseEntity.ok(usuarios.save(u));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Integer id) {
        if (!usuarios.existsById(id)) return ResponseEntity.notFound().build();
        if (dispositivos.existsByUsuario_Id(id))
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "mensagem", "Este usuário possui dispositivos vinculados. Desvincule-os antes de excluir."));
        usuarios.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
