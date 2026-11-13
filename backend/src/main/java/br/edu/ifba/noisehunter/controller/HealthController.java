package br.edu.ifba.noisehunter.controller;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api/health")
public class HealthController {
 @GetMapping public Map<String,String> status(){return Map.of("sistema","NoiseHunter","status","online");}
}
