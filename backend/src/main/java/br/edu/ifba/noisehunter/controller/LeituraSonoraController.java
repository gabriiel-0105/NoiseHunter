package br.edu.ifba.noisehunter.controller;

import br.edu.ifba.noisehunter.dto.LeituraSonoraRequest;
import br.edu.ifba.noisehunter.model.Dispositivo;
import br.edu.ifba.noisehunter.model.LeituraSonora;
import br.edu.ifba.noisehunter.repository.DispositivoRepository;
import br.edu.ifba.noisehunter.repository.LeituraSonoraRepository;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** API local de teste: recebe amplitude bruta, SEM inferir dB ou disparos. */
@RestController
@RequestMapping("/api/leituras")
public class LeituraSonoraController {
    private final LeituraSonoraRepository leituras;
    private final DispositivoRepository dispositivos;

    @Value("${noisehunter.limiar-amplitude:1000}")
    private int limiarAmplitude;

    public LeituraSonoraController(LeituraSonoraRepository leituras, DispositivoRepository dispositivos) {
        this.leituras = leituras;
        this.dispositivos = dispositivos;
    }

    @GetMapping
    public List<LeituraSonora> listar() {
        return leituras.findTop100ByOrderByDataHoraDesc();
    }

    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody LeituraSonoraRequest pedido) {
        Dispositivo d = dispositivos.findById(pedido.dispositivoId()).orElse(null);
        if (d == null) {
            return ResponseEntity.badRequest().body(Map.of("mensagem", "Dispositivo nao encontrado."));
        }
        LeituraSonora l = new LeituraSonora();
        l.setDispositivo(d);
        l.setAmplitudePp(pedido.amplitudePp());
        l.setIntervaloMs(pedido.intervaloMs());
        l.setClassificacao(pedido.amplitudePp() >= limiarAmplitude ? "PICO_INTENSO" : "AMBIENTE");
        l.setDataHora(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(leituras.save(l));
    }
}
