<template>
  <AdminLayout>
    <div class="dashboard-page">
      <section class="dashboard-hero mb-6">
        <div class="hero-content">
          <div class="d-flex align-center ga-2 mb-3">
            <v-icon icon="mdi-waveform" size="small" />
            <span class="hero-eyebrow">NOISEHUNTER · PAINEL DE CONTROLE</span>
          </div>
          <h1 class="hero-title">Monitoramento sonoro</h1>
          <p class="hero-description">Acompanhe dispositivos, registros e tendências acústicas em um só lugar.</p>
          <div class="d-flex flex-wrap align-center ga-3 mt-4">
            <span class="hero-updated">{{ ultimaAtualizacao ? `Atualizado em ${formatarData(ultimaAtualizacao)}` : 'Aguardando atualização' }}</span>
          </div>
        </div>
        <div class="hero-illustration" aria-hidden="true">
          <div class="signal signal-1"></div>
          <div class="signal signal-2"></div>
          <div class="signal signal-3"></div>
          <v-icon icon="mdi-access-point" size="72" color="white" />
        </div>
      </section>

      <div class="d-flex align-center flex-wrap justify-space-between ga-3 mb-5">
        <div>
          <h2 class="text-h6 font-weight-bold">Visão geral</h2>
          <p class="text-body-2 text-medium-emphasis">Informações consultadas na API e armazenadas no MySQL.</p>
        </div>
        <div class="d-flex flex-wrap align-center ga-2 dashboard-controls">
          <v-select
            v-model="dispositivoSelecionado"
            :items="opcoesDispositivos"
            label="Dispositivo"
            density="compact"
            variant="outlined"
            hide-details
            item-title="title"
            item-value="value"
            class="device-filter"
            aria-label="Filtrar por dispositivo"
          />
          <v-select
            v-model="periodo"
            :items="opcoesPeriodo"
            label="Período"
            density="compact"
            variant="outlined"
            hide-details
            item-title="title"
            item-value="value"
            class="period-filter"
            aria-label="Selecionar período"
          />
          <v-btn prepend-icon="mdi-refresh" color="primary" :loading="carregando" @click="atualizar">Atualizar</v-btn>
        </div>
      </div>

      <v-alert v-if="erro" type="error" variant="tonal" class="mb-5" icon="mdi-alert-circle-outline">
        {{ erro }} Confirme se o backend está ativo na porta 8081 e se o MySQL está acessível.
      </v-alert>

     

      <v-row class="mb-1">
        <v-col v-for="cartao in cartoes" :key="cartao.titulo" cols="12" sm="6" xl="3">
          <v-card class="metric-card h-100" rounded="xl" elevation="0" border>
            <div class="d-flex justify-space-between align-start">
              <div class="metric-icon" :class="`metric-icon--${cartao.cor}`"><v-icon :icon="cartao.icone" size="24" /></div>
              <v-chip size="x-small" variant="tonal" :color="cartao.cor">{{ cartao.tag }}</v-chip>
            </div>
            <div class="metric-value mt-4">{{ cartao.valor }}</div>
            <div class="font-weight-medium text-body-2 mt-1">{{ cartao.titulo }}</div>
            <div class="text-caption text-medium-emphasis mt-1">{{ cartao.descricao }}</div>
          </v-card>
        </v-col>
      </v-row>

      <v-row class="mt-1">
        <v-col cols="12" lg="8">
          <v-card class="h-100 chart-card" rounded="xl" elevation="0" border>
            <div class="d-flex flex-wrap justify-space-between ga-2 align-start mb-1">
              <div>
                <h3 class="text-h6 font-weight-bold">Evolução dos registros</h3>
                <p class="text-body-2 text-medium-emphasis">{{ periodo === 7 ? 'Ocorrências por dia nos últimos 7 dias' : 'Ocorrências por intervalo de 5 dias nos últimos 30 dias' }}</p>
              </div>
              <v-chip size="small" color="primary" variant="tonal" prepend-icon="mdi-chart-bar">{{ eventosFiltrados.length }} registros</v-chip>
            </div>
            <div class="bar-chart" role="img" :aria-label="descricaoGrafico">
              <div v-for="(barra, indice) in barras" :key="indice" class="bar-column">
                <span class="bar-count">{{ barra.valor }}</span>
                <div class="bar-track">
                  <div class="bar-fill" :style="{ height: `${barra.altura}%` }"></div>
                </div>
                <span class="bar-label">{{ barra.rotulo }}</span>
              </div>
            </div>
            <div class="chart-footer">Contagem de ocorrências registradas · os dias sem registros aparecem com zero.</div>
          </v-card>
        </v-col>
        <v-col cols="12" lg="4">
          <v-card class="h-100 chart-card" rounded="xl" elevation="0" border>
            <h3 class="text-h6 font-weight-bold">Tipos de ocorrência</h3>
            <p class="text-body-2 text-medium-emphasis">Distribuição dos eventos no período</p>
            <div class="donut-container">
              <div class="donut" :style="{ background: gradienteRosca }" role="img" :aria-label="descricaoDistribuicao">
                <div class="donut-center">
                  <div class="donut-total">{{ eventosFiltrados.length }}</div>
                  <div class="donut-subtitle">eventos</div>
                </div>
              </div>
            </div>
            <div v-for="tipo in distribuicao" :key="tipo.tipo" class="d-flex align-center mb-3 ga-2">
              <span class="legend-dot" :style="{ backgroundColor: tipo.cor }"></span>
              <span class="text-body-2 flex-grow-1">{{ tipo.nome }}</span>
              <span class="font-weight-bold text-body-2">{{ tipo.total }}</span>
              <span class="text-caption text-medium-emphasis legend-percent">{{ tipo.percentual }}%</span>
            </div>
          </v-card>
        </v-col>
      </v-row>

      <v-row class="mt-1">
        <v-col cols="12" lg="8">
          <v-card rounded="xl" elevation="0" border class="h-100">
            <div class="d-flex justify-space-between align-center flex-wrap ga-2 pa-5">
              <div>
                <h3 class="text-h6 font-weight-bold">Ocorrências recentes</h3>
                <p class="text-body-2 text-medium-emphasis">Os últimos registros do período selecionado</p>
              </div>
              <v-btn to="/eventos" variant="text" color="primary" append-icon="mdi-arrow-right">Ver histórico</v-btn>
            </div>
            <v-divider />
            <div class="table-scroll">
              <v-table density="comfortable" class="recent-table">
                <thead><tr><th>Data e hora</th><th>Dispositivo</th><th>Tipo</th><th>Intensidade</th></tr></thead>
                <tbody>
                  <tr v-for="evento in eventosRecentes" :key="evento.id">
                    <td class="text-no-wrap">{{ formatarData(evento.registradoEm) }}</td>
                    <td>{{ evento.dispositivo?.nome || 'Não informado' }}</td>
                    <td><v-chip :color="corTipo(evento.tipo)" variant="tonal" size="small">{{ nomesTipos[evento.tipo] || evento.tipo }}</v-chip></td>
                    <td class="text-no-wrap font-weight-medium">{{ formatarDb(evento.decibeis) }}</td>
                  </tr>
                  <tr v-if="!eventosRecentes.length"><td colspan="4" class="text-center text-medium-emphasis pa-8">Nenhuma ocorrência encontrada para os filtros selecionados.</td></tr>
                </tbody>
              </v-table>
            </div>
          </v-card>
        </v-col>
        <v-col cols="12" lg="4">
          <v-card rounded="xl" elevation="0" border class="h-100 summary-card">
            <h3 class="text-h6 font-weight-bold mb-1">Resumo do monitoramento</h3>
            <p class="text-body-2 text-medium-emphasis mb-5">Indicadores dos registros</p>
            <div class="summary-item">
              <div class="summary-icon" style="color:#D97706;background:#FEF3C7"><v-icon icon="mdi-car" /></div>
              <div class="flex-grow-1"><div class="summary-value">{{ totalVeiculos }}</div><div class="text-body-2 text-medium-emphasis">Ruídos de veículos</div></div>
            </div>
            <div class="summary-item">
              <div class="summary-icon" style="color:#DC2626;background:#FEE2E2"><v-icon icon="mdi-alert-outline" /></div>
              <div class="flex-grow-1"><div class="summary-value">{{ totalDisparos }}</div><div class="text-body-2 text-medium-emphasis">Possíveis disparos (simulados)</div></div>
            </div>
            <div class="summary-item">
              <div class="summary-icon" style="color:#0891B2;background:#CFFAFE"><v-icon icon="mdi-calendar-today-outline" /></div>
              <div class="flex-grow-1"><div class="summary-value">{{ eventosHoje }}</div><div class="text-body-2 text-medium-emphasis">Eventos registrados hoje</div></div>
            </div>
            <v-divider class="my-4" />
            <v-btn to="/dispositivos" block variant="tonal" color="primary" prepend-icon="mdi-access-point">Gerenciar dispositivos</v-btn>
            <v-btn to="/eventos" block class="mt-2" color="primary" prepend-icon="mdi-plus">Registrar evento de teste</v-btn>
          </v-card>
        </v-col>
      </v-row>
    </div>
  </AdminLayout>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { listarDispositivos, listarEventos, nomesTipos, formatarData } from '@/services/noisehunter_services'

const dispositivos = ref([])
const eventos = ref([])
const carregando = ref(false)
const erro = ref('')
const ultimaAtualizacao = ref(null)
const periodo = ref(7)
const dispositivoSelecionado = ref('todos')
const opcoesPeriodo = [
  { title: 'Últimos 7 dias', value: 7 },
  { title: 'Últimos 30 dias', value: 30 },
]

const opcoesDispositivos = computed(() => [
  { title: 'Todos os dispositivos', value: 'todos' },
  ...dispositivos.value.map((d) => ({ title: d.nome, value: d.id })),
])

function inicioDoDia(data) {
  return new Date(data.getFullYear(), data.getMonth(), data.getDate())
}

function adicionarDias(data, quantidade) {
  const copia = new Date(data)
  copia.setDate(copia.getDate() + quantidade)
  return copia
}

function dataValida(valor) {
  if (!valor) return null
  const data = new Date(valor)
  return Number.isNaN(data.getTime()) ? null : data
}

function numeroSeguro(valor) {
  if (valor === null || valor === undefined || valor === '') return null
  const numero = Number(valor)
  return Number.isFinite(numero) ? numero : null
}

function formatarNumero(valor) {
  return new Intl.NumberFormat('pt-BR', { maximumFractionDigits: 1 }).format(valor)
}

function formatarDb(valor) {
  const numero = numeroSeguro(valor)
  return numero === null ? '—' : `${formatarNumero(numero)} dB`
}

const hoje = ref(inicioDoDia(new Date()))
const eventosFiltrados = computed(() => {
  const comeco = adicionarDias(hoje.value, -(periodo.value - 1))
  return eventos.value
    .filter((evento) => {
      const data = dataValida(evento.registradoEm)
      if (!data || data < comeco) return false
      return dispositivoSelecionado.value === 'todos' || Number(evento.dispositivo?.id) === Number(dispositivoSelecionado.value)
    })
    .sort((a, b) => new Date(b.registradoEm) - new Date(a.registradoEm))
})

const decibeisValidos = computed(() => eventosFiltrados.value.map((evento) => numeroSeguro(evento.decibeis)).filter((valor) => valor !== null))
const mediaDb = computed(() => decibeisValidos.value.length ? formatarDb(decibeisValidos.value.reduce((a, b) => a + b, 0) / decibeisValidos.value.length) : '—')
const picoDb = computed(() => decibeisValidos.value.length ? formatarDb(Math.max(...decibeisValidos.value)) : '—')
const totalVeiculos = computed(() => eventosFiltrados.value.filter((evento) => evento.tipo === 'VEICULO_BARULHENTO').length)
const totalDisparos = computed(() => eventosFiltrados.value.filter((evento) => evento.tipo === 'DISPARO_SUSPEITO').length)
const eventosHoje = computed(() => eventosFiltrados.value.filter((evento) => {
  const data = dataValida(evento.registradoEm)
  return data && data >= hoje.value && data < adicionarDias(hoje.value, 1)
}).length)

const cartoes = computed(() => [
  {
    titulo: 'Dispositivos cadastrados',
    valor: dispositivoSelecionado.value === 'todos' ? dispositivos.value.length : dispositivos.value.filter((d) => Number(d.id) === Number(dispositivoSelecionado.value)).length,
    descricao: 'Sensores cadastrados no sistema',
    icone: 'mdi-access-point', cor: 'primary', tag: 'Cadastro',
  },
  {
    titulo: 'Ocorrências no período',
    valor: eventosFiltrados.value.length,
    descricao: `Registros dos últimos ${periodo.value} dias`,
    icone: 'mdi-waveform', cor: 'info', tag: 'Registros',
  },
  {
    titulo: 'Intensidade média',
    valor: mediaDb.value,
    descricao: 'Média dos registros filtrados',
    icone: 'mdi-chart-line', cor: 'success', tag: 'Média',
  },
  {
    titulo: 'Maior intensidade',
    valor: picoDb.value,
    descricao: 'Maior valor cadastrado no período',
    icone: 'mdi-volume-high', cor: 'warning', tag: 'Pico',
  },
])

const barras = computed(() => {
  const quantidade = periodo.value === 7 ? 7 : 6
  const tamanhoGrupo = periodo.value === 7 ? 1 : 5
  const inicio = adicionarDias(hoje.value, -(periodo.value - 1))
  const resultado = Array.from({ length: quantidade }, (_, indice) => {
    const primeiroDia = adicionarDias(inicio, indice * tamanhoGrupo)
    const proximoGrupo = adicionarDias(primeiroDia, tamanhoGrupo)
    const ultimoDia = adicionarDias(proximoGrupo, -1)
    const total = eventosFiltrados.value.filter((evento) => {
      const data = dataValida(evento.registradoEm)
      return data && data >= primeiroDia && data < proximoGrupo
    }).length
    const rotulo = tamanhoGrupo === 1
      ? primeiroDia.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' })
      : `${primeiroDia.toLocaleDateString('pt-BR', { day: '2-digit' })}–${ultimoDia.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' })}`
    return { valor: total, rotulo }
  })
  const maior = Math.max(1, ...resultado.map((b) => b.valor))
  return resultado.map((b) => ({ ...b, altura: b.valor === 0 ? 0 : Math.max(8, b.valor / maior * 100) }))
})

const descricaoGrafico = computed(() => barras.value.map((b) => `${b.rotulo}: ${b.valor} eventos`).join('; '))
const tipos = [
  { tipo: 'VEICULO_BARULHENTO', nome: 'Veículo barulhento', cor: '#F59E0B' },
  { tipo: 'DISPARO_SUSPEITO', nome: 'Possível disparo', cor: '#EF4444' },
  { tipo: 'OUTRO_RUIDO', nome: 'Outro ruído', cor: '#0EA5E9' },
]
const distribuicao = computed(() => tipos.map((tipo) => {
  const total = eventosFiltrados.value.filter((evento) => evento.tipo === tipo.tipo).length
  return { ...tipo, total, percentual: eventosFiltrados.value.length ? Math.round(total * 100 / eventosFiltrados.value.length) : 0 }
}))
const gradienteRosca = computed(() => {
  const total = eventosFiltrados.value.length
  if (total === 0) return 'conic-gradient(#E2E8F0 0% 100%)'
  let acumulado = 0
  const partes = distribuicao.value.map((item) => {
    const inicio = acumulado
    acumulado += item.total * 100 / total
    return `${item.cor} ${inicio}% ${acumulado}%`
  })
  return `conic-gradient(${partes.join(', ')})`
})
const descricaoDistribuicao = computed(() => distribuicao.value.map((t) => `${t.nome}: ${t.total}`).join('; '))
const eventosRecentes = computed(() => eventosFiltrados.value.slice(0, 6))
function corTipo(tipo) {
  return tipo === 'DISPARO_SUSPEITO' ? 'error' : tipo === 'VEICULO_BARULHENTO' ? 'warning' : 'info'
}

async function atualizar() {
  carregando.value = true
  erro.value = ''
  try {
    const [dispositivosRecebidos, eventosRecebidos] = await Promise.all([listarDispositivos(), listarEventos()])
    dispositivos.value = Array.isArray(dispositivosRecebidos) ? dispositivosRecebidos : []
    eventos.value = Array.isArray(eventosRecebidos) ? eventosRecebidos : []
    hoje.value = inicioDoDia(new Date())
    ultimaAtualizacao.value = new Date()
    if (dispositivoSelecionado.value !== 'todos' && !dispositivos.value.some((d) => Number(d.id) === Number(dispositivoSelecionado.value))) {
      dispositivoSelecionado.value = 'todos'
    }
  } catch (e) {
    erro.value = 'Não foi possível carregar os dados do dashboard.'
  } finally {
    carregando.value = false
  }
}

onMounted(atualizar)
</script>

<style scoped>
.dashboard-page { max-width: 1600px; margin: 0 auto; }
.dashboard-hero { position: relative; overflow: hidden; min-height: 220px; padding: 30px 36px; border-radius: 20px; color: white; background: linear-gradient(110deg,#102D49 0%,#13496A 63%,#087F90 100%); display: flex; align-items: center; }
.hero-content { position: relative; z-index: 2; }
.hero-eyebrow { font-size: 11px; font-weight: 800; letter-spacing: 1.3px; opacity: .86; }
.hero-title { font-size: clamp(25px, 3vw, 36px); font-weight: 800; letter-spacing: -.7px; line-height: 1.2; }
.hero-description { color: #DFEFF5; margin-top: 12px; font-size: 14px; max-width: 530px; }
.hero-updated { color: #C9E5EF; font-size: 12px; }
.hero-illustration { position: absolute; top: 50%; right: 12%; transform: translateY(-50%); width: 146px; height: 146px; display: flex; align-items: center; justify-content: center; }
.signal { position: absolute; border: 1px solid rgba(255,255,255,.18); border-radius: 50%; }
.signal-1 { inset: -10px; }.signal-2 { inset: -45px; }.signal-3 { inset: -80px; }
.device-filter { min-width: 200px; max-width: 260px; }.period-filter { min-width: 165px; max-width: 190px; }
.metric-card { padding: 22px; background-color: #fff; }
.metric-icon { height: 45px; width: 45px; border-radius: 12px; display: flex; align-items: center; justify-content: center; }
.metric-icon--primary { background:#E0EEF7; color:#123153; }.metric-icon--info { background:#CFFAFE; color:#0891B2; }.metric-icon--success { background:#DCFCE7; color:#15803D; }.metric-icon--warning { background:#FEF3C7; color:#B45309; }
.metric-value { font-size: clamp(26px, 2.4vw, 34px); font-weight: 800; letter-spacing: -.8px; line-height: 1.3; }
.chart-card { padding: 22px; background: white; }
.bar-chart { height: 250px; display: flex; gap: 12px; margin-top: 25px; align-items: stretch; }
.bar-column { flex: 1; min-width: 0; display: flex; flex-direction: column; align-items: center; justify-content: flex-end; }
.bar-count { font-size: 12px; font-weight: 700; color: #3A5368; margin-bottom: 7px; }
.bar-track { width: 100%; flex: 1; min-height: 10px; background: linear-gradient(0deg,#F0F6F9,#FBFCFE); border-radius: 8px 8px 0 0; display:flex; align-items:flex-end; overflow:hidden; }
.bar-fill { width: 100%; background: linear-gradient(0deg,#0B7991,#2DC4D0); border-radius: 7px 7px 0 0; transition: height .35s ease; }
.bar-label { color: #5B7083; font-size: 11px; white-space: nowrap; margin-top: 10px; }
.chart-footer { font-size: 11px; color: #718096; margin-top: 20px; }
.donut-container { display:flex; justify-content:center; padding: 25px 0 22px; }
.donut { width: 170px; height: 170px; border-radius: 50%; display:flex; align-items:center; justify-content:center; }
.donut-center { width: 119px; height: 119px; border-radius: 50%; background:#fff; display:flex; justify-content:center; align-items:center; flex-direction:column; }
.donut-total { font-size: 32px; font-weight: 800; line-height: 1.2; }.donut-subtitle { font-size: 12px; color:#64748B; }
.legend-dot { width: 10px; height: 10px; border-radius: 50%; flex-shrink: 0; }.legend-percent { width: 39px; text-align:right; }
.table-scroll { overflow-x: auto; }.recent-table { min-width: 600px; }.recent-table th { font-weight: 700; color: #475569; }
.summary-card { padding: 22px; }.summary-item { display:flex; align-items:center; gap:14px; margin-bottom:22px; }
.summary-icon { width:44px; height:44px; display:flex; align-items:center; justify-content:center; border-radius:12px; }
.summary-value { font-weight: 800; font-size:23px; line-height:1.2; }
@media (max-width: 700px) { .dashboard-hero { padding: 25px 20px; min-height: 230px; }.hero-illustration { opacity:.18; right:0; }.dashboard-controls { width:100%; }.device-filter,.period-filter { flex:1 1 150px; min-width:150px; max-width:none; }.bar-chart { gap:5px; height:205px; }.bar-label { font-size:9px; }.metric-card { padding:18px; } }
</style>
