package br.edu.ifba.noisehunter.controller;

import br.edu.ifba.noisehunter.dto.OcorrenciaRequest;
import br.edu.ifba.noisehunter.model.Dispositivo;
import br.edu.ifba.noisehunter.model.Ocorrencia;
import br.edu.ifba.noisehunter.repository.DispositivoRepository;
import br.edu.ifba.noisehunter.repository.OcorrenciaRepository;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ocorrencias")
public class OcorrenciaController {
    private static final Set<String> TIPOS = Set.of("VEICULO_BARULHENTO", "DISPARO_SUSPEITO", "OUTRO_RUIDO");
    private final OcorrenciaRepository ocorrencias;
    private final DispositivoRepository dispositivos;

    public OcorrenciaController(OcorrenciaRepository ocorrencias, DispositivoRepository dispositivos) {
        this.ocorrencias = ocorrencias;
        this.dispositivos = dispositivos;
    }

    @GetMapping
    public List<Ocorrencia> listar() { return ocorrencias.findAllByOrderByDataHoraDesc(); }

    @GetMapping("/{id}")
    public ResponseEntity<Ocorrencia> buscar(@PathVariable Integer id) {
        return ocorrencias.findById(id).map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody OcorrenciaRequest r) {
        if (!TIPOS.contains(r.tipoSom()))
            return ResponseEntity.badRequest().body(Map.of("mensagem", "Tipo de som inválido."));
        Dispositivo d = dispositivos.findById(r.dispositivoId()).orElse(null);
        if (d == null) return ResponseEntity.badRequest().body(Map.of("mensagem", "Dispositivo não encontrado."));
        Ocorrencia o = new Ocorrencia();
        o.setTipoSom(r.tipoSom());
        o.setDecibeis(r.decibeis());
        o.setDataHora(LocalDateTime.now());
        o.setLocalizacao(r.localizacao() == null || r.localizacao().isBlank()
            ? d.getLocalizacao() : r.localizacao().trim());
        o.setDispositivo(d);
        return ResponseEntity.status(HttpStatus.CREATED).body(ocorrencias.save(o));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        if (!ocorrencias.existsById(id)) return ResponseEntity.notFound().build();
        ocorrencias.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
